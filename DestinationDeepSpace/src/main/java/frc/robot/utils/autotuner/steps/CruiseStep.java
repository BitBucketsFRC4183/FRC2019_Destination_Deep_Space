package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * identification of initial cruise speed (85% of max)
 *     Cs = 0.85 * tp100
 * invocation of motion magic mode and command some rotations, R (e.g., 10)
 *     Make note of the error in ticks (terr)
 */
public class CruiseStep extends TuningStep {
    private int terr;


    
    public CruiseStep(int windowSize, WPI_TalonSRX motor, int kf_tp100) {
        super(windowSize, motor,DataCollectionType.Position);

        value = (int) (0.75 * kf_tp100);
        valueString = value + "";

        report += "cruise velocity: " + value + " ticks per 100ms\n\n";
        report += "Getting error with kF and cruise velocity...";
    }

    

    public boolean update() {
        // get + and - positions
        boolean done = collectData();

        // if done with that, get average speed at %
        if (done) {
            report += "average positive error: " + error_pos.average() + " ticks \n";
            report += "average negative error: " + error_neg.average() + " ticks \n\n";

            terr = (int) ((error_pos.average() + error_neg.average()) / 2); // average of two (average) errors

            report += "average total error: " + terr + " ticks";
        }

        return done;
    }

    public int getTickError() {
        return terr;
    }
}