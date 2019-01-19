package frc.robot.subsystem.scoring;

import edu.wpi.first.wpilibj.command.Command;

public class MoveArm extends Command {
    private ScoringSubsystem sss;
    private final double DEG;

    public MoveArm(double deg) {
        DEG = deg;
        sss = ScoringSubsystem.instance();
        requires(sss);
    }

    protected void execute() {
        sss.directArmTo(DEG, true);
    }

    protected boolean isFinished() {
        return false;
    }
}