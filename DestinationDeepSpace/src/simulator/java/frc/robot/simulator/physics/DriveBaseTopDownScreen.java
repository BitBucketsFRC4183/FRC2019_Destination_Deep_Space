package frc.robot.simulator.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import frc.robot.Robot;
import frc.robot.simulator.physics.bodies.DriveBaseTop;
import frc.robot.simulator.physics.bodies.DriveBaseTop;
import frc.robot.simulator.physics.bodies.TopDownField;
import frc.robot.subsystem.drive.DriveSubsystem;

/**
 * A simple screen displaying the drive base in a side view
 */
public class DriveBaseTopDownScreen extends AbstractPhysicsSimulationScreen {

    private Robot robot;
    private Stage stage;
    private PhysicsSimulation physicsSimulation;
    private World world;
    private TopDownField field;
    private DriveBaseTop driveBase;
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;

    // no gravity, let stuff float
    private Vector2 gravity = new Vector2(0, 0);


    public DriveBaseTopDownScreen(PhysicsSimulation physicsSimulation, Robot robot) {
        this.physicsSimulation = physicsSimulation;
        this.robot = robot;

        // make our camera a 5x5 meter space
        field = new TopDownField();
        float worldWidth = field.getWidth();
        float worldHeight = field.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, worldWidth,
                worldHeight);
        camera.update();
        Viewport viewport = new FitViewport(worldWidth,
                worldHeight, camera);

        stage = new Stage(viewport);
        debugRenderer = new Box2DDebugRenderer();

        // create a world to simulate the physics in
        world = new World(gravity, true);

        stage.addActor(field);

        driveBase = new DriveBaseTop(world, worldWidth/2, worldHeight/2);
        Vector2 startingPositionWorld = field.getFieldCoordsForPixel(1240, 410);
        driveBase.setTransform( startingPositionWorld.x + driveBase.getWidth()/2, worldHeight - startingPositionWorld.y, -90 * MathUtils.degreesToRadians);
        stage.addActor(driveBase);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // wheels move at 20000 ticks / 100ms
        // or 2.44140625 revolutions per 100ms
        // or 24.4 revolutions per second
        // motor speed is in radians per second
        // 2 pi radians is a full revolution so
        // 24.4 rev/s = 2(pi)*24.4 radians / sec so about 50
        float motorOutput = 1f;

        driveBase.setFrontLeftMotorOutput(
                (float) (motorOutput * DriveSubsystem.instance().getLeftFrontMotor().getMotorOutputPercent()));
        driveBase.setFrontRightOutput(
                (float) (motorOutput * DriveSubsystem.instance().getRightFrontMotor().getMotorOutputPercent()));
        driveBase.setRearLeftMotorOutput(
                (float) (motorOutput * DriveSubsystem.instance().getLeftRearMotor().getMotorOutputPercent()));
        driveBase.setRearRightMotorOutput(
                (float) (motorOutput * DriveSubsystem.instance().getRightRearMotor().getMotorOutputPercent()));

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
