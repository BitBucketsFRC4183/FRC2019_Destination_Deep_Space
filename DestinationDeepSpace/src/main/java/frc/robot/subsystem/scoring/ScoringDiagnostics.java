package frc.robot.subsystem.scoring;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ScoringDiagnostics {
    private static enum ScoringDiagnosticMode { NONE, ANGLE };
    private static SendableChooser<ScoringDiagnosticMode> modeChooser;

    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();

    public static void init() {
        modeChooser = new SendableChooser<ScoringDiagnosticMode>();

        modeChooser.setDefaultOption("None", ScoringDiagnosticMode.NONE);
        modeChooser.addOption("Angle", ScoringDiagnosticMode.ANGLE);

        SmartDashboard.putData("Scoring Subsystem Diagnostic Mode", modeChooser);
    }



    public static void periodic() {
        ScoringDiagnosticMode mode = modeChooser.getSelected();

        scoringSubsystem.directArmTo(30, true);
    }
}