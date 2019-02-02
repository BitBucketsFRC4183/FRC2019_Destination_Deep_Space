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
        ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();

        if (level == ScoringConstants.ScoringLevel.INVALID) {
            return false;
        }
        
        boolean switchOrientation = oi.switchOrientation();

        if ((level != ScoringConstants.ScoringLevel.NONE) ^ switchOrientation) {
            Command nextState = null;
            
            if (level != ScoringConstants.ScoringLevel.NONE) {
                nextState = new ArmLevel(level);
            } else {
                nextState = new OrientationSwitch();
            }

            return CommandUtils.stateChange(nextState);
        }

        return false;
    }
}