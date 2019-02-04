package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class CruiseStep extends TuningStep {
    private final int TARGET;

    private int terr;


    
    public CruiseStep(int windowSize, WPI_TalonSRX motor, int kf_tp100, int target) {
        super(windowSize, motor,DataCollectionType.Position);

        value = (int) (0.75 * kf_tp100);
        TARGET = target;
    }

    

    public boolean update() {
        // get + and - positions
        boolean done = collectDataPosition(TARGET);

        // if done with that, get average speed at %
        if (done) {
            terr = (int) ((pos.average() + neg.average()) / 2); // average of two (average) errors
        }

        return done;
    }

    public int getTickError() {
        return terr;
    }
}