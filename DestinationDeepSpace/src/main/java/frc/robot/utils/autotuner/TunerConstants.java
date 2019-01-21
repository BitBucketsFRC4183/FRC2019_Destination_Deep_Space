package frc.robot.utils.autotuner;

public class TunerConstants {
    	/**
	 * Which PID slot to pull gains from. Starting 2018, you can choose from
	 * 0,1,2 or 3. Only the first two (0,1) are visible in web-based
	 * configuration.
	 */
	public static final int kSlotIdx = 0;

	/**
	 * Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops. For
	 * now we just want the primary one.
	 */
	public static final int kPIDLoopIdx = 0;

	/**
	 * set to zero to skip waiting for confirmation, set to nonzero to wait and
	 * report to DS if action fails.
	 */
    public static final int kTimeoutMs = 30;
    


    public static final int WINDOW_MAX_VALUE = 1000000; // we should HOPEFULLY be getting smaller values

    public static final int ROTATIONS = 10;
    public static final int TICKS_PER_REV = 8192;
    public static final int TARGET = ROTATIONS * TICKS_PER_REV;



    // ratio of oscillations about average of a window to window length
    // any higher ratio will mean it is considered to be oscillating
    public static final double OSCILLATIONS_PER_WINDOW_LENGTH = 0.3;



    public static final int KF_WINDOW_SIZE = 100;
    public static final int KF_DIFFERENCE_TOLERANCE = 20;

    public static final int CRUISE_WINDOW_SIZE = 100;
    public static final int CRUISE_DIFFERENCE_TOLERANCE = 20;

    public static final int KP_WINDOW_SIZE = 100;
    public static final int KP_DIFFERENCE_TOLERANCE = 20;
    public static final int KP_OSCILLATION_THRESHHOLD = (int) (OSCILLATIONS_PER_WINDOW_LENGTH * KP_WINDOW_SIZE);

    public static final int KD_WINDOW_SIZE = 100;
    public static final int KD_DIFFERENCE_TOLERANCE = 20;

    public static final int KI_WINDOW_SIZE = 100;
    public static final int KI_DIFFERENCE_TOLERANCE = 20;
    public static final int KI_OSCILLATION_THRESHHOLD = (int) (OSCILLATIONS_PER_WINDOW_LENGTH * KI_WINDOW_SIZE);
    public static final int KI_STEADY_STATE_ERROR_THRESHHOLD = 10;
}