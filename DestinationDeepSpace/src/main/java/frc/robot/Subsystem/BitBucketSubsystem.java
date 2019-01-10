/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.Subsystem;
import frc.robot.Subsystem.SubsystemUtilities.DiagnosticsState;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public abstract class BitBucketSubsystem extends Subsystem {
	
	protected boolean runDiagnostics = false;
	public DiagnosticsState lastKnownState = DiagnosticsState.UNKNOWN;
	public int DIAG_LOOPS_RUN = 5;
	
	public BitBucketSubsystem() {
		
	}

	public abstract void diagnosticsInit();
	
	public abstract void diagnosticsExecute();
	
	public abstract void diagnosticsCheck();
	
	public abstract void setDiagnosticsFlag(boolean enable);
	
	public abstract boolean getDiagnosticsFlag();
	
	@Override
    protected abstract void initDefaultCommand();
    
    @Override
    public abstract void periodic();
}