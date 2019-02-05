package frc.robot.subsystem.scoring;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

public class Idle extends Command {
    private static OI oi = OI.instance();
    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();

    public Idle() {
        requires(scoringSubsystem);
        setRunWhenDisabled(true); // Idle command
    }

    @Override
    protected void initialize() {
        System.out.println(this.getClass().getName() + " SCORING START" + " " + System.currentTimeMillis()/1000);
        scoringSubsystem.disable();
    }

    @Override
    protected boolean isFinished() {
        boolean forceIdle = oi.operatorIdle();

        if (forceIdle) {
            return false;
        }


        
		// get selected level on joystick (NONE if none selected, INVALID if multiple selected)
        ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();

		// if two levels are selected, there is uncertainty in what to do
		// so don't change the state
        if (level == ScoringConstants.ScoringLevel.INVALID) {
            return false;
        }
        
        boolean switchOrientation = oi.switchOrientation();

        // if either a level is selected XOR the switch orientation button is pressed
        // (we want changing the level and orientation to be mutually exclusive)
        if ((level != ScoringConstants.ScoringLevel.NONE) ^ switchOrientation) {
			// change to ArmLevel state corresponding to selected level
            if (level != ScoringConstants.ScoringLevel.NONE) {
				return CommandUtils.stateChange(new ArmLevel(level));
			}

			// change to OrientationSwitch state
			if (switchOrientation) {
				return CommandUtils.stateChange(new OrientationSwitch());
            }
        }

        return false;
    }
}