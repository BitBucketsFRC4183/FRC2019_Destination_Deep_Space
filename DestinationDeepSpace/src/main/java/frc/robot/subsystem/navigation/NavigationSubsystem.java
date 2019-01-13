/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.navigation;

import frc.robot.subsystem.BitBucketSubsystem;

/**
 * Add your docs here.
 */
public class NavigationSubsystem extends BitBucketSubsystem {
  	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	  
	// Singleton method; use NavigationSubsystem.instance() to get the NavigationSubsystem instance.
	public static NavigationSubsystem instance() {
		if(inst == null)
			inst = new NavigationSubsystem();
		return inst;		
	}
	private static NavigationSubsystem inst;	

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

}
