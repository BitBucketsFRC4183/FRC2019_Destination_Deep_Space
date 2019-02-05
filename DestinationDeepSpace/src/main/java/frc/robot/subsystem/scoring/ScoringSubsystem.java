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
	// last level the arm was at
	private ScoringConstants.ScoringLevel lastLevel = ScoringConstants.ScoringLevel.NONE;





	private ScoringSubsystem() {
		setName("ScoringSubsystem");



		rollerMotor    = new WPI_TalonSRX(MotorId.INTAKE_MOTOR_ID);
		rotationMotor1 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR1_ID);
		rotationMotor2 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR2_ID);

		// initialize motors before setting sensor positions and follower modes
		// otherwise, it may clear those settings
		TalonUtils.initializeMotorDefaults(rollerMotor);
		TalonUtils.initializeMotorDefaults(rotationMotor1);
		TalonUtils.initializeMotorDefaults(rotationMotor2);

		rotationMotor2.setInverted(true); // TODO: JUNIOR CONFIG



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

		rotationMotor1.setSelectedSensorPosition(0);
		rotationMotor2.setSelectedSensorPosition(0);

		rotationMotor1.configMotionAcceleration(2897, 20);
		rotationMotor2.configMotionAcceleration(2897, 20);

		rotationMotor1.configMotionCruiseVelocity(15000, 20);
		rotationMotor2.configMotionCruiseVelocity(15000, 20);



		setAllMotorsZero();
	}





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


	
	// Put methods for controlling this subsystem
	// here. Call these from Commands.



	/** Command the arm to a level */
	public void goToLevel(ScoringConstants.ScoringLevel level) {
		// neither level should get to here in the first place
		//     ... but just in case
		if (
			level == ScoringConstants.ScoringLevel.NONE ||
			level == ScoringConstants.ScoringLevel.INVALID
		) {
			return;
		}

		double angle_rad = level.getAngle_rad();

		// .switchOrientation() needs to know the last level the arm was at
		// if not, then whether the arm should be in the front or back is useless
		// because we have no way to recommand it to the right level
		lastLevel = level;

		directArmTo(angle_rad);
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



	/* stop all current subsystem functions (used in Idle) */
	public void disable() {
		setAllMotorsZero();
	}



	/* switch the orientation of the arm */
	public void switchOrientation() {
		front = !front;

		// go to the last level the arm was at, but this time
		// with the new orientation (handled by the method)
		goToLevel(lastLevel);
	}





	/** Get the selected level on the joystick */
	// Used so much in commands that I just put it in the subsystem
	public ScoringConstants.ScoringLevel getSelectedLevel() {
		boolean hp = oi.hp();
        boolean ground = oi.ground();
        boolean bCargo = oi.bCargo();
        boolean bLoadingStation = oi.bLoadingStation();
		boolean bRocket1 = oi.bRocket1();
		
		ScoringConstants.ScoringLevel level = ScoringConstants.ScoringLevel.NONE;

		if (hp) {
			level = ScoringConstants.ScoringLevel.HP;
		}
		if (ground) {
			if (level == ScoringConstants.ScoringLevel.NONE) { level = ScoringConstants.ScoringLevel.GROUND; }
			else { return ScoringConstants.ScoringLevel.INVALID; }
		}
		if (bCargo) {
			if (level == ScoringConstants.ScoringLevel.NONE) { level = ScoringConstants.ScoringLevel.BALL_CARGO; }
			else { return ScoringConstants.ScoringLevel.INVALID; }
		}
		if (bLoadingStation) {
			if (level == ScoringConstants.ScoringLevel.NONE) { level = ScoringConstants.ScoringLevel.BALL_LOADING_STATION; }
			else { return ScoringConstants.ScoringLevel.INVALID; }
		}
		if (bRocket1) {
			if (level == ScoringConstants.ScoringLevel.NONE) { level = ScoringConstants.ScoringLevel.BALL_ROCKET_1; }
			else { return ScoringConstants.ScoringLevel.INVALID; }
		}

		return level;
	}



	public int getArmLevelTickError() {
		int err1 = Math.abs(rotationMotor1.getClosedLoopError());
		int err2 = Math.abs(rotationMotor2.getClosedLoopError());

		return Math.max(err1, err2);
	}





	// Internal control of subsystem



	private void setAllMotorsZero() {
		rollerMotor.set(ControlMode.PercentOutput, 0);
		rotationMotor1.set(ControlMode.PercentOutput, 0);
	}



	/**
	 * Direct the robot arm to a certain angle.
	 */
	// private because we only want other classes to change the angle via goToLevel()
	private void directArmTo(double angle_rad) {
		double ticks = angle_rad * ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV / (2 * Math.PI);

		// if the arm is in the back of the robot
		if (front == false) {
			// switch the ticks so that the arm will go to intended position on the back too
			// ticks = 0 means arm is just up
			ticks *= -1;
		}

		rotationMotor1.set(ControlMode.MotionMagic, ticks);
		rotationMotor2.set(ControlMode.MotionMagic, ticks);
	}



	/** Get angle from normal of scoring arm (90 deg = exactly forward) */
	public double getAngle_deg() {
		int ticks = rotationMotor1.getSelectedSensorPosition(0);

		return 360.0 * ticks / ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV;
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
	protected void initDefaultCommand() {
	}

	@Override
	public void periodic() {
		if(oi.intakeActive()){
			setRollers(1.0);
		} else{
			setRollers(0.0);
		}
		clearDiagnosticsEnabled();
		updateBaseDashboard();
		if (getTelemetryEnabled()) {
			SmartDashboard.putNumber(getName() + "/Arm Angle", getAngle_deg());
			SmartDashboard.putNumber(getName() + "/Arm Ticks", rotationMotor1.getSelectedSensorPosition());
			SmartDashboard.putNumber(getName() + "/Arm Error", rotationMotor1.getClosedLoopError());
		}
		// commands will handle dealing with arm manipulation
	}

	@Override
	public void diagnosticsInitialize() {
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
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();

		SmartDashboard.putNumber(getName() + "/Test Angle", 0);
	}










	// Physics sim commands



	public TalonSRX getRotationMotor1() {
		return rotationMotor1;
	}
	
	public void manualArmOperate() {
		rotationMotor1.set(ControlMode.PercentOutput, oi.manualArmRotate());
	}
	public TalonSRX getRollerMotor() {
		return rollerMotor;
	}
}