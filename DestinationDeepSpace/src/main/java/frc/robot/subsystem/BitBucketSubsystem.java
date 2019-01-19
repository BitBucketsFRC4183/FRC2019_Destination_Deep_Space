/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem;

import frc.robot.subsystem.SubsystemUtilities.DiagnosticsEnableState;
import frc.robot.subsystem.SubsystemUtilities.DiagnosticsState;
import frc.robot.subsystem.SubsystemUtilities.TelemetryEnableState;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public abstract class BitBucketSubsystem extends Subsystem {
	
	protected boolean initializedBase = false;
	protected SendableChooser<TelemetryEnableState> telemetryEnableState;
	protected SendableChooser<DiagnosticsEnableState> diagnosticsEnableState;

	public DiagnosticsState lastKnownState = DiagnosticsState.UNKNOWN;
	public int DIAG_LOOPS_RUN = 5;

	protected int periodicCounter = 0;
	
	public BitBucketSubsystem() {
		
	}

	protected void initializeBaseDashboard()
	{
		telemetryEnableState = new SendableChooser<TelemetryEnableState>();
		telemetryEnableState.setDefaultOption("Off", TelemetryEnableState.OFF);
		telemetryEnableState.addOption( "On",  TelemetryEnableState.ON);
		
		SmartDashboard.putData(getName() + "/Telemetry", telemetryEnableState);

		diagnosticsEnableState = new SendableChooser<DiagnosticsEnableState>();
		diagnosticsEnableState.setDefaultOption("Off", DiagnosticsEnableState.OFF);
		diagnosticsEnableState.addOption( "On",  DiagnosticsEnableState.ON);
		
		SmartDashboard.putData(getName() + "/Diagnostics", diagnosticsEnableState);

		initializedBase = true;
		SmartDashboard.putBoolean(getName() + "/InitializedBase", initializedBase);
	}

	/** updateBaseDashboard - call from derived class periodic function */
	protected void updateBaseDashboard()
	{
		SmartDashboard.putNumber(getName() + "/PeriodicCounter", periodicCounter++);
	}
	
	public boolean getTelemetryEnabled()
	{
		return (telemetryEnableState.getSelected() == TelemetryEnableState.ON);
	}
	
	public boolean getDiagnosticsEnabled()
	{
		return (diagnosticsEnableState.getSelected() == DiagnosticsEnableState.ON);
	}

	public abstract void initialize();		// Force all derived classes to have these interfaces

	public abstract void diagnosticsInit();
	
	public abstract void diagnosticsExecute();
	
	public abstract void diagnosticsCheck();
	
	@Override
    protected abstract void initDefaultCommand();
    
    @Override
    public abstract void periodic();
}