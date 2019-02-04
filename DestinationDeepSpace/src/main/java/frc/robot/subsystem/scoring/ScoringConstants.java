package frc.robot.subsystem.scoring;

public class ScoringConstants {
	public enum ScoringLevel {
		INVALID              (0),       // multiple levels are selected
		NONE                 (0),       // no levels are selected

		GROUND               (0),       // hatch panel ground pickup
		
		HP                   (19.0),    // cargo scoring

		BALL_CARGO           (31.5),    // cargo scoring
		BALL_LOADING_STATION (37.0),    // loading station pickup
		BALL_ROCKET_1        (27.5);    // rocket 1 scoring



		private final double HEIGHT_INCH;
		private final double ANGLE_RAD;

		ScoringLevel(double height_inch) {
			HEIGHT_INCH = height_inch;
			// tip of arm is given by (height off floor) + (length) * cos(angle)
			ANGLE_RAD = Math.acos((height_inch - ARM_AXIS_HEIGHT_OFF_FLOOR_INCH) / ARM_LENGTH_INCH);
		}

		public double getAngle_rad() {
			return ANGLE_RAD;
		}
	}


	public static final double ARM_LENGTH_INCH = 27.5;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR_INCH = 19;



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 8192;



	// TODO: actual value
	public static final int ROTATION_MOTOR_ERROR_DEADBAND_TICKS = 50;



	public static final double LEVEL_CHANGE_TIMEOUT_SEC = 1.5;
}