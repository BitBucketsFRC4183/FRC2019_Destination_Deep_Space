package frc.robot.subsystem.scoring;

public class ScoringConstants {
	public enum ScoringLevel {
		HP_GROUND            (0),       // hatch panel ground pickup
		HP_CARGO             (19.0),    // cargo scoring
		HP_ROCKET_1          (19.0),    // rocket 1 scoring

		BALL_GROUND          (0.0),     // ball group pickup
		BALL_CARGO           (31.5),    // cargo scoring
		BALL_LOADING_STATION (37.0),    // loading station pickup
		BALL_ROCKET_1        (27.5);    // rocket 1 scoring



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



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 8192;
	public static final double ARM_MOTOR_RADIUS = 0;

	// if encoder reads a higher value, then the arm is in the back of the robot
	public static final int ARM_MOTOR_SWITCH_TICK_THRESHOLD = ARM_MOTOR_NATIVE_TICKS_PER_REV / 2;



	// drive has to be going in the same direction for at least 1 second before
	// scoring arm rotates with it
	public static final double SECONDS_BEFORE_SCORING_ROTATION = 1.0;
	// TODO: 20Hz driver station?
	public static final int ITERATIONS_BEFORE_SCORING_ROTATION = (int) (SECONDS_BEFORE_SCORING_ROTATION * 20);
}