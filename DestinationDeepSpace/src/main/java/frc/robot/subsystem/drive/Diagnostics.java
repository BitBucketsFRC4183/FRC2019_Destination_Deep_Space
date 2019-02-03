/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.drive;

import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;


/**
 * Add your docs here.
 */
public class Diagnostics extends Command {
    private final DriveSubsystem driveSubsystem = DriveSubsystem.instance();

    private int diagInitLoops;
	
    public Diagnostics() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(driveSubsystem);
    	diagInitLoops = 0;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	driveSubsystem.diagnosticsInitialize();
    	System.out.println("Entering Drive Diagnostics");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	System.out.println("Executing Drive Diagnostics");
    	if(diagInitLoops < driveSubsystem.DIAG_LOOPS_RUN) {
    		driveSubsystem.diagnosticsPeriodic();
    		diagInitLoops++;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {

    	if(diagInitLoops >= driveSubsystem.DIAG_LOOPS_RUN) {
    		System.out.println("Checking Drive Diagnostics");
    		driveSubsystem.diagnosticsCheck();
    		return CommandUtils.stateChange(new Idle());
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
