/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import frc.robot.Robot;
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
    driveSubsystem.doAlignDrive(oi.axisForward.get(), oi.axisTurn.get());
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    if (oi.btnDriveLock.get() || oi.sbtnShake.get())
    	{
    		return CommandUtils.stateChange(this, new DriveLock());
    	}
    	else if( ! oi.btnAlignLock.get()) 
    	{
    		return CommandUtils.stateChange(this, new DriverControl());
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
