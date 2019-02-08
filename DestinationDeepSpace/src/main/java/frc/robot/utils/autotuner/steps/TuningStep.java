package frc.robot.utils.autotuner.steps;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.DFT_DataWindow;
import frc.robot.utils.autotuner.DataWindow;
import frc.robot.utils.autotuner.TunerConstants;



public abstract class TuningStep {
    protected double value;       // current value of tuning constant
    protected String valueString; // how to store the value in the code



    protected boolean finishedPos = false; // has it finished collecting all positive data values?

    protected DataWindow     error_pos;    // positive error values
    protected DataWindow     error_neg;    // negative error values

    protected DFT_DataWindow position_pos; // position position values
    protected DFT_DataWindow position_neg; // negative position values

    protected DataWindow     velocity_pos; // positive velocity values
    protected DataWindow     velocity_neg; // negative data values

    protected DataWindow     power_pos;    // positive data power values
    protected DataWindow     power_neg;    // negative data power values



    protected final WPI_TalonSRX MOTOR;



    protected static enum DataCollectionType { Velocity, Position };
    protected final DataCollectionType DATA_COLLECTION_TYPE; // what data this step collections
    private final ControlMode CONTROL_MODE;
    private final double      COMMAND_VALUE;



    protected String report;





    /**
     * @param windowSize amount of +/- data to collect
     * @param motor
     * @param mode either MotionMagic or PercentOutput
     */
    public TuningStep(int windowSize, WPI_TalonSRX motor, DataCollectionType dataCollectionType) {
        valueString = "";



        finishedPos = false;

        error_pos    = new DataWindow(windowSize);
        error_neg    = new DataWindow(windowSize);

        position_pos = new DFT_DataWindow(windowSize);
        position_neg = new DFT_DataWindow(windowSize);

        velocity_pos = new DataWindow(windowSize);
        velocity_neg = new DataWindow(windowSize);

        power_pos    = new DataWindow(windowSize);
        power_neg    = new DataWindow(windowSize);



        MOTOR = motor;



        DATA_COLLECTION_TYPE = dataCollectionType;
        
        switch (DATA_COLLECTION_TYPE) {
            default: {}
            case Velocity: {
                CONTROL_MODE = ControlMode.PercentOutput;
                COMMAND_VALUE = 1.0;

                break;
            }
            case Position: {
                CONTROL_MODE = ControlMode.MotionMagic;
                COMMAND_VALUE = TunerConstants.TARGET;

                break;
            }
        }



        report += "========== " + getClass().getName() + " TUNING REPORT ==========";
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



        if (DATA_COLLECTION_TYPE == DataCollectionType.Velocity) {
            DataWindow velocity = (!finishedPos) ? velocity_pos : velocity_neg;
            DataWindow power    = (!finishedPos) ? power_pos    : power_neg;

            return
                velocity.isFilled() && velocity.maxDif() <= TunerConstants.VELOCITY_STABILITY_THRESHOLD_TP100MS &&
                power   .isFilled() && power   .maxDif() <= TunerConstants.POWER_STABILITY_THRESHOLD;
        }

        if (DATA_COLLECTION_TYPE == DataCollectionType.Position) {
            DataWindow error = (!finishedPos) ? error_pos : error_neg;

            return
                error.isFilled() && error.maxDif() <= TunerConstants.POSITION_STABILITY_THRESHOLD_TICKS;
        }



        return false; // gotta love Java
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
    private void put(int error, int position, int velocity, double power) {
        SmartDashboard.putNumber(TunerConstants.ERROR_KEY,    error);
        SmartDashboard.putNumber(TunerConstants.POSITION_KEY, position);
        SmartDashboard.putNumber(TunerConstants.VELOCITY_KEY, velocity);
        SmartDashboard.putNumber(TunerConstants.POWER_KEY,    power);
    }





    protected boolean collectData() {
        if (!finishedPos) {
            MOTOR.set(CONTROL_MODE, COMMAND_VALUE);



            int error    = Math.abs(MOTOR.getClosedLoopError());
            int position = MOTOR.getSelectedSensorPosition();
            int velocity = MOTOR.getSelectedSensorVelocity();
            double power = MOTOR.getMotorOutputPercent();
            
            error_pos   .add(error);
            position_pos.add(position);
            velocity_pos.add(velocity);
            power_pos   .add(power);

            put(error, position, velocity, power);



            if (isStable()) {
                report += "Finished collecting stable positive data\n\n";
                report += "errors (ticks):\n"               + error_pos   .toString() + "\n";
                report += "positions (ticks):\n"            + position_pos.toString() + "\n";
                report += "velocities (ticks per 100ms):\n" + velocity_pos.toString() + "\n";
                report += "power outputs (%):\n"            + power_pos   .toString() + "\n";
                report += "\n";
                


                finishedPos = true;
            }
        } else {
            MOTOR.set(CONTROL_MODE, -COMMAND_VALUE);



            int error    = Math.abs(MOTOR.getClosedLoopError());
            int position = MOTOR.getSelectedSensorPosition();
            int velocity = MOTOR.getSelectedSensorVelocity();
            double power = MOTOR.getMotorOutputPercent();
            
            error_neg   .add(error);
            position_neg.add(position);
            velocity_neg.add(velocity);
            power_neg   .add(power);

            put(error, position, velocity, power);



            if (isStable()) {
                report += "Finished collecting stable negative data\n\n";
                report += "errors (ticks):\n"               + error_neg   .toString() + "\n";
                report += "positions (ticks):\n"            + position_neg.toString() + "\n";
                report += "velocities (ticks per 100ms):\n" + velocity_neg.toString() + "\n";
                report += "power outputs (%):\n"            + power_neg   .toString() + "\n";
                report += "\n";



                finishedPos = false;

                return true; // done collecting data
            }
        }

        return false; // more data to collect still
    }



    // how to update
    public abstract boolean update();



    public String getReport() {
        return report;
    }








    public void logDFT() {
        for (int i = 0; i < 5; i++) { System.out.println(); }
        System.out.println(getClass().getName() + " DISCRETE FOURIER TRANSFORM DATA");
        for (int i = 0; i < 2; i++) { System.out.println(); }
        


        System.out.println("POSITIVE VALUES");
        for (int i = 0; i < position_pos.size(); i++) {
            // units of getFrequency() in 50Hz
            double f = position_pos.getFrequency(i) / 50;
            System.out.println(i + ": f = " + f + ", A = " + position_pos.getAmplitude(i).norm());
        }
        System.out.print("A = [");
        for (int i = 0; i < position_pos.size() - 1; i++) {
            System.out.println(position_pos.getAmplitude(i).norm() + ", ");
        }
        System.out.println(position_pos.getAmplitude(position_pos.size() - 1).norm() + "]");



        for (int i = 0; i < 2; i++) { System.out.println(); }


        
        System.out.println("NEGATIVE VALUES");
        for (int i = 0; i < position_neg.size(); i++) {
            // units of getFrequency() in 50Hz
            double f = position_neg.getFrequency(i) / 50;
            System.out.println(i + ": f = " + f + ", A = " + position_neg.getAmplitude(i).norm());
        }
        System.out.print("B = [");
        for (int i = 0; i < position_neg.size() - 1; i++) {
            System.out.println(position_neg.getAmplitude(i).norm() + ", ");
        }
        System.out.println(position_neg.getAmplitude(position_neg.size() - 1).norm() + "]");



        for (int i = 0; i < 5; i++) { System.out.println(); }
    }
}