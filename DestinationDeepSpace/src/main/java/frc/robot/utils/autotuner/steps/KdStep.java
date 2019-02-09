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
    private int sserr;


    
    public KdStep(int windowSize, WPI_TalonSRX motor, double kf) {
        super(windowSize, motor, DataCollectionType.Position);

        value = 10 * kf;
        valueString = value + "";
        
        report += "kD: " + value + "\n";
        report += "Getting steady-state PDF error...\n";
    }



    public boolean update() {
        // get + and - positions
        boolean done = collectData();

        // if done with that, get average speed at %
        if (done) {
            report += "average positive error: " + error_pos.average() + " ticks \n";
            report += "average negative error: " + error_neg.average() + " ticks \n\n";

            sserr = (int) ((error_pos.average() + error_neg.average()) / 2); // average of two (average) errors

            report += "steady-state error: " + sserr + " ticks";
        }

        return done;
    }
    


    public int getSteadyStateError() {
        return sserr;
    }
}