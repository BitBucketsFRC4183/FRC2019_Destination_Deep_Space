/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.CommandUtils;
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
    driveSubsystem.drive(oi.speed(), oi.turn());
  }

  // Make this return true when this Command no longer needs to run execute()
  protected boolean isFinished() 
  {
    // If more than one button is pressed resolve the conflict
    boolean lock = oi.driveLock();
    boolean align = oi.alignLock();
    boolean turn180 = (oi.quickTurn_deg() == 180.0);

    // If we are no longer requesting this command then
    // we can determine if an explicit next command is being
    // made. If it looks like the user is pressing multiple
    // buttons for a next command then we just want to return
    // to driver control until they can make up their mind

    if ( ((lock || align) ^ turn180) && (lock ^ (align || turn180)))
    {
      // Ignore drive locking and rapid turn if we are moving forward
      // in a "significant" way
      if(driveSubsystem.getVelocity_ips() < DriveConstants.LOCK_DEADBAND_IPS)
      {
        if (lock)
        {
          return CommandUtils.stateChange(new DriveLock());
        }

        // The speed may be low, but if we are already turning above
        // some threshold, just ignore the command since the driver
        // is touching the stick
        if (turn180 && (driveSubsystem.getTurnRate_dps() < DriveConstants.ALIGN_DEADBAND_DPS))
        {
          return CommandUtils.stateChange(new TurnBy(180.0,5.0));
        }
      }
      
      // The align lock (drive straight) should not be engaged if we are
      // turning rapidly
      if (driveSubsystem.getTurnRate_dps() < DriveConstants.ALIGN_DEADBAND_DPS)
      {
        if(align) 
        {
          return CommandUtils.stateChange(new AlignLock());
        }
      }
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
