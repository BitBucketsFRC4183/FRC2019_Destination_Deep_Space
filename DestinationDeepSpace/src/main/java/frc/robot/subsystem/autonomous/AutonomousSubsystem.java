/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous;

import frc.robot.subsystem.BitBucketSubsystem;

public class AutonomousSubsystem extends BitBucketSubsystem {

    // Singleton method; use AutonomousSubsystem.instance() to get the AutonomousSubsystem instance.
	public static AutonomousSubsystem instance() {
		if (inst == null) {
            inst = new AutonomousSubsystem();
        }

		return inst;
	}
    private static AutonomousSubsystem inst;
    




    @Override
	protected void initDefaultCommand() {
    }
    
    @Override
	public void periodic() {
    }



    @Override
	public void diagnosticsInitialize() {
	}

	@Override
	public void diagnosticsPeriodic() {
    }

    @Override
	public void diagnosticsCheck() {		
    }
    
    @Override
	public void initialize() {
		initializeBaseDashboard();
    }
    


    public void disable() {

    }
}