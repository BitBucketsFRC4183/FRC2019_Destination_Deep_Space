/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.autonomous.motion.Waypoint;

public class AutonomousSubsystem extends BitBucketSubsystem {

    // Singleton method; use AutonomousSubsystem.instance() to get the AutonomousSubsystem instance.
	public static AutonomousSubsystem instance() {
		if (inst == null) {
            inst = new AutonomousSubsystem();
        }

		return inst;
	}
    private static AutonomousSubsystem inst;



    public enum AutoChoices {
        NONE       (new Waypoint[] {}),
        RIGHT_HATCH(AutonomousConstants.LEFT_WAYPOINTS),
        LEFT_HATCH (AutonomousConstants.RIGHT_WAYPOINTS);

        private Waypoint[] waypoints;
        AutoChoices(Waypoint[] wps) {
            waypoints = wps;
        }

        public Waypoint[] getWaypoints() {
            return waypoints;
        }
    }

    private static SendableChooser<AutoChoices> autoChooser;
    




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
        autoChooser = new SendableChooser<AutoChoices>();
        autoChooser.setDefaultOption("NONE", AutoChoices.NONE);
        autoChooser.addOption("LEFT",  AutoChoices.LEFT_HATCH);
        autoChooser.addOption("RIGHT", AutoChoices.RIGHT_HATCH);
        SmartDashboard.putData("Auto Choices", autoChooser);

		initializeBaseDashboard();
    }
    


    public void disable() {

    }




    public Waypoint[] getWaypoints() {
        return autoChooser.getSelected().getWaypoints();
    }
}