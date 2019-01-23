package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class KdStep extends TuningStep {
    private final int TARGET;

    private int sserr;


    
    public KdStep(int windowSize, WPI_TalonSRX motor, double kf, int target) {
        super(windowSize, motor);

        TARGET = target;

        value = 10 * kf;
    }

    

    public boolean update() {
        // get + and - positions
        boolean done = collectDataPosition(TARGET);

        // if done with that, get average speed at %
        if (done) {
            sserr = (pos.average() + neg.average()) / 2; // average of two (average) errors
        }

        return done;
    }
    


    public int getSteadyStateError() {
        return sserr;
    }
}