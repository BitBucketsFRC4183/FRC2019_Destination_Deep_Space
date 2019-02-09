package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



/**
 * Compute initial Kp
 *     Kp = (0.1 * 1023)/terr
 *     Command another +/- R rotations
 *     Test for oscillation or manually test for backdrive
 *     Keep doubling Kp until oscillations start and then back off a little
 *     Make note of overshoot
 */
public class KpStep extends TuningStep {
    private final double KP0;

    private boolean hasOscillated = false;
    private double lastMultiplier = 1;
    private double multiplierChange = 0.5;



    public KpStep(int windowSize, WPI_TalonSRX motor, int cruise_terr) {
        super(windowSize, motor, DataCollectionType.Position);

        KP0 = ((int) (1000000 * (0.1 * 1023) / cruise_terr)) / 1000000.0; // truncate to 6 decimal places
        report += "initial kP guess: " + KP0 + "\n\n";

        value = KP0;
        valueString = KP0 + "";
    }



    public boolean update() {
        // get + and - positions
        boolean done = collectData();

        if (done) {
            if (!isOscillating()) {
                // hasn't reached oscillations yet
                if (hasOscillated == false) {
                    report += "Data is not oscillating, doubling kP...";

                    // double kP
                    value *= 2;

                    // the problem we have is that we want to add in *2
                    // but if multiplying kP by 2 produces instability,
                    // it will be annoying to get rid of the *2 in
                    // the value string.
                    // However, lastMultiplier = 1 means that this is the first time
                    // data is being processed. So, if data has been processed already
                    // and it is not oscillating. its safe to add in the *2
                    // from the previous iteration (NOT THIS ONE - THIS WILL BE
                    // ADDED IN THE NEXT ONE BECAUSE INSTABILITY MAY ARISE)
                    if (lastMultiplier != 1) {
                        valueString += "*" + 2;
                    } else {
                        // indicate data has been processed
                        lastMultiplier = 2;
                    }
                // stopped oscillating, good!
                } else {
                    // KP0*2*2*2...*lastMultiplier
                    valueString += "*" + lastMultiplier;

                    report += "Data has stopped oscillating\n\n";
                    report += "kP: " + valueString + " = " + value;


                    return true; // your job is done here
                }
            } else {
                hasOscillated = true;

                // undo effects of previous multiplication
                value /= lastMultiplier;

                // remove the change from the multiplier
                lastMultiplier -= multiplierChange;
                // half the change
                // 0.5 + 0.25 + 0.125 + ... = 1 after all
                multiplierChange /= 2;

                // new multiplier factor
                value *= lastMultiplier;

                report += "Data is oscillating, backing off...\n";
                report += "kP: " + (valueString + "*" + lastMultiplier) + " = " + value + "\n\n";
            }
        }

        return false; // keep going
    }
}