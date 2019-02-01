package frc.robot.subsystem.scoring;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

public class ScoringIdle extends Command {
    private static OI oi = OI.instance();
    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();

    public ScoringIdle() {
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
        boolean hp = oi.hp();
        boolean ground = oi.ground();
        boolean bCargo = oi.bCargo();
        boolean bLoadingStation = oi.bLoadingStation();
        boolean bRocket1 = oi.bRocket1();
        boolean switchOrientation = oi.switchOrientation();

        if (hp ^ ground ^ bCargo ^ bLoadingStation ^ bRocket1 ^ switchOrientation) {
            Command nextState = null;

            ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();
            if (level != null ^ switchOrientation) {
                if (level != null) {
                    nextState = new ArmLevel(level);
                } else {
                    nextState = new OrientationSwitch();
                }
            }

            return CommandUtils.stateChange(nextState);
        }

        return false;
    }
}