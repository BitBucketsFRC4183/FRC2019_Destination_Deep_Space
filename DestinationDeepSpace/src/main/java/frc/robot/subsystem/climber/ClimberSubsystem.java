/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.climber;

import frc.robot.subsystem.BitBucketSubsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.MotorId;
import frc.robot.ServoId;
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
	final int IDLE = 0;
	final int ARMED = 1;
	final int CLIMB = 2;
	int state = IDLE;

	private ClimberSubsystem() {
		setName("ClimberSubsystem");
		highClimbServo = new Servo(ServoId.HIGH_CLIMB_SERVO_ID);
		highClimbMotor1 = new WPI_TalonSRX(MotorId.HIGH_CLIMB_MOTOR_1_ID);
		highClimbMotor2 = new WPI_TalonSRX(MotorId.HIGH_CLIMB_MOTOR_2_ID);
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
		switch (state) {
			case IDLE:{
				if (armClimber()){
					state = ARMED;
				}
			}
				break;
			case ARMED:{
				if (climb()){
					state = CLIMB;
				}
			}
				break;
			case CLIMB: {
				highClimb();
			}	
				break;
			default:{

			}
				break;
		}

		updateBaseDashboard();

		if (getTelemetryEnabled())
		{
		}
		if (getDiagnosticsEnabled())
		{
			double angle = SmartDashboard.getNumber(getName()+"/ServoTestAngle(deg)", 0.0);
			highClimbServo.setAngle(angle);
		}
		SmartDashboard.putNumber(getName()+"/CurrentServoAngle(deg)",highClimbServo.getAngle());
		
	}

	@Override
	public void diagnosticsExecute() {
		// TODO: Auto-generated method stub
		
	}

	public void initialize() {
		initializeBaseDashboard();

		initializeDashboard();
	}

	void initializeDashboard()
	{
		SmartDashboard.putNumber(getName()+"/ServoTestAngle(deg)", 0.0);
	}

	@Override
	public void diagnosticsInit() {

	}

	@Override
	public void diagnosticsCheck() {

	}
}
