/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.MotorId;
import frc.robot.RobotMap;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.utils.TalonUtils;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Add your docs here.
 */
public class ScoringSubsystem extends BitBucketSubsystem {
	// Singleton method; use ScoringSubsystem.instance() to get the ScoringSubsystem instance.
	public static ScoringSubsystem instance() {
		if(inst == null)
			inst = new ScoringSubsystem();
		return inst;
	}
	private static ScoringSubsystem inst;


	private final WPI_TalonSRX topRollerMotor;
	private final WPI_TalonSRX bottomRollerMotor;

	private final WPI_TalonSRX rotationMotor1;
	private final WPI_TalonSRX rotationMotor2;



	private ScoringSubsystem()
	{
		setName("ScoringSubsystem");



		topRollerMotor = new WPI_TalonSRX(MotorId.TOP_INTAKE_MOTOR_ID);
		bottomRollerMotor = new WPI_TalonSRX(MotorId.BOTTOM_INTAKE_MOTOR_ID);

		bottomRollerMotor.setInverted(true);
		bottomRollerMotor.follow(topRollerMotor);



		rotationMotor1 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR1_ID);
		rotationMotor2 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR2_ID);

		// set it to 0 at starting position (front of robot)
		rotationMotor1.setSelectedSensorPosition(0);
		rotationMotor2.follow(rotationMotor1);



		TalonUtils.initializeMotorDefaults(topRollerMotor);
		TalonUtils.initializeMotorDefaults(bottomRollerMotor);

		TalonUtils.initializeMotorDefaults(rotationMotor1);
		TalonUtils.initializeMotorDefaults(rotationMotor2);



		// TODO: TEMPORARY VALUES + also not the best place to put them in the first place
		TalonUtils.initializeMotorFPID        (rotationMotor1, 0, 0, 0, 0, 0);
		TalonUtils.initializeQuadEncoderMotor (rotationMotor1, 1);

		TalonUtils.initializeMotorFPID        (rotationMotor2, 0, 0, 0, 0, 0);
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

		// TODO: set up PID constants
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
		topRollerMotor.set(ControlMode.PercentOutput, pow);
		// bottom will follow AND BE OPPOSITE
	}



	public void setAllMotorsZero() {
		topRollerMotor.set(ControlMode.PercentOutput, 0);
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
		if (getTelemetryEnabled()) {
			SmartDashboard.putNumber(getName() + "/Arm Angle", getAngle());
			SmartDashboard.putNumber(getName() + "/Arm Ticks", rotationMotor1.getSelectedSensorPosition());
		}

		
		double angle = SmartDashboard.getNumber(getName() + "/Test Angle", 0);
		directArmTo(angle, true);

		updateBaseDashboard();
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();



		SmartDashboard.putNumber(getName() + "/Test Angle", 0);
	}

}