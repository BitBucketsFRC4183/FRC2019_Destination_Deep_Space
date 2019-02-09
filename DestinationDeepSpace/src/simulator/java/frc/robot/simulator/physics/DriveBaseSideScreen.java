package frc.robot.simulator.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import frc.robot.Robot;
import frc.robot.simulator.physics.bodies.Ball;
import frc.robot.simulator.physics.bodies.DriveBaseSide;
import frc.robot.simulator.physics.bodies.Flooer;
import frc.robot.subsystem.drive.DriveSubsystem;
import frc.robot.subsystem.scoring.ScoringSubsystem;

/**
 * A simple screen displaying the drive base in a side view
 */
public class DriveBaseSideScreen extends AbstractPhysicsSimulationScreen {

    private Robot robot;
    private Stage stage;
    private PhysicsSimulation physicsSimulation;
    private World world;
    private DriveBaseSide driveBaseLeftSide;
    private DriveBaseSide driveBaseRightSide;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private MotorStatsText motorStatsText;
    private Texture ballTexture;
    private Ball ball;

    // no gravity, let stuff float

    // Ignore the previous statement.
    float g = 9.80665f;
    float grav = -g;


    private Vector2 gravity = new Vector2(0, grav);

    public DriveBaseSideScreen(PhysicsSimulation physicsSimulation, Robot robot) {
        ballTexture = new Texture("assets/Ball.png");
        this.physicsSimulation = physicsSimulation;
        this.robot = robot;

        // make our camera a 5x5 meter space
        float camWidth = 5;
        float camHeight = 5;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, camWidth, camHeight * ((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth()));
        camera.update();
        Viewport viewport = new FitViewport(camWidth, camHeight * ((float)Gdx.graphics.getHeight() / Gdx.graphics.getWidth()), camera);

        stage = new Stage(viewport);
        debugRenderer = new Box2DDebugRenderer();

        // create a world to simulate the physics in
        world = new World(gravity, true);

        // create a couple actors
        driveBaseLeftSide = new DriveBaseSide(world, 2f, .6f);
        Flooer flooer = new Flooer(world);
        ball = new Ball(world, ballTexture, 10, 10);
        motorStatsText = new MotorStatsText(camWidth / 2, camHeight/2);

        stage.addActor(driveBaseLeftSide);
        stage.addActor(flooer);
        stage.addActor(motorStatsText);
        stage.addActor(ball);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0.7f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // wheels move at 20000 ticks / 100ms
        // or 2.44140625 revolutions per 100ms
        // or 24.4 revolutions per second
        // motor speed is in radians per second
        // 2 pi radians is a full revolution so
        // 24.4 rev/s = 2(pi)*24.4 radians / sec so about 50
        float motorSpeed = 50f;

        driveBaseLeftSide.setFrontMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getLeftMasterMotor().getMotorOutputPercent()));
        /// TODO: There could be more than one (1) slave motor, index 0 = master, above
        driveBaseLeftSide.setRearMotorSpeed((float) (motorSpeed * DriveSubsystem.instance().getLeftMotor(1).getMotorOutputPercent()));
        driveBaseLeftSide.setArmAngle((float) (MathUtils.degreesToRadians * (ScoringSubsystem.instance().getAngle_deg())));
        driveBaseLeftSide.setTopRollerSpeed((float) (motorSpeed * -ScoringSubsystem.instance().getRollerMotor().getMotorOutputPercent()));
        driveBaseLeftSide.setBottomRollerSpeed((float) (motorSpeed * ScoringSubsystem.instance().getRollerMotor().getMotorOutputPercent()));

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS) && camera.zoom > 0.1) {
            camera.zoom -= 0.01*camera.zoom;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS) && camera.zoom > 0.1) {
            camera.zoom += 0.01*camera.zoom;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            camera.translate(-2.0f*camera.zoom*delta, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            camera.translate(2*camera.zoom*delta, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.G)) {
            camera.translate(0, -2*camera.zoom*delta, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.T)) {
            camera.translate(0, 2*camera.zoom*delta, 0);
        }

        camera.update();

        stage.act();
        stage.draw();
        debugRenderer.render(world, stage.getCamera().combined);
        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


}
