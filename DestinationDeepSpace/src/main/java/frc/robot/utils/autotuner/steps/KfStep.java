package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.TunerConstants;
import frc.robot.utils.autotuner.DataWindow;

public class KfStep extends TuningStep {
    private int tp100;

    private DataWindow powerWindow;



    public KfStep(int windowSize, WPI_TalonSRX motor) {
        super(windowSize, motor);

        powerWindow = new DataWindow(windowSize);
    }
    

    
    public void putPower(WPI_TalonSRX motor) {
        SmartDashboard.putNumber(TunerConstants.POWER_DATA_KEY, motor.getMotorOutputPercent());
    }



    public boolean update() {
        // get + and - velocities
        boolean done = collectDataVelocity();

        // if done with that, get average speed at %
        if (done) {
            tp100 = (int) ((pos.average() - neg.average()) / (2 * MOTOR.getMotorOutputPercent())); // average of two speeds

            // calculate kf
            value = 1023.0 / tp100;
        }

        return done;
    }
    


    public int getTp100() {
        return tp100;
    }
}