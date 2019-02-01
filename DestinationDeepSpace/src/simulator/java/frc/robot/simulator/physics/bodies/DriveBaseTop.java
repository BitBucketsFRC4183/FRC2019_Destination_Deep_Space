package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import frc.robot.simulator.physics.MathConstants;

/**
 * Created by julienvillegas on 31/01/2017.
 */

public class DriveBaseTop extends Image {

    private Body body;
    private World world;

    public static final float INCHES_TO_METERS = 0.0254f;

    public DriveBaseTop(World world, float x, float y) {
        super(new Texture("assets/drive_base_top_down.png"));
        this.world = world;
        setPosition(x, y);
        setSize(30 * MathConstants.INCHES_TO_METERS, 30 * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());
        // Create a body in the world using our definition
        body = this.world.createBody(bodyDef);
        body.setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);

        // Now define the dimensions of the physics shape
        PolygonShape shape = new PolygonShape();

        // our actual collision box is shaped like the drive base
        // dimensions of texture is 473x512 for a ratio of .92
        shape.setAsBox(30*.92f * MathConstants.INCHES_TO_METERS/2, 30 * MathConstants.INCHES_TO_METERS/2);

        // FixtureDef is a confusing expression for physical properties
        // Basically is where you, in addition to defining the shape of the body
        // you also define it's properties like density, restitution and others we will see shortly
        // If you are wondering, density and area are used to calculate over all mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1;
        fixtureDef.friction = 1;
        fixtureDef.restitution = .2f;
        Fixture fixture = body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(body.getAngle()*  MathUtils.radiansToDegrees);
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);
    }

    public void setTransform(float x, float y, float angle) {
        this.getBody().setTransform(x, y, angle);

    }

    public Body getBody() {
        return body;
    }

	public void setFrontLeftMotorOutput(float f) {
	}

	public void setFrontRightOutput(float f) {
	}

	public void setRearLeftMotorOutput(float f) {
	}

	public void setRearRightMotorOutput(float f) {
	}
}
