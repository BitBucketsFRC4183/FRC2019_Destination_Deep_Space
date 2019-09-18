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





// TODO: do we want an auto state running motors or the drive subsystem?
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



    private TrajectoryFinder traj;

    private Notifier notifier;
    private MotionPoint[] mps;
    private int iteration = 0;

    private boolean isFinished;
    private boolean finishedPushing;

    private DriveSubsystem drive;
    private WPI_TalonSRX leftMotor;
    private WPI_TalonSRX rightMotor;

    private MotionProfileStatus leftStatus;
    private MotionProfileStatus rightStatus;

    private double leftPos = 0;
    private double rightPos = 0;

    private double lastT = 0;



    private AutonomousSubsystem autonomousSubsystem = AutonomousSubsystem.instance();



    private class AutoLoop implements Runnable {
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

            // should both be synced but just in case
            if (leftStatus.isLast || rightStatus.isLast) {
                isFinished = true;

                // AutoDrive will call AutoPeriod.stop()

                return;
            }

            if (iteration == mps.length) {
                finishedPushing = true;
            }

            // once you have enough points, start running profile
            if (
                leftStatus.btmBufferCnt  > AutonomousConstants.MINIMUM_BUFFER_POINTS &&
                rightStatus.btmBufferCnt > AutonomousConstants.MINIMUM_BUFFER_POINTS
            ) {
                setMPValue(SetValueMotionProfile.Enable);
            }

            if (!finishedPushing) {
                // if you can add points to the buffer, do that
                if (leftStatus.btmBufferCnt != AutonomousConstants.TALON_MP_POINTS
                    || rightStatus.btmBufferCnt != 128) {
                    addNextMPs();
                }
            }
        }
    }



    public AutoPeriod() {
        generateTrajectory();

        leftMotor = drive.getLeftMotor(0);
        rightMotor = drive.getRightMotor(0);

        // download MPs twice as fast as execution time of an MP
        leftMotor.changeMotionControlFramePeriod((int) (AutonomousConstants.LOOP_MS_PER / 2));
        rightMotor.changeMotionControlFramePeriod((int) (AutonomousConstants.LOOP_MS_PER / 2));

        leftMotor.clearMotionProfileTrajectories();
        rightMotor.clearMotionProfileTrajectories();

        mps = traj.getMotionPoints();

        notifier = new Notifier(new AutoLoop());
        // twice as fast as duration of trajectory points
        notifier.startPeriodic(0.5 / AutonomousConstants.LOOP_HERTZ);

        leftStatus  = new MotionProfileStatus();
        rightStatus = new MotionProfileStatus();



        setMPValue(SetValueMotionProfile.Disable);


        for (int i = 0; i < AutonomousConstants.TALON_MP_POINTS; i++) {
            addNextMPs();
        }
    }

    private boolean addNextMPs() {
        if (iteration == mps.length) {
            return false;
        }

        MotionPoint mp  = mps[iteration];

        TrajectoryPoint leftP  = new TrajectoryPoint();
        TrajectoryPoint rightP = new TrajectoryPoint();

        double t = mp.t;

        double dt = t - lastT;

        // really bad approximation of acculumated position but best we have rn
        // without restructing all the MP code
        leftPos  += mp.left_vel  * dt;
        rightPos += mp.right_vel * dt;

        leftP.position = DriveConstants.inchToTicks(leftPos);
        leftP.velocity = DriveConstants.ipsToTicksP100(mp.left_vel);
        leftP.profileSlotSelect0 = DriveConstants.PID_MP_SLOT;
        // TODO: maybe consider making that value an int?
        leftP.timeDur = (int) (AutonomousConstants.LOOP_MS_PER);
        leftP.useAuxPID = false; // don't know what it is but we're in MP mode so don't use it
        leftP.zeroPos = (iteration == 0);
        leftP.isLastPoint = (iteration == mps.length - 1);

        rightP.position = DriveConstants.inchToTicks(rightPos);
        rightP.velocity = DriveConstants.ipsToTicksP100(mp.right_vel);
        rightP.profileSlotSelect0 = DriveConstants.PID_MP_SLOT;
        // TODO: maybe consider making that value an int?
        rightP.timeDur = (int) (AutonomousConstants.LOOP_MS_PER);
        rightP.useAuxPID = false; // don't know what it is but we're in MP mode so don't use it
        rightP.zeroPos = (iteration == 0);
        rightP.isLastPoint = (iteration == mps.length - 1);



        leftMotor.pushMotionProfileTrajectory(leftP);
        rightMotor.pushMotionProfileTrajectory(rightP);

        iteration++;



        return true;
    }



    private void setMPValue(SetValueMotionProfile val) {
        leftMotor. set(ControlMode.MotionProfile, val.value);
        rightMotor.set(ControlMode.MotionProfile, val.value);
    }



    private void generateTrajectory() {
        traj = new TrajectoryFinder(
            TrajectoryFinder.MotionProfile.TRAPEZOIDAL,
            PathFinder.PathType.CUBIC_HERMITE,
            autonomousSubsystem.getWaypoints(),
            DriveConstants.DRIVE_MOTOR_MOTION_ACCELERATION_IPSPS,
            DriveConstants.DRIVE_MOTOR_MOTION_CRUISE_SPEED_IPS,
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