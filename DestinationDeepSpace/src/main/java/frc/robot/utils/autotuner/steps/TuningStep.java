package frc.robot.utils.autotuner.steps;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.DataWindow;
import frc.robot.utils.autotuner.TunerConstants;



public abstract class TuningStep {
    protected double value; // current value of tuning constant

    protected boolean finishedPos; // has it finished collecting all positive data values?
    protected DataWindow pos; // positive data values
    protected DataWindow neg; // negative data values

    protected final WPI_TalonSRX MOTOR;



    /**
     * @param windowSize amount of +/- data to collect
     * @param motor
     * @param mode either MotionMagic or PercentOutput
     */
    public TuningStep(int windowSize, WPI_TalonSRX motor) {
        finishedPos = false;
        pos = new DataWindow(windowSize);
        neg = new DataWindow(windowSize);

        MOTOR = motor;
    }



    public double getValue() {
        return value;
    }





    /** Get whether or not the data is stable */
    protected static boolean isStable() {
        // TODO: implement automatic way to determine it if user chooses to use it instead
        if (SmartDashboard.getBoolean(TunerConstants.STABLE_KEY, false)) {
            SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false); // reset it for next data
            
            return true;
        }

        return false;
    }

    /** Get whether or not the data is oscillating */
    protected static boolean isOscillating() {
        // TODO: implement automatic way to determine it if user chooses to use it instead
        if (SmartDashboard.getBoolean(TunerConstants.OSCILLATING_KEY, false)) {
            SmartDashboard.putBoolean(TunerConstants.OSCILLATING_KEY, false);

            return true;
        }

        return false;
    }



    /** Put some data on the Dashboard */
    private static void put(int val) {
        SmartDashboard.putNumber(TunerConstants.DATA_KEY, val);
    }





    /**
     * @param target target position to rotate motors to
     * @param tolerance tolerance on data differences until data is stable
     * 
     * @return true if collected both positive and negative data, false otherwise
     */
    public boolean collectDataPosition(int target) {
        if (!finishedPos) {
            MOTOR.set(ControlMode.MotionMagic, target);

            int data = MOTOR.getClosedLoopError(); // get position
            put(data);
            data = Math.abs(data); // make sure error is positive

            pos.add(data); // add to + input data

            if (isStable()) {
                finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -target);

            int data = MOTOR.getClosedLoopError(); // get position
            put(data);
            data = Math.abs(data); // make sure error is positive

            neg.add(data); // add to - input data

            if (isStable()) {
                finishedPos = false;

                return true; // dont collecting data
            }
        }

        return false; // more data to collect still
    }



    /**
     * @param tolerance tolerance on data differences until data is stable
     * 
     * @return true if collected both positive and negative data, false otherwise
     */
    public boolean collectDataVelocity() {
        if (!finishedPos) {
            MOTOR.set(ControlMode.PercentOutput, 1.0);

            int data = MOTOR.getSelectedSensorVelocity(); // get velocity
            put(data);
            pos.add(data); // add to + input data


            if (isStable()) {
                finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.PercentOutput, -1.0);

            int data = MOTOR.getSelectedSensorVelocity(); // get velocity
            put(data);
            neg.add(data); // add to - input data

            // enough stable data
            if (isStable()) {
                // finished collecting data, set finishedPos to false
                // in case more iterations of data collecting need
                // to be run (like until it is oscillating)
                finishedPos = false;

                return true; // done collecting data
            }
        }

        return false; // more data to collect still
    }



    // how to update
    public abstract boolean update();
}