/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.RobotMap;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.SubsystemUtilities.SubsystemTelemetryState;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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



	private final WPI_TalonSRX topRollerMotor = new WPI_TalonSRX(RobotMap.TOP_INTAKE_MOTOR_ID);
	private final WPI_TalonSRX bottomRollerMotor = new WPI_TalonSRX(RobotMap.BOTTOM_INTAKE_MOTOR_ID);

	// TODO: depending on the hardware, there may be (probably will be) more motors to rotate the scoring mechanism
	private final WPI_TalonSRX rotationMotor1 = new WPI_TalonSRX(RobotMap.ROTATION_MOTOR1_ID);
	private final WPI_TalonSRX rotationMotor2 = new WPI_TalonSRX(RobotMap.ROTATION_MOTOR2_ID);
	
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
		bottomRollerMotor.setInverted(true);
		// should be opposite of the top one at all times
		bottomRollerMotor.follow(topRollerMotor);

	}

}
