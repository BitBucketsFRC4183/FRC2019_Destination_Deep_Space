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
	
	private VisionSubsystem()
	{
		setName("VisionSubsystem");
	}

	private LightingSubsystem lightingSubsystem = LightingSubsystem.instance();

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

		// Turn on illuminator with a default brightness
		setIlluminatorOn(VisionConstants.DEFAULT_ILLUMINATOR_BRIGHTNESS);
	}

	protected void setIlluminatorOff()
	{
		lightingSubsystem.set(LightingObjects.VISION_SUBSYSTEM,
							  LightingControl.FUNCTION_OFF,
							  LightingControl.COLOR_BLACK,
							  0,
							  0);		
	}

	protected void setIlluminatorOn(int brightness)
	{
		lightingSubsystem.set(LightingObjects.VISION_SUBSYSTEM,
							  LightingControl.FUNCTION_ON,
							  LightingControl.COLOR_GREEN,
							  0,
							  brightness);		
	}

}
