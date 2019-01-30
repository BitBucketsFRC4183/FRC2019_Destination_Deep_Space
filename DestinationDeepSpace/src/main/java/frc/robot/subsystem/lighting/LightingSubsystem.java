/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.lighting;

import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.lighting.LightingConstants.LightingObjects;

/**
 * Add your docs here.
 */
public class LightingSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	  
	// Singleton method; use LightingSubsystem.instance() to get the LightingSubsystem instance.
	public static LightingSubsystem instance() {
		if(inst == null)
			inst = new LightingSubsystem();
		return inst;
	}
	private static LightingSubsystem inst;
	private LightingControl lightingControl = LightingControl.instance();

	private LightingSubsystem()
	{
		setName("LightingSubsystem");
	}

	private String[] lastCommands = new String[LightingObjects.values().length];


	public boolean isReady()
	{
		return lightingControl.isReady();
	}
	/** set - pass through to underlying lighting control
	 *  Converts enumeration of available lighting objects to their respective ordinal values
	 *  Provides future opportunity for this subsystem to intervene and/or report state to Dashboard
	 *  for testing of the lighting subsystem itself.
	*/
	public void set(LightingObjects lightingObject, String function, String color, int nspace, int period_msec)
	{
		lastCommands[lightingObject.getValue()] = lightingControl.set(lightingObject.getValue(), function, color, nspace, period_msec);
	}	
	public void set(LightingObjects lightingObject, String function, String color, int nspace, int period_msec, int brightness)
	{
		lastCommands[lightingObject.getValue()] = lightingControl.set(lightingObject.getValue(), function, color, nspace, period_msec, brightness);
	}

	/** setAll functions - convenience in terms of subsystem layout
	 * 
	 * 		AllOff
	 * 		AllSleeping	- 3 second violet pulse
	 * 		AllSparkles - random twinkling at specified period (Default = 100 msec)
	 * 		AllCaution  - 1 Hz amber blink
	 * 		AllFail     - 1 Hz red blink
	 */
	public void setAllOff()
	{
		for (LightingObjects lightingObject: LightingObjects.values())
		{
			lightingControl.setOff(lightingObject.getValue());
		}
	}
	public void setAll(String function, String color, int nspace, int period_msec)
	{
		for (LightingObjects lightingObject: LightingObjects.values())
		{
			lightingControl.set(lightingObject.getValue(),
								function,
								color,
								nspace,
								period_msec);
		}
	}
	public void setAllSleeping()
	{
		setAll(LightingControl.FUNCTION_SNORE,
			   LightingControl.COLOR_VIOLET,
			   0,	// nspace - don't care
			   0);	// period_msec - don't care
	}
	public void setAllSparkles(int period_msec)
	{
		setAll(LightingControl.FUNCTION_SPARKLES,
		       LightingControl.COLOR_BLACK,			// don't care, sparkles are random
			   0,	            	// nspace - don't care
			   period_msec);		// period_msec - nice
	}
	public void setAllSparkles()	// Default period
	{
		setAll(LightingControl.FUNCTION_SPARKLES,
		       LightingControl.COLOR_BLACK,		// don't care, sparkles are random
			   0,     			// nspace - don't care
			   100) ; 			// period_msec - nice default
	}
	public void seAllWarning()
	{
		setAll(LightingControl.FUNCTION_BLINK,
		       LightingControl.COLOR_ORANGE,
			   0,     			// nspace - don't care
			   500) ; 			// period_msec - nice default
	}
	public void setAllFail()
	{
		setAll(LightingControl.FUNCTION_BLINK,
		       LightingControl.COLOR_RED,
			   0,     			// nspace - don't care
			   500) ; 			// period_msec - nice default
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
		updateBaseDashboard();
		if (getTelemetryEnabled())
		{
			
		}
		if (getDiagnosticsEnabled())
		{
			
		}
		
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();

		setAllSleeping();

	}

}
