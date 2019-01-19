/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.RobotMap;
import frc.robot.subsystem.BitBucketSubsystem;

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



	private final WPI_TalonSRX topRollerMotor = new WPI_TalonSRX(RobotMap.TOP_INTAKE_MOTOR_ID);
	private final WPI_TalonSRX bottomRollerMotor = new WPI_TalonSRX(RobotMap.BOTTOM_INTAKE_MOTOR_ID);

	// TODO: depending on the hardware, there may be (probably will be) more motors to rotate the scoring mechanism
	private final WPI_TalonSRX rotationMotor = new WPI_TalonSRX(RobotMap.ROTATION_MOTOR_ID);
	


	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	/*
	 * I drew this for a method I realized we didn't even need but decided to keep it, enjoy!
	 * 
	 * 
	 *        \ * /
	 *         \_/
 	 *         //
	 *        //
	 *  _____//_____
	 *  |          |
	 * ==O========O==
	 */



	/**
	 * Direct the robot arm to a certain angle.
	 * If front is true, the angle is from the front of the robot, if it is false, it
	 * is from the back of the robot
	 */
	public void directArmTo(double angle, boolean front) {
		// moved distance = r * angle
		double dist = 2 * Math.PI * ScoringConstants.ARM_MOTOR_RADIUS * angle / 360;

		int currentTicks = rotationMotor.getSelectedSensorPosition(0);
		int ticks = (int) (dist / ScoringConstants.ARM_MOTOR_NATIVE_INCHES_PER_TICK);

		// if the arm is in the back of the robot
		if (front == false) {
			// switch the ticks so that the arm will go to intended position on the back too
			ticks = 2 * ScoringConstants.ARM_MOTOR_SWITCH_TICK_THRESHOLD - ticks;
		}

		// TODO: set up PID constants
		rotationMotor.set(ControlMode.MotionMagic, ticks);
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
		bottomRollerMotor.set(ControlMode.PercentOutput, -pow);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDiagnosticsFlag(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDiagnosticsFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize() {

	}

}
