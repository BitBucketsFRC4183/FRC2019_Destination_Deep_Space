package frc.robot.subsystem.scoring;

public class ScoringConstants {

	public final static double MAX_ARM_MOTOR_CURRENT_AMPS = 60.0;

	public final static int ARM_BIAS_TICKS = 765; //FLAGSTAFF: 776 + 40 + 2974 - 78; //BAG BOT IS: 3737 - 57; // subtracted from absolute encoder value

	public final static int ARM_MAX_SPEED_TICKS_PER_100MS = 300;
	public final static int ARM_CRUISE_SPEED_TICKS_PER_100MS = (int)(0.50 * ARM_MAX_SPEED_TICKS_PER_100MS * 1.2);
	public final static int ARM_ACCELERATION_TICKS_PER_100MS_PER_SEC = (int) (ARM_CRUISE_SPEED_TICKS_PER_100MS * 1.5);

	public final static boolean ARM_MOTOR_INVERSION = false;
	public final static boolean ARM_MOTOR_SENSOR_PHASE = false;
	public final static double ARM_MOTION_MAGIC_KF = 3.41;
	public final static double ARM_MOTION_MAGIC_KP = 0.146143*2*2*2  *2*1.5;
	public final static double ARM_MOTION_MAGIC_KI = 0.001;
	public final static double ARM_MOTION_MAGIC_KD = 8.0*10.0*0.146143*2*2*2 *2*2*1.5;
	public final static int    ARM_MOTION_MAGIC_IZONE = 120;

	public final static double FRONT_LIMIT_ANGLE = -108.5;
	public final static double BACK_LIMIT_ANGLE  = 111;

	public final static boolean ROLLER_MOTOR_INVERSION = false;

	public final static double ANGLE_TOLERANCE_DEG = 4.0; // If we made it to within this angle then call it good

	public enum ScoringLevel {
		INVALID              (0),       // multiple levels are selected
		MANUAL               (0),       // no description necessary, but it's here anyway. 
		NONE                 (0),       // no levels are selected
        TOP_DEAD_CENTER      (0), //deg
		GROUND               (113), //deg vs inches -->(5.0),       // hatch panel ground pickup
		
		HP                   (87.5), //(87.5)(83.0), //deg vs inches -->(19.0),    // cargo scoring
		HP_AUTO              (93.0), // different for auto assist so that camera can see it

		BALL_CARGO           (63.0), // deg vs inches -->(31.5),    // cargo scoring
		BALL_LOADING_STATION (49.0), // deg vs inches -->(37.0),    // loading station pickup
		BALL_ROCKET_1        (72.0); // deg vs inches -->(27.5);    // rocket 1 scoring



		private final double HEIGHT_INCH;
		private final double ANGLE_RAD;

		ScoringLevel(double angle_deg) {
			HEIGHT_INCH = 0; //height_inch;
			// tip of arm is given by (height off floor) + (length) * cos(angle)
			//ANGLE_RAD = Math.acos((height_inch - ARM_AXIS_HEIGHT_OFF_FLOOR_INCH) / ARM_LENGTH_INCH);
			ANGLE_RAD = Math.toRadians(angle_deg);
		}

		public double getAngle_rad() {
			return ANGLE_RAD;
		}
	}


	public static final double ARM_LENGTH_INCH = 27.5;
	public static final double ARM_AXIS_HEIGHT_OFF_FLOOR_INCH = 19;



	public static final int ARM_MOTOR_NATIVE_TICKS_PER_REV = 4096;
	public static final int BEAK_MOTOR_NATIVE_TICKS_PER_REV = 4096;



	// TODO: actual value
	public static final int ROTATION_MOTOR_ERROR_DEADBAND_TICKS = 50;

	// Set to a number between 0 and 1, controls the deadband for the manual arm control joystick.
	public static final double ARM_MANUAL_DEADBAND = 0.2;

	// Set to a positive number, the maximum speed (degrees per second) in manual control is set to this number.
	public static final double ARM_MANUAL_SPEED_MAX = 10;



	public static final double LEVEL_CHANGE_TIMEOUT_SEC = 30.0;


	public enum BeakPosition {
		HATCH_GRAPPLE_BEAK     (3*BEAK_MOTOR_NATIVE_TICKS_PER_REV),   // Grapple a hatch panel.
		HATCH_RELEASE_BEAK     (0);   // Release a hatch panel by releasing the beak.
		
		private final double BEAK_TICKS;

		BeakPosition(double targetBeakTicks) {
			
			BEAK_TICKS = targetBeakTicks;
			
		}
		public double getBeak_ticks() {
			return BEAK_TICKS;
		}
	}
										  
	public final static int     BEAK_MAX_SPEED_TICKS_PER_100MS = 9500;
	public final static int     BEAK_CRUISE_SPEED_TICKS_PER_100MS = (int)(0.75 * BEAK_MAX_SPEED_TICKS_PER_100MS);
	public final static int     BEAK_ACCELERATION_TICKS_PER_100MS_PER_SEC = (int) (BEAK_CRUISE_SPEED_TICKS_PER_100MS * 1.5);

	public final static boolean BEAK_MOTOR_INVERSION = false;
	public final static boolean BEAK_MOTOR_SENSOR_PHASE = false;
	public final static double  BEAK_MOTION_MAGIC_KF = 1023.0/9500.0;
	public final static double  BEAK_MOTION_MAGIC_KP = (0.1 * 1023 / 1800)*2*2*2*2;
	public final static double  BEAK_MOTION_MAGIC_KI = .001;
	public final static double  BEAK_MOTION_MAGIC_KD = 10*(0.1 * 1023 / 1800)*2*2*2*2;
	public final static int     BEAK_MOTION_MAGIC_IZONE = 200;
}