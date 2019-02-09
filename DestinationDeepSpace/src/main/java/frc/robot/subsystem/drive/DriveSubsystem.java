/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.MotorId;
import frc.robot.subsystem.drive.DriveConstants;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.navigation.NavigationSubsystem;
import frc.robot.utils.Deadzone;
import frc.robot.utils.JoystickScale;//for sam <3
import frc.robot.utils.talonutils.TalonUtils;


/**
 * Add your docs here.
 */
public class DriveSubsystem extends BitBucketSubsystem {

	// Singleton method; use DriveSubsystem.instance() to get the DriveSubsystem instance.
	public static DriveSubsystem instance() {
		if(inst == null)
			inst = new DriveSubsystem();
		return inst;
	}
	private static DriveSubsystem inst;

	// Reference any other singletons we need
	private final OI oi = OI.instance();
	private final NavigationSubsystem navigation = NavigationSubsystem.instance();


	// drive styles that driver can choose on the shuffleboard
	public enum DriveStyle {
		WPI_Arcade,
		BB_Arcade,
		Velocity
		// add in curvature & velocity later
	}
	private static SendableChooser<DriveStyle> driveStyleChooser;

	// Allow the driver to try different scaling functions on the joysticks
	private static SendableChooser<JoystickScale> forwardJoystickScaleChooser;
	private static SendableChooser<JoystickScale> turnJoystickScaleChooser;

	// Define the motor sets; this applies to motors grouped on single gearbox
	// or separate; plan is for 3 motors per side in final configuration but
	// we also have robots with 2 and some with one motor per corner. The standard
	// differential drive class allows for simple left/right specification and
	// we can use the built-in SRX follower mode to minimize CAN traffic for any
	// other drive style when motors are clustered. HOWEVER, if we have independent
	// gearboxes (like JUNIOR) then we can only use followers in the standard
	// differential drives (like arcade) that depend only on percent output commands.
	// If we want to use more physically coupled mechanics (like an acceleration limited
	// velocity control mode) then we will need to command all motors in a sequence
	// and will increase CAN traffic correspondingly.
	//
	// Since our preference is to cluster motors (this year) we should probably 
	// make every effort to minimize the CAN traffic to ensure we have some response
	// head space. HOWEVER, there will be a time when we will want indepedent control
	// (like swerve) and we will simply need to handle that when the need arises.
	//
	// For now, just create a master motor and a collection of slave motors for each side.
	private final WPI_TalonSRX leftMotors[];

	private final WPI_TalonSRX rightMotors[];

	private static DifferentialDrive differentialDrive;


	// Can adjust these to help the robot drive straight with zero turn stick.
	// +Values will add +yaw correct (CCW viewed from top) when going forward.
	private final double YAW_CORRECT_VELOCITY = 0.0;  // Multiplied by inch/sec so value will be small!
	private final double YAW_CORRECT_ACCEL = 0.0;
	
	private final double LOW_SENS_GAIN = 0.6;		
	private final double ALIGN_LOOP_GAIN = 0.04;
  
	private final int EDGES_PER_ENCODER_COUNT = 4;	// Always for quadrature
	
	// they always be saying "yee haw" but never "yaw hee" :(
	private double yawSetPoint;
	


	
	enum TestSubmodes
	{
		NONE,
		DIAGNOSTICS,
		MOVE_TEST,
		TURN_TEST,
		PROFILE_TEST
  	}
  	private static SendableChooser<TestSubmodes> testModeChooser;
	
	private static double testModePeriod_sec = 2.0;

	Idle initialCommand;

	// Keep track of when followers are need or being used
	private boolean usingFollowers = true;
	private boolean velocityMode = false;
	private boolean motionMode   = false;

	// ASSUME left and right are symmetrical
	private final int NUM_MOTORS_PER_SIDE = DriveConstants.LEFT_DRIVE_MOTOR_IDS.length;

  	private DriveSubsystem()
  	{
		setName("DriveSubsystem");
						
		// Make joystick scale chooser and put it on the dashboard
		forwardJoystickScaleChooser = new SendableChooser<JoystickScale>();
		forwardJoystickScaleChooser.setDefaultOption( "Linear",    JoystickScale.LINEAR);
		forwardJoystickScaleChooser.addOption(  "Square",    JoystickScale.SQUARE);
		forwardJoystickScaleChooser.addOption(  "Cube",      JoystickScale.CUBE);
		forwardJoystickScaleChooser.addOption(  "Sine",      JoystickScale.SINE);

		SmartDashboard.putData( getName()+"/Forward Joystick Scale", forwardJoystickScaleChooser);

		turnJoystickScaleChooser = new SendableChooser<JoystickScale>();
		turnJoystickScaleChooser.addOption( "Linear",    JoystickScale.LINEAR);
		turnJoystickScaleChooser.setDefaultOption(  "Square",    JoystickScale.SQUARE);
		turnJoystickScaleChooser.addOption(  "Cube",      JoystickScale.CUBE);
		turnJoystickScaleChooser.addOption(  "Sine",      JoystickScale.SINE);
		
		SmartDashboard.putData( getName()+"/Turn Joystick Scale", turnJoystickScaleChooser);
		

		driveStyleChooser = new SendableChooser<DriveStyle>();
		driveStyleChooser.setDefaultOption("WPI Arcade", DriveStyle.WPI_Arcade);
		driveStyleChooser.addOption("Bit Buckets Arcade", DriveStyle.BB_Arcade);
		driveStyleChooser.addOption("Velocity", DriveStyle.Velocity);

		SmartDashboard.putData( getName()+"/Drive Style", driveStyleChooser);


		
		// TODO: These may need to be removed
		testModeChooser = new SendableChooser<TestSubmodes>();
		testModeChooser.setDefaultOption("None", TestSubmodes.NONE);
		testModeChooser.addOption("Diagnostics", TestSubmodes.DIAGNOSTICS);
		testModeChooser.addOption("Move Test", TestSubmodes.MOVE_TEST);
		testModeChooser.addOption("Turn Test", TestSubmodes.TURN_TEST);
		testModeChooser.addOption("Profile Test", TestSubmodes.PROFILE_TEST);
		
		DIAG_LOOPS_RUN = (int) SmartDashboard.getNumber("DIAG_LOOPS_RUN", 10);
		
		testModePeriod_sec = SmartDashboard.getNumber("Test Mode Period (sec)", 2.0);
		
		// Create each left side motor controller, initialize it, and default to following
		// NOTE: The inversion behavior also depends on physical configuration and is
		// specified in the inversion flag array; in general odd motor counts will have
		// outer motors moving the same way and the inner motors alternating opposite
		// direction, but that assumption should NOT be used here; make the designer
		// explicitly tell us in the array which way is which for each motor in the
		// same order as the ID array
		//
		// NOTE: Motor 0 in the array is always the master
		leftMotors = new WPI_TalonSRX[NUM_MOTORS_PER_SIDE];
		rightMotors = new WPI_TalonSRX[NUM_MOTORS_PER_SIDE];
		for (int i = 0; i < NUM_MOTORS_PER_SIDE; i++)
		{
			leftMotors[i] = new WPI_TalonSRX(DriveConstants.LEFT_DRIVE_MOTOR_IDS[i]);
            leftMotors[i].setName(getName(),"Left_" + Integer.toString(i));

			TalonUtils.initializeMotorDefaults(leftMotors[i]);
			if (i > 0)
			{   // Slave motor for now
				leftMotors[i].follow(leftMotors[0]);
			}

			leftMotors[i].setInverted(DriveConstants.LEFT_DRIVE_MOTOR_INVERSION_FLAG[i]);

			// Assume all motor controllers have a sensor or access to one and the phase
			// is always the same (for now)
			leftMotors[i].setSensorPhase(DriveConstants.LEFT_DRIVE_MOTOR_SENSOR_PHASE);
	
			// Set relevant frame periods to be at least as fast as periodic rate
			// NOTE: This increases load on CAN bus, so pay attention as more motor
			// controllers are added to the system as we may want slave motors running
			// at a reduced rate
			leftMotors[i].setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 
											DriveConstants.HIGH_STATUS_FRAME_PERIOD_MS, 
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			leftMotors[i].setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 
											DriveConstants.HIGH_STATUS_FRAME_PERIOD_MS, 
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			
			leftMotors[i].configNeutralDeadband(DriveConstants.LEFT_DRIVE_MOTOR_NEUTRAL_DEADBAND,
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			
			leftMotors[i].configOpenloopRamp(DriveConstants.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
										DriveConstants.CONTROLLER_TIMEOUT_MS);

			leftMotors[i].configClosedloopRamp(DriveConstants.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
													DriveConstants.CONTROLLER_TIMEOUT_MS);	
													
			// For ALL motors in case we disable slaving
			TalonUtils.initializeQuadEncoderMotor(leftMotors[i]);

			// Set closed loop gains in different slots for different uses
			TalonUtils.initializeMotorFPID(leftMotors[i], 
										DriveConstants.MOTION_MAGIC_KF, 
										DriveConstants.MOTION_MAGIC_KP, 
										DriveConstants.MOTION_MAGIC_KI, 
										DriveConstants.MOTION_MAGIC_KD, 
										DriveConstants.MOTION_MAGIC_IZONE,
										DriveConstants.PID_MOTION_MAGIC_SLOT);

			// Motion Magic likes to specify a trapezoidal speed profile
			// These two settings provide the top speed and acceleration (slope) of profile
			leftMotors[i].configMotionCruiseVelocity(DriveConstants.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS, 
												DriveConstants.CONTROLLER_TIMEOUT_MS);
			leftMotors[i].configMotionAcceleration(DriveConstants.DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS, 
												DriveConstants.CONTROLLER_TIMEOUT_MS);

			TalonUtils.initializeMotorFPID(leftMotors[i], 
										DriveConstants.VELOCITY_KF, 
										DriveConstants.VELOCITY_KP, 
										DriveConstants.VELOCITY_KI, 
										DriveConstants.VELOCITY_KD, 
										DriveConstants.VELOCITY_IZONE,
										DriveConstants.PID_VELOCITY_SLOT);

			// !!!!!!!!!!!!!!! RIGHT !!!!!!!!!!!!!!!!!
			// TODO: May make this into a function with a few arguments							
			rightMotors[i] = new WPI_TalonSRX(DriveConstants.RIGHT_DRIVE_MOTOR_IDS[i]);
            rightMotors[i].setName(getName(),"Right_" + Integer.toString(i));			
			TalonUtils.initializeMotorDefaults(rightMotors[i]);
			if (i > 0)
			{   // Slave motor for now
				rightMotors[i].follow(rightMotors[0]);
			}

			rightMotors[i].setInverted(DriveConstants.RIGHT_DRIVE_MOTOR_INVERSION_FLAG[i]);

			// Assume all motor controllers have a sensor or access to one and the phase
			// is always the same (for now)
			rightMotors[i].setSensorPhase(DriveConstants.RIGHT_DRIVE_MOTOR_SENSOR_PHASE);
	
			// Set relevant frame periods to be at least as fast as periodic rate
			// NOTE: This increases load on CAN bus, so pay attention as more motor
			// controllers are added to the system as we may want slave motors running
			// at a reduced rate
			rightMotors[i].setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 
											DriveConstants.HIGH_STATUS_FRAME_PERIOD_MS, 
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			rightMotors[i].setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 
											DriveConstants.HIGH_STATUS_FRAME_PERIOD_MS, 
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			
			rightMotors[i].configNeutralDeadband(DriveConstants.RIGHT_DRIVE_MOTOR_NEUTRAL_DEADBAND,
											DriveConstants.CONTROLLER_TIMEOUT_MS);
			
			rightMotors[i].configOpenloopRamp(DriveConstants.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
										DriveConstants.CONTROLLER_TIMEOUT_MS);

			rightMotors[i].configClosedloopRamp(DriveConstants.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
													DriveConstants.CONTROLLER_TIMEOUT_MS);	
													
			// For ALL motors in case we disable slaving
			TalonUtils.initializeQuadEncoderMotor(rightMotors[i]);

			// Set closed loop gains in different slots for different uses
			TalonUtils.initializeMotorFPID(rightMotors[i], 
										DriveConstants.MOTION_MAGIC_KF, 
										DriveConstants.MOTION_MAGIC_KP, 
										DriveConstants.MOTION_MAGIC_KI, 
										DriveConstants.MOTION_MAGIC_KD, 
										DriveConstants.MOTION_MAGIC_IZONE,
										DriveConstants.PID_MOTION_MAGIC_SLOT);

			// Motion Magic likes to specify a trapezoidal speed profile
			// These two settings provide the top speed and acceleration (slope) of profile
			rightMotors[i].configMotionCruiseVelocity(DriveConstants.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS, 
												DriveConstants.CONTROLLER_TIMEOUT_MS);
			rightMotors[i].configMotionAcceleration(DriveConstants.DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS, 
												DriveConstants.CONTROLLER_TIMEOUT_MS);

			TalonUtils.initializeMotorFPID(rightMotors[i], 
										DriveConstants.VELOCITY_KF, 
										DriveConstants.VELOCITY_KP, 
										DriveConstants.VELOCITY_KI, 
										DriveConstants.VELOCITY_KD, 
										DriveConstants.VELOCITY_IZONE,
										DriveConstants.PID_VELOCITY_SLOT);

									
		}	


		// Now get the other modes set up
		setNeutral(NeutralMode.Brake);
		
		// Now that we have the motor instances set up the differential drive
		// as a 2-motor solution regardless of how manu actual motors we have
		// We are taking advantage of the follower mode to minimize CAN traffic
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// NOTE: This only works on drives where all motors on a side drive the
		// same wheels
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		differentialDrive = new DifferentialDrive(leftMotors[0], rightMotors[0]);

		// Since we going to use the TalonSRX in this class, the inversion, if needed is
		// going to be passed to controllers so positive commands on left and right both
		// move the wheels in the same direction. This means we don't want the diff drive
		// algorithm to also do the inversion
		differentialDrive.setRightSideInverted(false);
		
		// Create the motion profile driver
	}
  

  	public double getTestModePeriod_sec()
    {
    	return testModePeriod_sec;
    }
    public TestSubmodes getTestSubmode()
    {
    	return testModeChooser.getSelected();
    }
    
        
    /// TODO: Should provide more control, see junk bot example for an enumerated
    /// selector that can be different per axis
    public void setMotionVelocity(double fraction_full_speed) 
    {
		for (int i = 0; i < NUM_MOTORS_PER_SIDE; ++i)
		{
			leftMotors[i].configMotionCruiseVelocity((int)(fraction_full_speed * DriveConstants.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS), 
													DriveConstants.CONTROLLER_TIMEOUT_MS);
			rightMotors[i].configMotionCruiseVelocity((int)(fraction_full_speed * DriveConstants.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS), 
													DriveConstants.CONTROLLER_TIMEOUT_MS);
		}
    }

    private double shapeAxis( double x) 
    {
		  x = Deadzone.f( x, .05);
		  return Math.signum(x) * (x*x);
  	}

	void selectFollowerState(boolean needFollowers)
	{
		if (needFollowers && ! usingFollowers)
		{
			for (int i = 1; i < NUM_MOTORS_PER_SIDE; ++i)
			{
				leftMotors[i].follow(leftMotors[0]);
				rightMotors[i].follow(rightMotors[0]);
			}
			usingFollowers = true;
	
		}
		else
		{
			usingFollowers = false;
		}
	}

	void selectVelocityMode(boolean needVelocityMode)
	{
		if (needVelocityMode && ! velocityMode)
		{
			selectFollowerState(DriveConstants.CLOSED_LOOP_FOLLOWER);
			selectMotionMode(false);

			for (int i = 0; i < NUM_MOTORS_PER_SIDE; ++i)
			{
				leftMotors[i].selectProfileSlot(DriveConstants.PID_VELOCITY_SLOT, 
				                                DriveConstants.PRIMARY_PID_LOOP);
				rightMotors[i].selectProfileSlot(DriveConstants.PID_VELOCITY_SLOT, 
				                                 DriveConstants.PRIMARY_PID_LOOP);
			}			
			velocityMode = true;
		}
		else
		{
			velocityMode = false;
		}					
	}

	void selectMotionMode(boolean needMotionMode)
	{
		if (needMotionMode && ! motionMode)
		{
			selectFollowerState(DriveConstants.CLOSED_LOOP_FOLLOWER);
			selectVelocityMode(false);

			for (int i = 0; i < NUM_MOTORS_PER_SIDE; ++i)
			{
				leftMotors[i].selectProfileSlot(DriveConstants.PID_MOTION_MAGIC_SLOT, 
												DriveConstants.PRIMARY_PID_LOOP);
				rightMotors[i].selectProfileSlot(DriveConstants.PID_MOTION_MAGIC_SLOT, 
												DriveConstants.PRIMARY_PID_LOOP);
			}
			motionMode = true;
		}
		else
		{
			motionMode = false;
		}
	}
	/**
	 * drive - takes a speed and turn factor and passes to the selected drive algorithm
	 * Context depends upon which algorithm is selected, but is generally [-1,1] domain
	 * unless otherwise indicated.
	 * 
	 * Velocity drive selection requires inputs of feet/sec and deg/sec (TBD)
	 */
	public void drive(double speed, double turn) {

		// Rescale to the desired shape
		/// TODO: Add deadband to rescale
		speed = forwardJoystickScaleChooser.getSelected().rescale(speed, DriveConstants.JOYSTICK_DEADBAND);
		SmartDashboard.putNumber(getName()+"/Speed Factor",speed);
		turn = turnJoystickScaleChooser.getSelected().rescale(turn, DriveConstants.JOYSTICK_DEADBAND);
		SmartDashboard.putNumber(getName()+"/Turn Factor",turn);

		if(oi.lowSensitivity()) 
		{
			speed *= LOW_SENS_GAIN;
			turn *= LOW_SENS_GAIN;
		}

		if (ds.isTest())
		{
			testDrive();
		}
		else
		{
			DriveStyle style = driveStyleChooser.getSelected();

			switch (style) {
				// Even though the enumeration should be correct
				// it is a best practice to always explicitly set a default
				// just in case the interface has a glitch and the wrong
				// signal reaches here. The default can either fall through
				// or do something else, but now we made a choice
				default:
				case WPI_Arcade: {
					// DO NOT let the diff drive square the inputs itself
					// All scaling is external to this drive function
					selectFollowerState(true);
					selectVelocityMode(false);
					selectMotionMode(false);
					differentialDrive.arcadeDrive(speed, turn, false);

					break;
				}

				case BB_Arcade: {
					arcadeDrive(speed, turn);

					break;
				}

				case Velocity: {
					velocityDrive(speed, turn);

					break;
				}
			}
		}
	}

	/**
	 * testDrive - special test features for tuning and testing
	 * the drive train.
	 * 
	 * Include dashboard input/output for helping with FPID tuning
	 * by allowing trigger and control of 
	 * 		forward/reverse speed sample for Kf = (%v * 1023)/tp100
	 * 			Where
	 * 				%v is percent of full power (ideally 100%)
	 * 				tp100 is ticks per 100 ms
	 *      identification of initial cruise speed (85% of max)
	 * 			Cs = 0.85 * tp100
	 * 		invocation of motion magic mode and command some rotations, R (e.g., 10)
	 * 		Make note of the error in ticks (terr)
	 * 		Compute initial Kp
	 * 			Kp = (0.1 * 1023)/terr
	 * 		Command another +/- R rotations
	 * 			Test for oscillation or manually test for backdrive
	 * 			Keep doubling Kp until oscillations start and then back off a little
	 * 			Make note of overshoot
	 * 		Estimate initial Kd
	 * 			Kd = 10 * Kp
	 * 		Command another +/- R rotations
	 * 			Test for oscilation and overshoot
	 * 			Test for steady state error (sserr)
	 * 			Set I-Zone to define when Ki is needed
	 * 				Iz = sserr * 2.5
	 * 			Estimate Ki
	 * 				Ki = 0.001
	 * 			Keep doubling Ki until sserr gets sufficiently close to zero
	 * 				Stop and back off if oscillations appear
	 * 		
	 */
	private void testDrive() {

	}

	// +turnStick produces right turn (CW from above, -yaw angle)
    /// TODO: Consider re-designing this to reduce turn by up to 50% at full forward speed
	private void arcadeDrive(double speed, double turn) 
	{
		// The following functions on do something if the state needs to be changed
		selectFollowerState(true);
		selectVelocityMode(false);	/// TODO: Create setPercentMode to auto disable V and M
		selectMotionMode(false);

		double maxSteer = 1.0 - Math.abs(speed) / 2.0;	// Reduce steering by up to 50%
		double steer = maxSteer * turn;
		
		leftMotors[0].set(ControlMode.PercentOutput, speed + steer);
		rightMotors[0].set(ControlMode.PercentOutput, speed - steer);
	}


	public static double map(double x, double inMin, double inMax, double outMin, double outMax)
	{
	   return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
	}
	/**
	 * velocityDrive converts inputs to physical unit limits and
	 * drives the motors using a velocity control closed-loop profile
	 */
	public void velocityDrive(double speed, double turn)
	{
		// The following functions only do something if the state needs
		// to be changed.
		selectVelocityMode(true);

		// Scale the input to physical units (with implied limits)
		double speed_ips = map(speed,
								 -1.0,
								  1.0,
								 -DriveConstants.MAX_SPEED_IPS,
								 DriveConstants.MAX_SPEED_IPS);
		double turn_radps   = map(turn,
								 -1.0,
								  1.0,
								 -DriveConstants.MAX_TURN_RADPS,
								 DriveConstants.MAX_TURN_RADPS);
		SmartDashboard.putNumber(getName()+"/Commanded Speed (ips)", speed_ips);
		SmartDashboard.putNumber(getName()+"/Commanded Turn (dps)", Math.toDegrees(turn_radps));

		double diffSpeed_ips = turn_radps * DriveConstants.WHEEL_TRACK_INCHES / 2.0;

		// Compute, report, and limit lateral acceleration
		if (Math.abs(turn_radps * speed_ips) > DriveConstants.MAX_LAT_ACCELERATION_IPSPS)
		{
			speed_ips = Math.signum(speed_ips)*DriveConstants.MAX_LAT_ACCELERATION_IPSPS/Math.abs(turn_radps);
		}
		double latAccel_gs = turn_radps * speed_ips / 12.0 / DriveConstants.STANDARD_G_FTPSPS;
		double turnRadius_inches = speed_ips / turn_radps;
		SmartDashboard.putNumber(getName()+"/Lat Accel (g)", latAccel_gs );
		SmartDashboard.putNumber(getName()+"/Turn Radius (inches)",turnRadius_inches);
		SmartDashboard.putNumber(getName()+"/Acheived Speed (ips)", speed_ips);
		SmartDashboard.putNumber(getName()+"/Acheived Turn (dps)", Math.toDegrees(turn_radps));


		int speed_tickP100 = DriveConstants.ipsToTicksP100(speed_ips);
		int diffSpeed_tickP100 = DriveConstants.ipsToTicksP100(diffSpeed_ips);

		int leftSpeed_tickP100 = speed_tickP100 + diffSpeed_tickP100;
		int rightSpeed_tickP100 = speed_tickP100 - diffSpeed_tickP100;

		SmartDashboard.putNumber(getName() + "/leftSpeed (tps)",leftSpeed_tickP100);
		SmartDashboard.putNumber(getName() + "/rightSpeed (tps)",rightSpeed_tickP100);

		leftMotors[0].set(ControlMode.Velocity, leftSpeed_tickP100);
		rightMotors[0].set(ControlMode.Velocity, rightSpeed_tickP100);		

		if ( ! usingFollowers)
		{
			for (int i = 1; i < NUM_MOTORS_PER_SIDE; ++i)
			{
				leftMotors[i].set(ControlMode.Velocity, leftSpeed_tickP100);
				rightMotors[i].set(ControlMode.Velocity, rightSpeed_tickP100);
			}
		}		

	}

	public void doAutoTurn( double turn) {
		arcadeDrive( 0.0, turn);				
	}
	
	public void setAlignDrive(boolean start) {
		if(start) {
			yawSetPoint = navigation.getYaw_deg();
		} 
	}
	
	public void doAlignDrive(double fwdStick, double turnStick) {
					
		if(oi.lowSensitivity())
			fwdStick *= LOW_SENS_GAIN;
		
		fwdStick = shapeAxis(fwdStick);
		turnStick = shapeAxis(turnStick);
					
		if( fwdStick == 0.0 && turnStick == 0.0) {
			setAllMotorsZero();
		}
		else {
			
			// Turn stick is + to the right,
			// +yaw is CCW looking down,
			// so + stick should lower the setpoint. 
			yawSetPoint += -0.3 * turnStick;
			
			double error = -ALIGN_LOOP_GAIN * (yawSetPoint - navigation.getYaw_deg());
			error = -ALIGN_LOOP_GAIN * -navigation.getYawRate_degPerSec();
			SmartDashboard.putNumber(getName()+"/IMU_ERROR", error);
			arcadeDrive( fwdStick, error + yawCorrect());
		}
	}
	
	// Autonomous: drive in straight line
	public void doAutoStraight( double fwd) {
		if( fwd == 0.0)
			setAllMotorsZero();
		else {
			double error = ALIGN_LOOP_GAIN * (yawSetPoint - navigation.getYaw_deg());				
			arcadeDrive( fwd, error + yawCorrect());				
		}			
	}
	@Override
	protected void initDefaultCommand() 
	{
		// NOTE NOTE NOTE: Moved to startIdle so it does not automatically interfere
		// setDefaultCommand(new Idle());		
		
	}

	// Always start the 
	public void startIdle()
	{
		// Don't use default commands as they can catch you by surprise
		System.out.println("Starting " + getName() + " Idle...");
		if (initialCommand == null)
		{
			initialCommand = new Idle();	// Only create it once
		}
		initialCommand.start();
	}

	// Plase one-time initialization here
	public void initialize() 
	{		
		initializeBaseDashboard();
	}
	
	@Override
	public void periodic() {

		updateBaseDashboard();
		if (getTelemetryEnabled())
		{

		}
		if (getDiagnosticsEnabled())
		{

		}		
	}
  	
	public void disable() {
		setAllMotorsZero();
	}
	
	// Might need to change from .set(value) to .set(mode, value)
	private void setAllMotorsZero() 
	{
		selectFollowerState(true);
		leftMotors[0].set(ControlMode.PercentOutput, 0.0);
		rightMotors[0].set(ControlMode.PercentOutput, 0.0);
	}
	
	/// TODO: This is redundant with other similar functions
	public void doLockDrive(double value) 
	{
		selectMotionMode(true);
		
		leftMotors[0].set(ControlMode.MotionMagic, value);
		rightMotors[0].set(ControlMode.MotionMagic, value);			
	}


	/** 
	 * setNeutral is a pass through interface to each motor in the subsystem
	 * 
	 * @param neutralMode is either Coast or Brake. Braking will apply force to come to a stop at zero input
	 */
	private void setNeutral(NeutralMode neutralMode) 
	{
		for (int i = 0; i < NUM_MOTORS_PER_SIDE; ++i)
		{
			leftMotors[i].setNeutralMode(neutralMode);
			rightMotors[i].setNeutralMode(neutralMode);
		}		
	}

	/// TODO: This function makes no sense, need to decide if we should
	/// just scrap it
	private double yawCorrect() {
		return YAW_CORRECT_VELOCITY * getVelocity_ips() 
				+ YAW_CORRECT_ACCEL * getAverageCurrent_amps();
	}
	public double getRightPosition_inch() {
		// Right motor encoder reads -position when going forward!
		// TODO: This is wrong! Need new constants
		return -DriveConstants.WHEEL_CIRCUMFERENCE_INCHES * 
		        rightMotors[0].getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP);						
	}
	
	private int getMotorNativeUnits(WPI_TalonSRX m) {
		return m.getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP);
	}
	
	public int getRightNativeUnits() {
		return getMotorNativeUnits(rightMotors[0]);
	}
	
	public int getLeftNativeUnits() {
		return getMotorNativeUnits(leftMotors[0]);
	}
	
	private double getMotorEncoderUnits(WPI_TalonSRX m) {
		return getMotorNativeUnits(m)/EDGES_PER_ENCODER_COUNT;
	}
	
	public double getRightEncoderUnits() {
		return getMotorEncoderUnits(rightMotors[0]);
	}
	
	public double getLeftEncoderUnits() {
		return getMotorEncoderUnits(leftMotors[0]);
	}
	
	private ControlMode getMotorMode(WPI_TalonSRX m) {
		return m.getControlMode();
	}
	
	public ControlMode getRightFrontMode() {
		return getMotorMode(rightMotors[0]);
	}
	
	public ControlMode getLeftFrontMode() {
		return getMotorMode(leftMotors[0]);
	}
	
	public ControlMode getLeftMode(int index) {
		index = index % NUM_MOTORS_PER_SIDE;
		return getMotorMode(leftMotors[index]);
	}
	
	public ControlMode getRightMode(int index) {
		index = index % NUM_MOTORS_PER_SIDE;
		return getMotorMode(rightMotors[index]);
	}
	
	/// TODO: Move to DriveConstants and rename
	public double inchesToNativeTicks(double inches) {
		return (double)DriveConstants.DRIVE_MOTOR_NATIVE_TICKS_PER_REV * (inches / DriveConstants.WHEEL_CIRCUMFERENCE_INCHES);
	}

	public double getVelocity_ips() {
		// Average left and right side to find centerline velocity
		// NOTE: We assume that both left and right motors are configured to provide
		// proper sensor phase and output polarity so their data can simply be
		// added together

		int velocity_tp100 = (leftMotors[0].getSelectedSensorVelocity(DriveConstants.PRIMARY_PID_LOOP) + 
		                      rightMotors[0].getSelectedSensorVelocity(DriveConstants.PRIMARY_PID_LOOP))/2;
		return DriveConstants.ticksP100ToIps(velocity_tp100);
	}
	public double getTurnRate_dps()
	{
		int differentialVelocity_tp100 = leftMotors[0].getSelectedSensorVelocity(DriveConstants.PRIMARY_PID_LOOP) -
										 rightMotors[0].getSelectedSensorVelocity(DriveConstants.PRIMARY_PID_LOOP);
		return Math.toDegrees(DriveConstants.ticksP100ToIps(differentialVelocity_tp100) / DriveConstants.WHEEL_TRACK_INCHES);
	}

	public double getTotalCurrent_amps() {
		double amps = 0;
		for (int i = 0; i < NUM_MOTORS_PER_SIDE; ++i)
		{
			amps += Math.abs(leftMotors[i].getOutputCurrent()) + Math.abs(rightMotors[i].getOutputCurrent());
		}
		return 	amps;

	}
	public double getAverageCurrent_amps() {
		return getTotalCurrent_amps() / 4.0;
	}
	
	// Set up a single motor for position control
	private void resetMotion(WPI_TalonSRX m) 
	{
		// Stop as quickly as possible
		m.set(ControlMode.PercentOutput, 0.0);
		
		// Clear the encoder to start a motion relative to "here"
		m.setSelectedSensorPosition(0, DriveConstants.PRIMARY_PID_LOOP, DriveConstants.CONTROLLER_TIMEOUT_MS);
	}
	
	// Set up the entire drive system for position control
	public void resetMotion() 
	{
		resetMotion(leftMotors[0]);
		resetMotion(rightMotors[0]);
	}
	
	// Set a specific motor for a motion magic position
	private void setPosition(WPI_TalonSRX m, double nativeTicks) {
		
		m.set(ControlMode.MotionMagic, nativeTicks);
	}
	
	// Set all motors to drive in the same direction for same distance
	public void move_inches(double value_inches) 
	{
		setPosition(leftMotors[0],  inchesToNativeTicks(value_inches));
		setPosition(rightMotors[0], inchesToNativeTicks(value_inches));
	}
	
	@Override
	public void diagnosticsCheck() {
		/* Reset flag */
	}

	// Move is complete when we are within tolerance and can consider starting the next move
	public boolean isMoveComplete(double distance_inches)	// At timeout should be used with this
	{
		int ticks = (int)inchesToNativeTicks(distance_inches);
		int errorL = (int) Math.abs(ticks - leftMotors[0].getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP));
		int errorR = (int) Math.abs(ticks - rightMotors[0].getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP));
		return (errorL  < DriveConstants.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS) &&
			   (errorR < DriveConstants.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS);
	}

	public void turn_degrees(double angle_degrees)
	{
		// Use motion magic to run both sides in opposite directions
		double targetPos_ticks = (angle_degrees * DriveConstants.WHEEL_ROTATION_PER_FRAME_DEGREES) * DriveConstants.DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
		
		// Assuming rotation is right hand rule about nadir (i.e., down vector is Z because X is out front and Y is out right side)
		// then Right Motor back and Left Motor forward is rotate to right (which is a positive rotation)
		
		leftMotors[0].set(ControlMode.MotionMagic,  targetPos_ticks);
		rightMotors[0].set(ControlMode.MotionMagic, -targetPos_ticks);		
		
	}
	
	public boolean isTurnComplete(double  angle_degrees) // A timeout should be used with this
	{
		// Using the same drive error for move and turn is not a universal thing
		// In this case if the wheels are 6.25 and track is 24.25 and tolerance is 0.125 inches on move
		// then the equivalent angle is about 0.6 degrees of frame rotation.
		
		double targetPos_ticks = (angle_degrees * DriveConstants.WHEEL_ROTATION_PER_FRAME_DEGREES) * DriveConstants.DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
		int errorL = (int) Math.abs(targetPos_ticks - (leftMotors[0].getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP)));
		int errorR = (int) Math.abs(-targetPos_ticks - (rightMotors[0].getSelectedSensorPosition(DriveConstants.PRIMARY_PID_LOOP)));
		return (errorL  < DriveConstants.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION) &&
			   (errorR < DriveConstants.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION);
		
  }

  	/* Any hardware devices used in this subsystem must
	*  have a check here to see if it is still connected and 
	*  working properly. For motors check for current draw.
	*  Return true iff all devices are working properly. Otherwise
	*  return false. This sets all motors to percent output
	*/
	@Override
	public void diagnosticsInitialize() {

	}

	@Override
	public void diagnosticsPeriodic() {
		/* Init Diagnostics */
		SmartDashboard.putBoolean(getName()+"/RunningDiag", true);
		
		/// COMMENTED OUT BECAUSE THIS IS NOT SAFE, we need a different way to test motors
		// rightMotors.set(ControlMode.PercentOutput, DriveConstants.MOTOR_TEST_PERCENT);
		// rightRearMotor.set(ControlMode.PercentOutput, -DriveConstants.MOTOR_TEST_PERCENT);
		// leftMotors.set(ControlMode.PercentOutput, -DriveConstants.MOTOR_TEST_PERCENT);
		// leftRearMotor.set(ControlMode.PercentOutput, DriveConstants.MOTOR_TEST_PERCENT);
	}


	public WPI_TalonSRX getLeftMasterMotor() {
		return leftMotors[0];
	}
	public WPI_TalonSRX getRightMasterMotor() {
		return rightMotors[0];
	}
	public WPI_TalonSRX getLeftMotor(int index) {
		index = index % NUM_MOTORS_PER_SIDE;
		return leftMotors[index];
	}
	public WPI_TalonSRX getRightMotor(int index) {
		index = index % NUM_MOTORS_PER_SIDE;
		return rightMotors[index];
	}
}
