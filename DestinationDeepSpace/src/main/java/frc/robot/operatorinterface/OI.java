package frc.robot.operatorinterface;

// import java.util.HashSet;
// import java.util.Set;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
	// Singleton method; use OI.instance() to get the OI instance.
	public static OI instance() {
		if(inst == null)
			inst = new OI();
		return inst;		
	}
	private static OI inst;	
	private OI() {
	}

	// private static ControllerMapper controllerMapper = ControllerMapper.ps4();
	private static ControllerMapper controllerMapper = ControllerMapper.xbox();

	private final static int DRIVER_JOYSTICK_ID = 0;
	private final static int OPERATOR_JOYSTICK_ID = 1;

    private final static Joystick driverControl = new Joystick(DRIVER_JOYSTICK_ID);
    private final static Joystick operatorControl = new Joystick(OPERATOR_JOYSTICK_ID);

	//****************************
	// AXIS DEFINITIONS
	//****************************
    private final static int DRIVE_SPEED_AXIS            = controllerMapper.getLeftStickY();
    private final static int DRIVE_TURN_AXIS             = controllerMapper.getRightStickX();

	/**
	 * speed - returns a speed command from driver joystick in the normal range [-1,1]
	 * except when the invertDrive button is indicated, which causes the range to be [1,-1]
	 */
	public double speed()
    {
		// Default to -1 to make up-stick positive because raw up-stick is negative
        return (invertDrive()?1.0:-1.0)*driverControl.getRawAxis(DRIVE_SPEED_AXIS);
	}
	/** turn - returns a turn rate command from the driver joystick in the normal range [-1,1] */
    public double turn()
    {
		// Defult to -1 because right stick is naturally negative and we want that to
		// really mean positive rate turn. Since traditional motion mechanics uses a
		// RPY or NED reference frame where Y (yaw) or D (down) are both positive
		// "down", right hand coordinate rules dictate that positive rotations are to
		// the right (i.e. vectors R x P = Y and N x E = D)
        return -driverControl.getRawAxis(DRIVE_TURN_AXIS);
	}
	
	/**
	 * Temporary function to manually operate the scoring arm with the driver controller
	 * @return
	 */
	public double manualArmRotate() {
		return driverControl.getRawAxis(controllerMapper.getRightStickY());
	}

	/**
	 * quickTurn_deg - returns a desired turn of +/-45, +/-90, +/-135 or 180 degrees
	 * This can be used in a main drive loop to initiate a command that induces
	 * a rapid closed loop turn when speed is below some threshold. It is recommended
	 * that the designer use this indication to initiate a command that temporarily
	 * overrides joystick control, return control after the turn is complete OR
	 * return control at some abort condition (e.g., joystick, button like POV = 0, or timeout)
	 * 
	 * Returns -1 when no turn is requested
	 * Returns 0, +/-45, +/-90, +/-135 or 180 depending on which buttons are pressed
	 */
	public double quickTurn_deg()
	{
		double result = driverControl.getPOV(0);

		if (result != -1)
		{
			// Split the circle to [-180,180] rather than [0,360]
			if (result > 180)
			{
				result -= 180;
			}
		}

		return result;
	}
	
	//****************************
	// BUTTON DEFINITIONS
	//****************************
	private final static int DRIVE_LOW_SENSITIVE_BUTTON  = controllerMapper.getR1();
	private final static int DRIVE_INVERT_BUTTON         = controllerMapper.getR2();
	private final static int DRIVE_ALIGN_LOCK_BUTTON     = controllerMapper.getL1();
	private final static int DRIVE_LOCK_BUTTON     		 = controllerMapper.getL2();

	private final static int ARM_CLIMBER                 = controllerMapper.getBrandButton();
	private final static int CLIMB                       = controllerMapper.getLStickButton();

	private final static int TEST_MOVE_BY_BUTTON         = controllerMapper.getTriangle(); /// TODO: Temp, use dashboard instead

	public boolean lowSensitivity()
	{
		return driverControl.getRawButton(DRIVE_LOW_SENSITIVE_BUTTON);
	}

	/** 
	 * invertDrive - a private function used by speed() to invert the speed joystick sense temporarily
	 */
	private boolean invertDrive()
	{
		return driverControl.getRawButton(DRIVE_INVERT_BUTTON);
	}
	/**
	 * alighLock - indicates driver would like to have some help driving straight
	 * or otherwise resist a turn
	 */
	public boolean alignLock()
	{
		return driverControl.getRawButton(DRIVE_ALIGN_LOCK_BUTTON);
	}
	/**
	 * driveLock - indicates driver would like to have some help holding position
	 * The intent is to use the drive system position control loops to act like
	 * a brake.
	 */
	public boolean  driveLock()
	{
		return driverControl.getRawButton(DRIVE_LOCK_BUTTON);
	}

    public boolean testMoveBy() /// TODO: Temporary, use dashboard instead
    {
        return driverControl.getRawButton(TEST_MOVE_BY_BUTTON);
	}	
	
    public boolean armClimber()
    {
        return driverControl.getRawButton(ARM_CLIMBER) && operatorControl.getRawButton(ARM_CLIMBER);
	}	
	
    public boolean climb()
    {
        return operatorControl.getRawButton(CLIMB);
    }	
	
}



