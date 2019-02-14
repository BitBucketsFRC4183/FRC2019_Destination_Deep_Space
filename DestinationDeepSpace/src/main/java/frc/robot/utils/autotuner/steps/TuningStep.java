package frc.robot.utils.autotuner.steps;


import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.AutoTuner;
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



    protected static enum DataCollectionType { Velocity, Position };
    protected final DataCollectionType DATA_COLLECTION_TYPE; // what data this step collections
    private final ControlMode CONTROL_MODE;
    private final double      COMMAND_VALUE;



    // whether to allow for automatic stability/oscillation detection
    protected boolean autoDetection = true;



    //protected String report;





    public TuningStep(DataCollectionType dataCollectionType) {
        valueString = "";



        finishedPos = false;

        int windowSize = TunerConstants.DATA_WINDOW_SIZE;

        error_pos    = new DataWindow(windowSize);
        error_neg    = new DataWindow(windowSize);

        position_pos = new DFT_DataWindow(windowSize);
        position_neg = new DFT_DataWindow(windowSize);

        velocity_pos = new DataWindow(windowSize);
        velocity_neg = new DataWindow(windowSize);

        power_pos    = new DataWindow(windowSize);
        power_neg    = new DataWindow(windowSize);



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



        log("========== " + getClass().getName() + " TUNING REPORT ==========");
    }





    public double getValue() {
        return value;
    }





    /** Get whether or not the data is stable */
    protected boolean isStable() {
        // manual detection if automatic isn't cooperating
        if (SmartDashboard.getBoolean(TunerConstants.STABLE_KEY, false)) {
            SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false);

            return true;
        }



        if (autoDetection == false) { return false; }



        if (DATA_COLLECTION_TYPE == DataCollectionType.Velocity) {
            DataWindow velocity = (!finishedPos) ? velocity_pos : velocity_neg;
            DataWindow power    = (!finishedPos) ? power_pos    : power_neg;

            return
                velocity.isFilled() && velocity.maxDif() <= TunerConstants.VELOCITY_STABILITY_THRESHOLD_TP100MS &&
                power   .isFilled() && power   .maxDif() <= TunerConstants.POWER_STABILITY_THRESHOLD;
        }

        if (DATA_COLLECTION_TYPE == DataCollectionType.Position) {
            DataWindow position = (!finishedPos) ? position_pos : position_neg;

            return
            position.isFilled() && position.maxDif() <= TunerConstants.POSITION_STABILITY_THRESHOLD_TICKS;
        }



        return false; // gotta love Java
    }

    /** Get whether or not the data is oscillating */
    protected boolean isOscillating() {
        // TODO: implement automatic way to determine it if user chooses to use it instead
        if (SmartDashboard.getBoolean(TunerConstants.OSCILLATING_KEY, false)) {
            SmartDashboard.putBoolean(TunerConstants.OSCILLATING_KEY, false);

            return true;
        }



        if (autoDetection == false) { return false; }



        return false;
    }



    /** Put some data on the Dashboard */
    private void put(int error, int position, int velocity, double power) {
        // make sure the Dashboard adds the values while they
        // accurately represent the data
        // (Dashboard does not update if you put the same value as
        // it was before)
        double epsilon = Math.random() * 0.0001;

        SmartDashboard.putNumber(TunerConstants.ERROR_KEY,    error + epsilon);
        SmartDashboard.putNumber(TunerConstants.POSITION_KEY, position + epsilon);
        SmartDashboard.putNumber(TunerConstants.VELOCITY_KEY, velocity + epsilon);
        SmartDashboard.putNumber(TunerConstants.POWER_KEY,    power + epsilon);
    }





    protected boolean collectData() {
        String rep = "";

        if (getMotor() == null) { return false; }

        if (!finishedPos) {
            getMotor().set(CONTROL_MODE, COMMAND_VALUE);



            int error    = Math.abs(getMotor().getClosedLoopError());
            int position = getMotor().getSelectedSensorPosition();
            int velocity = getMotor().getSelectedSensorVelocity();
            double power = getMotor().getMotorOutputPercent();
            
            error_pos   .add(error);
            position_pos.add(position);
            velocity_pos.add(velocity);
            power_pos   .add(power);

            put(error, position, velocity, power);



            if (isStable()) {
                rep += "Finished collecting stable positive data\n\n";
                rep += "errors (ticks):\n"               + error_pos   .toString() + "\n";
                rep += "positions (ticks):\n"            + position_pos.toString() + "\n";
                rep += "velocities (ticks per 100ms):\n" + velocity_pos.toString() + "\n";
                rep += "power outputs (%):\n"            + power_pos   .toString() + "\n";
                rep += "\n";
                


                finishedPos = true;
            }
        } else {
            getMotor().set(CONTROL_MODE, -COMMAND_VALUE);



            int error    = Math.abs(getMotor().getClosedLoopError());
            int position = getMotor().getSelectedSensorPosition();
            int velocity = getMotor().getSelectedSensorVelocity();
            double power = getMotor().getMotorOutputPercent();
            
            error_neg   .add(error);
            position_neg.add(position);
            velocity_neg.add(velocity);
            power_neg   .add(power);

            put(error, position, velocity, power);



            if (isStable()) {
                rep += "Finished collecting stable negative data\n\n";
                rep += "errors (ticks):\n"               + error_neg   .toString() + "\n";
                rep += "positions (ticks):\n"            + position_neg.toString() + "\n";
                rep += "velocities (ticks per 100ms):\n" + velocity_neg.toString() + "\n";
                rep += "power outputs (%):\n"            + power_neg   .toString() + "\n";
                rep += "\n";



                finishedPos = false;

                log(rep);

                return true; // done collecting data
            }
        }

        log(rep);

        return false; // more data to collect still
    }



    // how to update
    public abstract boolean update();



    /*public String getReport() {
        return report;
    }

    public void writeReport() {
        File f = new File(getClass().getName() + " tuner report.txt");
        try {
            PrintWriter w = new PrintWriter(f);

            w.print(report);

            w.close();
        } catch (FileNotFoundException e) {

        }
    }*/

    protected void log(String data) {
        String console = SmartDashboard.getString("TestMode/AutoTuner/Console", "");
        SmartDashboard.putString("TestMode/AutoTuner/Console", console + "\n" + data);
    }








    /** Push DFT info to dashboard */
    public void logDFT() {
        /*for (int i = 0; i < 5; i++) { System.out.println(); }
        System.out.println(getClass().getName() + " DISCRETE FOURIER TRANSFORM DATA");
        for (int i = 0; i < 2; i++) { System.out.println(); }*/
        


        String dft = "f_pos = [";
        for (int i = 0; i < position_pos.size() - 1; i++) {
            dft += (position_pos.getFrequency(i) / 50) + ", ";
        }
        dft += position_pos.getFrequency(position_pos.size() - 1) / 50 + "] | ";

        dft += "A_pos = [";
        for (int i = 0; i < position_pos.size() - 1; i++) {
            dft += position_pos.getAmplitude(i).norm() + ", ";
        }
        dft += position_pos.getAmplitude(position_pos.size() - 1).norm() + "] | ";



        dft += "f_neg = [";
        for (int i = 0; i < position_neg.size() - 1; i++) {
            dft += (position_neg.getFrequency(i) / 50) + ", ";
        }
        dft += position_neg.getFrequency(position_neg.size() - 1) / 50 + "] | ";

        dft += "A_neg = [";
        for (int i = 0; i < position_neg.size() - 1; i++) {
            dft += position_neg.getAmplitude(i).norm() + ", ";
        }
        dft += position_neg.getAmplitude(position_neg.size() - 1).norm() + "]";



        SmartDashboard.putString("TestMode/AutoTuner/DFT", dft);
    }



    /** Standardize motor across AutoTuner and all TuningSteps */
    protected WPI_TalonSRX getMotor() {
        return AutoTuner.getMotor();
    }
}