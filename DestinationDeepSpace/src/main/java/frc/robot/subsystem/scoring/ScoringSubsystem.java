/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.MotorId;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.drive.DriveSubsystem;
import frc.robot.utils.talonutils.TalonUtils;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Add your docs here.
 */
public class ScoringSubsystem extends BitBucketSubsystem {
	private final OI oi = OI.instance();

	// Singleton method; use ScoringSubsystem.instance() to get the ScoringSubsystem instance.
	public static ScoringSubsystem instance() {
		if(inst == null)
			inst = new ScoringSubsystem();
		return inst;
	}
	private static ScoringSubsystem inst;


	private final WPI_TalonSRX rollerMotor;

	private final WPI_TalonSRX rotationMotor1;
	private final WPI_TalonSRX rotationMotor2;



	// is the robot moving forward?
	private boolean lastForward = true;
	private boolean forward = true;
	// how long has the robot been going in the direction its been going for?
	private int forwardIterations = 0;



	private ScoringSubsystem()
	{
		setName("ScoringSubsystem");



		rollerMotor = new WPI_TalonSRX(MotorId.INTAKE_MOTOR_ID);



		rotationMotor1 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR1_ID);
		rotationMotor2 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR2_ID);

		// set it to 0 at starting position (front of robot)
		rotationMotor1.setSelectedSensorPosition(0);
		rotationMotor2.follow(rotationMotor1);



		TalonUtils.initializeMotorDefaults(rollerMotor);

		TalonUtils.initializeMotorDefaults(rotationMotor1);
		TalonUtils.initializeMotorDefaults(rotationMotor2);



		// TODO: TEMPORARY VALUES + also not the best place to put them in the first place
		double kf = 0.05115;
		double kp = 0.005683*2*2*2*2*2*2*1.5;
		double ki = 0.001;
		double kd = 10 * kp;
		int izone = 200;

		TalonUtils.initializeMotorFPID        (rotationMotor1, kf, kp, ki, kd, izone);
		TalonUtils.initializeQuadEncoderMotor (rotationMotor1, 1);

		TalonUtils.initializeMotorFPID        (rotationMotor2, kf, kp, ki, kd, izone);
		TalonUtils.initializeQuadEncoderMotor (rotationMotor2, 1);



		setAllMotorsZero();
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	/*
	 * I drew this for a method I realized we didn't even need but decided to keep it, enjoy!
	 * 
	 * 
	 *        \   /            \   /                               \   /            \   /
	 *         \_/              \_/               G O               \_/              \_/
 	 *          \\               \\                                 //               //
	 *           \\               \\              BIT              //               //
	 *       _____\\_____     _____\\_____                   _____//_____     _____//_____
	 *       |          |     |          |      BUCKETS      |          |     |          |
	 *      ==O========O==   ==O========O==                 ==O========O==   ==O========O==
	 */



	/**
	 * Direct the robot arm to a certain angle.
	 * If front is true, the angle is from the front of the robot, if it is false, it
	 * is from the back of the robot
	 */
	public void directArmTo(double angle, boolean front) {
		double rev = angle / 360;

		int ticks = (int) (rev * ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV);

		// if the arm is in the back of the robot
		if (front == false) {
			// switch the ticks so that the arm will go to intended position on the back too
			ticks = 2 * ScoringConstants.ARM_MOTOR_SWITCH_TICK_THRESHOLD - ticks;
		}

		rotationMotor1.set(ControlMode.MotionMagic, ticks);
	}



	// TODO: in the future, we will want to move the arm so it is parallel (not antiparallel)
	// to the robot's velocity. This should involve testing to see if the robot has been going
	// in the direction its going in for a long enough period of time (if it changes its velocity
	// back and forth, changing the arm position may tip the robot)
	// tl;dr - find "front" yourself
	public void goToLevel(ScoringConstants.ScoringLevel level, boolean front) {
		double height = level.getHeight();

		// tip of arm is given by (height off floor) + (length) * sin(angle)
		double angle = Math.asin((height - ScoringConstants.ARM_AXIS_HEIGHT_OFF_FLOOR) / ScoringConstants.ARM_LENGTH);

		directArmTo(angle, front);
	}



	/**
	 * + pow --> spit out
	 * - pow --> intake
	 */
	public void setRollers(double pow) {
		// may be the other way around depending on the placement of the motors and such
		// currently this assumes that a + signal to the top roller will cause it to intake

		/*
		 *    <---  __
		 *         /  \
		 *         \__/  --->
		 * 
		 *         O (ball) (NOT TO SCALE) -->
		 * 
		 *          __   --->
		 *         /  \
		 *    <--- \__/
		 */
		rollerMotor.set(ControlMode.PercentOutput, pow);
	}



	public void setAllMotorsZero() {
		rollerMotor.set(ControlMode.PercentOutput, 0);
		rotationMotor1.set(ControlMode.PercentOutput, 0);
	}

	







	public double getAngle() {
		int ticks = rotationMotor1.getSelectedSensorPosition();
		double rev = ticks / ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV;

		return 360 * rev;
	}





  	@Override
	public void diagnosticsInit() {
		// TODO Auto-generated method stub
		
		ScoringDiagnostics.init();
	}

	@Override
	public void diagnosticsCheck() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void periodic() {
		updateBaseDashboard();
		if (getTelemetryEnabled()) {
			SmartDashboard.putNumber(getName() + "/Arm Angle", getAngle());
			SmartDashboard.putNumber(getName() + "/Arm Ticks", rotationMotor1.getSelectedSensorPosition());
		}
		if (getDiagnosticsEnabled())
		{
			double angle = SmartDashboard.getNumber(getName() + "/Test Angle", 0);
			directArmTo(angle, true);
		}



		rotateScoringArm();
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub

		ScoringDiagnostics.periodic();
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();



		SmartDashboard.putNumber(getName() + "/Test Angle", 0);
	}





	private void rotateScoringArm() {
		// TODO: go to lowest selected arm level, we may want some ButtonMadness-esque implementation in the future, probably
		ScoringConstants.ScoringLevel level = ScoringConstants.ScoringLevel.HP_GROUND;

		if (oi.bLoadingStation()) { level = ScoringConstants.ScoringLevel.BALL_LOADING_STATION; }
		if (oi.bCargo()) { level = ScoringConstants.ScoringLevel.BALL_CARGO; }
		if (oi.bRocket1()) { level = ScoringConstants.ScoringLevel.BALL_ROCKET_1; }
		if (oi.hpCargo()) { level = ScoringConstants.ScoringLevel.HP_CARGO; }
		if (oi.hpRocket1()) { level = ScoringConstants.ScoringLevel.HP_ROCKET_1; }
		if (oi.bGround()) { level = ScoringConstants.ScoringLevel.BALL_GROUND; }
		if (oi.hpGround()) { level = ScoringConstants.ScoringLevel.HP_GROUND; }

		// get current applied to motors to get direction
		double current = DriveSubsystem.instance().getAverageCurrent_amps();
		boolean direction;

		// if robot not moving, keep arm where it is
		if (current == 0) {
			direction = lastForward;
		// if it is moving, set the direction
		} else {
			direction = (current > 0);
		}

		if (direction == lastForward) {
			forwardIterations++;
		// if the direction changed, reset number of iterations where it was constant
		} else {
			forwardIterations = 0;

			lastForward = direction;
		}


		// if enough time has past, change scoring arm direction (if necessary)
		if (forwardIterations >= ScoringConstants.ITERATIONS_BEFORE_SCORING_ROTATION) {
			forward = lastForward;
		}

		
		
		goToLevel(level, forward);
	}
}