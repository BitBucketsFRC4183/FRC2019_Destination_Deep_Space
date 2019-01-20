/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.lighting;

import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.lighting.LightingControl.LightingObjects;

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
	private LightingControl lightingControl;

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

		lightingControl.setAllSleeping();

	}

}
