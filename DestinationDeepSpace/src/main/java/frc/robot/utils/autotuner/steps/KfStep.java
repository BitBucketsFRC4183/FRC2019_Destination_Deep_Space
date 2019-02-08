package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * forward/reverse speed sample for Kf = (%v * 1023)/tp100
 * Where
 *     %v is percent of full power (ideally 100%)
 *     tp100 is ticks per 100 ms
 */
public class KfStep extends TuningStep {
    private int tp100;



    public KfStep(int windowSize, WPI_TalonSRX motor) {
        super(windowSize, motor, DataCollectionType.Velocity);
    }



    public boolean update() {
        // get + and - velocities
        boolean done = collectData();

        // if done with that, get average speed at %
        if (done) {
            report += "average positive velocity: "     + velocity_pos.average() + " ticks per 100ms \n";
            report += "average positive power output: " + power_pos   .average() + "% \n";
            report += "\n";
            report += "average negative velocity: "     + velocity_neg.average() + " ticks per 100ms \n";
            report += "average negative power output: " + power_neg   .average() + "% \n";
            report += "\n";


            // avg of two speeds / avg of two power %s
            tp100 = (int) ((velocity_pos.average() - velocity_neg.average()) / (power_pos.average() - power_neg.average()));
            report += "linearized maximum velocity: " + tp100 + " ticks per 100ms \n\n";



            // calculate kf
            value = 1023.0 / tp100;
            valueString = value + "";
            report += "kF: " + value;
        }

        return done;
    }
    


    public int getTp100() {
        return tp100;
    }
}