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
	private OI() {}

	private final static int DRIVER_JOYSTICK_ID = 0;
	private final static int OPERATOR_JOYSTICK_ID = 1;

    private final static Joystick driverControl = new Joystick(DRIVER_JOYSTICK_ID);
    private final static Joystick operatorControl = new Joystick(OPERATOR_JOYSTICK_ID);

	//****************************
	// AXIS DEFINITIONS
	//****************************
    private final static int DRIVE_SPEED_AXIS            = PS4Constants.LEFT_STICK_Y.getValue();
    private final static int DRIVE_TURN_AXIS             = PS4Constants.RIGHT_STICK_X.getValue();

	public double speed()
    {
        return driverControl.getRawAxis(DRIVE_SPEED_AXIS);
    }
    public double turn()
    {
        return -driverControl.getRawAxis(DRIVE_TURN_AXIS);
	}
	public double quickTurn_deg()
	{
		// getPOV will return -1 if nothing is pressed, so just return 0 deg
		// The non-zero value means the driver wants a turn
		double result = driverControl.getPOV(0);
		return (result > 0.0)?result:0.0;
	}
	
	//****************************
	// BUTTON DEFINITIONS
	//****************************
	private final static int DRIVE_LOW_SENSITIVE_BUTTON  = PS4Constants.R1.getValue();
	private final static int DRIVE_INVERT_BUTTON         = PS4Constants.R2.getValue();
	private final static int DRIVE_ALIGN_LOCK_BUTTON     = PS4Constants.L1.getValue();
	private final static int DRIVE_LOCK_BUTTON     		 = PS4Constants.L2.getValue();

	private final static int TEST_MOVE_BY_BUTTON         = PS4Constants.TRIANGLE.getValue(); /// TODO: Temp, use dashboard instead

	public boolean lowSensitivity()
	{
		return driverControl.getRawButton(DRIVE_LOW_SENSITIVE_BUTTON);
	}
	public boolean invertDrive()
	{
		return driverControl.getRawButton(DRIVE_INVERT_BUTTON);
	}
	public boolean alignLock()
	{
		return driverControl.getRawButton(DRIVE_ALIGN_LOCK_BUTTON);
	}
	public boolean  driveLock()
	{
		return driverControl.getRawButton(DRIVE_LOCK_BUTTON);
	}

    public static boolean testMoveBy() /// TODO: Temporary, use dashboard instead
    {
        return driverControl.getRawButton(TEST_MOVE_BY_BUTTON);
    }	
	
}



