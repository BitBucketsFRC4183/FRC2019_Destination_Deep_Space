package frc.robot.subsystem.scoring;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ScoringDiagnostics {
    private static enum ScoringDiagnosticMode { NONE, ANGLE };
    private static SendableChooser<ScoringDiagnosticMode> modeChooser;

    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();

    public static void init() {
        SmartDashboard.putNumber("ScoringSubsystem/Diagnostics/Angle", 0);
    }



    public static void periodic() {
        double angle = SmartDashboard.getNumber("ScoringSubsystem/Diagnostics/Angle", 0);
        
        scoringSubsystem.directArmTo(angle, true);
    }
}