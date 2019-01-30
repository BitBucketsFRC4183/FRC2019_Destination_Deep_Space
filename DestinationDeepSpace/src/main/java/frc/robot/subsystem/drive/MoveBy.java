/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import frc.robot.utils.CommandUtils;
import frc.robot.subsystem.drive.DriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class MoveBy extends Command
{
    private double timeout_sec;
	private double distance_inches;
	private double percent_velocity;
	private final DriveSubsystem driveSubsystem=DriveSubsystem.instance();
	
    public MoveBy(double inches, double aTimeout_sec) 
    {
    	requires(driveSubsystem);
    	
    	distance_inches = inches;
    	timeout_sec = aTimeout_sec;
    	percent_velocity = 1.0;
    }
    
    public MoveBy(double inches, double aTimeout_sec, double percent_speed) 
    {
    	requires(driveSubsystem);
    	
    	distance_inches = inches;
    	timeout_sec = aTimeout_sec;
    	percent_velocity = percent_speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	System.out.println(this.getClass().getName() + " Start" + " " + System.currentTimeMillis()/1000);
    	driveSubsystem.resetMotion();
		driveSubsystem.setMotionVelocity(percent_velocity);

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	System.out.println("Target: " + distance_inches + "\tCurrent: " + driveSubsystem.getLeftNativeUnits() + " \t" + driveSubsystem.getRightNativeUnits());
    	// Keep enforcing the current position request until we get there
    	driveSubsystem.move_inches(distance_inches);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
    	boolean timeout = (timeSinceInitialized() > timeout_sec);
    	//System.out.printf("Timeout is %s\n", timeout?"TRUE":"false");
    	
    	if (timeout || driveSubsystem.isMoveComplete(distance_inches)) 
    	{
    		driveSubsystem.setMotionVelocity(1.0);
    		return CommandUtils.stateChange(new Idle());
    	}
    	
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("MoveBy end");
    	System.out.println(this.getClass().getName() + " END" + " " + System.currentTimeMillis()/1000);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	System.out.println("MoveBy interrupted");
    	
    	end();
    }
}
