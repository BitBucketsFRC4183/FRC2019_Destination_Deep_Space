package frc.robot.utils.autotuner;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import frc.robot.utils.autotuner.steps.KfStep;
import frc.robot.utils.autotuner.steps.CruiseStep;
import frc.robot.utils.autotuner.steps.KpStep;
import frc.robot.utils.autotuner.steps.KiStep;
import frc.robot.utils.autotuner.steps.KdStep;


public class AutoTuner {
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

    private static SendableChooser<Step> stepSelector;



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


        
        SmartDashboard.putData("AutoTuner step", stepSelector);
    }
    


    /** Next iteration in tuning process */
    public static void periodic() {
        step = stepSelector.getSelected();

        switch (step) {
            default: {}
            case None: {
                break;
            }
            case Kf: {
                kfTune1();
                break;
            }
            case Cruise: {
                cruiseTune1();
                break;
            }
            case Kp: {
                kpTune1();
                break;
            }
            case Kd: {
                kdTune1();
                break;
            }
            case Ki: {
                kiTune1();
                break;
            }
        }
    }

    
    

    



    public static void tune(WPI_TalonSRX motor) {
        SmartDashboard.putBoolean(TunerConstants.STABLE_KEY, false);
        SmartDashboard.putBoolean(TunerConstants.OSCILLATING_KEY, false);
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, "");


        // copied from CTRE tuning helper code

        motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);
		motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, TunerConstants.kTimeoutMs);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, TunerConstants.kTimeoutMs);
        
		/* Set the peak and nominal outputs */
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

        /* Set acceleration and vcruise velocity - see documentation */
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

        SmartDashboard.putString(TunerConstants.PROCESS_KEY, s.toString());
        SmartDashboard.putString(TunerConstants.QUESTION_KEY, s.getInstructions());
    }




    
    private static void kfTune() {
        kf = new KfStep(TunerConstants.DATA_WINDOW_SIZE, motor);

        changeStep(Step.Kf);
    }

    private static void kfTune1() {
        boolean done = kf.update();
        // put the power output to the Dashboard to make sure its also stable
        SmartDashboard.putNumber(TunerConstants.POWER_DATA_KEY, motor.getMotorOutputPercent());

        if (done) {
            changeStep(Step.None);
        } else {
            setKf(kf.getValue());
        }
    }



    private static void cruiseTune() {
        cruise = new CruiseStep(TunerConstants.DATA_WINDOW_SIZE, motor, kf.getTp100(), TunerConstants.TARGET);

        setCruise(cruise.getSpeed());

        changeStep(Step.Cruise);
    }

    private static void cruiseTune1() {
        boolean done = cruise.update();

        if (done) {
            changeStep(Step.None);
        }
    }



    private static void kpTune() {
        kp = new KpStep(TunerConstants.DATA_WINDOW_SIZE, motor, cruise.getTickError(), TunerConstants.TARGET);

        setKp(kp.getValue());

        changeStep(Step.Kp);
    }

    private static void kpTune1() {
        boolean done = kp.update();

        if (done) {
            changeStep(Step.None);
        } else {
            setKp(kp.getValue());
        }
    }



    private static void kdTune() {
        kd = new KdStep(TunerConstants.DATA_WINDOW_SIZE, motor, kp.getValue(), TunerConstants.TARGET);

        setKd(kd.getValue());

        changeStep(Step.Kd);
    }

    private static void kdTune1() {
        boolean done = kd.update();

        if (done) {
            changeStep(Step.None);
        }
    }



    private static void kiTune() {
        ki = new KiStep(TunerConstants.DATA_WINDOW_SIZE, motor, kd.getSteadyStateError(), TunerConstants.TARGET);

        setIZone(ki.getIntegralZone());
        setKi(ki.getValue());

        step = Step.Ki;
    }

    private static void kiTune1() {
        boolean done = ki.update(); // initial ki value

        if (done) {
            step = Step.None;
        } else {
            setKi(ki.getValue());
        }
    }



    // TODO: written explanation of how it works
    // TODO: run some tests
    // TODO: write report to file
}