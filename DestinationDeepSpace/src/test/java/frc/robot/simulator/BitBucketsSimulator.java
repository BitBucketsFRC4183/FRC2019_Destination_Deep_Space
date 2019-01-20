package frc.robot.simulator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.snobot.simulator.ASimulator;
import com.snobot.simulator.DefaultDataAccessorFactory;
import com.snobot.simulator.Simulator;
import com.snobot.simulator.gui.SimulatorFrame;
import com.snobot.simulator.joysticks.IMockJoystick;
import com.snobot.simulator.joysticks.JoystickFactory;
import com.snobot.simulator.joysticks.joystick_specializations.XboxJoystick;
import com.snobot.simulator.robot_container.IRobotClassContainer;
import com.snobot.simulator.robot_container.JavaRobotContainer;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.SimulatorDataAccessor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.Robot;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public class BitBucketsSimulator extends Simulator {
    private static final Logger sLOGGER = LogManager.getLogger(BitBucketsSimulator.class);

    private IRobotClassContainer bitBucketsRobot; // The robot code to run
    private ASimulator bitBucketsSimulator; // The robot code to run
    private boolean snobotSimDriverStation;
    private String simulatorConfigFile;

    protected Thread bitBucketsRobotThread;
    protected Thread bitBucketsSimulatorThread;
    protected boolean runningSimulator;

    protected Game game;

    /**
     * Constructor
     *
     * @param aLogLevel                  The log level to set up the simulator with
     * @param aUserConfigDir             The config directory where settings are saved
     * @param aUseSnobotSimDriverstation
     * @throws Exception Throws an exception if the plugin loading failed
     */
    public BitBucketsSimulator(SimulatorDataAccessor.SnobotLogLevel aLogLevel, String aUserConfigDir, boolean aUseSnobotSimDriverstation) throws Exception {
        super(aLogLevel, aUserConfigDir, aUseSnobotSimDriverstation);
        this.snobotSimDriverStation = aUseSnobotSimDriverstation;
    }


    private static final String sUSER_CONFIG_DIR = "simulator_config/";

    public static void main(String[] aArgs) {
        DefaultDataAccessorFactory.initalize();

        Collection<String> argList = Arrays.asList(aArgs);
        boolean useBuiltinDriverStation = true;

        try {
            BitBucketsSimulator simulator = new BitBucketsSimulator(SimulatorDataAccessor.SnobotLogLevel.INFO, sUSER_CONFIG_DIR, useBuiltinDriverStation);
            simulator.startSimulation();
        } catch (ClassNotFoundException e) {
            LogManager.getLogger().log(Level.FATAL, "Class not found exception.  You either have an error in your properties file, "
                    + "or the project is not set up to be able to find the robot project you are attempting to create"
                    + "\nerror: " + e, e);

            System.exit(-1);
        } catch (UnsatisfiedLinkError e) {
            LogManager.getLogger().log(Level.FATAL, "Unsatisfied link error.  This likely means that there is a native "
                    + "call in WpiLib or the NetworkTables libraries.  Please tell PJ so he can mock it out.\n\nError Message: " + e, e);

            System.exit(-1);
        } catch (Exception e) {
            LogManager.getLogger().log(Level.ERROR, "Unknown exception...", e);
            System.exit(1);
        }
    }

    /**
     * Starts the simulation by starting the robot and the GUI
     *
     * @throws InstantiationException    Thrown the robot class could not be started with reflection
     * @throws IllegalAccessException    Thrown the robot class could not be started with reflection
     * @throws ClassNotFoundException    Thrown the robot class could not be started with reflection
     * @throws NoSuchMethodException     Thrown the robot class could not be started with reflection
     * @throws SecurityException         Thrown the robot class could not be started with reflection
     * @throws IllegalArgumentException  Thrown the robot class could not be started with reflection
     * @throws InvocationTargetException Thrown the robot class could not be started with reflection
     */
    public void startSimulation()
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, SecurityException,
            IllegalArgumentException, InvocationTargetException {

        try {
            Method loadConfigMethod = Simulator.class.getDeclaredMethod("loadConfig", String.class);
            loadConfigMethod.setAccessible(true);
            Field mPropertiesFileField = Simulator.class.getDeclaredField("mPropertiesFile");
            mPropertiesFileField.setAccessible(true);
            String mPropertiesFile = (String) mPropertiesFileField.get(this);
            loadConfigMethod.invoke(this, mPropertiesFile);

            Field mSimulatorConfigFileField = Simulator.class.getDeclaredField("mSimulatorConfigFile");
            mSimulatorConfigFileField.setAccessible(true);
            this.simulatorConfigFile = (String) mSimulatorConfigFileField.get(this);

            sendJoystickUpdate();

            Method printAsciiArtMethod = Simulator.class.getDeclaredMethod("printAsciiArt", String.class);
            printAsciiArtMethod.setAccessible(true);
            printAsciiArtMethod.invoke(this, "/com/snobot/simulator/snobot_sim.txt");

            Method createRobotMethod = Simulator.class.getDeclaredMethod("createRobot", String.class, String.class);
            createRobotMethod.setAccessible(true);
            Field mRobotClassNameField = Simulator.class.getDeclaredField("mRobotClassName");
            Field mRobotTypeField = Simulator.class.getDeclaredField("mRobotType");
            mRobotTypeField.setAccessible(true);
            mRobotClassNameField.setAccessible(true);
            String mRobotType = (String) mRobotTypeField.get(this);
            String mRobotClassName = (String) mRobotClassNameField.get(this);

            createRobotMethod.invoke(this, mRobotType, mRobotClassName);

            Field mRobot = Simulator.class.getDeclaredField("mRobot");
            Field mSimulator = Simulator.class.getDeclaredField("mSimulator");
            mRobot.setAccessible(true);
            mSimulator.setAccessible(true);

            bitBucketsSimulator = (ASimulator) mSimulator.get(this);
            bitBucketsRobot = (IRobotClassContainer) mRobot.get(this);

            // TODO: Make this more generic or at least throw if we don't have a java robot
            game = new Game(((JavaRobotContainer) bitBucketsRobot).getJavaRobot());


            if (bitBucketsSimulator != null && bitBucketsRobot != null) // NOPMD
            {
                bitBucketsRobotThread = new Thread(createRobotThread(), "RobotThread");
                bitBucketsSimulatorThread = new Thread(createSimulationThread(), "SimulatorThread");

                runningSimulator = true;
                sLOGGER.log(Level.INFO, "Starting simulator");

                bitBucketsSimulatorThread.start();
                bitBucketsRobotThread.start();

                LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
                config.width = 1440;
                config.height = 900;
                config.foregroundFPS = 60;
                new LwjglApplication(game, config);
            } else {
                if (bitBucketsSimulator != null) {
                    sLOGGER.log(Level.FATAL, "Could not start simulator, no simulator was created");
                }
                if (bitBucketsRobot != null) {
                    sLOGGER.log(Level.FATAL, "Could not start simulator, robot was created");
                }
                exitWithError();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendJoystickUpdate() {
        float xAxis = 0.0f;
        float yAxis = 0.0f;
        if (Gdx.input != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                yAxis = 1.0f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                yAxis = -1.0f;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                xAxis = -1.0f;
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                xAxis = 1.0f;
            }
        }

        if (xAxis == 0.0f && yAxis == 0.0f) {
            IMockJoystick[] joysticks = JoystickFactory.getInstance().getAll();
            for (int i = 0; i < joysticks.length; ++i) {
                IMockJoystick joystick = joysticks[i];
                DataAccessorFactory.getInstance().getDriverStationAccessor().setJoystickInformation(i, joystick.getAxisValues(),
                        joystick.getPovValues(), joystick.getButtonCount(), joystick.getButtonMask());

            }
        } else {
            DataAccessorFactory.getInstance().getDriverStationAccessor().setJoystickInformation(0, new float[] { xAxis, yAxis, xAxis },
                    new short[] { 0, 0, 0 }, 0, 0);
        }
    }

    private Runnable createSimulationThread() {
        return () -> {
            try {
                DataAccessorFactory.getInstance().getDriverStationAccessor().waitForProgramToStart();

                String errors = DataAccessorFactory.getInstance().getInitializationErrors();
                showInitializationMessage(errors);
                bitBucketsSimulator.setRobot(bitBucketsRobot);

                SimulatorFrame frame = new SimulatorFrame(simulatorConfigFile, true);
                setFrameVisible(frame);

                while (runningSimulator) {
                    if (snobotSimDriverStation) {
                        DataAccessorFactory.getInstance().getDriverStationAccessor().waitForNextUpdateLoop();
                    }
                    DataAccessorFactory.getInstance().getSimulatorDataAccessor().updateSimulatorComponents();

                    bitBucketsSimulator.update();
                    frame.updateLoop();
                    sendJoystickUpdate();
                }
            } catch (Throwable e) {
                sLOGGER.log(Level.FATAL, "Encountered fatal error, will exit.  Error: " + e.getMessage(), e);
                exitWithError();
            }
        };
    }

    private Runnable createRobotThread() {
        return () -> {
            try {
                DriverStation.getInstance();
                DataAccessorFactory.getInstance().getDriverStationAccessor().waitForNextUpdateLoop();
                bitBucketsRobot.startCompetition();
            } catch (UnsatisfiedLinkError e) {
                sLOGGER.log(Level.FATAL,
                        "Unsatisfied link error.  This likely means that there is a native "
                                + "call in WpiLib or the NetworkTables libraries.  Please tell PJ so he can mock it out.\n\nError Message: " + e,
                        e);
                exitWithError();
            } catch (Exception e) {
                sLOGGER.log(Level.FATAL, "Unexpected exception, shutting down simulator", e);
                exitWithError();
            }
        };
    }

}
