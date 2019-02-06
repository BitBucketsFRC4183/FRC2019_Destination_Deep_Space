/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.climber;

import frc.robot.subsystem.BitBucketSubsystem;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.MotorId;
import frc.robot.ServoId;
import frc.robot.operatorinterface.OI;
/**
 * Add your docs here.
 */
public class ClimberSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsystem
  	// here. Call these from Commands.

	// Singleton method; use ClimberSubsystem.instance() to get the ClimberSubsystem instance.
	Servo climbServo;
	WPI_TalonSRX  climbMotor1;
	WPI_TalonSRX climbMotor2;
	// TODO: Set proper values for angles and motors
	double highClimbAngle = 40;
	double highClimbSpeed = 0.25;

	double lowClimbAngle = 10;

	double start;

	public enum eState {
	IDLE,
	ARMED,
	HIGH_CLIMB,
	LOW_CLIMB;
	}

	eState state = eState.IDLE;

	private final OI oi = OI.instance();

	private ClimberSubsystem() {
		setName("ClimberSubsystem");
		climbServo  = new Servo(ServoId.CLIMB_SERVO_ID);
		climbMotor1 = new WPI_TalonSRX(MotorId.CLIMB_MOTOR_1_ID);
		climbMotor2 = new WPI_TalonSRX(MotorId.CLIMB_MOTOR_2_ID);

		climbMotor1.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,0);
		climbMotor1.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,LimitSwitchNormal.NormallyOpen,0);
		
		climbMotor1.overrideLimitSwitchesEnable(true);

		climbMotor1.setNeutralMode(NeutralMode.Brake);
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
		clearDiagnosticsEnabled();
		updateBaseDashboard();
		switch (state) {
			case IDLE:{
				climbMotor1.set(0);
				climbMotor2.set(0);
				if (oi.armClimber()){
					state = eState.ARMED;	
				}
			}
				break;
			case ARMED:{
				if (oi.highClimb()){
					state = eState.HIGH_CLIMB;
					start = Timer.getFPGATimestamp();
				}
				else if (oi.lowClimb()){
					state = eState.LOW_CLIMB;
				}
			}
				break;
			case HIGH_CLIMB: {
				highClimb();
			}	
				break;
			case LOW_CLIMB: {
				lowClimb();
				}	
					break;
			default:{

			}
				break;
		}

		if (getTelemetryEnabled())
		{
			updateDashboard();
		}
		
		if (climbMotor1.getSensorCollection().isFwdLimitSwitchClosed()) {
			climbMotor1.set(0);
			climbMotor2.set(0);
		}
		
	}

	@Override
	public void diagnosticsPeriodic() {
		updateBaseDashboard();
		if (getDiagnosticsEnabled())
		{
			double angle = SmartDashboard.getNumber(getName()+"/ServoTestAngle(deg)", 0.0);
			climbServo.setAngle(angle);
		}
		updateDashboard();
	}

	public void updateDashboard()
	{
		SmartDashboard.putNumber(getName()+"/CurrentServoAngle(deg)",climbServo.getAngle());
		SmartDashboard.putString(getName()+"/CurrentState()",state.name());
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
	public void diagnosticsInitialize() {

	}

	@Override
	public void diagnosticsCheck() {

	}

	private int highClimb() {
		int err = 0;
		climbServo.setAngle(highClimbAngle);
		if (Timer.getFPGATimestamp() - start > 1.0) {
			climbMotor1.set(highClimbSpeed);
			climbMotor2.set(highClimbSpeed);
		}
		return (err);
	}

	private int lowClimb() {
		int err = 0;
		climbServo.setAngle(lowClimbAngle);
		return (err);
	}

	public void startIdle() {
		state = eState.IDLE;
		climbServo.setAngle(0);
	}
}
