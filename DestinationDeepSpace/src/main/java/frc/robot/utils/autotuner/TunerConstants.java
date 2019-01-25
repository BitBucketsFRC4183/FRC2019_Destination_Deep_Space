package frc.robot.utils.autotuner;

public class TunerConstants {
	public static final String PROCESS_KEY     = "AutoTuner/Current process";
	public static final String DATA_KEY        = "AutoTuner/Data";
	public static final String POWER_DATA_KEY  = "AutoTuner/Power output";
    public static final String STABLE_KEY      = "AutoTuner/Stable";
	public static final String OSCILLATING_KEY = "AutoTuner/Oscillating";
	public static final String QUESTION_KEY    = "AutoTuner/What to do";
	

	
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
    


    public static final int ROTATIONS = 10;
    public static final int TICKS_PER_REV = 8192;
    public static final int TARGET = ROTATIONS * TICKS_PER_REV;



	public static final int DATA_WINDOW_SIZE = 25;
}