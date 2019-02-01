/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

public class TurnBy extends Command {
    private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();

    private double timeout_sec;
	private double angle_deg;
	
    public TurnBy(double degrees, double aTimeout_sec) 
    {
    	requires(driveSubsystem);
    	
    	angle_deg = degrees;
    	
    	timeout_sec = aTimeout_sec;
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	System.out.println(this.getClass().getName() + " Start" + " " + System.currentTimeMillis()/1000);
    	driveSubsystem.resetMotion();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	System.out.println("Target: " + angle_deg + "\tCurrent: " + driveSubsystem.getLeftNativeUnits() + " \t" + driveSubsystem.getRightNativeUnits());
    	driveSubsystem.turn_degrees(angle_deg);

    	// Keep enforcing the current position request until we get there
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
    	boolean timeout = (timeSinceInitialized() > timeout_sec);
    	
    	if (timeout || driveSubsystem.isTurnComplete(angle_deg)) 
    	{
    		return CommandUtils.stateChange(new Idle());
    		
    	}
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println(this.getClass().getName() + " END" + " " + System.currentTimeMillis()/1000);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
