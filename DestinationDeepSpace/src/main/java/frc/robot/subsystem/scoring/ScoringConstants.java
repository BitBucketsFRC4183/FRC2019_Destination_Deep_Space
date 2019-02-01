package frc.robot.subsystem.scoring;

public class ScoringConstants {
	public enum ScoringLevel {
		GROUND               (0),       // hatch panel ground pickup
		
		HP                   (19.0),    // cargo scoring

		BALL_CARGO           (31.5),    // cargo scoring
		BALL_LOADING_STATION (37.0),    // loading station pickup
		BALL_ROCKET_1        (27.5);    // rocket 1 scoring



		private final double HEIGHT;
		private final double ANGLE;

		ScoringLevel(double height) {
			HEIGHT = height;
			// tip of arm is given by (height off floor) + (length) * sin(angle)
			ANGLE = Math.asin((height - ARM_AXIS_HEIGHT_OFF_FLOOR) / ARM_LENGTH);
		}

		public double getAngle() {
			return ANGLE;
		}
	}


	public static final double ARM_LENGTH = 0;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR = 0;



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 8192;
	public static final double ARM_MOTOR_RADIUS = 0;

	// if encoder reads a higher value, then the arm is in the back of the robot
	public static final int ARM_MOTOR_SWITCH_TICK_THRESHOLD = ARM_MOTOR_NATIVE_TICKS_PER_REV / 2;

	

	// TODO: actual value
	public static int ROTATION_MOTOR_ERROR_DEADBAND_TICKS = 5;
}