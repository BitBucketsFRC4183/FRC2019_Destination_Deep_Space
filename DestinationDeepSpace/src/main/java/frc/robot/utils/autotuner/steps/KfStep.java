package frc.robot.utils.autotuner.steps;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.utils.autotuner.TunerConstants;
import frc.robot.utils.autotuner.DataWindow;

public class KfStep extends TuningStep {
    private int tp100;



    public KfStep(int windowSize, WPI_TalonSRX motor) {
        super(windowSize, motor);
    }



    public boolean update() {
        // get + and - velocities
        boolean done = collectDataVelocity();

        // if done with that, get average speed at %
        if (done) {
            // TODO: get average %voltage over a window
            tp100 = (int) ((pos.average() - neg.average()) / (2 * -MOTOR.getMotorOutputPercent())); // average of two speeds

            // calculate kf
            value = 1023.0 / tp100;
        }

        return done;
    }
    


    public int getTp100() {
        return tp100;
    }
}