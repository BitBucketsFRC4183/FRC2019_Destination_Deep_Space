/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.climber;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.operatorinterface.OI;

public class Idle extends Command {

  private final OI oi = OI.instance();

  private final ClimberSubsystem climberSubsystem = ClimberSubsystem.instance();
  public Idle() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(climberSubsystem);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    //thePressingButtonThing();
    if (oi.armClimber()){
    
    }
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }

  public boolean armClimber()
  {
    return oi.driverControl.getRawButton(oi.ARM_CLIMBER) && oi.operatorControl.getRawButton(oi.ARM_CLIMBER);
  }	
}
