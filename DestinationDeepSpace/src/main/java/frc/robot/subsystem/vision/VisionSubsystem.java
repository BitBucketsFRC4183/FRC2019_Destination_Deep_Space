/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.vision;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DigitalOutput;
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

	private boolean back;

	private DigitalOutput frontOutput;
	private DigitalOutput backOutput;
	
	private VisionSubsystem()
	{
		setName("VisionSubsystem");
	}

	private LightingSubsystem lightingSubsystem = LightingSubsystem.instance();

	private NetworkTableInstance networkTable = NetworkTableInstance.getDefault();
	private NetworkTable bvTable = networkTable.getTable("BucketVision");
	private NetworkTableEntry bvStateEntry = bvTable.getEntry("BucketVisionState");
	private NetworkTableEntry bvCameraNumber = bvTable.getEntry("CameraNum");
	private NetworkTableEntry bvExposure = bvTable.getEntry("Exposure");

	public CameraFeedback getClosestObjectData() {
		if (bvTable.getEntry("NumTargets").getValue().isDouble())
		{
			int numTargets = (int) bvTable.getEntry("NumTargets").getValue().getDouble();

			double[] distance = bvTable.getEntry("distance").getValue().getDoubleArray();

			double[] pos_x = bvTable.getEntry("pos_x").getValue().getDoubleArray();
			double[] pos_y = bvTable.getEntry("pos_y").getValue().getDoubleArray();

			double[] parallax = bvTable.getEntry("parallax").getValue().getDoubleArray();

			numTargets = Math.min(distance.length,Math.min(pos_x.length,Math.min(pos_y.length,parallax.length))); /// TODO: Temporary

			SmartDashboard.putNumber(getName() + "/Num Targets",numTargets);        
			if (numTargets == 0) {
				return null; // null if no target found (do we want this behavior?)
			}

			int min_index = -1;
			double min_offAxis = 2 * pos_x[0] - 1; // normalize to [-1, 1] from [0, 1]
			for (int i = 0; i < numTargets; i++) {
				double offAxis = 2 * pos_x[i] - 1; // normalize to [-1, 1] from [0, 1]

				if (Math.abs(offAxis) <= Math.abs(min_offAxis)) {
					if ((pos_y[i] > 0.0) && (pos_y[i] < 1.0))
					{
						min_index = i;
						min_offAxis = offAxis;
					}
				}
			}

			SmartDashboard.putNumber(getName() + "/Min Index",min_index);

			// If target is not acceptable
			if (min_index == -1)
			{
				return null;
			}

			double offAxis = 2 * pos_x[min_index] - 1; // normalize to [-1, 1] from [0, 1]



			boolean isInAutoAssistRegion = true; // TODO: for now

			return new CameraFeedback(
				isInAutoAssistRegion,
				parallax[min_index],
				offAxis,
				distance[min_index]
			);
		}
		else {

			return new CameraFeedback(
				false,
				0,
				0,
				0
			);
		}
	}

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
		bvStateEntry.setString("UNKNOWN");
		setIlluminatorSnore();

		frontOutput = new DigitalOutput(VisionConstants.FRONT_CAMERA_ID);
		backOutput  = new DigitalOutput(VisionConstants.BACK_CAMERA_ID );
		frontOutput.set(false);
		backOutput .set(false);
	}

	public void enableDriverExposure()
	{
		bvExposure.setNumber(VisionConstants.DRIVER_EXPOSURE);
		frontOutput.set(false);
		backOutput .set(false);
	}
	public void enableAutonomousExposure()
	{
		bvExposure.setNumber(VisionConstants.AUTONOMOUS_EXPOSURE);
		frontOutput.set(!back);
		backOutput .set(back);
	}
	public void enableFront()
	{
		bvCameraNumber.setNumber(VisionConstants.FRONT_CAMERA_ID);
		back = false;
		setIlluminatorOn(true,VisionConstants.DEFAULT_ILLUMINATOR_BRIGHTNESS);

	}
	public void enableBack()
	{
		bvCameraNumber.setNumber(VisionConstants.BACK_CAMERA_ID);
		back = true;
		setIlluminatorOn(false,VisionConstants.DEFAULT_ILLUMINATOR_BRIGHTNESS);
	}

	protected boolean isIlluminatorReady()
	{
		return lightingSubsystem.isReady();
	}

	protected void setIlluminatorOff()
	{
		if (illuminatorState != IlluminatorState.OFF)
		{
			lightingSubsystem.set(LightingObjects.FRONT_CAMERA,
								LightingControl.FUNCTION_OFF,
								LightingControl.COLOR_BLACK,
								0,
								0);
			lightingSubsystem.set(LightingObjects.BACK_CAMERA,
								LightingControl.FUNCTION_OFF,
								LightingControl.COLOR_BLACK,
								0,
								0);
			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.OFF:IlluminatorState.UNKNOWN;
		}
	}

	public void setIlluminatorOn(boolean front, int brightness)
	{
		if (illuminatorState != IlluminatorState.ON)
		{
			lightingSubsystem.set(front?LightingObjects.FRONT_CAMERA:LightingObjects.BACK_CAMERA,
								LightingControl.FUNCTION_ON,
								LightingControl.COLOR_GREEN,
								0,
								0,
								brightness);
			lightingSubsystem.set(front?LightingObjects.BACK_CAMERA:LightingObjects.FRONT_CAMERA,
								LightingControl.FUNCTION_OFF,
								LightingControl.COLOR_BLACK,
								0,
								0,
								0);

			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.ON:IlluminatorState.UNKNOWN;
		}		
	}
	protected void setIlluminatorSnore()
	{
		if (illuminatorState != IlluminatorState.SNORE)
		{
			lightingSubsystem.set(LightingObjects.FRONT_CAMERA,
								LightingControl.FUNCTION_SNORE,
								LightingControl.COLOR_VIOLET,
								0,
								0);			
			lightingSubsystem.set(LightingObjects.BACK_CAMERA,
								LightingControl.FUNCTION_SNORE,
								LightingControl.COLOR_VIOLET,
								0,
								0);			

			illuminatorState = lightingSubsystem.isReady()?IlluminatorState.SNORE:IlluminatorState.UNKNOWN;
		}				
	}

}
