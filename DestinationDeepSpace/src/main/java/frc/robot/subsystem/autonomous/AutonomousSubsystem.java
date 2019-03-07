/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous;

import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.utils.Controller;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;



public class AutonomousSubsystem extends BitBucketSubsystem {
    private final OI oi = OI.instance();

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
    


    private boolean inAutoAssistRegion = false;
    private double parallax = 0;
    private double offAxis = 0;

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
    


    private NetworkTableInstance networkTable = NetworkTableInstance.getDefault();
	private NetworkTable bvTable = networkTable.getTable("BucketVision");
	private NetworkTableEntry bvStateEntry = bvTable.getEntry("BucketVisionState");
    private NetworkTableEntry bvCameraNumber = bvTable.getEntry("CameraNum");
    
    public CameraFeedback getClosestObjectData() {
        // TODO: implement - get the CameraFeedback for the closest target

        int numTargets = (int) bvTable.getEntry("NumTargets").getValue().getDouble();
        if (numTargets == 0) {
            return null; // null if no target found (do we want this behavior?)
        }

        double[] distance = bvTable.getEntry("distance").getValue().getDoubleArray();
        double[] pos_x = bvTable.getEntry("pos_x").getValue().getDoubleArray();
        double[] parallax = bvTable.getEntry("parallax").getValue().getDoubleArray();

        int min_index = 0;
        double min_distance = distance[0];
        for (int i = 0; i < numTargets; i++) {
            if (distance[i] < min_distance) {
                min_index = i;
                min_distance = distance[i];
            }
        }

        double offAxis = 2 * pos_x[min_index] - 1; // normalize to [-1, 1] from [0, 1]

        return null;
    }



    @Override
	public void initialize() {
		initializeBaseDashboard();
    }
    


    public void disable() {

    }
}