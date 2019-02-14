package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



/**
 * Set I-Zone to define when Ki is needed
 *     Iz = sserr * 2.5
 *     Estimate Ki
 *     Ki = 0.001
 *     Keep doubling Ki until sserr gets sufficiently close to zero
 * 				Stop and back off if oscillations appear
 */
public class KiStep extends TuningStep {
    private final double KI0;
    private final int IZONE;

    private boolean hasOscillated = false;
    private double lastMultiplier = 1;
    private double multiplierChange = 0.5;



    public KiStep(int kd_sserr) {
        super(DataCollectionType.Position);

        IZONE = (int) (2.5 * kd_sserr);
        log("iZone: " + IZONE + "\n");

        KI0 = 0.001;
        log("initial kI guess: " + KI0 + "\n\n");

        value = KI0;
    }



    public boolean update() {
        // get + and - positions
        boolean done = collectData();

        if (done) {
            String rep = "";

            if (!isOscillating()) {
                // hasn't reached oscillations yet
                if (hasOscillated == false) {
                    rep += "Data is not oscillating, doubling kP...";

                    // double kI
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
                    // KI0*2*2*2...*lastMultiplier
                    valueString += "*" + lastMultiplier;

                    rep += "Data has stopped oscillating\n\n";
                    rep += "kI: " + valueString + " = " + value;


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

                rep += "Data is oscillating, backing off...\n";
                rep += "kI: " + (valueString + "*" + lastMultiplier) + " = " + value + "\n\n";
            }

            log(rep);
        }

        return false; // keep going
    }

    public int getIntegralZone() {
        return IZONE;
    }
}