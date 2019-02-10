package frc.robot.subsystem.drive;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.subsystem.BitBucketSubsystem;
import frc.robot.subsystem.navigation.BitBucketsAHRS;
import frc.robot.utils.JoystickScale;

/**
 * The DriveSubsystemTest class will unit test the DriveSubsystem. This requires mocking out a bunch
 * of static initializers and static classes so we can test only the DriveSubsystem code without
 * actually talking to a running robot.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
        NetworkTablesJNI.class,
        HAL.class,
        DriverStation.class,
        BitBucketsAHRS.class,
        DriveSubsystem.class,
        HALUtil.class,
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

    @Mock
    AHRS ahrs;

    @Mock
    WPI_TalonSRX mockLeftMotors[] = new WPI_TalonSRX[DriveSubsystem.NUM_MOTORS_PER_SIDE];

    @Mock
    WPI_TalonSRX mockRightMotors[] = new WPI_TalonSRX[DriveSubsystem.NUM_MOTORS_PER_SIDE];

    @Mock
    SendableChooser mockDriveStyleChooser;
    @Mock
    SendableChooser mockForwardJoystickScalerChooser;
    @Mock
    SendableChooser mockTurnJoystickScalerChooser;

    @Before
    public void beforeTest() throws Exception {
        // mock out all the static methods of these JNI classes
        // They get called when we instantiate the DriveSubsystem class and mocking them
        // makes their methods do nothing
        mockStatic(NetworkTablesJNI.class);
        mockStatic(HAL.class);
        mockStatic(HALUtil.class);

        // For each test, return mock drive train motors when the DriveSubsystem creates them
        // This requires @PrepareForTest(DriveSubsystem.class)
        for (int i = 0; i < DriveSubsystem.NUM_MOTORS_PER_SIDE; i++) {
            mockLeftMotors[i] = mock(WPI_TalonSRX.class);
            mockRightMotors[i] = mock(WPI_TalonSRX.class);
            whenNew(WPI_TalonSRX.class).withArguments(eq(DriveConstants.LEFT_DRIVE_MOTOR_IDS[i])).thenReturn(mockLeftMotors[i]);
            whenNew(WPI_TalonSRX.class).withArguments(eq(DriveConstants.RIGHT_DRIVE_MOTOR_IDS[i])).thenReturn(mockRightMotors[i]);
        }

        // Wire up our mock sendable chooser.
        whenNew(SendableChooser.class).withNoArguments()
        .thenReturn(mockForwardJoystickScalerChooser)
        .thenReturn(mockTurnJoystickScalerChooser)
        .thenReturn(mockDriveStyleChooser);

        // Mock the DriverStation class as well. We don't want it instantiated at all
        mockStatic(DriverStation.class);
        when(DriverStation.getInstance()).thenReturn(driverStation);

        // mock the BitBucketsAHRS class because when we create an ahrs object
        // buy calling BitBucketsAHRS.instance(), the ahrs object starts a thread
        mockStatic(BitBucketsAHRS.class);
        when(BitBucketsAHRS.instance()).thenReturn(ahrs);

        // Because the DriveSubsystem is a singleton, only one instance ever gets created
        // and our mock motors are only ever created for the first one. With unit tests, all mock
        // objects are reset every test. This means we can't verify that we are calling our mocked
        // motors the way we think we are after the first test (the mock is reset, the DriveSubystem instance
        // will still point to the old mocked motor
        //
        // Because of this, we treat each test case like it is creating a brand new robot and DriveSubsystem
        // per call. This requires us to modify the private constructor to make it public, and instantiate
        // a new instance each test.
        Constructor<DriveSubsystem> constructor = DriveSubsystem.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // for this test, create a fresh DriveSubsystem object
        DriveSubsystem instance = constructor.newInstance();
        mockStatic(DriveSubsystem.class);
        when(DriveSubsystem.instance()).thenReturn(instance);

        Field dsField = BitBucketSubsystem.class.getDeclaredField("ds");
        dsField.setAccessible(true);
        dsField.set(null, driverStation);
    }

    /**
     * Test the DriveSubsystem arcadeDrive function with no movement
     * @throws Exception
     */
    @Test
    public void testArcadeDrive() throws Exception {
        when(mockDriveStyleChooser.getSelected()).thenReturn(DriveSubsystem.DriveStyle.BB_Arcade);
        when(mockForwardJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);
        when(mockTurnJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);
        
        DriveSubsystem driveSubsystem = DriveSubsystem.instance();
        // call arcade drive
        driveSubsystem.drive(0, 0);

        // verify that each motor controller was called with the output we expected, in this case 0
        // in this case, only the front motors actually have their power output set
        verify(mockLeftMotors[0]).set(eq(ControlMode.PercentOutput), eq(0.0));
        verify(mockRightMotors[0]).set(eq(ControlMode.PercentOutput), eq(0.0));    
    }

    /**
     * Test driving forward with no turn
     * @throws Exception
     */
    @Test
    public void testArcadeDriveForward() throws Exception {
        when(mockDriveStyleChooser.getSelected()).thenReturn(DriveSubsystem.DriveStyle.BB_Arcade);
        when(mockForwardJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);
        when(mockTurnJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);

        DriveSubsystem driveSubsystem = DriveSubsystem.instance();
        // call arcade drive
        driveSubsystem.drive(1, 0);

        // verify that each motor controller was called with the output we expected, in this case 1
        // on each motor
        verify(mockLeftMotors[0]).set(eq(ControlMode.PercentOutput), eq(1.0));
        verify(mockRightMotors[0]).set(eq(ControlMode.PercentOutput), eq(1.0));    
    }

    /**
     * Test out driving forward and to the right. This should call our left motor at 1.5 speed
     * and our right motor at .5 speed, turning the robot while moving forward
     * @throws Exception
     */
    @Test
    public void testArcadeDriveForwardAndRight() throws Exception {
        when(mockDriveStyleChooser.getSelected()).thenReturn(DriveSubsystem.DriveStyle.BB_Arcade);
        when(mockForwardJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);
        when(mockTurnJoystickScalerChooser.getSelected()).thenReturn(JoystickScale.LINEAR);

        DriveSubsystem driveSubsystem = DriveSubsystem.instance();
        // call arcade drive
        driveSubsystem.drive(1, 1);

        // verify that each motor controller was called with the output we expected
        // in this case, we go heavy on the left side, light on the right side
        verify(mockLeftMotors[0]).set(eq(ControlMode.PercentOutput), eq(1.5));
        verify(mockRightMotors[0]).set(eq(ControlMode.PercentOutput), eq(0.5));    
    }
}
