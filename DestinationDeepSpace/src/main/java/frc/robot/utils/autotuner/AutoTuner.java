package frc.robot.utils.autotuner;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import frc.robot.utils.talonutils.MotorTestModes;

import frc.robot.utils.autotuner.steps.TuningStep;
import frc.robot.utils.autotuner.steps.KfStep;
import frc.robot.utils.autotuner.steps.CruiseStep;
import frc.robot.utils.autotuner.steps.KpStep;
import frc.robot.utils.autotuner.steps.KiStep;
import frc.robot.utils.autotuner.steps.KdStep;
import frc.robot.utils.autotuner.steps.ManualStep;



public class AutoTuner {
    // if you think about it enough, the Step really do just be a tuning state machine
    private static enum Step {
        None (""),
        Kf ("Tell if the velocity and power output datas are stable"),
        Cruise ("Tell if the position data is stable"),
        Kp ("Tell if the position data is stable and oscillating"),
        Kd ("Tell if the position data is stable"),
        Ki ("Tell if the position data is stable and oscillating"),
        Manual ("");



        private final String INSTRUCTIONS;
        Step(String instructions) {
            INSTRUCTIONS = instructions;
        }

        public String getInstructions() {
            return INSTRUCTIONS;
        }
    }





    // select the tuning step
    // 
    // IMPORTANT: tuning will not start until you deselect it
    // This is because it would go back to the same step
    //     if you had it selected still.
    // Ideally we would want to deselect a step upon user
    //     request to tune a constant, but it doesn't let
    //     you change the selection.
    private static SendableChooser<Step> stepSelector;
    // Once in a step, there are two "parts" - init and periodic
    // init just does the "first part" of each step
    //     this could be setting up the tuner given previous
    //     calculated constants. It can even give certain
    //     constants to the motor to give it the correct
    //     configuration for data collection
    // periodic does the "data collection" of each step
    //     and moves back to None. Once periodic is done
    //     repeat is set back to false to make sure the next
    //     step is properly initialized
    private static boolean repeat = false;



    private static Step step = Step.None;
    private static WPI_TalonSRX motor; // current motor for tuning
    private static boolean stepDone = false;



    public static WPI_TalonSRX getMotor() {
        return MotorTestModes.getMotor();
    }





    public static void stop() {
        if (motor != null) {
            motor.set(ControlMode.PercentOutput, 0);
            changeStep(Step.None);
        }
    }





    public static void init() {
        SmartDashboard.putBoolean("TestMode/AutoTuner/Emergency Stop", false);


        stepSelector = new SendableChooser<Step>();

        stepSelector.setDefaultOption("", Step.None);
        stepSelector.addOption("Tune kF", Step.Kf);
        stepSelector.addOption("Tune Cruise", Step.Cruise);
        stepSelector.addOption("Tune kP", Step.Kp);
        stepSelector.addOption("Tune kD", Step.Kd);
        stepSelector.addOption("Tune kI", Step.Ki);
        stepSelector.addOption("Manual", Step.Manual);


        
        SmartDashboard.putData("TestMode/AutoTuner/step", stepSelector);



        SmartDashboard.putBoolean("TestMode/AutoTuner/Log DFT", false);
    }
    


    /** Next iteration in tuning process */
    public static void periodic() {
        // AutoTuner EStop
        if (SmartDashboard.getBoolean("TestMode/AutoTuner/Emergency Stop", false)) {
            SmartDashboard.putBoolean("TestMode/AutoTuner/Emergency Stop", false);

            stop();

            return;
        }



        // button click
        if (SmartDashboard.getBoolean("TestMode/AutoTuner/Log DFT", false)) {
            SmartDashboard.putBoolean("TestMode/AutoTuner/Log DFT", false);

            logDFT();
        }



        // as a precaution
        if (motor == null) {
            return;
        }



        // selected step on SmartDashboard
        Step selected = stepSelector.getSelected();



        // if there was a change, change the step to
        // selected step
        if (selected != step) {
            // (this goes through noneInit first)
            changeStep(selected);
        }

        // if done with the current step, don't continue
        // running anything for it
        if (stepDone) {
            return;
        }

        // at this point, step != Step.None and selected == Step.None



        // TODO: maybe find a better way to organize this?
        switch (step) {
            default: {}
            case None: { break; }
            case Kf: {
                if (!repeat) {
                    kfTuneInit();
                } else {
                    kfTunePeriodic();
                }

                break;
            }
            case Cruise: {
                if (!repeat) {
                    cruiseTuneInit();
                } else {
                    cruiseTunePeriodic();
                }

                break;
            }
            case Kp: {
                if (!repeat) {
                    kpTuneInit();
                } else {
                    kpTunePeriodic();
                }
                
                break;
            }
            case Kd: {
                if (!repeat) {
                    kdTuneInit();
                } else {
                    kdTunePeriodic();
                }
                
                break;
            }
            case Ki: {
                if (!repeat) {
                    kiTuneInit();
                } else {
                    kiTunePeriodic();
                }
                
                break;
            }
            case Manual: {
                if (!repeat) {
                    manualInit();
                } else {
                    manualPeriodic();
                }
            }
        }

        // do the periodic for each step now that the inits
        // are guaranteed to have been run
        repeat = true;
    }

    
    

    



    public static void tune(WPI_TalonSRX m) {
        SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false);
        SmartDashboard.putBoolean(TunerConstants.OSCILLATING_KEY, false);
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, "");

        motor = m;

        // AutoTuner shares the same motor as MotorTestModes
        // so initialize the encoder from standardized method
        // there
        MotorTestModes.initializeEncoder();

        // copied from CTRE tuning helper code

        
        //motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);
		motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, TunerConstants.kTimeoutMs);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, TunerConstants.kTimeoutMs);
        
		// Set the peak and nominal outputs
		motor.configNominalOutputForward(0, TunerConstants.kTimeoutMs);
		motor.configNominalOutputReverse(0, TunerConstants.kTimeoutMs);
		motor.configPeakOutputForward(1, TunerConstants.kTimeoutMs);
        motor.configPeakOutputReverse(-1, TunerConstants.kTimeoutMs);
        
        motor.selectProfileSlot(TunerConstants.kSlotIdx, TunerConstants.kPIDLoopIdx);
        setKf(0);
        setKp(0);
        setKi(0);
        setIZone(0);
        setKd(0);

        // Set acceleration and vcruise velocity - see documentation
		setCruise(15000);
        motor.configMotionAcceleration(6000, TunerConstants.kTimeoutMs);
        
        motor.setSelectedSensorPosition(0, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);
    }





    private static KfStep kf;
    private static CruiseStep cruise;
    private static KpStep kp;
    private static KiStep ki;
    private static KdStep kd;
    private static ManualStep manual;





    // helper functions because we don't like typing a lot
    public static void setKf(double val) {
        motor.config_kF(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    public static void setCruise(int val) {
        motor.configMotionCruiseVelocity(val, TunerConstants.kTimeoutMs);
    }

    public static void setKp(double val) {
        motor.config_kP(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    public static void setKi(double val) {
        motor.config_kI(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    public static void setIZone(int val) {
        motor.config_IntegralZone(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    public static void setKd(double val) {
        motor.config_kD(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }





    private static void changeStep(Step s) {
        noneInit(); // go through None first
        step = s;
        repeat = false; // once step is changed, next should go through init
        stepDone = false; // new step means it cannot be finished yet

        SmartDashboard.putString(TunerConstants.PROCESS_KEY, s.toString());
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, s.getInstructions());
    }




    
    private static void noneInit() {
        motor.set(ControlMode.PercentOutput, 0);
    }



    private static void kfTuneInit() {
        kf = new KfStep();
    }

    private static void kfTunePeriodic() {
        stepDone = kf.update();
        // put the power output to the Dashboard to make sure its also stable

        SmartDashboard.putNumber("TestMode/AutoTuner/kf", kf.getValue());
    }



    private static void cruiseTuneInit() {
        if (kf == null) {
            // The step is not actually done, but it cannot
            // start correctly until kf has been run first.
            // By setting stepDone to true, the periodic
            // AutoTuner loop won't continue by running this
            // step's periodic, ensuring that it is not run
            // until kf has been gone through first.
            stepDone = true;

            // don't continue trying to initialize the step
            // until the proper conditions have been met
            return;
        }



        motor.setSelectedSensorPosition(0, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);

        cruise = new CruiseStep(kf.getTp100());

        /*
         * IMPORTANT: we have to command     some    position to the motor at this point
         * At first, we weren't. Then, when we set the cruise velocity. However,
         * previously, the motor was in percent output mode from kF.
         * So, upon setting the motor to MotionMagic in cruiseTunePeriodic(), either
         *     1) there was no cruise velocity set at all because it doesn't make sense
         *         to have one in velocity mode (this one is more likely)
         *     or
         *     2) a cruise velocity was set, but when a new control mode was given, all
         *         control settings were reset, so we had the default cruise velocity
         *         which is just 0
         * 
         * Since we just zeroed the sensor, we might as well command it to 0 so
         * it will "reach the target position" even with a cruise velocity of 0.
         * 
         * Past this point we won't have to deal with this again because all
         * other steps are Position steps that require MotionMagic.
         */
        motor.set(ControlMode.MotionMagic, 0);

        /*
         * Now that a new control mode has been set in which is makes sense to
         * have a cruise velocity, we can give it a cruise velocity that it will
         * use for the control mode.
         */
        setCruise((int) cruise.getValue());
        SmartDashboard.putNumber("TestMode/AutoTuner/Cruise", cruise.getValue());
    }

    private static void cruiseTunePeriodic() {
        stepDone = cruise.update();
    }



    private static void kpTuneInit() {
        if (cruise == null) {
            // The step is not actually done, but it cannot
            // start correctly until cruise has been run first.
            // By setting stepDone to true, the periodic
            // AutoTuner loop won't continue by running this
            // step's periodic, ensuring that it is not run
            // until cruise has been gone through first.
            stepDone = true;

            // don't continue trying to initialize the step
            // until the proper conditions have been met
            return;
        }



        kp = new KpStep(cruise.getTickError());

        setKp(kp.getValue());
    }

    private static void kpTunePeriodic() {
        stepDone = kp.update();

        SmartDashboard.putNumber("TestMode/AutoTuner/kp", kp.getValue());

        setKp(kp.getValue());
    }



    private static void kdTuneInit() {
        if (kp == null) {
            // The step is not actually done, but it cannot
            // start correctly until kp has been run first.
            // By setting stepDone to true, the periodic
            // AutoTuner loop won't continue by running this
            // step's periodic, ensuring that it is not run
            // until kp has been gone through first.
            stepDone = true;

            // don't continue trying to initialize the step
            // until the proper conditions have been met
            return;
        }



        kd = new KdStep(kp.getValue());

        setKd(kd.getValue());
    }

    private static void kdTunePeriodic() {
        stepDone = kd.update();

        SmartDashboard.putNumber("TestMode/AutoTuner/kd", kd.getValue());
    }



    private static void kiTuneInit() {
        if (kd == null) {
            // The step is not actually done, but it cannot
            // start correctly until kd has been run first.
            // By setting stepDone to true, the periodic
            // AutoTuner loop won't continue by running this
            // step's periodic, ensuring that it is not run
            // until kd has been gone through first.
            stepDone = true;

            // don't continue trying to initialize the step
            // until the proper conditions have been met
            return;
        }



        ki = new KiStep(kd.getSteadyStateError());

        setIZone(ki.getIntegralZone());
        setKi(ki.getValue()); // initial guess

        step = Step.Ki;
    }

    private static void kiTunePeriodic() {
        stepDone = ki.update(); // initial ki value

        SmartDashboard.putNumber("TestMode/AutoTuner/ki", ki.getValue());

        setKi(ki.getValue());
    }



    private static void manualInit() {
        manual = new ManualStep();
    }

    private static void manualPeriodic() {
        manual.update();
    }









    private static void logDFT() {
        TuningStep s = kf;

        if (step == Step.Cruise) { s = cruise; }
        if (step == Step.Kp) { s = kp; }
        if (step == Step.Kd) { s = kd; }
        if (step == Step.Ki) { s = ki; }

        s.logDFT();
    }



    // TODO: written explanation of how it works
    // TODO: run some tests
    // TODO: write report to file
}