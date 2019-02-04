package frc.robot.utils.autotuner.steps;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.DataWindow;
import frc.robot.utils.autotuner.TunerConstants;



public abstract class TuningStep {
    protected double value; // current value of tuning constant
    protected String valueString; // how to store the value in the code

    protected boolean finishedPos = false; // has it finished collecting all positive data values?
    protected DataWindow pos; // positive data values
    protected DataWindow neg; // negative data values
    protected DataWindow power_pos; // positive data power values
    protected DataWindow power_neg; // negative data power values

    protected final WPI_TalonSRX MOTOR;



    protected static enum DataCollectionType { Velocity, Position };
    protected final DataCollectionType DATA_COLLECTION_TYPE; // what data this step collections
    protected final int STABILITY_THRESHOLD;



    /**
     * @param windowSize amount of +/- data to collect
     * @param motor
     * @param mode either MotionMagic or PercentOutput
     */
    public TuningStep(int windowSize, WPI_TalonSRX motor, DataCollectionType dataCollectionType) {
        valueString = "";

        finishedPos = false;
        pos = new DataWindow(windowSize);
        neg = new DataWindow(windowSize);
        power_pos = new DataWindow(windowSize);
        power_neg = new DataWindow(windowSize);

        MOTOR = motor;

        DATA_COLLECTION_TYPE = dataCollectionType;
        
        switch (DATA_COLLECTION_TYPE) {
            default: {}
            case Velocity: {
                STABILITY_THRESHOLD = TunerConstants.VELOCITY_STABILITY_THRESHOLD_TP100MS;
                break;
            }
            case Position: {
                STABILITY_THRESHOLD = TunerConstants.POSITION_STABILITY_THRESHOLD_TICKS;
            }
        }
    }



    public double getValue() {
        return value;
    }





    /** Get whether or not the data is stable */
    protected boolean isStable() {
        boolean stable = SmartDashboard.getBoolean(TunerConstants.STABLE_KEY, false);
        // manual detection if automatic isn't cooperating
        if (stable) {
            SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false);

            return true;
        }



        if (!finishedPos) {
            return pos.isFilled() && pos.maxDif() <= STABILITY_THRESHOLD &&
            // position measurements don't require power measurements
            (DATA_COLLECTION_TYPE == DataCollectionType.Position || power_pos.maxDif() <= TunerConstants.POWER_STABILITY_THRESHOLD);
        } else {
            return
            (neg.isFilled() && neg.maxDif() <= STABILITY_THRESHOLD) &&
            // position measurements don't require power measurements
            (DATA_COLLECTION_TYPE == DataCollectionType.Position || power_neg.maxDif() <= TunerConstants.POWER_STABILITY_THRESHOLD);
        }
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
    private void put(int val, double power) {
        SmartDashboard.putNumber(TunerConstants.DATA_KEY, val);
        SmartDashboard.putNumber(TunerConstants.POWER_DATA_KEY, power);
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

            int err = MOTOR.getClosedLoopError(); // get position
            err = Math.abs(err); // make sure error is positive
            pos.add(err); // add to + input data

            double power = MOTOR.getMotorOutputPercent();
            power_pos.add(power);


            
            put(err, power);

            if (isStable()) {
                finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -target);

            int data = MOTOR.getClosedLoopError(); // get position
            data = Math.abs(data); // make sure error is positive
            neg.add(data); // add to - input data

            double power = MOTOR.getMotorOutputPercent();
            power_neg.add(power);



            put(data, power);

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
            pos.add(data); // add to + input data

            double power = MOTOR.getMotorOutputPercent();
            power_pos.add(power);



            put(data, power);

            if (isStable()) {
                finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.PercentOutput, -1.0);

            int data = MOTOR.getSelectedSensorVelocity(); // get velocity
            neg.add(data); // add to - input data

            double power = MOTOR.getMotorOutputPercent();
            power_neg.add(power);


            
            put(data, power);

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