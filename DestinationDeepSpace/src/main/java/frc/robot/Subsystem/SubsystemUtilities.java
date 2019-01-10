/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem;

/**
 * Add your docs here.
 */
public class SubsystemUtilities {
    public enum SubsystemTelemetryState 
	{
		OFF,
		ON
	}
	
	public enum DiagnosticsInformation 
	{
		SUBSYSTEM_BASIC,
		SUBSYSTEM_EXTENDED
	}
	
	public enum BITMode
	{
		INIT, 
		EXTENDED
	}

	public enum DiagnosticsState 
	{ 
		UNKNOWN,
		PASS,
		FAIL
	}

}
