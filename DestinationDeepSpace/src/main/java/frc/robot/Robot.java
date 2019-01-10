/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.Subsystem.ClimberSubsystem.ClimberSubsystem;
import frc.robot.Subsystem.DriveSubsystem.DriveSubsystem;
import frc.robot.Subsystem.LightingSubsystem.LightingSubsystem;
import frc.robot.Subsystem.NavigationSubsystem.NavigationSubsystem;
import frc.robot.Subsystem.ScoringSubsystem.ScoringSubsystem;
import frc.robot.Subsystem.VisionSubsystem.VisionSubsystem;


import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // Primary Subsystems that make up the major robot functions
  private DriveSubsystem    driveSubsystem;
  private ClimberSubsystem  climberSubsystem;
  private ScoringSubsystem  scoringSubsystem;

  // Support Subsystem that supplement the major robot functions
  private NavigationSubsystem navigationSubsystem;
  private VisionSubsystem     visionSubsystem;
  private LightingSubsystem   lightingSubsystem;


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
  }

  /**
   * This function initializes the robot each time disabled is entered
   * Only place items in here that need to be initialized at every
   * mode transition (don't put items here that only need to be initialized
   * once at power-up; use robotInit for that!)
   */
  @Override
  public void disabledInit() {

  }

  /**
   * This functions runs on the timed robot period.
   * Put things in here that need to be done every cycle, but only when disabled
   * If you need something to be done in any mode, then use robotPeriodic
   */
  @Override
  public void disabledPeriodic() {

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
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
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
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
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
  }
}
