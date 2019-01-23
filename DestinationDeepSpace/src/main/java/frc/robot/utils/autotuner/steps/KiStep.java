package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;



public class KiStep extends TuningStep {
    private final int TARGET;
    private final double KI0;
    private final int IZONE;

    private boolean hasOscillated = false;
    private double exponent = 0;
    private double exponentChange = 1;



    public KiStep(int windowSize, WPI_TalonSRX motor, int kd_sserr, int target) {
        super(windowSize, motor);

        IZONE = (int) (2.5 * kd_sserr);

        TARGET = target;
        KI0 = 0.001;

        value = KI0;
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

                value = KI0 * Math.pow(2, exponent);
            }
        }

        return false; // keep going
    }

    public int getIntegralZone() {
        return IZONE;
    }
}