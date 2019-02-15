package frc.robot.utils.talonutils;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.autotuner.AutoTuner;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;


public class MotorTestModes {
    private static enum TestMode { Manual, AutoTune }
    private static SendableChooser<TestMode> modeChooser = new SendableChooser<TestMode>();
    private static TestMode lastTestMode;



    // TODO: maybe have an enum of all motors instead of selecting motors by ID?
    private static WPI_TalonSRX motor;
    private static int lastMotorID = 0; // ID of last "used" motor



    public static enum EncoderType { Quad, MagEncoderRelative, MagEncoderAbsolute }
    private static SendableChooser<EncoderType> encoderChooser = new SendableChooser<EncoderType>();
    private static EncoderType lastEncoderType;



    // called from Robot.testInit
    public static void init() {
        modeChooser.setDefaultOption("Manual", TestMode.Manual);
        modeChooser.addOption("Auto Tuner", TestMode.AutoTune);

        SmartDashboard.putData("TestMode/mode", modeChooser);



        encoderChooser.setDefaultOption("Quad", EncoderType.Quad);
        encoderChooser.addOption("MagEncoderRelative", EncoderType.MagEncoderRelative);
        encoderChooser.addOption("MagEncoderAbsolute", EncoderType.MagEncoderAbsolute);

        SmartDashboard.putData(encoderChooser);



        SmartDashboard.putNumber("TestMode/Motor ID", 0);
        SmartDashboard.putNumber("TestMode/% voltage", 0);

    }



    // called from Robot.testPeriodic
    public static void periodic() {
        // get the selected motor ID
        int motorID = (int) SmartDashboard.getNumber("TestMode/Motor ID", 0);

        if (motorID == 0) {
            return;
        }



        // if it changed, update the motor to work with accordingly
        if (motorID != lastMotorID) {
            // might have been in AutoTuner mode, stop AutoTuning
            // the motor that was selected
            AutoTuner.stop();



            lastMotorID = motorID;

            motor = new WPI_TalonSRX(motorID);
            TalonUtils.initializeMotorDefaults(motor);
        }


        
        EncoderType encoderType = encoderChooser.getSelected();
        if (encoderType != lastEncoderType) {
            TalonUtils.initializeMotorDefaults(motor);

            lastEncoderType = encoderType;

            initializeEncoder();
        }




        // get the selected test mode
        TestMode mode = modeChooser.getSelected();


        // if manual,
        if (mode == TestMode.Manual) {
            // if the mode changed
            if (mode != lastTestMode) {
                AutoTuner.stop();



                lastTestMode = mode;

                // reset % voltage input to 0
                SmartDashboard.putNumber("TestMode/% voltage", 0);
            } else {
                // read % voltage input and send it to motor
                double v = SmartDashboard.getNumber("TestMode/% voltage", 0);

                motor.set(ControlMode.PercentOutput, v);
                int pos_ticks = motor.getSelectedSensorPosition();
                int abs_ticks = motor.getSensorCollection().getPulseWidthPosition() & 0xFFF;
                int vel_tickPer100ms = motor.getSelectedSensorVelocity();

                SmartDashboard.putNumber("TestMode/Voltage", motor.getMotorOutputVoltage());
                SmartDashboard.putNumber("TestMode/Encoder (ticks)", pos_ticks);
                SmartDashboard.putNumber("TestMode/Encoder (abs ticks)", abs_ticks);
                SmartDashboard.putNumber("TestMode/Encoder (ticksPer100ms)", vel_tickPer100ms);
            }
        } else {
            if (mode != lastTestMode) {
                lastTestMode = mode;

                AutoTuner.init(); // add in the AutoTuner Step chooser to the Dashboard

                AutoTuner.tune(motor); // start the process for tuning the motor
            } else {
                AutoTuner.periodic();
            }
        }
    }



    public static void initializeEncoder() {
        EncoderType encoderType = encoderChooser.getSelected();

        switch (encoderType) {
            default: {}
            case Quad: {
                TalonUtils.initializeQuadEncoderMotor(motor);
                break;
            }
            case MagEncoderAbsolute: {
                TalonUtils.initializeMagEncoderAbsoluteMotor(motor);
                break;
            }
            case MagEncoderRelative: {
                TalonUtils.initializeMagEncoderRelativeMotor(motor);
                break;
            }
        }
    }

    public static WPI_TalonSRX getMotor() {
        return motor;
    }
}