/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.scoring;

import frc.robot.MotorId;
import frc.robot.RobotMap;
import frc.robot.subsystem.BitBucketSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Add your docs here.
 */
public class ScoringSubsystem extends BitBucketSubsystem {
	// Singleton method; use ScoringSubsystem.instance() to get the ScoringSubsystem instance.
	public static ScoringSubsystem instance() {
		if(inst == null)
			inst = new ScoringSubsystem();
		return inst;
	}
	private static ScoringSubsystem inst;

	private ScoringSubsystem()
	{
		setName("ScoringSubsystem");
	}


	private final WPI_TalonSRX topRollerMotor = new WPI_TalonSRX(MotorId.TOP_INTAKE_MOTOR_ID);
	private final WPI_TalonSRX bottomRollerMotor = new WPI_TalonSRX(MotorId.BOTTOM_INTAKE_MOTOR_ID);

	// TODO: depending on the hardware, there may be (probably will be) more motors to rotate the scoring mechanism
	private final WPI_TalonSRX rotationMotor1 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR1_ID);
	private final WPI_TalonSRX rotationMotor2 = new WPI_TalonSRX(MotorId.ROTATION_MOTOR2_ID);
	
	private static SendableChooser<TestWheelPositions> testWheelChooser;

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
	 * If front is true, the angle is from the front of the robot, if it is false, it
	 * is from the back of the robot
	 */
	public void directArmTo(double angle, boolean front) {
		double rev = angle / 360;

		int ticks = (int) (rev * ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV);

		// if the arm is in the back of the robot
		if (front == false) {
			// switch the ticks so that the arm will go to intended position on the back too
			ticks = 2 * ScoringConstants.ARM_MOTOR_SWITCH_TICK_THRESHOLD - ticks;
		}

		// TODO: set up PID constants
		rotationMotor1.set(ControlMode.MotionMagic, ticks);
	}



	// TODO: in the future, we will want to move the arm so it is parallel (not antiparallel)
	// to the robot's velocity. This should involve testing to see if the robot has been going
	// in the direction its going in for a long enough period of time (if it changes its velocity
	// back and forth, changing the arm position may tip the robot)
	// tl;dr - find "front" yourself
	public void goToLevel(ScoringConstants.ScoringLevel level, boolean front) {
		double height = level.getHeight();

		// tip of arm is given by (height off floor) + (length) * sin(angle)
		double angle = Math.asin((height - ScoringConstants.ARM_AXIS_HEIGHT_OFF_FLOOR) / ScoringConstants.ARM_LENGTH);

		directArmTo(angle, front);
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
		topRollerMotor.set(ControlMode.PercentOutput, pow);
		// bottom will follow AND BE OPPOSITE
	}



	public void setAllMotorsZero() {
		topRollerMotor.set(ControlMode.PercentOutput, 0);
		rotationMotor1.set(ControlMode.PercentOutput, 0);
	}

	







	public double getAngle() {
		int ticks = rotationMotor1.getSelectedSensorPosition();
		double rev = ticks / ScoringConstants.ARM_MOTOR_NATIVE_TICKS_PER_REV;

		return 360 * rev;
	}





  	@Override
	public void diagnosticsInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void diagnosticsCheck() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void periodic() {
		if (getTelemetryEnabled()) {
			SmartDashboard.putNumber(getName() + "/Arm Angle", getAngle());
			SmartDashboard.putNumber(getName() + "/Arm Ticks", rotationMotor1.getSelectedSensorPosition());
		}

		// TODO: Guard with getDianosticsEnabled AND make inputs from Dashboard rather
		switch (testWheelChooser.getSelected()) {
			default: {}
			case DEG_0: {
				directArmTo(0, true);
				break;
			}
			case DEG_90: {
				directArmTo(90, true);
				break;
			}
			case DEG_180: {
				directArmTo(180, true);
				break;
			}
			case DEG_270: {
				directArmTo(270, true);
				break;
			}
		}

		updateBaseDashboard();
	}

	@Override
	public void diagnosticsExecute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() {
		initializeBaseDashboard();
		bottomRollerMotor.setInverted(true);
		// should be opposite of the top one at all times
		bottomRollerMotor.follow(topRollerMotor);

		// set it to 0 at starting position (front of robot)
		rotationMotor1.setSelectedSensorPosition(0);
		// follow 1
		rotationMotor2.follow(rotationMotor1);



		setAllMotorsZero();

		testWheelChooser = new SendableChooser<TestWheelPositions>();
		testWheelChooser.setDefaultOption("0 deg", TestWheelPositions.DEG_0);
		testWheelChooser.addOption("90 deg",       TestWheelPositions.DEG_90);
		testWheelChooser.addOption("180 deg",      TestWheelPositions.DEG_180);
		testWheelChooser.addOption("270 deg",      TestWheelPositions.DEG_270);

		SmartDashboard.putData(getName()+"/Test Wheel Chooser",testWheelChooser);
	}

}