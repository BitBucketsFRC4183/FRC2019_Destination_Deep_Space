package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * Estimate initial Kd
 *     Kd = 10 * Kp
 *     Command another +/- R rotations
 *     Test for oscilation and overshoot
 *     Test for steady state error (sserr)
 */
public class KdStep extends TuningStep {
    private final int TARGET;

    private int sserr;


    
    public KdStep(int windowSize, WPI_TalonSRX motor, double kf, int target) {
        super(windowSize, motor, DataCollectionType.Position);

        TARGET = target;

        value = 10 * kf;
    }

    

    public boolean update() {
        // get + and - positions
        boolean done = collectDataPosition(TARGET);

        // if done with that, get average speed at %
        if (done) {
            sserr = (int) ((pos.average() + neg.average()) / 2); // average of two (average) errors
        }

        return done;
    }
    


    public int getSteadyStateError() {
        return sserr;
    }
}