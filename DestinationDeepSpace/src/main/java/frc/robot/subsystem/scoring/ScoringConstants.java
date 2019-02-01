package frc.robot.subsystem.scoring;

public class ScoringConstants {
	public enum ScoringLevel {
		GROUND               (0),       // hatch panel ground pickup
		
		HP                   (19.0),    // cargo scoring

		BALL_CARGO           (31.5),    // cargo scoring
		BALL_LOADING_STATION (37.0),    // loading station pickup
		BALL_ROCKET_1        (27.5);    // rocket 1 scoring



		private final double HEIGHT_INCH;
		private final double ANGLE_DEG;

		ScoringLevel(double height_inch) {
			HEIGHT_INCH = height_inch;
			// tip of arm is given by (height off floor) + (length) * sin(angle)
			ANGLE_DEG = Math.toDegrees(Math.asin((height_inch - ARM_AXIS_HEIGHT_OFF_FLOOR_INCH) / ARM_LENGTH_INCH));
		}

		public double getAngle_deg() {
			return ANGLE_DEG;
		}
	}


	public static final double ARM_LENGTH_INCH = 0;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR_INCH = 0;



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 8192;

	// if encoder reads a higher value, then the arm is in the back of the robot
	public static final int ARM_MOTOR_SWITCH_THRESHOLD_TICKS = ARM_MOTOR_NATIVE_TICKS_PER_REV / 2;



	// TODO: actual value
	public static int ROTATION_MOTOR_ERROR_DEADBAND_TICKS = 5;
}