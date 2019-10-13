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



    public enum AutoStartChoices {
        NONE   (new Waypoint[] {}),
        LEFT   (new Waypoint[] {AutonomousConstants.START_LEFT,   AutonomousConstants.MID_LEFT}),
        CENTER (new Waypoint[] {AutonomousConstants.START_CENTER, AutonomousConstants.MID_CENTER}),
        RIGHT  (new Waypoint[] {AutonomousConstants.START_RIGHT,  AutonomousConstants.MID_RIGHT});

        private Waypoint[] waypoints;
        AutoStartChoices(Waypoint[] wps) {
            waypoints = wps;
        }

        public Waypoint[] getWaypoints() {
            return waypoints;
        }
    }

    public enum AutoEndChoices {
        NONE  (null),
        LEFT  (AutonomousConstants.END_LEFT),
        RIGHT (AutonomousConstants.END_RIGHT);

        private Waypoint waypoint;
        AutoEndChoices(Waypoint wp) {
            waypoint = wp;
        }

        public Waypoint getWaypoint() {
            return waypoint;
        }
    }

    private static SendableChooser<AutoStartChoices> autoStartChooser;
    private static SendableChooser<AutoEndChoices> autoEndChooser;
    




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
        autoStartChooser = new SendableChooser<AutoStartChoices>();
        autoStartChooser.setDefaultOption("NONE", AutoStartChoices.NONE);
        autoStartChooser.addOption("LEFT",  AutoStartChoices.LEFT);
        autoStartChooser.addOption("CENTER", AutoStartChoices.CENTER);
        autoStartChooser.addOption("RIGHT", AutoStartChoices.RIGHT);
        SmartDashboard.putData("Auto Start", autoStartChooser);

        autoEndChooser = new SendableChooser<AutoEndChoices>();
        autoEndChooser.setDefaultOption("NONE", AutoEndChoices.NONE);
        autoEndChooser.addOption("LEFT",  AutoEndChoices.LEFT);
        autoEndChooser.addOption("RIGHT", AutoEndChoices.RIGHT);
        SmartDashboard.putData("Auto Start", autoEndChooser);



		initializeBaseDashboard();
    }
    


    public void disable() {

    }




    public boolean doAuto() {
        return !(
            autoStartChooser.getSelected() == AutoStartChoices.NONE ||
            autoEndChooser.getSelected()   == AutoEndChoices.NONE
        );
    }
    public Waypoint[] getWaypoints() {
        // in case gets called despite no auto
        if (!doAuto()) {
            return new Waypoint[] {};
        }

        Waypoint[] start = autoStartChooser.getSelected().getWaypoints();
        Waypoint end   = autoEndChooser.getSelected().getWaypoint();

        return new Waypoint[] {start[0], start[1], end};
    }
}