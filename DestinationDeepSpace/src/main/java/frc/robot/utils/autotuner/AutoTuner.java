package frc.robot.utils.autotuner;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import frc.robot.utils.autotuner.steps.TuningStep;
import frc.robot.utils.autotuner.steps.KfStep;
import frc.robot.utils.autotuner.steps.CruiseStep;
import frc.robot.utils.autotuner.steps.KpStep;
import frc.robot.utils.autotuner.steps.KiStep;
import frc.robot.utils.autotuner.steps.KdStep;



public class AutoTuner {
    // if you think about it enough, the Step really do just be a tuning state machine
    private static enum Step {
        None (""),
        Kf ("Tell if the velocity and power output datas are stable"),
        Cruise ("Tell if the position data is stable"),
        Kp ("Tell if the position data is stable and oscillating"),
        Kd ("Tell if the position data is stable"),
        Ki ("Tell if the position data is stable and oscillating");



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



    public static void init() {
        stepSelector = new SendableChooser<Step>();

        stepSelector.setDefaultOption("", Step.None);
        stepSelector.addOption("Tune kF", Step.Kf);
        stepSelector.addOption("Tune Cruise", Step.Cruise);
        stepSelector.addOption("Tune kP", Step.Kp);
        stepSelector.addOption("Tune kD", Step.Kd);
        stepSelector.addOption("Tune kI", Step.Ki);


        
        SmartDashboard.putData("TestMode/AutoTuner/step", stepSelector);



        SmartDashboard.putBoolean("TestMode/AutoTuner/Log DFT", false);
    }
    


    /** Next iteration in tuning process */
    public static void periodic() {
        // button click
        if (SmartDashboard.getBoolean("TestMode/AutoTuner/Log DFT", false)) {
            SmartDashboard.putBoolean("TestMode/AutoTuner/Log DFT", false);

            logDFT();
        }



        // VERY IMPORTANT: step and selected are NOT the same
        // step is the step the AutoTuner actually is in
        // selected is the selected "requested" step on the dashboard
        //
        // Reason: if we let the step be the selected step on the dashboard,
        // you could end up with the following behavior.
        //     User wants to tune kF
        //     AutoTuner starts tuning kF
        //     AutoTuner finishes tuning kF
        //     User still has kF selected
        //     AutoTuner will now continue tuning kF
        Step selected = stepSelector.getSelected();



        // if the tuner is currently doing nothing but you want to do something,
        // get "ready" to do that "something"
        if (step == Step.None && selected != Step.None) {
            changeStep(selected);

            // make sure init is called
            repeat = false;

            // don't go further until the user deselects
            return;
        }

        // if tuner is in a step but something is selected, don't allow it to
        // continue (until deselected)
        if (step != Step.None && selected != Step.None) {
            return;
        }

        // at this point, step != Step.None and selected == Step.None



        // TODO: maybe find a better way to organize this?
        switch (step) {
            default: {}
            case None: {
                if (!repeat) {
                    //noneInit();
                }

                break;
            }
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
        }

        // do the periodic for each step now that the inits have been done
        repeat = true;
    }

    
    

    



    public static void tune(WPI_TalonSRX m) {
        SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false);
        SmartDashboard.putBoolean(TunerConstants.OSCILLATING_KEY, false);
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, "");

        motor = m;

        // copied from CTRE tuning helper code

        motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);
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





    // helper functions because we don't like typing a lot
    private static void setKf(double val) {
        motor.config_kF(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private static void setCruise(int val) {
        motor.configMotionCruiseVelocity(val, TunerConstants.kTimeoutMs);
    }

    private static void setKp(double val) {
        motor.config_kP(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private static void setKi(double val) {
        motor.config_kI(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private static void setIZone(int val) {
        motor.config_IntegralZone(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private static void setKd(double val) {
        motor.config_kD(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }





    private static void changeStep(Step s) {
        step = s;

        if (s == Step.None) {
            repeat = false; // once the step is changed, make sure init is called

            noneInit();
        }

        SmartDashboard.putString(TunerConstants.PROCESS_KEY, s.toString());
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, s.getInstructions());
    }




    
    private static void noneInit() {
        motor.set(ControlMode.PercentOutput, 0);
    }



    private static void kfTuneInit() {
        kf = new KfStep(TunerConstants.DATA_WINDOW_SIZE, motor);
    }

    private static void kfTunePeriodic() {
        boolean done = kf.update();
        // put the power output to the Dashboard to make sure its also stable

        SmartDashboard.putNumber("TestMode/AutoTuner/kf", kf.getValue());

        if (done) {
            changeStep(Step.None);
        } else {
            setKf(kf.getValue());
        }
    }



    private static void cruiseTuneInit() {
        if (kf == null) { return; }



        motor.setSelectedSensorPosition(0, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);

        cruise = new CruiseStep(TunerConstants.DATA_WINDOW_SIZE, motor, kf.getTp100());

        setCruise((int) cruise.getValue());
    }

    private static void cruiseTunePeriodic() {
        boolean done = cruise.update();

        SmartDashboard.putNumber("TestMode/AutoTuner/Cruise", cruise.getValue());
        
        if (done) {
            changeStep(Step.None);
        }
    }



    private static void kpTuneInit() {
        if (cruise == null) { return; }



        kp = new KpStep(TunerConstants.DATA_WINDOW_SIZE, motor, cruise.getTickError());

        setKp(kp.getValue());
    }

    private static void kpTunePeriodic() {
        boolean done = kp.update();

        SmartDashboard.putNumber("TestMode/AutoTuner/kp", kp.getValue());

        if (done) {
            changeStep(Step.None);
        } else {
            setKp(kp.getValue());
        }
    }



    private static void kdTuneInit() {
        if (kp == null) { return; }



        kd = new KdStep(TunerConstants.DATA_WINDOW_SIZE, motor, kp.getValue());

        setKd(kd.getValue());
    }

    private static void kdTunePeriodic() {
        boolean done = kd.update();

        SmartDashboard.putNumber("TestMode/AutoTuner/kd", kd.getValue());

        if (done) {
            changeStep(Step.None);
        }
    }



    private static void kiTuneInit() {
        if (kd == null) { return; }



        ki = new KiStep(TunerConstants.DATA_WINDOW_SIZE, motor, kd.getSteadyStateError());

        setIZone(ki.getIntegralZone());
        setKi(ki.getValue()); // initial guess

        step = Step.Ki;
    }

    private static void kiTunePeriodic() {
        boolean done = ki.update(); // initial ki value

        SmartDashboard.putNumber("TestMode/AutoTuner/ki", ki.getValue());

        if (done) {
            step = Step.None;
        } else {
            setKi(ki.getValue());
        }
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