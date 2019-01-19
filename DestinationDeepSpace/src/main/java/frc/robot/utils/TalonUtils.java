package frc.robot.utils;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import frc.robot.RobotMap;

public class TalonUtils {
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

        motor.stopMotor();

        // Group all of the slot configurations together for a loop
        for (int slotIdx = 0; slotIdx < 2; ++slotIdx)   // TODO: Source MAGIC NUMBER 2, what is the actual number of slots in the firmware?
        {
            motor.config_IntegralZone(slotIdx, 0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.config_kF(slotIdx, 0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.config_kP(slotIdx, 0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.config_kI(slotIdx, 0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.config_kD(slotIdx, 0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.configClosedLoopPeakOutput(slotIdx, 1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.configClosedLoopPeriod(slotIdx, 1, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.configMaxIntegralAccumulator(slotIdx, 0, RobotMap.CONTROLLER_TIMEOUT_MS);
            motor.configAllowableClosedloopError(slotIdx, 0, RobotMap.CONTROLLER_TIMEOUT_MS);
        }

        // motor.configAuxPIDPolarity(false, RobotMap.CONTROLLER_TIMEOUT_MS);

        motor.configOpenloopRamp(0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.configClosedloopRamp(0.0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // Start with current limiting disabled and set to a large value
        motor.enableCurrentLimit(false);
        motor.configContinuousCurrentLimit(90, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.configPeakCurrentDuration(0, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.configPeakCurrentLimit(0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // Assume we have no limit switches associated with this motor
        // motor.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configForwardSoftLimitEnable(false, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configForwardSoftLimitThreshold(0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configReverseSoftLimitEnable(false, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configReverseSoftLimitThreshold(0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // Disable motion magic and profile features until we know we are going to use them
        // motor.configMotionAcceleration(0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configMotionCruiseVelocity(0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configMotionProfileTrajectoryPeriod(0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // Reinforce factor default deadband and minimum output
        motor.configNeutralDeadband(0.04, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configNominalOutputForward(0.0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configNominalOutputReverse(0.0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // motor.configPeakOutputForward(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configPeakOutputReverse(1.0, RobotMap.CONTROLLER_TIMEOUT_MS);

        // Feedback sources, start with no local and no remote sensors
        // NOTE: Remote sensors can be things like the Pigeon IMU

        // motor.configRemoteFeedbackFilter(0, RemoteSensorSource.Off, 0, RobotMap.CONTROLLER_TIMEOUT_MS);
        for (int pidIdx = 0; pidIdx <= 1; ++pidIdx)
        {
            // motor.configSelectedFeedbackCoefficient(1.0, pidIdx, RobotMap.CONTROLLER_TIMEOUT_MS);
            // motor.configSelectedFeedbackSensor(FeedbackDevice.None, pidIdx, RobotMap.CONTROLLER_TIMEOUT_MS);

            // motor.setIntegralAccumulator(0, pidIdx, RobotMap.CONTROLLER_TIMEOUT_MS);
            // motor.setSelectedSensorPosition(0, pidIdx, RobotMap.CONTROLLER_TIMEOUT_MS);
        }

        // TODO: Figure out what these do!!!!
        //motor.configSensorTerm(sensorTerm, feedbackDevice, RobotMap.CONTROLLER_TIMEOUT_MS);
        //motor.configVelocityMeasurementPeriod(period, RobotMap.CONTROLLER_TIMEOUT_MS);
        //motor.configVelocityMeasurementWindow(windowSize, RobotMap.CONTROLLER_TIMEOUT_MS);

        // motor.enableVoltageCompensation(false);
        // motor.configVoltageCompSaturation(12.0, RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.configVoltageMeasurementFilter(32, RobotMap.CONTROLLER_TIMEOUT_MS);  // Default

        // Reinforce control frame period defaults
        // NOTE: Decreasing general control frame period to 1 ms will increase CAN traffic by about 15%
        motor.setControlFramePeriod(ControlFrame.Control_3_General, 10);
        // TODO: motor.setControlFramePeriod(ControlFrame.Control_4_Advanced, 10);
        //motor.setControlFramePeriod(ControlFrame.Control_6_MotProfAddTrajPoint, 10);


        motor.setNeutralMode(NeutralMode.Brake);

        motor.setSafetyEnabled(false);
        motor.setExpiration(0.500);

        motor.setInverted(false);
        motor.setSensorPhase(false);

        // Reinforce the factory defaults that we might change later
        // To improve CAN bus utilization consider setting the unused one to even lower rates (longer periods)
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General,        10, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0,      20, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature,    160, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat,   160, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth,    160, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic,  160, RobotMap.CONTROLLER_TIMEOUT_MS);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0,   160, RobotMap.CONTROLLER_TIMEOUT_MS);

        // motor.selectDemandType(false);   // Future feature

        // TODO: Read faults before clearing?
        // motor.clearMotionProfileHasUnderrun(RobotMap.CONTROLLER_TIMEOUT_MS);
        // motor.clearStickyFaults(RobotMap.CONTROLLER_TIMEOUT_MS);

        // motor.changeMotionControlFramePeriod(25);  // Assume that any motion profile will step at 50 ms
        // motor.clearMotionProfileTrajectories();
        // motor.enableHeadingHold(false); // Future feature

        // Use constants from slot 0 and loop 0 (i.e., primary PID) for basic closed loop control
        // See SRM Section 9.10 for more information about the Auxilliary loop
        // motor.selectProfileSlot(0, 0);

    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function.
         */

        int slotIdx = 0;
        initializeMotorFPID(motor, kF, kP, kI, kD, slotIdx);
    }
    public static void initializeMotorFPID(WPI_TalonSRX motor, double kF, double kP, double kI, double kD, int slotIdx){
        /**
         * The following T O D O is done.
         * T O D O: Actually write this function too.
         */

        int timeout = RobotMap.CONTROLLER_TIMEOUT_MS;

        motor.config_kF(slotIdx, kF, timeout);
        motor.config_kP(slotIdx, kP, timeout);
        motor.config_kI(slotIdx, kI, timeout);
        motor.config_kD(slotIdx, kD, timeout);
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
