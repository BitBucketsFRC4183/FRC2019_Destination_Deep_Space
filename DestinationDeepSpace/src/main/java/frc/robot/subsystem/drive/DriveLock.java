/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import edu.wpi.first.wpilibj.command.Command;

import frc.robot.utils.CommandUtils;
import frc.robot.operatorinterface.OI;

public class DriveLock extends Command {
  private final OI oi = OI.instance();
  private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();

  public DriveLock() {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chaSssis)
    requires(driveSubsystem);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println(this.getClass().getName() + " Start" + " " + System.currentTimeMillis()/1000);	
    driveSubsystem.resetMotion();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
      driveSubsystem.doLockDrive(0.0);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    // If more than one button is pressed resolve the conflict
    boolean lock = oi.driveLock();
    boolean align = oi.alignLock();
    boolean turn180 = (oi.quickTurn_deg() == 180.0);

    // If we are no longer requesting this command then
    // we can determine if an explicit next command is being
    // made. If it looks like the user is pressing multiple
    // buttons for a next command then we just want to return
    // to driver control until they can make up their mind

    if (!lock)
    {
      if (align && ! turn180) 
      {
        return CommandUtils.stateChange(new AlignLock());
      }
      else if (turn180 && !align)
      {
        return CommandUtils.stateChange(new TurnBy(180.0,5.0));
      }
      else
      {
        return CommandUtils.stateChange(new DriverControl());
      }
    }
    return false;
    
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    System.out.println(this.getClass().getName() + " END" + " " + System.currentTimeMillis()/1000);

  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    end();
  }

  protected double sineWave(double f_Hz) {
    return Math.sin(f_Hz * (2 * Math.PI) * (System.currentTimeMillis() / 1000.0));
  }
  
  protected double squareWave(double f_Hz){
    return ((f_Hz*( System.currentTimeMillis() / 1000.0) % 1.0 ) < 0.5 ? -1 : 1 );
  }
}
