/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem;

import frc.robot.subsystem.SubsystemUtilities.DiagnosticsState;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public abstract class BitBucketSubsystem extends Subsystem {
	
	protected static DriverStation ds = DriverStation.getInstance(); // Convenience

	protected boolean initializedBase = false;

	// We require that extended telementry and diagnostics enabling
	// reuturn to "OFF" at each reset. Because SendableChoosers remember
	// their state in the NetworkTable (somewhere) we need to use
	// boolean types for simple, resettable switches.
	protected boolean telemetryEnabled = false;		// Really an "extended" telemetry set (more bandwidth)
	protected boolean diagnosticsEnabled = false;

	public DiagnosticsState lastKnownState = DiagnosticsState.UNKNOWN;
	public int DIAG_LOOPS_RUN = 5;

	protected int periodicCounter = 0;
	
	public BitBucketSubsystem() {
		
	}

	protected void initializeBaseDashboard()
	{
		SmartDashboard.putBoolean(getName()+"/TelemetryEnabled", telemetryEnabled);
		SmartDashboard.putBoolean(getName()+"/DiagnosticsEnabled", diagnosticsEnabled);

		initializedBase = true;
		SmartDashboard.putBoolean(getName() + "/InitializedBase", initializedBase);
	}

	/** updateBaseDashboard - call from derived class periodic function */
	protected void updateBaseDashboard()
	{
		SmartDashboard.putNumber(getName() + "/PeriodicCounter", periodicCounter++);
		SmartDashboard.putString(getName() + "/CurrentCommand",getCurrentCommandName());
	}

	/**
	 * getTelementryEnabled - returns the current dashboard state
	 * NOTE: "Extended" Telemetry can be enabled any time at the expense of
	 * network bandwidth
	 */
	public boolean getTelemetryEnabled()
	{
		telemetryEnabled = SmartDashboard.getBoolean(getName() + "/TelemetryEnabled", false);
		return telemetryEnabled;
	}
	/**
	 * getDiagnosticsEnabled - returns the current dashboard state
	 * NOTE: Diagnostics can only be enabled when the DriverStation is in test mode
	 */
	public boolean getDiagnosticsEnabled()
	{
		diagnosticsEnabled = SmartDashboard.getBoolean(getName() + "/DiagnosticsEnabled", false);
		if (! ds.isTest())
		{
			diagnosticsEnabled = false;
			SmartDashboard.putBoolean(getName() + "/DiagnosticsEnabled", diagnosticsEnabled);
		}
		return diagnosticsEnabled;
	}
	public void clearDiagnosticsEnabled()
	{
		diagnosticsEnabled = false;
		SmartDashboard.putBoolean(getName() + "/DiagnosticsEnabled", diagnosticsEnabled);
	}

	public abstract void initialize();		// Force all derived classes to have these interfaces

	public abstract void diagnosticsInitialize();
	
	public abstract void diagnosticsPeriodic();
	
	public abstract void diagnosticsCheck();
	
	@Override
    protected abstract void initDefaultCommand();
    
    @Override
    public abstract void periodic();
}