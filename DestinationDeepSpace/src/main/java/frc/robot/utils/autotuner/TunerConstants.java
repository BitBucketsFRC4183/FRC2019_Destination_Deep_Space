package frc.robot.utils.autotuner;

public class TunerConstants {
	public static final String PROCESS_KEY     = "TestMode/AutoTuner/Current process";

	public static final String ERROR_KEY       = "TestMode/AutoTuner/Error (ticks)";
	public static final String VELOCITY_KEY    = "TestMode/AutoTuner/Velocity (ticks per 100ms)";
	public static final String POWER_KEY       = "TestMode/AutoTuner/Power";
	public static final String POSITION_KEY    = "TestMode/AutoTuner/Position";

    public static final String STABLE_KEY      = "TestMode/AutoTuner/Stable";
	public static final String OSCILLATING_KEY = "TestMode/AutoTuner/Oscillating";
	public static final String QUESTION_KEY    = "TestMode/AutoTuner/What to do";
	

	
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
    


	// target rotations for position control
    public static final int ROTATIONS = 10;
    public static final int TICKS_PER_REV = 8192;
    public static final int TARGET = ROTATIONS * TICKS_PER_REV;



	// how many data points to collect (same for + and -)
	// manually we usually use 4
	public static final int DATA_WINDOW_SIZE = 25;



	// maximum difference in a velocity data window for the data to be "stable"
	// 45 degree / second maximum uncertainty (tested)
	public static final int VELOCITY_STABILITY_THRESHOLD_TP100MS = (int) (45.0 / 3600 * 8192);

	// TODO: probably good values, maybe experiment
	public static final int POSITION_STABILITY_THRESHOLD_TICKS = 10;
	public static final double POWER_STABILITY_THRESHOLD = 0.01;
}