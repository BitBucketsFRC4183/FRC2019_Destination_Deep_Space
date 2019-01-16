package frc.robot.subsystem.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.SPIJNI;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.RobotMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * The DriveSubsystemTest class will unit test the DriveSubsystem. This requires mocking out a bunch
 * of static initializers and static classes so we can test only the DriveSubsystem code without
 * actually talking to a running robot.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        MotControllerJNI.class,
        NetworkTablesJNI.class,
        HAL.class,
        SPIJNI.class,
        DriverStation.class
})
@SuppressStaticInitializationFor({
        "edu.wpi.first.networktables.NetworkTablesJNI",
        "edu.wpi.first.hal.JNIWrapper",
        "com.ctre.phoenix.CTREJNIWrapper",
        "edu.wpi.first.wpilibj.DriverStation"
})
public class DriveSubsystemTest {

    /**
     * Create a mock of the DriverStation so that it will not start a thread when instantiated.
     */
    @Mock
    DriverStation driverStation;

    // These handles will be used later to verify that our motors are being called
    // with the demand values we expect
    long leftFrontMotorHandle = 1;
    long leftRearMotorHandle = 2;
    long rightFrontMotorHandle = 3;
    long rightRearMotorHandle = 4;

    @Before
    public void beforeTest() throws Exception {
        // mock out all the static methods of these JNI classes
        mockStatic(NetworkTablesJNI.class);
        mockStatic(HAL.class);
        mockStatic(SPIJNI.class);
        mockStatic(MotControllerJNI.class);

        // when a motor controller is created, return a handle we can use later for verifying that our
        // motors are being called. We have to do this weird MOTOR_ID bitwise or'd with some random stuff because
        // that's how the JNI calls create. It's possible we could spy the WPI_TalonSRX class and intercept the
        // create instead, but this works for now
        mockStatic(MotControllerJNI.class);
        when(MotControllerJNI.Create(RobotMap.LEFT_DRIVE_MOTOR_FRONT_ID | 0x02040000)).thenReturn(leftFrontMotorHandle);
        when(MotControllerJNI.Create(RobotMap.LEFT_DRIVE_MOTOR_REAR_ID | 0x02040000)).thenReturn(leftRearMotorHandle);
        when(MotControllerJNI.Create(RobotMap.RIGHT_DRIVE_MOTOR_FRONT_ID | 0x02040000)).thenReturn(rightFrontMotorHandle);
        when(MotControllerJNI.Create(RobotMap.RIGHT_DRIVE_MOTOR_REAR_ID | 0x02040000)).thenReturn(rightRearMotorHandle);

        mockStatic(DriverStation.class);
        when(DriverStation.getInstance()).thenReturn(driverStation);
    }

    /**
     * Test the DriveSubsystem arcadeDrive function with no movement
     * @throws Exception
     */
    @Test
    public void testArcadeDriveNoMovement() throws Exception {
        DriveSubsystem driveSubsystem = new DriveSubsystem();

        // Reset the mock on this JNI so we only need to verify the Set_4 calls
        mockStatic(MotControllerJNI.class);

        // call arcade drive
        driveSubsystem.arcadeDrive(0, 0);

        // verify that each motor controller was called with the output we expected, in this case 0
        // in this case, only the front motors actually have their power output set
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(leftFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(0.0d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(rightFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(0.0d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);

        verifyNoMoreInteractions(MotControllerJNI.class);
    }

    /**
     * Test driving forward with no turn
     * @throws Exception
     */
    @Test
    public void testArcadeDriveForward() throws Exception {
        DriveSubsystem driveSubsystem = new DriveSubsystem();

        // Reset the mock on this JNI so we only need to verify the Set_4 calls
        mockStatic(MotControllerJNI.class);

        // call arcade drive
        driveSubsystem.arcadeDrive(1, 0);

        // verify that each motor controller was called with the output we expected, in this case 0
        // in this case, only the front motors actually have their power output set
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(leftFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(1.0d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(rightFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(1.0d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);

        verifyNoMoreInteractions(MotControllerJNI.class);
    }

    /**
     * Test out driving forward and to the right. This should call our left motor at 1.5 speed
     * and our right motor at .5 speed, turning the robot while moving forward
     * @throws Exception
     */
    @Test
    public void testArcadeDriveForwardAndRight() throws Exception {
        DriveSubsystem driveSubsystem = new DriveSubsystem();

        // mock out the Set_4 native function on the motor
        mockStatic(MotControllerJNI.class);

        // call arcade drive
        driveSubsystem.arcadeDrive(1, 1);

        // verify that each motor controller was called with the output we expected, in this case 0
        // in this case, only the front motors actually have their power output set
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(leftFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(1.5d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);
        MotControllerJNI.Set_4(eq(rightFrontMotorHandle), eq(ControlMode.PercentOutput.value), eq(.5d), eq(0.0d), eq(DemandType.Neutral.value));
        verifyStatic(MotControllerJNI.class);

        verifyNoMoreInteractions(MotControllerJNI.class);
    }
}
