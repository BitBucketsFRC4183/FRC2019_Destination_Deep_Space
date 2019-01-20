package frc.robot.simulator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.snobot.simulator.gui.module_widget.SpeedControllerGraphicDisplay;
import com.snobot.simulator.simulator_components.ctre.CtreTalonSrxSpeedControllerSim;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.MotorId;
import frc.robot.Robot;
import frc.robot.RobotMap;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Game extends ApplicationAdapter {

    final static Logger log = LoggerFactory.getLogger(Game.class);

    private RobotBase frcRobot;

    private Texture robotImage;
    private Rectangle robot;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Sprite robotSprite;

    List<Integer> motorPorts;

    public Game(RobotBase robot) {
        this.frcRobot = robot;
    }

    @Override
    public void create() {
        robotImage = new Texture(Gdx.files.internal("assets/junior-wire.png"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 2880, 1800);

        batch = new SpriteBatch();

        robot = new Rectangle();
        robot.width = 512;
        robot.height = 512;
        robot.x = 1440 / 2 - robot.width / 2;
        robot.y = 20;

        robotSprite = new Sprite(robotImage);

        motorPorts = DataAccessorFactory.getInstance().getSpeedControllerAccessor().getPortList();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        double leftFrontMotorSpeed = DataAccessorFactory.getInstance().getSpeedControllerAccessor().getVoltagePercentage(MotorId.LEFT_DRIVE_MOTOR_FRONT_ID + CtreTalonSrxSpeedControllerSim.sCTRE_OFFSET);
        double rightFrontMotorSpeed = DataAccessorFactory.getInstance().getSpeedControllerAccessor().getVoltagePercentage(MotorId.RIGHT_DRIVE_MOTOR_FRONT_ID + CtreTalonSrxSpeedControllerSim.sCTRE_OFFSET);


        int speed = 1000;
        robot.y += speed * Gdx.graphics.getDeltaTime() * leftFrontMotorSpeed;

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        robotSprite.setPosition(robot.x, robot.y);
//        robotSprite.setRotation(50);
        robotSprite.draw(batch);

        batch.draw(robotImage, robot.x, robot.y);
        batch.end();



    }

    @Override
    public void dispose() {
        robotImage.dispose();
        batch.dispose();
    }


}
