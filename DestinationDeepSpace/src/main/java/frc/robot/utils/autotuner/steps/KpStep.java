package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



public class KpStep extends TuningStep {
    private final int TARGET;
    private final double KP0;

    private boolean hasOscillated = false;
    private double exponent = 0;
    private double exponentChange = 1;



    public KpStep(int windowSize, WPI_TalonSRX motor, int cruise_terr, int target) {
        super(windowSize, motor);

        TARGET = target;
        KP0 = (0.1 * 1023) / cruise_terr;

        value = KP0;
    }



    public boolean update() {
        // get + and - positions
        boolean done = collectDataPosition(TARGET);

        if (done) {
            if (!isOscillating()) {
                // hasn't reached oscillations yet
                if (hasOscillated == false) {
                    exponent += 1;
                // stopped oscillating, good!
                } else {
                    return true; // your job is done here
                }
            } else {
                hasOscillated = true;

                // lower the exponent by decreasing amounts
                exponentChange /= 2;
                exponent -= exponentChange;

                value = KP0 * Math.pow(2, exponent);
            }
        }

        return false; // keep going
    }
}