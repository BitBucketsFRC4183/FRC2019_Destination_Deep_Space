package frc.robot.subsystem.autonomous;

import edu.wpi.first.wpilibj.Notifier;

import frc.robot.subsystem.autonomous.motion.TrajectoryFinder;
import frc.robot.subsystem.autonomous.motion.MotionPoint;
import frc.robot.subsystem.autonomous.motion.PathFinder;

import frc.robot.subsystem.drive.DriveSubsystem;
import frc.robot.subsystem.drive.DriveConstants;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;





/*
 * really bad naming...
 * 
 * 2019 was the first year there was no "autonomous period," instead there was a "sandstorm period"
 * However, wpilib still called this period of time the autonomous period
 * Within the sandstorm period, there is a length (period) of time in which we run our autonomous
 *     code - that is now our "autonomous period" within the sandstorm period
 * This is not an autonomous periodic, and it is not a State/Command though it practically
 *     acts like one
 * It creates the auto trajectory and feeds the motion points to the robot at a speed faster than
 *     States/Commands could, which allows us to execute auto with a better degree of accuracy
 */
public class AutoPeriod {
    // singleton method
    // shouldn't be making multiple autos but just in case
    private static AutoPeriod inst;
    public static AutoPeriod instance() {
        if (inst == null) {
            inst = new AutoPeriod();
        }

        return inst;
    }



    // trajectory to be used to execute auto
    private TrajectoryFinder traj;

    // wpilib notifier used to run high frequency auto loop
    private Notifier notifier;
    // array of motion points robot must cross
    private MotionPoint[] mps;
    // count of MPs that have been pushed to motors
    private int iteration = 0;

    // whether its finished executing motion profile
    private boolean isFinished;
    // whether its finished pushing MPs to motors
    private boolean finishedPushing;

    // drive stuff
    private DriveSubsystem drive;
    private WPI_TalonSRX leftMotor;
    private WPI_TalonSRX rightMotor;

    // info about MPs from left and right encoder-having motors
    // others follow these, don't need to consider
    private MotionProfileStatus leftStatus;
    private MotionProfileStatus rightStatus;

    // increment by small changes in motion profiles
    // these are inches moved by left and right wheels
    private double leftPos = 0;
    private double rightPos = 0;



    private AutonomousSubsystem autonomousSubsystem = AutonomousSubsystem.instance();



    private class AutoLoop implements Runnable {
        // method to be run in high frequency loop
        public void run() {
            if (isFinished) {
                return;
            }



            // run the motors according to next MP
            leftMotor.processMotionProfileBuffer();
            rightMotor.processMotionProfileBuffer();

            // get the current status of the profiles
            leftMotor. getMotionProfileStatus(leftStatus);
            rightMotor.getMotionProfileStatus(rightStatus);

            // both should be synced but just in case
            if (leftStatus.isLast || rightStatus.isLast) {
                // done with auto
                isFinished = true;

                // AutoDrive will call AutoPeriod.stop()
                // it calls isItFinished() in AutoDrive.isFinished
                // great naming i know

                return;
            }

            // if its out of MPs to push
            if (iteration == mps.length) {
                finishedPushing = true;
            }

            // once you have enough points, start running profile
            if (
                leftStatus.btmBufferCnt  > AutonomousConstants.MINIMUM_BUFFER_POINTS &&
                rightStatus.btmBufferCnt > AutonomousConstants.MINIMUM_BUFFER_POINTS
            ) {
                // enable motion profiling

                // when approaching end of motion profile, there won't be MINIMUM_BUFFER_POINTS
                // buffer points, but motors should be on Enable already (doesn't get Disabled)
                // so we don't need to worry about disabling towards end of MP
                setMPValue(SetValueMotionProfile.Enable);
            }

            // if there's more MPs to push (motors only handle 128 at a time), add more
            if (!finishedPushing) {
                // if you can add points to the buffer, do that
                if (
                    leftStatus.btmBufferCnt  != AutonomousConstants.TALON_MP_POINTS ||
                    rightStatus.btmBufferCnt != AutonomousConstants.TALON_MP_POINTS
                ) {
                    addNextMPs();
                }
            }
        }
    }



    public AutoPeriod() {
        generateTrajectory();

        leftMotor = drive.getLeftMotor(0);
        rightMotor = drive.getRightMotor(0);

        // download MPs into Talons twice as fast as execution time of an MP
        // this is the recommended procedure by CTRE
        leftMotor.changeMotionControlFramePeriod((int) (AutonomousConstants.LOOP_MS_PER / 2));
        rightMotor.changeMotionControlFramePeriod((int) (AutonomousConstants.LOOP_MS_PER / 2));

        // shouldn't be anything there but clear it anyways
        leftMotor.clearMotionProfileTrajectories();
        rightMotor.clearMotionProfileTrajectories();

        mps = traj.getMotionPoints();

        notifier = new Notifier(new AutoLoop());
        // twice as fast as duration of trajectory points (recommended)
        notifier.startPeriodic(0.5 * AutonomousConstants.LOOP_S_PER);

        // get initial statuses
        leftStatus  = new MotionProfileStatus();
        rightStatus = new MotionProfileStatus();



        setMPValue(SetValueMotionProfile.Disable);


        // try to fill buffer
        for (int i = 0; i < AutonomousConstants.TALON_MP_POINTS; i++) {
            addNextMPs();
        }
    }

    private void addNextMPs() {
        // necessary only if there's less than AutonomousConstants.TALON_MP_POINTS MPs to load in
        // initially, AutonomousConstants.TALON_MP_POINTS points are loaded in before trying to
        //     motion profile
        // this is done via a for loop that runs AutonomousConstants.TALON_MP_POINTS times
        // if there's less MPs than this number, don't want to keep adding more
        //     since there aren't any more
        // we can have between 159 to 186 MPs according to simulator so this
        //     shouldn't be a problem, but just in case :)
        if (iteration == mps.length) {
            return;
        }

        // get next MP
        MotionPoint mp  = mps[iteration];

        // CTRE's equivalent of our MotionPoint, except that ours has a lot more data
        // bc of testing
        TrajectoryPoint leftP  = new TrajectoryPoint();
        TrajectoryPoint rightP = new TrajectoryPoint();

        // really bad approximation of acculumated position but best we have rn
        // without restructuring all the MP code
        // if we had a closed loop velocity controller, would not be a problem
        // but motion profiling is more accurate (closed loop in position and
        // has feedforward velocity) for, well, motion profiling...
        // so we need to have position data too
        leftPos  += mp.left_vel  * AutonomousConstants.LOOP_S_PER;
        rightPos += mp.right_vel * AutonomousConstants.LOOP_S_PER;

        // calculate left trajectory point
        leftP.position = DriveConstants.inchToTicks(leftPos);
        leftP.velocity = DriveConstants.ipsToTicksP100(mp.left_vel);
        leftP.profileSlotSelect0 = DriveConstants.PID_MP_SLOT;
        leftP.timeDur = (int) (AutonomousConstants.LOOP_MS_PER);
        leftP.zeroPos = (iteration == 0); // zero encoders on first MP
        leftP.isLastPoint = (iteration == mps.length - 1);

        // calculate right trajectory point
        rightP.position = DriveConstants.inchToTicks(rightPos);
        rightP.velocity = DriveConstants.ipsToTicksP100(mp.right_vel);
        rightP.profileSlotSelect0 = DriveConstants.PID_MP_SLOT;
        rightP.timeDur = (int) (AutonomousConstants.LOOP_MS_PER);
        rightP.zeroPos = (iteration == 0); // zero encoders on first MP
        rightP.isLastPoint = (iteration == mps.length - 1);



        // push the trajectory points into the MP
        leftMotor.pushMotionProfileTrajectory(leftP);
        rightMotor.pushMotionProfileTrajectory(rightP);

        // new MP
        iteration++;
    }



    /**
     * Command drive motors to the specified MotionProfileValue, like Enable/Disable/Hold
     * 
     * @param val
     */
    private void setMPValue(SetValueMotionProfile val) {
        leftMotor. set(ControlMode.MotionProfile, val.value);
        rightMotor.set(ControlMode.MotionProfile, val.value);
    }



    private void generateTrajectory() {
        traj = new TrajectoryFinder(
            TrajectoryFinder.MotionProfile.TRAPEZOIDAL,
            PathFinder.PathType.QUINTIC_HERMITE, // 254 uses quintic so here we are
            autonomousSubsystem.getWaypoints(),
            DriveConstants.DRIVE_MOTOR_MOTION_ACCELERATION_IPSPS,
            DriveConstants.MAX_ALLOWED_SPEED_IPS,
            -DriveConstants.DRIVE_MOTOR_MOTION_ACCELERATION_IPSPS,
            0, 0
        );
    }

    public boolean isItFinished() {
        return isFinished;
    }

    public void stop() {
        notifier.close();

        setMPValue(SetValueMotionProfile.Disable);
    }
}