/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;
import edu.wpi.first.wpilibj.command.Command;

public class AlignLock extends Command {
  private final OI oi = OI.instance();
  private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();

  public AlignLock() 
  {
    // Use requires() here to declare subsystem dependencies
    // eg. requires(chassis);
    requires(driveSubsystem);

  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    System.out.println(this.getClass().getName() + " Start" + " " + System.currentTimeMillis()/1000);
    driveSubsystem.setAlignDrive(true);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    driveSubsystem.doAlignDrive(oi.speed(), oi.turn());
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
    if (!align)
    {
      if (lock && !turn180) 
      {
        return CommandUtils.stateChange(new DriveLock());
      }
      else if (turn180 && !lock)
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
        driveSubsystem.setAlignDrive(false);
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    end();
  }
}
