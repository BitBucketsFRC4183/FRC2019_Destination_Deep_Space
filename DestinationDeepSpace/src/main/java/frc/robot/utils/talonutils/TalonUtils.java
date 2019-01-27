package frc.robot.utils.talonutils;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;

public class TalonUtils {

    public static int MAX_STATUS_FRAME_PERIOD = 160;
    /**
     * initializeMotor - set all of the motor configuration states to a known value
     * This is important when we are not sure if the motor is in a factory state
     * @param motor
     *
     * The following T O D O is done.
     * T O D O: Move this to a separate package?
     */
    public static void initializeMotorDefaults(WPI_TalonSRX motor)
    {
        // TODO: Check ErrorCode?
        motor.configFactoryDefault(RobotMap.CONTROLLER_TIMEOUT_MS);

        motor.stopMotor();

    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int iZone){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function.
         */

        int slotIdx = 0;
        initializeMotorFPID(motor, kF, kP, kI, kD, iZone, slotIdx);
    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int iZone, int slotIdx){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function too.
         */

        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        motor.selectProfileSlot(slotIdx, RobotMap.PRIMARY_PID_LOOP);
        motor.config_kF(slotIdx, kF, timeout);
        motor.config_kP(slotIdx, kP, timeout);
        motor.config_kI(slotIdx, kI, timeout);
        motor.config_kD(slotIdx, kD, timeout);
        motor.config_IntegralZone(slotIdx, iZone, RobotMap.CONTROLLER_TIMEOUT_MS);
    }

    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor) {
        initializeQuadEncoderMotor(motor, MAX_STATUS_FRAME_PERIOD);
    }

    /**
     * Initializes the quad encoder motor, whatever that means.
     */
    public static void initializeQuadEncoderMotor(WPI_TalonSRX motor, int statusFramePeriod) {
        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;
        int pidLoop = RobotMap.PRIMARY_PID_LOOP;


        motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, pidLoop, timeout);
        motor.setSelectedSensorPosition(0,pidLoop, timeout);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, statusFramePeriod, timeout);

    }
}
