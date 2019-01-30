package frc.robot.utils;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class CommandUtils {
	
	private static DriverStation ds = DriverStation.getInstance();
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
	public static boolean stateChange(Command toState) {
		
		// When Robot Disabled, only Idle states run.
		// Idles are the subsystems' default states that we normally
		// force manually; we avoid using the WPI default command
		// handling because it has this habit of starting when we
		// forget an opqaue transition inside the WPI context.
		//
		// Only allow transition out of Idle when in either
		// Teleop or Autonomous or Test (if we decide to process
		// commands in test mode)
		if (ds.isAutonomous() || ds.isTest())
		{
			// In auto or test, just return at completion
			// but don't transition automatically
			// In auto modes we will transition using a CommandGroup
			// or as directed by more sophisticated logic of
			// an AutonomousSubsystem
			return true;
		}
		else if( ! ds.isDisabled() ) 
		{
			// In teleop we enforce some automatic
			// transitions to maintain ordered states
			// when operator inputs could be harmful due
			// to mashing of controls or other states
			// that are not observable by the operator
			toState.start();
			return true;
		}
		else
		{
			return false;
		}
	}

}
