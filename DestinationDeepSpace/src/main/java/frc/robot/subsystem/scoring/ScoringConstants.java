package frc.robot.subsystem.scoring;

public class ScoringConstants {
	public enum ScoringLevel {
		HATCH_PANEL_LEVEL_C  (19.0),    // cargo
		HATCH_PANEL_LEVEL_R1 (19.0),    // rocket 1
		HATCH_PANEL_LEVEL_R2 (47.0),    // rocket 2

		BALL_LEVEL_GROUND    (0.0),     // group pickup
		BALL_LEVEL_C         (40.0),    // cargo
		BALL_LEVEL_LS        (37.0),    // loading station   TODO: do we want this? we can pick up balls from the ground
		BALL_LEVEL_R1        (27.5),    // rocket 1
		BALL_LEVEL_R2        (27.5);    // rocket 2          TODO: get the actual value



		private final double HEIGHT;
		ScoringLevel(double height) {
			HEIGHT = height;
		}

		public double getHeight() {
			return HEIGHT;
		}
	}


	public static final double ARM_LENGTH = 0;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR = 0;



	// TODO: this is VERY likely wrong
	public static final int ARM_MOTOR_NATIVE_INCHES_PER_TICK = 8192;
	public static final double ARM_MOTOR_RADIUS = 0;

	// if encoder reads a higher value, then the arm is in the back of the robot
	public static final int ARM_MOTOR_SWITCH_TICK_THRESHOLD = (int) ((Math.PI / 2 * ARM_MOTOR_RADIUS) / ARM_MOTOR_NATIVE_INCHES_PER_TICK);
}