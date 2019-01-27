package frc.robot.utils.talonutils;



import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.autotuner.AutoTuner;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class MotorTestModes {
    public static enum TestMode { Manual, AutoTune }



    // TODO: maybe have an enum of all motors instead of selecting motors by ID?
    private static WPI_TalonSRX lastMotor;
    private static int lastMotorID = 0; // ID of last "used" motor
    private static TestMode lastTestMode;



    private static SendableChooser<TestMode> modeChooser = new SendableChooser<TestMode>();



    // called from Robot.testInit
    public static void init() {
        modeChooser.setDefaultOption("Manual", TestMode.Manual);
        modeChooser.addOption("Auto Tuner", TestMode.AutoTune);


        SmartDashboard.putData("Test Mode", modeChooser);
        SmartDashboard.putNumber("Motor ID", 0);
    }



    // called from Robot.testPeriodic
    public static void periodic() {
        // get the selected motor ID
        int motorID = (int) SmartDashboard.getNumber("Motor ID", 0);

        if (motorID == 0) {
            return;
        }

        // if it changed, update the motor to work with accordingly
        if (motorID != lastMotorID) {
            lastMotorID = motorID;

            lastMotor = new WPI_TalonSRX(motorID);
        }


        // get the selected test mode
        TestMode mode = modeChooser.getSelected();


        // if manual,
        if (mode == TestMode.Manual) {
            // if the mode changed
            if (mode != lastTestMode) {
                lastTestMode = mode;

                // reset % voltage input to 0
                SmartDashboard.putNumber("% voltage", 0);
            } else {
                // read % voltage input and send it to motor
                double v = SmartDashboard.getNumber("% voltage", 0);

                lastMotor.set(ControlMode.PercentOutput, v);
            }
        } else {
            if (mode != lastTestMode) {
                lastTestMode = mode;

                AutoTuner.init(); // add in the AutoTuner Step chooser to the Dashboard

                AutoTuner.tune(lastMotor); // start the process for tuning the motor
            } else {
                AutoTuner.periodic();
            }
        }
    }
}