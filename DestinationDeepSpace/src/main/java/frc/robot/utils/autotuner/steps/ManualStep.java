package frc.robot.utils.autotuner.steps;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.AutoTuner;



public class ManualStep extends TuningStep {
    private static final String PREFIX = "TestMode/AutoTuner/Manual/";



    public ManualStep() {
        super(DataCollectionType.Position);



        SmartDashboard.putNumber(PREFIX + "kF",    0);
        SmartDashboard.putNumber(PREFIX + "kP",    0);
        SmartDashboard.putNumber(PREFIX + "kD",    0);
        SmartDashboard.putNumber(PREFIX + "IZone", 0);
        SmartDashboard.putNumber(PREFIX + "kI",    0);

        AutoTuner.setKf(0);
        AutoTuner.setKp(0);
        AutoTuner.setKd(0);
        AutoTuner.setIZone(0);
        AutoTuner.setKi(0);



        autoDetection = false;
    }



    public boolean update() {
        boolean done = collectData();

        if (done) {
            double kf =       SmartDashboard.getNumber(PREFIX + "kF", 0);
            double kp =       SmartDashboard.getNumber(PREFIX + "kP", 0);
            double kd =       SmartDashboard.getNumber(PREFIX + "kD", 0);
            int iZone = (int) SmartDashboard.getNumber(PREFIX + "kF", 0);
            double ki =       SmartDashboard.getNumber(PREFIX + "kI", 0);

            AutoTuner.setKf(kf);
            AutoTuner.setKp(kp);
            AutoTuner.setKd(kd);
            AutoTuner.setIZone(iZone);
            AutoTuner.setKi(ki);
        }

        return false; // exit using AutoTuner EStop
    }
}