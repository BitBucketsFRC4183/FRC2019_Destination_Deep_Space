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
	// waypoints to cross in auto
	public static final Waypoint[] LEFT_WAYPOINTS  = {};
	public static final Waypoint[] RIGHT_WAYPOINTS = {};
	public static final int MINIMUM_BUFFER_POINTS = 5;
	// there's a better way to keep track of this but it works for now
	public static final int TALON_MP_POINTS = 128;
}