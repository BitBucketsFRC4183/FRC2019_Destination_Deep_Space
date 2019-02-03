/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.MotorId;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.utils.talonutils.TalonUtils;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Add your docs here.
 */
public class ScoringSubsystem extends BitBucketSubsystem {
	private final OI oi = OI.instance();

	// Singleton method; use ScoringSubsystem.instance() to get the ScoringSubsystem instance.
	public static ScoringSubsystem instance() {
		if(inst == null)
			inst = new ScoringSubsystem();
		return inst;
	}
	private static ScoringSubsystem inst;


	private Idle initialCommand;


	private final WPI_TalonSRX rollerMotor;

	private final WPI_TalonSRX rotationMotor1;
	private final WPI_TalonSRX rotationMotor2;



	// last orientation of the robot's arm
	// true --> front
	// false --> back
	private boolean front = true;



	private ScoringSubsystem()
	{
		setName("ScoringSubsystem");



		rollerMotor = new WPI_TalonSRX(MotorId.INTAKE_MOTOR_ID);



		rotationMotor1 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR1_ID);
		rotationMotor2 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR2_ID);

		// set it to 0 at starting position (front of robot)
		rotationMotor1.setSelectedSensorPosition(0);
		rotationMotor2.follow(rotationMotor1);



		TalonUtils.initializeMotorDefaults(rollerMotor);

		TalonUtils.initializeMotorDefaults(rotationMotor1);
		TalonUtils.initializeMotorDefaults(rotationMotor2);



		// TODO: TEMPORARY VALUES + also not the best place to put them in the first place
		double kf = 0.05115;
		double kp = 0.005683*2*2*2*2*2*2*1.5;
		double ki = 0.001;
		double kd = 10 * kp;
		int izone = 200;

		TalonUtils.initializeMotorFPID        (rotationMotor1, kf, kp, ki, kd, izone);
		TalonUtils.initializeQuadEncoderMotor (rotationMotor1, 1);

		TalonUtils.initializeMotorFPID        (rotationMotor2, kf, kp, ki, kd, izone);
		TalonUtils.initializeQuadEncoderMotor (rotationMotor2, 1);



		setAllMotorsZero();
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	/*
	 * I drew this for a method I realized we didn't even need but decided to keep it, enjoy!
	 * 
	 * 
	 *        \   /            \   /                               \   /            \   /
	 *         \_/              \_/               G O               \_/              \_/
 	 *          \\               \\                                 //               //
	 *           \\               \\              BIT              //               //
	 *       _____\\_____     _____\\_____                   _____//_____     _____//_____
	 *       |          |     |          |      BUCKETS      |          |     |          |
	 *      ==O========O==   ==O========O==                 ==O========O==   ==O========O==
	 */



	/**
	 * Direct the robot arm to a certain angle.
	 */
	public void directArmTo(double angle) {
		double rev = angle / 360;

		int ticks = (int) (rev * ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV);

		// if the arm is in the back of the robot
		if (front == false) {
			// switch the ticks so that the arm will go to intended position on the back too
			ticks = 2 * ScoringConstants.ARM_MOTOR_SWITCH_THRESHOLD_TICKS - ticks;
		}

		rotationMotor1.set(ControlMode.MotionMagic, ticks);
	}



	public void goToLevel(ScoringConstants.ScoringLevel level) {
		double angle = level.getAngle_deg();

		directArmTo(angle);
	}

	public void manualArmOperate() {
		rotationMotor1.set(ControlMode.PercentOutput, OI.instance().manualArmRotate());
	}

	/**
	 * + pow --> spit out
	 * - pow --> intake
	 */
	public void setRollers(double pow) {
		// may be the other way around depending on the placement of the motors and such
		// currently this assumes that a + signal to the top roller will cause it to intake

		/*
		 *    <---  __
		 *         /  \
		 *         \__/  --->
		 * 
		 *         O (ball) (NOT TO SCALE) -->
		 * 
		 *          __   --->
		 *         /  \
		 *    <--- \__/
		 */
		rollerMotor.set(ControlMode.PercentOutput, pow);
	}



	public void setAllMotorsZero() {
		rollerMotor.set(ControlMode.PercentOutput, 0);
		rotationMotor1.set(ControlMode.PercentOutput, 0);
	}

	public void disable() {
		setAllMotorsZero();
	}

	







	public double getAngle() {
		int ticks = rotationMotor1.getSelectedSensorPosition();
		double rev = (ticks + 0.0) / ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV;

		return 360 * rev;
	}

	// switch the orientation of the arm
	public void switchOrientation() {
		front = !front;
	}

	/** Get the selected level on the joystick */
	// Used so much in commands that I just put it in the subsystem
	public ScoringConstants.ScoringLevel getSelectedLevel() {
		boolean hp = oi.hp();
        boolean ground = oi.ground();
        boolean bCargo = oi.bCargo();
        boolean bLoadingStation = oi.bLoadingStation();
		boolean bRocket1 = oi.bRocket1();
		
		ScoringConstants.ScoringLevel level = null;

		if (hp) {
			level = ScoringConstants.ScoringLevel.HP;
		}
		if (ground) {
			if (level == null) { level = ScoringConstants.ScoringLevel.GROUND; }
			else { return null; }
		}
		if (bCargo) {
			if (level == null) { level = ScoringConstants.ScoringLevel.BALL_CARGO; }
			else { return null; }
		}
		if (bLoadingStation) {
			if (level == null) { level = ScoringConstants.ScoringLevel.BALL_LOADING_STATION; }
			else { return null; }
		}
		if (bRocket1) {
			if (level == null) { level = ScoringConstants.ScoringLevel.BALL_ROCKET_1; }
			else { return null; }
		}

		return level;
	}

	public int getArmLevelTickError() {
		return rotationMotor1.getClosedLoopError();
	}
	
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	public void startIdle() {
		// Don't use default commands as they can catch you by surprise
		System.out.println("Starting " + getName() + " Idle...");
		if (initialCommand == null) {
			initialCommand = new Idle(); // Only create it once
		}
		initialCommand.start();
	}

	@Override
	public void periodic() {
		clearDiagnosticsEnabled();
		updateBaseDashboard();
		if (getTelemetryEnabled()) {
			SmartDashboard.putNumber(getName() + "/Arm Angle", getAngle());
			SmartDashboard.putNumber(getName() + "/Arm Ticks", rotationMotor1.getSelectedSensorPosition());
		}
		// commands will handle dealing with arm manipulation
	}

	@Override
	public void diagnosticsInitialize() {
		// TODO Auto-generated method stub
	}

	@Override
	public void diagnosticsPeriodic() {
		updateBaseDashboard();
		if (getDiagnosticsEnabled())
		{
			double angle = SmartDashboard.getNumber(getName() + "/Test Angle", 0);
			directArmTo(angle);
		}

		// commands will handle dealing with arm manipulation
	}

	@Override
	public void diagnosticsCheck() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();

		SmartDashboard.putNumber(getName() + "/Test Angle", 0);
	}

	public TalonSRX getRotationMotor1() {
		return rotationMotor1;
	}
}