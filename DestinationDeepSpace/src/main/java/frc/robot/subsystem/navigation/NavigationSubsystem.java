/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.navigation;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

	private NavigationSubsystem()
	{
		setName("NavigationSubsystem");
	}

	private AHRS ahrs;

	// TODO: provide an accessor that fetches the most current state (6-DOF)
	// and returns it in a structure for reference; this allows consumer
	// to fetch the best available state, at the same time the periodic
	// loop can be keeping a limited history to allow image sensor data
	// to be correlated to past position and angle data; this history
	// would be interpolated if there is sufficient time synchronization
	// between this Robot code and the image data.

	@Override
	public void initialize() {
		initializeBaseDashboard();
		ahrs = BitBucketsAHRS.instance();
	}

  	@Override
	public void diagnosticsInitialize() {
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

	protected void updateDashboard() {
		SmartDashboard.putNumber( getName() + "/Yaw Angle (deg)", getYaw_deg());
		SmartDashboard.putNumber( getName() + "/Yaw Rate (dps)", getYawRate_degPerSec());
	}

	@Override
	public void periodic() {
		clearDiagnosticsEnabled();		
		updateBaseDashboard();
		if (getTelemetryEnabled())
		{
			
		}
		if (getDiagnosticsEnabled())
		{
			
		}		
		updateDashboard();
		
	}

	@Override
	public void diagnosticsPeriodic() {
		// TODO Auto-generated method stub
		
	}

	public double getYaw_deg() {
		return ahrs.getYaw();
	}
	public double getYawRate_degPerSec() {
		return ahrs.getRate();
	}


}
