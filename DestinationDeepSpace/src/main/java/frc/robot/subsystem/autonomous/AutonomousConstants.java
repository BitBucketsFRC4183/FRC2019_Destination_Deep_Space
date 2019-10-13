package frc.robot.subsystem.autonomous;

import frc.robot.subsystem.autonomous.motion.Waypoint;

public class AutonomousConstants {    
	
	public static final double CAMERA_FPS = 30; // TODO: actual

    public static final double OFF_AXIS_KP = 2;//3.5 (simulator);
	public static final double OFF_AXIS_KI = 0;
	public static final double OFF_AXIS_KD = 0;

	public static final double PARALLAX_KP = 0;//-150 (simulator);
	public static final double PARALLAX_KI = 0;
    public static final double PARALLAX_KD = 0;
    
    public static final double MIN_PARALLAX_DISTANCE = 0; // (simulator)
	public static final double OFF_AXIS_GAIN_BOOST = 1; // (simulator)

	public static final double GUIDANCE_STOP = 0.5;



	// frequency of motion controller loop
	public static final double LOOP_HERTZ = 100.0;
	public static final double LOOP_MS_PER = 1000.0 / LOOP_HERTZ;


	// waypoints to choose from
	// starting positions
	public static final Waypoint START_LEFT   = new Waypoint(FieldConstants.START_X, -FieldConstants.DIF_Y, 0);
	public static final Waypoint START_CENTER = new Waypoint(FieldConstants.START_X, 0,                     0);
	public static final Waypoint START_RIGHT  = new Waypoint(FieldConstants.START_X, FieldConstants.DIF_Y,  0);
	// middle positions (after getting to gruond level)
	public static final Waypoint MID_LEFT   = new Waypoint(FieldConstants.MID_X, -FieldConstants.DIF_Y, 0);
	public static final Waypoint MID_CENTER = new Waypoint(FieldConstants.MID_X, 0,                     0);
	public static final Waypoint MID_RIGHT  = new Waypoint(FieldConstants.MID_X, FieldConstants.DIF_Y,  0);
	// ending positions
	public static final Waypoint END_LEFT  = new Waypoint(FieldConstants.END_X, -FieldConstants.END_Y, 0);
	public static final Waypoint END_RIGHT = new Waypoint(FieldConstants.END_X, FieldConstants.END_Y,  0);


	public static final int MINIMUM_BUFFER_POINTS = 5;
	// there's a better way to keep track of this but it works for now
	public static final int TALON_MP_POINTS = 128;

	/*
	 * After the robot's auto phase, it will try to auto align so it can face the retroreflexive tape
	 * If the angle from the camera to the tape is within this threshold, we can safely
	 * assume drivers can go in for the hatch
	 */
	public static final double POST_AUTO_ALIGN_ANGLE_THRESHOLD = 5 * Math.PI / 180;
}