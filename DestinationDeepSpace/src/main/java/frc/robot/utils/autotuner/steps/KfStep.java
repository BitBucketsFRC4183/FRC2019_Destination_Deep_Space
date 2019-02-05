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
        boolean done = collectDataVelocity();

        // if done with that, get average speed at %
        if (done) {
            // avg of two speeds / avg of two power %s
            tp100 = (int) ((pos.average() - neg.average()) / (power_pos.average() - power_neg.average()));

            // calculate kf
            value = 1023.0 / tp100;
        }

        return done;
    }
    


    public int getTp100() {
        return tp100;
    }
}