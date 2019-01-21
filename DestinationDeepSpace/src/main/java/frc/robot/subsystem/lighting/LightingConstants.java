/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.lighting;

/**
 * Add your docs here.
 */
public class LightingConstants {
    
    public enum LightingObjects
	{
		// Currently planning on lighting on these controls
		VISION_SUBSYSTEM(0),
		DRIVE_SUBSYSTEM(1),
		SCORING_SUBSYSTEM(2),
		CLIMB_SUBSYSTEM(3);
		// RESERVED 4 - 9
		
		private int value;
		
		LightingObjects(int value)
		{
			this.value = value;
		}
		
		public int getValue() { return value; }
	};	
}
