/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.climber;

import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.RobotMap;
/**
 * Add your docs here.
 */
public class ClimberSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsystem
  	// here. Call these from Commands.

	// Singleton method; use ClimberSubsystem.instance() to get the ClimberSubsystem instance.
	Servo highClimbServo;
	WPI_TalonSRX highClimbMotor1;
	WPI_TalonSRX highClimbMotor2;

	public ClimberSubsystem() {
		highClimbServo = new Servo(RobotMap.HIGH_CLIMB_SERVO);
		highClimbMotor1 = new WPI_TalonSRX(RobotMap.HIGH_CLIMB_MOTOR_1);
		highClimbMotor2 = new WPI_TalonSRX(RobotMap.HIGH_CLIMB_MOTOR_2);
	}

	public static ClimberSubsystem instance() {
		if(inst == null)
			inst = new ClimberSubsystem();
		return inst;
	}
	private static ClimberSubsystem inst;
	
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void periodic() {
		updateBaseDashboard();
		
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDiagnosticsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void initialize() {
		initializeBaseDashboard();
	}

	@Override
	public void diagnosticsInit() {

	}

	@Override
	public void diagnosticsCheck() {

	}
}
