package frc.robot.simulator;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.snobot.simulator.DefaultDataAccessorFactory;
import com.snobot.simulator.joysticks.IMockJoystick;
import com.snobot.simulator.joysticks.JoystickFactory;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot.simulator.wrapper_accessors.SimulatorDataAccessor;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Robot;
import frc.robot.simulator.physics.PhysicsSimulation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The BitBucketsSimulatorLauncher launches a simulator, a robot and a physics simulation
 */
public class BitBucketsSimulatorLauncher {
    private static final Logger log = LogManager.getLogger(BitBucketsSimulator.class);

    private Robot robot;
    private BitBucketsSimulator simulator;

    protected boolean runningSimulator;
    protected Thread robotThread;
    protected Thread simulatorThread;


    /**
     * Called by the launcher
     */
    public void launch() {
        // initialize the simulator data stuff
        DefaultDataAccessorFactory.initalize();

        // set some simulator defaults
        DataAccessorFactory.getInstance().getSimulatorDataAccessor().setLogLevel(SimulatorDataAccessor.SnobotLogLevel.DEBUG);
        DataAccessorFactory.getInstance().getSimulatorDataAccessor().reset();

        // Create a custom simulator (currently doesn't do anything)
        simulator = new BitBucketsSimulator();
        simulator.loadConfig("simulator_config/simulator_config.yml");

        // create an instance of our robot
        robot = new Robot();
        robot.robotInit();

        // setup our custom simulator
        simulator.setRobot(robot);
        simulator.create();

        // create a couple threads. One thread updates the robot, one updates the simulator
        robotThread = new Thread(robotThread(), "RobotThread");
        simulatorThread = new Thread(simulationThread(), "SimulatorThread");

        // start the threads
        log.log(Level.INFO, "Starting simulator");
        runningSimulator = true;
        simulatorThread.start();
        robotThread.start();

        // create the physics simulation. This is the actual drawing window
        createPhysicsSimulation();
    }

    private void createPhysicsSimulation() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // field is 1592 × 656
        config.width = 1592;
        config.height = 900;
    //    config.width = 1592;
    //    config.height = 656;
        config.foregroundFPS = 60;

        // this creates a new application with access to our robot and simulator
        new LwjglApplication(new PhysicsSimulation(robot), config);
    }

    /**
     * Send joystick updates to the mock DriverStation. These joysticks have to be
     * configuration in SnobotSim
     */
    private void sendJoystickUpdate() {
        IMockJoystick[] joysticks = JoystickFactory.getInstance().getAll();
        for (int i = 0; i < joysticks.length; ++i) {
            IMockJoystick joystick = joysticks[i];
            DataAccessorFactory.getInstance().getDriverStationAccessor().setJoystickInformation(i, joystick.getAxisValues(),
                    joystick.getPovValues(), joystick.getButtonCount(), joystick.getButtonMask());
        }
    }

    /**
     * A thread to update our custom simulator.
     * @return
     */
    private Runnable simulationThread() {
        return () -> {
            try {
                DataAccessorFactory.getInstance().getDriverStationAccessor().waitForProgramToStart();

                String errors = DataAccessorFactory.getInstance().getInitializationErrors();
                if (!"".equals(errors)) {
                    log.error("Startiup errors: " + errors);
                }

                while (runningSimulator) {
                    // make sure our robot is not disabled while simulating.
                    // IT'S ALWAYS LIVE
                    DataAccessorFactory.getInstance().getDriverStationAccessor().setDisabled(false);
                    DataAccessorFactory.getInstance().getDriverStationAccessor().waitForNextUpdateLoop();
                    DataAccessorFactory.getInstance().getSimulatorDataAccessor().updateSimulatorComponents();

                    // call update() on BitBucketsSimulator
                    simulator.update();

                    // send a joystick update every loop
                    sendJoystickUpdate();
                }
            } catch (Throwable e) {
                log.log(Level.FATAL, "Encountered fatal error, will exit.  Error: " + e.getMessage(), e);
                exitWithError();
            }
        };
    }

    /**
     * A thread to update our robot periodically
     * @return
     */
    private Runnable robotThread() {
        return () -> {
            try {
                DriverStation.getInstance();
                DataAccessorFactory.getInstance().getDriverStationAccessor().waitForNextUpdateLoop();
                robot.startCompetition();
            } catch (UnsatisfiedLinkError e) {
                log.log(Level.FATAL,
                        "Unsatisfied link error.  This likely means that there is a native "
                                + "call in WpiLib or the NetworkTables libraries.  Please tell PJ so he can mock it out.\n\nError Message: " + e,
                        e);
                exitWithError();
            } catch (Exception e) {
                log.log(Level.FATAL, "Unexpected exception, shutting down simulator", e);
                exitWithError();
            }
        };
    }

    /**
     * Stops the all the simulator threads (that we can stop)
     */
    protected void stop() {
        log.log(Level.INFO, "Stopping simulator");

        if (simulatorThread != null) {
            try {
                runningSimulator = false;
                simulatorThread.join();
                simulatorThread = null;
            } catch (InterruptedException e) {
                log.log(Level.FATAL, e);
            }
        }

        if (robotThread != null) {
            robotThread.interrupt();
            robotThread.stop();
            robotThread = null;
        }
    }

    /**
     * Shuts down the simulator when an error has occurred
     */
    protected void exitWithError() {
        stop();
        System.exit(-1);
    }
}
