/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.vision;

import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.lighting.LightingControl;
import frc.robot.subsystem.lighting.LightingSubsystem;
import frc.robot.subsystem.lighting.LightingConstants.LightingObjects;

/**
 * Add your docs here.
 */
public class VisionSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsBitBucketSubsystemystem
  	// here. Call these from Commands.

	// Singleton method; use VisionSubsystem.instance() to get the VisionSubsystem instance.
	public static VisionSubsystem instance() {
		if(inst == null)
			inst = new VisionSubsystem();
		return inst;		
	}
	private static VisionSubsystem inst;

	enum IlluminatorState
	{
		UNKNOWN,
		OFF,
		SNORE,
		ON
	}
	private IlluminatorState illuminatorState = IlluminatorState.UNKNOWN;
	
	private VisionSubsystem()
	{
		setName("VisionSubsystem");
	}

	private LightingSubsystem lightingSubsystem = LightingSubsystem.instance();

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void periodic() {
		clearDiagnosticsEnabled();		
		if (ds.isDisabled())
		{
			setIlluminatorSnore();
		}
		else if (ds.isTest())
		{
			setIlluminatorOff();			
		}
		else
		{
			setIlluminatorOn(VisionConstants.DEFAULT_ILLUMINATOR_BRIGHTNESS);
		}

		updateBaseDashboard();	
		if (getTelemetryEnabled())
		{
			
		}
	}

	@Override
	public void diagnosticsInitialize() {
		// TODO Auto-generated method stub
	}

	@Override
	public void diagnosticsPeriodic() {
		updateBaseDashboard();
		if (getDiagnosticsEnabled())
		{

			/// TODO: Add controls for illuminator on/off and camera controls here
		}


	}

	@Override
	public void diagnosticsCheck() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {

		initializeBaseDashboard();

		// Turn on illuminator in a snoring posture
		setIlluminatorSnore();
	}

	protected boolean isIlluminatorReady()
	{
		return lightingSubsystem.isReady();
	}

	protected void setIlluminatorOff()
	{
		if (illuminatorState != IlluminatorState.OFF)
		{
			lightingSubsystem.set(LightingObjects.VISION_SUBSYSTEM,
								LightingControl.FUNCTION_OFF,
								LightingControl.COLOR_BLACK,
								0,
								0);
			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.OFF:illuminatorState.UNKNOWN;
		}
	}

	protected void setIlluminatorOn(int brightness)
	{
		if (illuminatorState != IlluminatorState.ON)
		{
			lightingSubsystem.set(LightingObjects.VISION_SUBSYSTEM,
								LightingControl.FUNCTION_ON,
								LightingControl.COLOR_GREEN,
								0,
								0,
								brightness);

			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.ON:illuminatorState.UNKNOWN;
		}		
	}
	protected void setIlluminatorSnore()
	{
		if (illuminatorState != IlluminatorState.SNORE)
		{
			lightingSubsystem.set(LightingObjects.VISION_SUBSYSTEM,
								LightingControl.FUNCTION_SNORE,
								LightingControl.COLOR_VIOLET,
								0,
								0);			
			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.SNORE:illuminatorState.UNKNOWN;
		}				
	}

}
