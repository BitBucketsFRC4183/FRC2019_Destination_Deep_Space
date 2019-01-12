/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.subsystem.climber.ClimberSubsystem;
import frc.robot.subsystem.drive.DriveSubsystem;
import frc.robot.subsystem.lighting.LightingSubsystem;
import frc.robot.subsystem.navigation.NavigationSubsystem;
import frc.robot.subsystem.scoring.ScoringSubsystem;
import frc.robot.subsystem.vision.VisionSubsystem;
import frc.robot.operatorinterface.OI;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.grsadle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // Use this runMode variable to determine the 
	// current running mode of the Robot.
	public enum RunMode { DISABLED, AUTO, TELEOP, TEST };
	public static RunMode runMode = RunMode.DISABLED;
  public static RunMode lastState = runMode;	
  
  // Primary Subsystems that make up the major robot functions
  public static DriveSubsystem    driveSubsystem;
  public static ClimberSubsystem  climberSubsystem;
  public static ScoringSubsystem  scoringSubsystem;

  public static OI oi;

  // Support Subsystem that supplement the major robot functions
  public static NavigationSubsystem navigationSubsystem;
  public static VisionSubsystem     visionSubsystem;
  public static LightingSubsystem   lightingSubsystem;


  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    oi = OI.instance();
    // Create the robot subsystems at initialization
    // Doing it here rather than in a constructor eliminates potential global dependencies
    driveSubsystem = new DriveSubsystem();
    climberSubsystem = new ClimberSubsystem();
    scoringSubsystem = new ScoringSubsystem();

    navigationSubsystem = new NavigationSubsystem();
    visionSubsystem = new VisionSubsystem();
    lightingSubsystem = new LightingSubsystem();

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

    // NOTE: Normally one should not need to worry about the current order of execution
    // within the scheduler and the InterativeRobotBase underneath the TimeRobot class
    // our Robot is derived from.
    // However, the order is imporant to understanding what interpretations can be
    // made on records of operator inputs and responses.
    //
    // The current order (as identified by examining the Scheduler run and iterative loop
    // functions) is as follows:
    //
    //  IterativeRobotBase.loopFunc() runs on each Timed cycle
    //    Checks for mode (Disabled, Auto, TeleOp, else Test)
    //      Runs the appropriate xInit function if there was a mode transition
    //      Runs the appropriate xPeriodic function
    //    Runs robotPeriodic()
    //    Updates the Smartdashboard
    //    Updates the LiveWindow
    //    Updates the Shuffleboard
    //
    //  Scheduler.getInstance().run(), below
    //    Exits when Robot is Disabled
    //    Queries Buttons in reverse index order to ensure that Button #0 is highest priority
    //    Executes each Subsystem.periodic() function
    //    Executes each Command scheduled by all previous actions
    //    Adds any new commands for next cycle
    //    Adds in any default commands for next cycle
    //    
    // Graphically the sequence looks like the following
    //    loopFunc      |-----------------|
    //    xPeriodic      |----|          |
    //    robotPerodic        |-----|    |
    //    dashboards                |----|
    //
    // We can execute the Scheduler in any of the periodic functions.
    // Here we choose to do so in the robotPeriodic function to produce
    // the following sequence each timing iteration.
    //
    //    loopFunc      |---------------------------|
    //    xPeriodic      |----|                     |
    //    robotPerodic        |---------------------|
    //    Scheduler           |---------------|    |
    //    buttons             |---|           |    |
    //    Subsystem.periodic      |---|       |    |
    //    Commands                    |---|   |    |
    //    Add Commands                    |---|    |
    //    dashboards                          |----|

    Scheduler.getInstance().run();
  }

  /**
   * This function initializes the robot each time disabled is entered
   * Only place items in here that need to be initialized at every
   * mode transition (don't put items here that only need to be initialized
   * once at power-up; use robotInit for that!)
   */
  @Override
  public void disabledInit() {
    oi.setDisabledMode();
  }

  /**
   * This functions runs on the timed robot period.
   * Put things in here that need to be done every cycle, but only when disabled
   * If you need something to be done in any mode, then use robotPeriodic
   */
  @Override
  public void disabledPeriodic() {
    // NOTE: because this code executes before robotPeriodic in each iteration
    // the actions here occur BEFORE the scheduled commands run; this means that
    // commands can be added during this execution cycle and will be acted upon
    // within the current cycle.

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    oi.setAutoMode();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // NOTE: because this code executes before robotPeriodic in each iteration
    // the actions here occur BEFORE the scheduled commands run; this means that
    // commands can be added during this execution cycle and will be acted upon
    // within the current cycle.
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called each time teleop is entered
   * Only place items in here that need to occur at that boundary
   * If you need something initialized only at power-up then use robotInit
   */
  @Override
  public void teleopInit() {
    oi.setTeleopMode();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    // NOTE: because this code executes before robotPeriodic in each iteration
    // the actions here occur BEFORE the scheduled commands run; this means that
    // commands can be added during this execution cycle and will be acted upon
    // within the current cycle.
  }

  /**
   * This function is called each time test is entered
   * Only place items in here that need to occur at that boundary
   * If you need something initialized only at power-up then use robotInit
   */
  @Override
  public void testInit() {
  }  
  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
    // NOTE: because this code executes before robotPeriodic in each iteration
    // the actions here occur BEFORE the scheduled commands run; this means that
    // commands can be added during this execution cycle and will be acted upon
    // within the current cycle.
  }
}
