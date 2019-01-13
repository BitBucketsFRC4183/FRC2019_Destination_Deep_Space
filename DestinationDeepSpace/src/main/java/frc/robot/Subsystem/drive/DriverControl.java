/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import edu.wpi.first.wpilibj.command.Command;

import frc.robot.utils.CommandUtils;
import frc.robot.Robot;
import frc.robot.operatorinterface.OI;

public class DriverControl extends Command {
  private static OI oi = OI.instance();
  private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();

  public DriverControl() 
  {
      // Use requires() here to declare subsystem dependencies
      // eg. requires(chassis);
    requires( driveSubsystem);
  }

  // Called just before this Command runs the first time
  protected void initialize() 
  {
    System.out.println(this.getClass().getName() + " Start" + " " + System.currentTimeMillis()/1000);
  }

  // Called repeatedly when this Command is scheduled to run
  protected void execute() 
  {
    driveSubsystem.arcadeDrive(oi.axisForward.get(), oi.axisTurn.get());

  }

  // Make this return true when this Command no longer needs to run execute()
  protected boolean isFinished() 
  {
    if (oi.btnDriveLock.get() || oi.sbtnShake.get())
    {
      return CommandUtils.stateChange(this, new DriveLock());
    }
    else if(oi.btnAlignLock.get()) 
    {
      return CommandUtils.stateChange(this, new AlignLock());
    }
    else if (oi.btn180.get())
    {
      return CommandUtils.stateChange(this, new TurnBy(180.0,5.0));
    } 
    
      return false;
  }

  // Called once after isFinished returns true
  protected void end() 
  {
    System.out.println(this.getClass().getName() + " END" + " " + System.currentTimeMillis()/1000);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  protected void interrupted() 
  {
    end();
  }
}
