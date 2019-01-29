/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
/**
 * Add your docs here.
 */
public class DriveConstants {
    public static final double JOYSTICK_DEADBAND = 0.2;

    // Set velocity follower type to false when independent gear boxes are being used
    // Set to true of all wheels on one side are physically linked
    public static final boolean CLOSED_LOOP_FOLLOWER = false;

    public static final double MAX_SPEED_IPS = 144.0;
    public static final double MAX_TURN_DPS  = 360.0;
    public static final double MAX_TURN_RADPS = Math.toRadians(MAX_TURN_DPS);
    public static final double STANDARD_G_FTPSPS = 32.1740;
    public static final double MAX_LAT_ACCELERATION_IPSPS = STANDARD_G_FTPSPS * 12.0;
    public static final double LOCK_DEADBAND_IPS = 12.0;  // ignore button command changes above this speed
    public static final double ALIGN_DEADBAND_DPS = 45.0; // ignore button command changes above this turn rate

    //TODO: substitute in correct values once we get them
    public static final double WHEEL_TRACK_INCHES = 18.25;
    public static final double WHEEL_DIAMETER_INCHES = 4.0;
    public static final double WHEEL_CIRCUMFERENCE_INCHES = Math.PI*WHEEL_DIAMETER_INCHES;
    public static final double TRACK_TO_CIRCUMFERENCE_RATIO = WHEEL_TRACK_INCHES / WHEEL_DIAMETER_INCHES;
    public static final double WHEEL_ROTATION_PER_FRAME_DEGREES = TRACK_TO_CIRCUMFERENCE_RATIO / 360.0;

    // Identify what type of feedback device we will use on this drive base
    // Assume that all feedback devices are the same type on all axels that
    // need to be measured.
    public static final FeedbackDevice DRIVE_MOTOR_FEEDBACK_DEVICE = FeedbackDevice.QuadEncoder;
    public static final double DRIVE_MOTOR_NATIVE_TICKS_PER_REV=8192; // AMT-201 at 2048 pulses per rev

    // The motor controllers we use (TalonSRX) return velocity in terms of native ticks per 100 ms 
    // and expect commands to be similarly dimensioned.
    // Even though this is a constants package, we provide the convenient conversion to/from
    // inches per second to the native ticks per 100 ms.
    // NOTE: Integer truncation is assumed for a maximum reduction of 10 ticks per second.
    // For 8192 ticks per rev that is error of < 0.07 RPM
    public static int ipsToTicksP100(double ips)
    {
        double rps = ips / WHEEL_CIRCUMFERENCE_INCHES;
        return (int)(DRIVE_MOTOR_NATIVE_TICKS_PER_REV * rps / 10.0);
    }
    public static double ticksP100ToIps(int ticksP100)
    {
        double rps = ticksP100 * 10.0 / DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
        return rps * WHEEL_CIRCUMFERENCE_INCHES;
    }

    // FIRST !!! get the sensor phase correct.
    // If positive input to motor controller (green LED) makes the sensor
    // return positive increasing counts then the sensor phase is set correctly.
    // E.g., start with false, if the counts go the correct direction you are good
    // to go; if not, set the flag to true (indicating the sensor inverted from the
    // positive input).
    public static final boolean LEFT_DRIVE_MOTOR_SENSOR_PHASE = false;
    public static final boolean RIGHT_DRIVE_MOTOR_SENSOR_PHASE = false;

    // SECOND !!! if you need the motor to move in the opposite direction when
    // positive is commanded, set the appropriate inversion flag true
    //
    // NOTE: If you are using a WPI drive class that performs inversion arithmetically
    // then look for a function to disable it; in order to use both the WPI class
    // and other control modes in the robot, the motor the inversions (if needed) must be in
    // the physical controller firmware, not the software.
    public static final boolean LEFT_DRIVE_MOTOR_INVERSION_FLAG = true;
    public static final boolean RIGHT_DRIVE_MOTOR_INVERSION_FLAG = false;

    
    // Define some constants for using the motor controllers
    // TODO: move this to a common motor class or utility

    public static final int PRIMARY_PID_LOOP  = 0; // Constants to support new Talon interface types
	public static final int CASCADED_PID_LOOP = 1; // That should have been enumerated rather than int
	public static final int CONTROLLER_TIMEOUT_MS = 100; // Default timeout to wait for configuration response
    
    public static final int SUPER_HIGH_STATUS_FRAME_PERIOD_MS  =   5;	// CAUTION!
	public static final int HIGH_STATUS_FRAME_PERIOD_MS        =  10;	
	public static final int MEDIUM_HIGH_STATUS_FRAME_PERIOD_MS =  20;
	public static final int MEDIUM_STATUS_FRAME_PERIOD_MS      =  50;
    public static final int LOW_STATUS_FRAME_PERIOD_MS         = 100;

 

    public static final double DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC   = 0.250;	// Second from neutral to full (easy on the gears)
    public static final double DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC = 0.0;	    // No ramp rate on closed loop (use Motion Magic)

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
        
    // Motion Magic Control Constant (JUNIOR)
    public static final int PID_MOTION_MAGIC_SLOT = 0;
    public static double MOTION_MAGIC_KF 	 = 0.05115; 
    public static double MOTION_MAGIC_KP 	 = 0.005683*2*2*2*2*2*2*1.5;
    public static double MOTION_MAGIC_KI 	 = 0.001;
    public static double MOTION_MAGIC_KD 	 = 10 * MOTION_MAGIC_KP;	// Start with 10 x Kp for increased damping of overshoot
    public static int    MOTION_MAGIC_IZONE = 200; 

    // Velocity Control Constant (JUNIOR)
    public static final int PID_VELOCITY_SLOT = 1;
    public static double VELOCITY_KF 	 = 0.05115; 
    public static double VELOCITY_KP 	 = 0.14014/2;
    public static double VELOCITY_KI 	 = 0.001;
    public static double VELOCITY_KD 	 = 10 * VELOCITY_KP;	// Start with 10 x Kp for increased damping of overshoot
    public static int    VELOCITY_IZONE   = 400; 

    public static final double MOTOR_TEST_PERCENT = 0.5;

	public static final double TURN_SIGN = 1.0; // For Junior
}
