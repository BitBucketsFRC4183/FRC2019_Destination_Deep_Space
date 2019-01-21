package frc.robot.utils.autotuner;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;


public class AutoTuner {
    private static ArrayList<AutoTuner> tuners = new ArrayList<AutoTuner>();

    private static enum Step {
        None,
        Kf,
        Cruise,
        Kp,
        Kd,
        Ki;
    }

    
    private final WPI_TalonSRX MOTOR;

    private Step step = Step.None;





    // kf
    private double kf = 0;
    private double kf_percent = 0;
    private boolean kf_finishedPos = false;
    private double kf_tp100 = 0;
    private DataWindow kf_pos = new DataWindow(TunerConstants.KF_WINDOW_SIZE);
    private DataWindow kf_neg = new DataWindow(TunerConstants.KF_WINDOW_SIZE);



    // cruise
    private int cruise = 0;
    private boolean cruise_finishedPos = false;
    private double cruise_terr = 0;
    private DataWindow cruise_pos = new DataWindow(TunerConstants.CRUISE_WINDOW_SIZE);
    private DataWindow cruise_neg = new DataWindow(TunerConstants.CRUISE_WINDOW_SIZE);



    // kp
    private double kp = 0;
    private double kp0 = 0; // initial kp
    private double kp_exponent = 0; // kp = kp0 * 2^(exponent)
    // add 1 to exponent until oscillating, then cut this by 2 each time
    // because 0.5 + 0.25 + ... = 1, this works
    private double kp_exponentChange = 1;
    // if it reached the oscillation, it will keep decreasing the exponent until there
    // is no oscillation, then it will be done
    private int kp_oscillations = 0;
    private boolean kp_reachedOscillation = false;
    private boolean kp_finishedPos = false;
    private DataWindow kp_pos = new DataWindow(TunerConstants.KP_WINDOW_SIZE);
    private DataWindow kp_neg = new DataWindow(TunerConstants.KP_WINDOW_SIZE);



    // kd
    private double kd = 0;
    private boolean kd_finishedPos = false;
    private int kd_sserr = 0;
    private DataWindow kd_pos = new DataWindow(TunerConstants.KD_WINDOW_SIZE);
    private DataWindow kd_neg = new DataWindow(TunerConstants.KD_WINDOW_SIZE);



    // ki
    private double ki = 0;
    private int ki_iZone = 0;
    private double ki0 = 0; // initial kp
    private double ki_exponent = 0; // kp = kp0 * 2^(exponent)
    // add 1 to exponent until oscillating, then cut this by 2 each time
    // because 0.5 + 0.25 + ... = 1, this works
    private double ki_exponentChange = 1;
    // if it reached the oscillation, it will keep decreasing the exponent until there
    // is no oscillation, then it will be done
    private int ki_oscillations = 0;
    private boolean ki_reachedOscillation = false;
    private boolean ki_finishedPos = false;
    private int ki_sserr = 0;
    private DataWindow ki_pos = new DataWindow(TunerConstants.KI_WINDOW_SIZE);
    private DataWindow ki_neg = new DataWindow(TunerConstants.KI_WINDOW_SIZE);





    public AutoTuner(WPI_TalonSRX m) {
        MOTOR = m;

        // copied from CTRE tuning helper code

        MOTOR.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);
		MOTOR.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, TunerConstants.kTimeoutMs);
        MOTOR.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, TunerConstants.kTimeoutMs);
        
		/* Set the peak and nominal outputs */
		MOTOR.configNominalOutputForward(0, TunerConstants.kTimeoutMs);
		MOTOR.configNominalOutputReverse(0, TunerConstants.kTimeoutMs);
		MOTOR.configPeakOutputForward(1, TunerConstants.kTimeoutMs);
        MOTOR.configPeakOutputReverse(-1, TunerConstants.kTimeoutMs);
        
        MOTOR.selectProfileSlot(TunerConstants.kSlotIdx, TunerConstants.kPIDLoopIdx);
        setKf(0);
        setKp(0);
        setKi(0);
        setIZone(0);
        setKd(0);

        /* Set acceleration and vcruise velocity - see documentation */
		setCruise(15000); // TODO: select these values on Dashboard
        MOTOR.configMotionAcceleration(6000, TunerConstants.kTimeoutMs);
        
        MOTOR.setSelectedSensorPosition(0, TunerConstants.kPIDLoopIdx, TunerConstants.kTimeoutMs);

        tuners.add(this);
    }





    // helper functions because we don't like typing a lot
    private void setKf(double val) {
        MOTOR.config_kF(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private void setCruise(int val) {
        MOTOR.configMotionCruiseVelocity(val, TunerConstants.kTimeoutMs);
    }

    private void setKp(double val) {
        MOTOR.config_kP(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private void setKi(double val) {
        MOTOR.config_kI(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private void setIZone(int val) {
        MOTOR.config_IntegralZone(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }

    private void setKd(double val) {
        MOTOR.config_kD(TunerConstants.kSlotIdx, val, TunerConstants.kTimeoutMs);
    }





    public void update() {
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





    public void kfTune(double percent) {
        kf_percent = percent;
        step = Step.Kf;
    }

    private void kfTune1() {
        if (!kf_finishedPos) {
            MOTOR.set(ControlMode.PercentOutput, kf_percent);
            int data = MOTOR.getSelectedSensorVelocity();
            kf_pos.add(data);

            // enough data and consistent data
            if (kf_pos.isFilled() && kf_pos.maxDif() <= TunerConstants.KF_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> reached velocity corresponding to %

                kf_finishedPos = true; // done with positive measurements
            }
        } else {
            MOTOR.set(ControlMode.PercentOutput, -kf_percent);
            int data = -MOTOR.getSelectedSensorVelocity(); // DataWindows only works well with allow nonnegative values
            if (data >= 0) {
                kf_neg.add(data); // add datas when encoder reading is negative
            }

            // enough data and consistent data
            if (kf_neg.isFilled() && kf_neg.maxDif() <= TunerConstants.KF_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> reached velocity corresponding to %
                kf_finishedPos = true;

                // take the average of negative & positive velocities at %
                kf_tp100 = (kf_pos.average() + kf_neg.average()) / 2;
                kf = (kf_percent * 1023);

                setKf(kf); // set the value

                step = Step.None; // done tuning kf
            }
        }
    }



    public void cruiseTune() {
        cruise = (int) (0.75 * kf_tp100); // TODO: choose % of tp100 from Dashboard?
        setCruise(cruise);

        step = Step.Cruise;
    }

    private void cruiseTune1() {
        if (!cruise_finishedPos) {
            MOTOR.set(ControlMode.MotionMagic, TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            cruise_pos.add(data);

            // enough data and consistent data
            if (cruise_pos.isFilled() && cruise_pos.maxDif() <= TunerConstants.CRUISE_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> stability

                cruise_finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -TunerConstants.TARGET); // other way around
            int data = Math.abs(MOTOR.getClosedLoopError());
            cruise_neg.add(data);

            // enough data and consistent data
            if (cruise_neg.isFilled() && cruise_neg.maxDif() <= TunerConstants.CRUISE_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> stability

                cruise_terr = (cruise_pos.average() + cruise_neg.average()) / 2;

                step = Step.None; // done with cruise
            }
        }
    }



    public void kpTune() {
        kp0 = 0.1 * 1023 / cruise_terr;
        kp = kp0;
        setKp(kp);

        step = Step.Kp;
    }

    private void kpTune1() {
        if (!kp_finishedPos) {
            kp_oscillations = 0; // reset oscillations to 0

            MOTOR.set(ControlMode.MotionMagic, TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            kp_pos.add(data);

            // enough data and consistent data
            if (kp_pos.isFilled() && kp_pos.maxDif() <= TunerConstants.KP_DIFFERENCE_TOLERANCE) {
                kp_oscillations = kp_pos.getOscillations(); // get # of oscillations

                kp_finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            kp_neg.add(data);

            // enough data and consistent data
            if (kp_neg.isFilled() && kp_neg.maxDif() <= TunerConstants.KP_DIFFERENCE_TOLERANCE) {
                // get # of oscillations
                // using max so that if oscillating on + but not -, it WILL count as oscillating
                kp_oscillations = Math.max(kp_oscillations, kp_pos.getOscillations());



                // restart data collection cycle in next iteration
                // it may or may not continue collecting data (see next part of code)
                kp_finishedPos = false;



                // if it is oscillating
                if (kp_oscillations >= TunerConstants.KP_OSCILLATION_THRESHHOLD) {
                    kp_exponentChange /= 2; // half the exponent change
                    kp_exponent -= kp_exponentChange; // lower the exponent
                    kp = kp0 * Math.pow(2, kp_exponent);
                    setKp(kp);

                    kp_reachedOscillation = true;

                    // need to collect data in next iteration
                    kp_pos.reset();
                    kp_neg.reset();
                } else {
                    // if it stopped oscillating, done tuning
                    if (kp_reachedOscillation) {
                        step = Step.None;
                    // hasnt reached oscillating point yet, increase kp
                    } else {
                        kp_exponent++; // double kp
                        kp = kp0 * Math.pow(2, kp_exponent);
                        setKp(kp);

                        // need to collect data in next iteration
                        kp_pos.reset();
                        kp_neg.reset();
                    }
                }
            }
        }
    }



    public void kdTune() {
        kd = 10 * kp;
        setKd(kd);
    }

    // TODO: test for overshoot (somewhere)
    private void kdTune1() {
        if (!kd_finishedPos) {
            MOTOR.set(ControlMode.MotionMagic, TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            kd_pos.add(data);

            // enough data and consistent data
            if (kd_pos.isFilled() && kd_pos.maxDif() <= TunerConstants.KD_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> stability

                kd_finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -TunerConstants.TARGET); // other way around
            int data = Math.abs(MOTOR.getClosedLoopError());
            kd_neg.add(data);

            // enough data and consistent data
            if (kd_neg.isFilled() && kd_neg.maxDif() <= TunerConstants.KD_DIFFERENCE_TOLERANCE) {
                // consistent measurements --> stability

                kd_sserr = (kd_pos.average() + kd_neg.average()) / 2;

                step = Step.None; // done with kd
            }
        }
    }



    public void kiTune() {
        ki_iZone = (int) (2.5 * kd_sserr);
        setIZone(ki_iZone);

        ki0 = 0.001;
        ki = ki0;
        setKi(ki);

        step = Step.Ki;
    }

    private void kiTune1() {
        if (!ki_finishedPos) {
            ki_oscillations = 0; // reset oscillations to 0

            MOTOR.set(ControlMode.MotionMagic, TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            ki_pos.add(data);

            // enough data and consistent data
            if (ki_pos.isFilled() && ki_pos.maxDif() <= TunerConstants.KI_DIFFERENCE_TOLERANCE) {
                ki_oscillations = ki_pos.getOscillations(); // get # of oscillations

                ki_finishedPos = true;
            }
        } else {
            MOTOR.set(ControlMode.MotionMagic, -TunerConstants.TARGET);
            int data = Math.abs(MOTOR.getClosedLoopError());
            ki_neg.add(data);

            // enough data and consistent data
            if (ki_neg.isFilled() && ki_neg.maxDif() <= TunerConstants.KI_DIFFERENCE_TOLERANCE) {
                ki_sserr = (ki_pos.average() + ki_neg.average()) / 2;

                // if the error is small enough, we're done here
                if (ki_sserr <= TunerConstants.KI_STEADY_STATE_ERROR_THRESHHOLD) {
                    step = Step.None;

                    return;
                }

                // get # of oscillations
                // using max so that if oscillating on + but not -, it WILL count as oscillating
                ki_oscillations = Math.max(ki_oscillations, ki_pos.getOscillations());



                // restart data collection cycle in next iteration
                // it may or may not continue collecting data (see next part of code)
                ki_finishedPos = false;



                // if it is oscillating
                if (ki_oscillations >= TunerConstants.KI_OSCILLATION_THRESHHOLD) {
                    ki_exponentChange /= 2; // half the exponent change
                    ki_exponent -= ki_exponentChange; // lower the exponent
                    ki = ki0 * Math.pow(2, ki_exponent);
                    setKi(ki);

                    ki_reachedOscillation = true;

                    // need to collect data in next iteration
                    ki_pos.reset();
                    ki_neg.reset();
                } else {
                    // if it stopped oscillating, done tuning
                    if (ki_reachedOscillation) {
                        step = Step.None;
                    // hasnt reached oscillating point yet, increase kp
                    } else {
                        ki_exponent++; // double kp
                        ki = ki0 * Math.pow(2, ki_exponent);
                        setKi(ki);

                        // need to collect data in next iteration
                        ki_pos.reset();
                        ki_neg.reset();
                    }
                }
            }
        }
    }

    // TODO: write report to file
    // TODO: push stuff to Dashboard + read inputs
    // TODO: allow flexibility in TuningConstants (possibly through Dashboard)
    // TODO: option to just autotune itself with no user interaction at all
    // TODO: connect to robot periodic
    // TODO: cry after writing this much code & realizing it doesn't work because code does that
}