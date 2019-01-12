/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot; //Saba was here
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

/**
 * Add your docs here.
 */
public class RobotMap {
    public static double inch2Meter(double inch)
	{
		return 0.3048 * inch / 12.0; 
	}
	
	public static double meter2inch(double meter)
	{
		return 12.0 * meter / 0.3048; 
	}
	
	public static double signedSqrt(double value) 
	{
		if(value > 0)
			return Math.sqrt(value);
		else if(value < 0)
			return -Math.sqrt(Math.abs(value));
		else 
			return 0;
	}
	
	public static double signedPow(double base, double power)
	{
		if(base > 0)
			return Math.pow(base, power);
		else if(base < 0)
			return -Math.pow(Math.abs(base), power);
		else 
			return 0;
    }
    
    public static final int TELEOP_TOTAL_TIME = 135; // 2mins 15sec
	
	public static final int PRIMARY_PID_LOOP  = 0; // Constants to support new Talon interface types
	public static final int CASCADED_PID_LOOP = 1; // That should have been enumerated rather than int
	public static final int CONTROLLER_TIMEOUT_MS = 100; // Default timeout to wait for configuration response
    
    public static final int SUPER_HIGH_STATUS_FRAME_PERIOD_MS  =   5;	// CAUTION!
	public static final int HIGH_STATUS_FRAME_PERIOD_MS        =  10;	
	public static final int MEDIUM_HIGH_STATUS_FRAME_PERIOD_MS =  20;
	public static final int MEDIUM_STATUS_FRAME_PERIOD_MS      =  50;
    public static final int LOW_STATUS_FRAME_PERIOD_MS         = 100;
    
    /*DRIVE SUBSYSTEM*/
    public static final int LEFT_DRIVE_MOTOR_FRONT_ID  = 1;	// TODO: These are Junior's Numbers, consider 
	public static final int LEFT_DRIVE_MOTOR_REAR_ID   = 4;
	public static final int RIGHT_DRIVE_MOTOR_FRONT_ID = 2;
    public static final int RIGHT_DRIVE_MOTOR_REAR_ID  = 3;
    
    public static final boolean LEFT_DRIVE_MOTOR_INVERSION_FLAG = true;
   	public static final boolean RIGHT_DRIVE_MOTOR_INVERSION_FLAG = false;
   		
   		// If positive controller command yields positive rotation and positive encoder speed
   		// then the motor sensor phase should be left false. If the encoder reads a negative
   		// value when commanding positive rotation (as designed for the mechanism) then the
   		// sense should be inverted by setting the flag to true
   	public static final boolean LEFT_DRIVE_MOTOR_SENSOR_PHASE = false;
   	public static final boolean RIGHT_DRIVE_MOTOR_SENSOR_PHASE = false;
   	public static final FeedbackDevice DRIVE_MOTOR_FEEDBACK_DEVICE = FeedbackDevice.QuadEncoder;
    public static final int DRIVE_MOTOR_NATIVE_TICKS_PER_REV = 8192;	// AMT-201 at 2048 pulses per rev
    
    public static final double DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC   = 0.250;	// Second from neutral to full (easy on the gears)
   	public static final double DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC = 0.0;	    // No ramp rate on closed loop (use Motion Magic)
   	public static final double WHEEL_DIAMETER_INCHES = 6.25;
   	public static final double WHEEL_CIRCUMFERENCE_INCHES = (WHEEL_DIAMETER_INCHES * Math.PI);
   	public static final double WHEEL_TRACK_INCHES = 24.25;
   	public static final double TRACK_TO_CIRCUMFERENCE_RATIO = WHEEL_TRACK_INCHES / WHEEL_DIAMETER_INCHES;
   	public static final double WHEEL_ROTATION_PER_FRAME_DEGREES = TRACK_TO_CIRCUMFERENCE_RATIO / 360.0;
   	public static final double DRIVE_MOTOR_NATIVE_TICKS_PER_FRAME_DEGREES = DRIVE_MOTOR_NATIVE_TICKS_PER_REV * WHEEL_ROTATION_PER_FRAME_DEGREES;
   	public static final double DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS = 9926.8;	// per 100 ms, average of 10 samples
   	public static final double MAXIMUM_MOTION_ERROR_INCHES = 0.125;	// Convert into native ticks later
   	public static final double MAXIMUM_ROTATION_ERROR_INCHES = 0.50;
   	public static final double DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC = WHEEL_CIRCUMFERENCE_INCHES * 
			(DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS /
			 DRIVE_MOTOR_NATIVE_TICKS_PER_REV) * 10;
   	public static final double DRIVE_MAXIMUM_NO_LOAD_SPEED_FT_PER_SEC = DRIVE_MAXIMUM_NO_LOAD_SPEED_IN_PER_SEC / 12.0;

    // These Motion Magic values defined the shape of the trapezoidal profile for speed
	// The cruise speed is the maximum speed during the profile and is chosen to keep   		// below the maximum (which varies with battery voltage). The acceleration is the
   	// slope allowed to reach the cruise speed or zero (hence, a trapezoid)
    //
    // Setting this to 80% of maximum is a reasonable place to start;
    // However, the acceleration is currently default to reach cruising speed within 1 second 
    // and may need to be increased or decreased depending on static friction limits of tires
           
    public static final int DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS = (int)(0.80 * 
        DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS);
    public static final int DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS = (int) (4346/1.5); // 0.26 g on wood//DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS;

    public static final int DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS = (int) (MAXIMUM_MOTION_ERROR_INCHES * DRIVE_MOTOR_NATIVE_TICKS_PER_REV / WHEEL_CIRCUMFERENCE_INCHES);
   	public static final int DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION = (int) (MAXIMUM_ROTATION_ERROR_INCHES * DRIVE_MOTOR_NATIVE_TICKS_PER_REV / WHEEL_CIRCUMFERENCE_INCHES);
   	// The left and right sides may not be precisely balanced in terms of
   	// friction at really low speeds. We would like fine control to be balanced
   	// so the neutral deadband is adjusted to determine when the motors start
   	// moving on each side. This also prevents the motor from moving when
   	// really small commands are passed through.
   	//
   	// The values are determined empirically by simply driving the motors slowly
   	// until they first start to move on one side and not the other. Increase the
   	// values until the desired response is achieved.
   	public static final double LEFT_DRIVE_MOTOR_NEUTRAL_DEADBAND  = 0.003;
   	public static final double RIGHT_DRIVE_MOTOR_NEUTRAL_DEADBAND = 0.007;
   		
   	public static double driveMotorKf 	 = 1023.0 / DRIVE_MOTOR_FULL_THROTTLE_AVERAGE_SPEED_NATIVE_TICKS; 
   	public static double driveMotorKp 	 = 0.6704;				// Is currently 32 x (10% of 1023/error_at_10_rotations)
   	public static double driveMotorKi 	 = 0.0;            	// Very small values will help remove any final friction errors
   	public static double driveMotorKd 	 = 10 * driveMotorKf;	// Start with 10 x Kp for increased damping of overshoot
    public static int    driveMotorIZone = 0;
       
    /*
    * 								==========================================
    * 								========== AUTONOMOUS CONSTANTS ==========
    * 								==========================================
    */
    public static final double DRIVESTRAIGHT_MIN_DRIVE = 0;
    public static final double TURNBY_MIN_DRIVE = 0;

    public static final double MOTION_PROFILE_PERIOD_MS = 50;
    public static final double MINIMUM_MOVE_FORWARD_INCH = 85;	/// TODO: Check This, front of bumper well across line	

    //The else case if all desired paths fail and we want the robot to drive forward only.
    public static final long DRIVE_FORWARD_DELAY_MS = 3000;
    /*
    * 								===========================================
    * 								========== DIAGNOSTICS CONSTANTS ==========	
    * 								===========================================
    */
    public static final double MINIMUM_MOTOR_CURR = 1.25; 
    public static final double MOTOR_TEST_PERCENT = 0.5;
    public static final double TIMEOUT_GAME_INFO_SEC = 5;
}
