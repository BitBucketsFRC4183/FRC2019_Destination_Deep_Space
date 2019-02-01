package frc.robot.subsystem.drive;

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

import frc.robot.utils.CommandUtils;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystem.drive.DriveConstants;

public class Idle extends Command {
	private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();
	
	static double lastTestModeTime_sec = 0.0;
	
	// Toggle sign on each one
	double moveDistance_inches = -2.0 * DriveConstants.WHEEL_CIRCUMFERENCE_INCHES;
	double turnAngle_deg = -45.0;

	private DriverStation ds = DriverStation.getInstance();
	
    public Idle() 
    {
        // Use requires() here to declare subsystem dependencies
    	requires(driveSubsystem);
    	setRunWhenDisabled(true);  // Idle state needs this!
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	System.out.println(this.getClass().getName() + " DRIVE START" + " " + System.currentTimeMillis()/1000);
    	driveSubsystem.disable();

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {    	
    	// Getting into test mode requires 2 conditions to avoid inadvertent activation
    	// of future other test modes
    	if( ds.isTest())
    	{
    		// Throttle the test mode to prevent it from triggering more
    		// often than is necessary; serves a couple of purposes.
    		// a) some tests could be destructive if running over and over quickly
    		// b) running some tests back-to-back can make it hard to see what is happening
    		// A changeable default test period of 2 seconds provides a reasonable chance to see
    		// what is happening
    		if (driveSubsystem.getDiagnosticsEnabled())	// Diagnostics can only be run once per reset cycle
    		{
    				// Don't run repeatedly because it could be harmful
    			return CommandUtils.stateChange(new Diagnostics());
    		}
		}

		// NOTE: isOperatorControl always returns true in Snobot. Need to know if
		// that is true in real DS. For now the solution is to test for the other
		// two states together
	  
		if( ! ds.isAutonomous() && !ds.isDisabled()) 
		{
			return CommandUtils.stateChange(new DriverControl());
		}

		return false;
    }

    // Called once after isFinished returns true
    protected void end() 
    {    
    	System.out.println(this.getClass().getName() + " DRIVE END" + " " + System.currentTimeMillis()/1000);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	end();
    }
}
