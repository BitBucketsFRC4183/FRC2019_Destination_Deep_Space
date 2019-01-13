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
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.SubsystemUtilities.SubsystemTelemetryState;
import frc.robot.subsystem.navigation.BitBucketsAHRS;
import frc.robot.utils.Deadzone;
import frc.robot.utils.JoystickScale;//for sam <3

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

	private final OI oi = OI.instance();

  	// Put methods for controlling this subsystem
  	// here. Call these from Commands.

	private final AHRS ahrs = BitBucketsAHRS.instance();

  	private final double INCH_PER_WHEEL_ROT = RobotMap.WHEEL_CIRCUMFERENCE_INCHES;
	

	// Can adjust these to help the robot drive straight with zero turn stick.
	// +Values will add +yaw correct (CCW viewed from top) when going forward.
	private final double YAW_CORRECT_VELOCITY = 0.0;  // Multiplied by inch/sec so value will be small!
	private final double YAW_CORRECT_ACCEL = 0.0;
	
	private final double LOW_SENS_GAIN = 0.6;		
	private final double ALIGN_LOOP_GAIN = 0.04;
  
	private final int EDGES_PER_ENCODER_COUNT = 4;	// Always for quadrature
	
	private double yawSetPoint;
		
	private final TalonSRX leftFrontMotor;		// User follower mode
	private final TalonSRX leftRearMotor;

	private final TalonSRX rightFrontMotor;		// Use follower mode
	private final TalonSRX rightRearMotor;
	
	private static SendableChooser<SubsystemTelemetryState> telemetryState;
	
	private static SendableChooser<JoystickScale> forwardJoystickScaleChooser;
	private static SendableChooser<JoystickScale> turnJoystickScaleChooser;
	
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

		
  public DriveSubsystem()
  {
    this.setName("DriveSubsystem");
    setName("DriveSubsystem");
              
    // Make joystick scale chooser and put it on the dashboard
    forwardJoystickScaleChooser = new SendableChooser<JoystickScale>();
    forwardJoystickScaleChooser.addDefault( "Linear",    JoystickScale.LINEAR);
    forwardJoystickScaleChooser.addObject(  "Square",    JoystickScale.SQUARE);
    forwardJoystickScaleChooser.addObject(  "Cube",      JoystickScale.CUBE);
    forwardJoystickScaleChooser.addObject(  "Sine",      JoystickScale.SINE);
      
    SmartDashboard.putData( "Forward Joystick Scale", forwardJoystickScaleChooser);    	

    turnJoystickScaleChooser = new SendableChooser<JoystickScale>();
    turnJoystickScaleChooser.addDefault(  "Square",    JoystickScale.SQUARE);
    turnJoystickScaleChooser.addObject( "Linear",    JoystickScale.LINEAR);
    turnJoystickScaleChooser.addObject(  "Cube",      JoystickScale.CUBE);
    turnJoystickScaleChooser.addObject(  "Sine",      JoystickScale.SINE);
       
    SmartDashboard.putData( "Turn Joystick Scale", turnJoystickScaleChooser);    	
    
    testModeChooser = new SendableChooser<TestSubmodes>();
    testModeChooser.addDefault("None", TestSubmodes.NONE);
    testModeChooser.addObject("Diagnostics", TestSubmodes.DIAGNOSTICS);
    testModeChooser.addObject("Move Test", TestSubmodes.MOVE_TEST);
    testModeChooser.addObject("Turn Test", TestSubmodes.TURN_TEST);
    testModeChooser.addObject("Profile Test", TestSubmodes.PROFILE_TEST);
    
    DIAG_LOOPS_RUN = (int) SmartDashboard.getNumber("DIAG_LOOPS_RUN", 10);
      
    testModePeriod_sec = SmartDashboard.getNumber("Test Mode Period (sec)", 2.0);
      
    leftFrontMotor = new TalonSRX(RobotMap.LEFT_DRIVE_MOTOR_FRONT_ID);
    leftRearMotor = new TalonSRX(RobotMap.LEFT_DRIVE_MOTOR_REAR_ID);
    leftRearMotor.follow(leftFrontMotor);
    
      
    /// TODO: Create setupMasterMotor function
    /// TODO: Create setupSlaveMotor function
    /// Each function should take a list of argument constants for inversion, sense, sensor type, deadbands, etc
    
    leftFrontMotor.setInverted(RobotMap.LEFT_DRIVE_MOTOR_INVERSION_FLAG);
    leftRearMotor.setInverted(RobotMap.LEFT_DRIVE_MOTOR_INVERSION_FLAG);
      
    leftFrontMotor.setSensorPhase(RobotMap.LEFT_DRIVE_MOTOR_SENSOR_PHASE);
      
    // Set relevant frame periods to be at least as fast as periodic rate
    // NOTE: This increases load on CAN bus, so pay attention as more motor
    // controllers are added to the system
    leftFrontMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 
                                    RobotMap.HIGH_STATUS_FRAME_PERIOD_MS, 
                                    RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 
                                    RobotMap.HIGH_STATUS_FRAME_PERIOD_MS, 
                                    RobotMap.CONTROLLER_TIMEOUT_MS);
    
    leftFrontMotor.configNeutralDeadband(RobotMap.LEFT_DRIVE_MOTOR_NEUTRAL_DEADBAND,
                                      RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configNeutralDeadband(RobotMap.LEFT_DRIVE_MOTOR_NEUTRAL_DEADBAND, 
                                    RobotMap.CONTROLLER_TIMEOUT_MS);
    
    leftFrontMotor.configOpenloopRamp(RobotMap.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
                                  RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configOpenloopRamp(RobotMap.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
                                           RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.configClosedloopRamp(RobotMap.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
                                              RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configClosedloopRamp(RobotMap.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
                                             RobotMap.CONTROLLER_TIMEOUT_MS);

    // Always configure peak and nominal outputs to be full scale and 0 respectively
    // We will apply limits in other ways, as needed
    leftFrontMotor.configPeakOutputForward(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.configPeakOutputReverse(-1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.configNominalOutputForward(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.configNominalOutputReverse(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    leftRearMotor.configPeakOutputForward(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configPeakOutputReverse(-1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configNominalOutputForward(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftRearMotor.configNominalOutputReverse(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    // Configure for closed loop control
    // Our drives use the "front" motor in a group for control; i.e., where the sensor is located
    leftFrontMotor.configSelectedFeedbackSensor(RobotMap.DRIVE_MOTOR_FEEDBACK_DEVICE, 
                                            RobotMap.PRIMARY_PID_LOOP, 
                                            RobotMap.CONTROLLER_TIMEOUT_MS);
    
    // Set closed loop gains in slot0 - see documentation (2018 SRM Section 12.6)
    // The gains are determined empirically following the Software Reference Manual
    // Summary:
    //	Run drive side at full speed, no-load, forward and initiate SelfTest on System Configuration web page
    //  Observe the number of encoder ticks per 100 ms, the % output, and voltage
    //  Collect data in both forward and backwards (e.g., 5 fwd, 5 back)
    //  Average the absolute value of that number, adjust as measured_ticks / percentage_factor
    //  Compute Kf = 1023 / adjusted_tick_average
    //  The using that value, run the Motion Magic forward 10 revolutions at the encoder scale
    //  Note the error (in ticks)
    //  Compute Kp = 0.1 * 1023 / error as a starting point
    //  Command any position through Motion Magic and attempt to turn the motor by hand while holding the command
    //  If the axle turns, keep doubling the Kp until it stops turning (or at leasts resists vigorously without
    //  oscillation); if it oscillates, you must drop the gain.
    //  Run the Motion Magic for at least 10 rotations in each direction
    //  Make not of any misses or overshoot.
    //  If there is unacceptable overshoot then set Kd = 10 * Kp as a starting point and re-test
    //
    //  Put drive train on ground with weight and re-test to see if position is as commanded.
    //  If not, then add SMALL amounts of I-zone and Ki until final error is removed.
    leftFrontMotor.selectProfileSlot(0, RobotMap.PRIMARY_PID_LOOP);
    leftFrontMotor.config_kF(0, RobotMap.driveMotorKf, RobotMap.CONTROLLER_TIMEOUT_MS);		/// TODO: Move constants to map/profile
    leftFrontMotor.config_kP(0, RobotMap.driveMotorKp, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.config_kI(0, RobotMap.driveMotorKi, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.config_kD(0, RobotMap.driveMotorKd, RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.config_IntegralZone(0, RobotMap.driveMotorIZone, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    /* set acceleration and vcruise velocity - see documentation */
    leftFrontMotor.configMotionCruiseVelocity(RobotMap.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS, 
                                          RobotMap.CONTROLLER_TIMEOUT_MS);
    leftFrontMotor.configMotionAcceleration(RobotMap.DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS, 
                                        RobotMap.CONTROLLER_TIMEOUT_MS);
    
    
    /* zero the sensor */
    leftFrontMotor.setSelectedSensorPosition(0, RobotMap.PRIMARY_PID_LOOP, RobotMap.CONTROLLER_TIMEOUT_MS);
    
              
    // Use follower mode to minimize shearing commands that could occur if
    // separate commands are sent to each motor in a group
    leftRearMotor.set(ControlMode.Follower, leftFrontMotor.getDeviceID());
    
    rightFrontMotor  = new TalonSRX(RobotMap.RIGHT_DRIVE_MOTOR_FRONT_ID);
    rightRearMotor   = new TalonSRX(RobotMap.RIGHT_DRIVE_MOTOR_REAR_ID);
    
    rightRearMotor.follow(rightFrontMotor);
    
    rightFrontMotor.setInverted(RobotMap.RIGHT_DRIVE_MOTOR_INVERSION_FLAG);
    rightRearMotor.setInverted(RobotMap.RIGHT_DRIVE_MOTOR_INVERSION_FLAG);

    rightFrontMotor.setSensorPhase(RobotMap.RIGHT_DRIVE_MOTOR_SENSOR_PHASE);

    rightFrontMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 
                                              RobotMap.HIGH_STATUS_FRAME_PERIOD_MS, 
                                              RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 
                                              RobotMap.HIGH_STATUS_FRAME_PERIOD_MS, 
                                              RobotMap.CONTROLLER_TIMEOUT_MS);
    
    rightFrontMotor.configNeutralDeadband(RobotMap.RIGHT_DRIVE_MOTOR_NEUTRAL_DEADBAND, 
                                      RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configNeutralDeadband(RobotMap.RIGHT_DRIVE_MOTOR_NEUTRAL_DEADBAND, 
                                      RobotMap.CONTROLLER_TIMEOUT_MS);
  
    rightFrontMotor.configOpenloopRamp(RobotMap.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
                          RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configOpenloopRamp(RobotMap.DRIVE_MOTOR_OPEN_LOOP_RAMP_SEC, 
                      RobotMap.CONTROLLER_TIMEOUT_MS);
    
    rightFrontMotor.configClosedloopRamp(RobotMap.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
                                      RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configClosedloopRamp(RobotMap.DRIVE_MOTOR_CLOSED_LOOP_RAMP_SEC, 
                                    RobotMap.CONTROLLER_TIMEOUT_MS);

    // Always configure peak and nominal outputs to be full scale and 0 respectively
    // We will apply limits in other ways, as needed	    	
    rightFrontMotor.configPeakOutputForward(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.configPeakOutputReverse(-1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.configNominalOutputForward(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.configNominalOutputReverse(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    rightRearMotor.configPeakOutputForward(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configPeakOutputReverse(-1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configNominalOutputForward(0, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightRearMotor.configNominalOutputReverse(0, RobotMap.CONTROLLER_TIMEOUT_MS);

    // Configure for closed loop control
    // Our drives use the "front" motor in a group for control; i.e., where the sensor is located
    leftFrontMotor.configSelectedFeedbackSensor(RobotMap.DRIVE_MOTOR_FEEDBACK_DEVICE, 
                                            RobotMap.PRIMARY_PID_LOOP, 
                                            RobotMap.CONTROLLER_TIMEOUT_MS);
    
    // Set closed loop gains in slot0 - see documentation (2018 SRM Section 12.6)
    // The gains are determined empirically following the Software Reference Manual
    // Summary:
    //	Run drive side at full speed, no-load, forward and initiate SelfTest on System Configuration web page
    //  Observe the number of encoder ticks per 100 ms, the % output, and voltage
    //  Collect data in both forward and backwards (e.g., 5 fwd, 5 back)
    //  Average the absolute value of that number, adjust as measured_ticks / percentage_factor
    //  Compute Kf = 1023 / adjusted_tick_average
    //  The using that value, run the Motion Magic forward 10 revolutions at the encoder scale
    //  Note the error (in ticks)
    //  Compute Kp = 0.1 * 1023 / error as a starting point
    //  Command any position through Motion Magic and attempt to turn the motor by hand while holding the command
    //  If the axle turns, keep doubling the Kp until it stops turning (or at leasts resists vigorously without
    //  oscillation); if it oscillates, you must drop the gain.
    //  Run the Motion Magic for at least 10 rotations in each direction
    //  Make not of any misses or overshoot.
    //  If there is unacceptable overshoot then set Kd = 10 * Kp as a starting point and re-test
    //
    //  Put drive train on ground with weight and re-test to see if position is as commanded.
    //  If not, then add SMALL amounts of I-zone and Ki until final error is removed.
    rightFrontMotor.selectProfileSlot(0, RobotMap.PRIMARY_PID_LOOP);
    rightFrontMotor.config_kF(0, RobotMap.driveMotorKf, RobotMap.CONTROLLER_TIMEOUT_MS);		/// TODO: Move constants to map/profile
    rightFrontMotor.config_kP(0, RobotMap.driveMotorKp, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.config_kI(0, RobotMap.driveMotorKi, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.config_kD(0, RobotMap.driveMotorKd, RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.config_IntegralZone(0, RobotMap.driveMotorIZone, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    /* set acceleration and vcruise velocity - see documentation */
    rightFrontMotor.configMotionCruiseVelocity(RobotMap.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS, 
                                           RobotMap.CONTROLLER_TIMEOUT_MS);
    rightFrontMotor.configMotionAcceleration(RobotMap.DRIVE_MOTOR_MOTION_ACCELERATION_NATIVE_TICKS, 
                                         RobotMap.CONTROLLER_TIMEOUT_MS);
  
    /* zero the sensor */
    rightFrontMotor.setSelectedSensorPosition(0, RobotMap.PRIMARY_PID_LOOP, RobotMap.CONTROLLER_TIMEOUT_MS);
    
    // Use follower mode to minimize shearing commands that could occur if
    // separate commands are sent to each motor in a group
    rightRearMotor.set(ControlMode.Follower, rightFrontMotor.getDeviceID());

    // Now get the other modes set up
    setNeutral(NeutralMode.Brake);
    
    
    // Create the motion profile driver

          
    telemetryState = new SendableChooser<SubsystemTelemetryState>();
    telemetryState.addDefault("Off", SubsystemTelemetryState.OFF);
    telemetryState.addObject( "On",  SubsystemTelemetryState.ON);
    
    SmartDashboard.putData("DriveTelemetry", telemetryState);
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
    	leftFrontMotor.configMotionCruiseVelocity((int)(fraction_full_speed * RobotMap.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS), 
					                                    RobotMap.CONTROLLER_TIMEOUT_MS);
    	rightFrontMotor.configMotionCruiseVelocity((int)(fraction_full_speed * RobotMap.DRIVE_MOTOR_MOTION_CRUISE_SPEED_NATIVE_TICKS), 
    													RobotMap.CONTROLLER_TIMEOUT_MS);
    }

    private double shapeAxis( double x) 
    {
		  x = Deadzone.f( x, .05);
		  return Math.signum(x) * (x*x);
  	}

	// +turnStick produces right turn (CW from above, -yaw angle)
    /// TODO: Consider re-designing this to reduce turn by up to 50% at full forward speed
	public void arcadeDrive(double fwdStick, double turnStick) 
	{
		
		// Shape axis for human control
		/// TODO: axis shaping should be controllable via dashboard
		/// see examples of selector for linear, square, cube, and sine
		/// TODO: May want different shapes on fwd and turn
		
		fwdStick = forwardJoystickScaleChooser.getSelected().rescale(fwdStick);
		turnStick = turnJoystickScaleChooser.getSelected().rescale(turnStick);
		
		if(oi.btnLowSensitiveDrive.get()) 
		{
			fwdStick *= LOW_SENS_GAIN;
			turnStick *= LOW_SENS_GAIN;
		}
		if(oi.btnInvertAxis.get()) {
			fwdStick *= -1.0;
		}
		double maxSteer = 1.0 - Math.abs(fwdStick) / 2.0;	// Reduce steering by up to 50%
		double steer = maxSteer * turnStick;
		
		leftFrontMotor.set(ControlMode.PercentOutput, fwdStick + steer);
		rightFrontMotor.set(ControlMode.PercentOutput, fwdStick - steer);
//		
//		/// TODO: Probably harmless. It is not clear that this 0,0 check will actually
//		/// do anything unless shapeAxis actually forces zero for some
//		/// shapes. In general, if the value is below the neutral deadband, nothing will move
//		/// so the minimum of both left and right deadbands is the determining factor
//		if( fwdStick == 0.0 && turnStick == 0.0) {
//			setAllMotorsZero();
//		}
//		else {
//			// Turn stick is + to the right;
//			// but arcadeDrive 2nd arg + produces left turn
//			// (this is +yaw when yaw is defined according to right-hand-rule
//			// with z-axis up, so arguably correct).
//			// Anyhow need the - sign on turnStick to make it turn correctly.
//			drive.arcadeDrive( fwdStick, turnStick + yawCorrect(), false);
//		}
	}
	public void doAutoTurn( double turn) {
		arcadeDrive( 0.0, turn);				
	}
	
	public void setAlignDrive(boolean start) {
		if(start) {
			yawSetPoint = ahrs.getYaw();
		} 
	}
	
	public void doAlignDrive(double fwdStick, double turnStick) {
					
		if(oi.btnLowSensitiveDrive.get())
			fwdStick *= LOW_SENS_GAIN;
		
		if(oi.btnInvertAxis.get()) {
			fwdStick *= -1.0;
		}
		
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
			
			double error = -ALIGN_LOOP_GAIN * (yawSetPoint - ahrs.getYaw());
			error = -ALIGN_LOOP_GAIN * -ahrs.getRate();
			SmartDashboard.putNumber("IMU_ERROR", error);
			arcadeDrive( fwdStick, error + yawCorrect());
		}
	}
	
	// Autonomous: drive in straight line
	public void doAutoStraight( double fwd) {
		if( fwd == 0.0)
			setAllMotorsZero();
		else {
			double error = ALIGN_LOOP_GAIN * (yawSetPoint - ahrs.getYaw());				
			arcadeDrive( fwd, error + yawCorrect());				
		}			
	}
	@Override
	protected void initDefaultCommand() 
	{
		// Moved to initialize so it does not automatically interfere
		System.out.println("Setting default command normally");
		// setDefaultCommand(new Idle());		
		
	}

	public void initialize() 
	{
		System.out.println("Setting default command Mike's way");
		Idle initialCommand = new Idle();
		initialCommand.start();
		
  }
  
  
	@Override
	public void periodic() {
		// TODO Auto-generated method stub
		
	}
  
  @Override
	public void setDiagnosticsFlag(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDiagnosticsFlag() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void disable() {
		setAllMotorsZero();
	}
	
	// Might need to change from .set(value) to .set(mode, value)
	private void setAllMotorsZero() 
	{
		leftFrontMotor.set(ControlMode.PercentOutput, 0.0);
		leftRearMotor.set(ControlMode.PercentOutput, 0.0);
		rightFrontMotor.set(ControlMode.PercentOutput, 0.0);
		rightRearMotor.set(ControlMode.PercentOutput, 0.0);			
	}
	/// TODO: This is redundant with other similar functions
	public void doLockDrive(double value) 
	{
		leftFrontMotor.set(ControlMode.MotionMagic, value);
		rightFrontMotor.set(ControlMode.MotionMagic, value);			
	}


	/** 
	 * setNeutral is a pass through interface to each motor in the subsystem
	 * 
	 * @param neutralMode is either Coast or Brake. Braking will apply force to come to a stop at zero input
	 */
	private void setNeutral(NeutralMode neutralMode) 
	{	
		leftFrontMotor.setNeutralMode(neutralMode);
		leftRearMotor.setNeutralMode(neutralMode);
		rightFrontMotor.setNeutralMode(neutralMode);
		rightRearMotor.setNeutralMode(neutralMode);
		
	}
	private double yawCorrect() {
		return YAW_CORRECT_VELOCITY * getFwdVelocity_ips() 
				+ YAW_CORRECT_ACCEL * getFwdCurrent();
	}
	public double getRightPosition_inch() {
		// Right motor encoder reads -position when going forward!
		// TODO: This is wrong! Need new constants
		return -INCH_PER_WHEEL_ROT * rightFrontMotor.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP);						
	}
	
	private int getMotorNativeUnits(TalonSRX m) {
		return m.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP);
	}
	
	public int getRightNativeUnits() {
		return getMotorNativeUnits(rightFrontMotor);
	}
	
	public int getLeftNativeUnits() {
		return getMotorNativeUnits(leftFrontMotor);
	}
	
	private double getMotorEncoderUnits(TalonSRX m) {
		return getMotorNativeUnits(m)/EDGES_PER_ENCODER_COUNT;
	}
	
	public double getRightEncoderUnits() {
		return getMotorEncoderUnits(rightFrontMotor);
	}
	
	public double getLeftEncoderUnits() {
		return getMotorEncoderUnits(leftFrontMotor);
	}
	
	private ControlMode getMotorMode(TalonSRX m) {
		return m.getControlMode();
	}
	
	public ControlMode getRightFrontMode() {
		return getMotorMode(rightFrontMotor);
	}
	
	public ControlMode getLeftFrontMode() {
		return getMotorMode(leftFrontMotor);
	}
	
	public ControlMode getLeftRearMode() {
		return getMotorMode(leftRearMotor);
	}
	
	public ControlMode getRightRearMode() {
		return getMotorMode(rightRearMotor);
	}
	
	public double inchesToNativeTicks(double inches) {
		return (double)RobotMap.DRIVE_MOTOR_NATIVE_TICKS_PER_REV * (inches / RobotMap.WHEEL_CIRCUMFERENCE_INCHES);
	}

	public double getFwdVelocity_ips() {
		// Right side motor reads -velocity when going forward!
		double fwdSpeedRpm = (leftFrontMotor.getSelectedSensorVelocity(RobotMap.PRIMARY_PID_LOOP) - rightFrontMotor.getSelectedSensorVelocity(RobotMap.PRIMARY_PID_LOOP))/2.0;
		return (INCH_PER_WHEEL_ROT / 60.0) * fwdSpeedRpm;
	}
	public double getFwdCurrent() {
		// OutputCurrent always positive so apply sign of drive voltage to get real answer.
		// Also, right side has -drive when going forward!
		double leftFront = leftFrontMotor.getOutputCurrent() * Math.signum( leftFrontMotor.getMotorOutputVoltage());
		double leftRear = leftRearMotor.getOutputCurrent() * Math.signum( leftRearMotor.getMotorOutputVoltage());
		double rightFront = -rightFrontMotor.getOutputCurrent() * Math.signum( rightFrontMotor.getMotorOutputVoltage());
		double rightRear = -rightRearMotor.getOutputCurrent() * Math.signum( rightRearMotor.getMotorOutputVoltage());
		return (leftFront + leftRear + rightFront + rightRear)/4.0;
	}
	
	public double getPosition_inch() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	// Set up a single motor for position control
	private void resetMotion(TalonSRX m) 
	{
		// Stop as quickly as possible
		m.set(ControlMode.PercentOutput, 0.0);
		
		// Clear the encoder to start a motion relative to "here"
		m.setSelectedSensorPosition(0, RobotMap.PRIMARY_PID_LOOP, RobotMap.CONTROLLER_TIMEOUT_MS);
	}
	
	// Set up the entire drive system for position control
	public void resetMotion() 
	{
		resetMotion(leftFrontMotor);
		resetMotion(rightFrontMotor);
	}
	
	// Set a specific motor for a motion magic position
	private void setPosition(TalonSRX m, double nativeTicks) {
		
		m.set(ControlMode.MotionMagic, nativeTicks);
	}
	
	// Set all motors to drive in the same direction for same distance
	public void move_inches(double value_inches) 
	{
		setPosition(leftFrontMotor,  inchesToNativeTicks(value_inches));
		setPosition(rightFrontMotor, inchesToNativeTicks(value_inches));
	}
	
	/* Any hardware devices used in this subsystem must
	*  have a check here to see if it is still connected and 
	*  working properly. For motors check for current draw.
	*  Return true iff all devices are working properly. Otherwise
	*  return false. This sets all motors to percent output
	*/
	@Override
	public void diagnosticsInit() {
		
	}
	
	@Override
	public void diagnosticsExecute() {

		/* Init Diagnostics */
		SmartDashboard.putBoolean("RunningDiag", true);
		
		rightFrontMotor.set(ControlMode.PercentOutput, RobotMap.MOTOR_TEST_PERCENT);
		rightRearMotor.set(ControlMode.PercentOutput, -RobotMap.MOTOR_TEST_PERCENT);
		leftFrontMotor.set(ControlMode.PercentOutput, -RobotMap.MOTOR_TEST_PERCENT);
		leftRearMotor.set(ControlMode.PercentOutput, RobotMap.MOTOR_TEST_PERCENT);
	}
	
	@Override
	public void diagnosticsCheck() {
		/* Reset flag */
		
	}

	// Move is complete when we are within tolerance and can consider starting the next move
	public boolean isMoveComplete(double distance_inches)	// At timeout should be used with this
	{
		int ticks = (int)inchesToNativeTicks(distance_inches);
		int errorL = (int) Math.abs(ticks - leftFrontMotor.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP));
		int errorR = (int) Math.abs(ticks - rightFrontMotor.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP));
		return (errorL  < RobotMap.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS) &&
			   (errorR < RobotMap.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS);
	}

	public void turn_degrees(double angle_degrees)
	{
		// Use motion magic to run both sides in opposite directions
		double targetPos_ticks = (angle_degrees * RobotMap.WHEEL_ROTATION_PER_FRAME_DEGREES) * RobotMap.DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
		
		// Assuming rotation is right hand rule about nadir (i.e., down vector is Z because X is out front and Y is out right side)
		// then Right Motor back and Left Motor forward is rotate to right (which is a positive rotation)
		
		leftFrontMotor.set(ControlMode.MotionMagic,  targetPos_ticks);
		rightFrontMotor.set(ControlMode.MotionMagic, -targetPos_ticks);		
		
	}
	
	public boolean isTurnComplete(double  angle_degrees) // A timeout should be used with this
	{
		// Using the same drive error for move and turn is not a universal thing
		// In this case if the wheels are 6.25 and track is 24.25 and tolerance is 0.125 inches on move
		// then the equivalent angle is about 0.6 degrees of frame rotation.
		
		double targetPos_ticks = (angle_degrees * RobotMap.WHEEL_ROTATION_PER_FRAME_DEGREES) * RobotMap.DRIVE_MOTOR_NATIVE_TICKS_PER_REV;
		int errorL = (int) Math.abs(targetPos_ticks - (leftFrontMotor.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP)));
		int errorR = (int) Math.abs(-targetPos_ticks - (rightFrontMotor.getSelectedSensorPosition(RobotMap.PRIMARY_PID_LOOP)));
		return (errorL  < RobotMap.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION) &&
			   (errorR < RobotMap.DRIVE_MOTOR_MAX_CLOSED_LOOP_ERROR_TICKS_ROTATION);
		
  }
}
