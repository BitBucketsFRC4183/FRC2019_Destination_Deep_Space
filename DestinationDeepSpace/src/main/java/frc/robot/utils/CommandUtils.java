package frc.robot.utils;

import frc.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class CommandUtils {
	
	/**
	 * In a Command, when performing a state transition,
	 * call this function rather than doing nextCommand.start() directly.
	 * Specifically: in isFinished(), check your events, and if a state transition 
	 * is indicated, do "return CommandUtils.stateChange( nextState)".
	 * 
	 * This function always returns true so your isFinished() will return true,
	 * which is correct because you're leaving the state.
	 * 
	 * @param next  The Command that represents the next state
	 */
	public static boolean stateChange( Command fromState, Command toState) {
		
		// When Robot Disabled, only Idle states run
		// (because Idle's are the subsystem's default states).
		// Only allow transition out of Idle when in either
		// Teleop or Autonomous.
		// (Mode Test is currently pretty useless). 
		
		if( Robot.runMode != Robot.RunMode.DISABLED) {
			
			toState.start();
			return true;
		}
		else
			return false;
	}
	
	public static boolean autoStateChange(Command fromState, Command toState) 
	{
		return (Robot.runMode == Robot.RunMode.AUTO)?true:stateChange(fromState, toState);
		
	}

}
