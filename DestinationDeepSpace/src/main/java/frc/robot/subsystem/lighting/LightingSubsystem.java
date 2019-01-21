/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.lighting;

import frc.robot.subsystem.BitBucketSubsystem;

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

	private LightingSubsystem()
	{
		setName("LightingSubsystem");
	}

	public enum LightingObjects
	{
		// Currently planning on lighting on these controls
		VISION_SUBSYSTEM(0),
		DRIVE_SUBSYSTEM(1),
		SCORING_SUBSYSTEM(2),
		CLIMB_SUBSYSTEM(3);
		// RESERVED 4 - 9
		
		private int value;
		
		LightingObjects(int value)
		{
			this.value = value;
		}
		
		public int getValue() { return value; }
	};	
	
	private LightingControl lightingControl;

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
		
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();

		lightingControl = new LightingControl();

		setAllSleeping();

	}

}
