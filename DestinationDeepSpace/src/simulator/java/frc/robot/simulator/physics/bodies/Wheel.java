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

public class Wheel extends Image {

    private Body body;
    private World world;

    public Wheel(World world, float x, float y){
        super(new Texture("assets/pneumatic_wheels_alpha.png"));
        this.world = world;
        setPosition(x, y);
        setSize(6f * MathConstants.INCHES_TO_METERS, 6f * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX(), getY());
        // Create a body in the world using our definition
        body = this.world.createBody(bodyDef);
        body.setTransform(getX() + getWidth() / 2, getY() + getHeight() / 2, 0);

        // Now define the dimensions of the physics shape
        CircleShape shape = new CircleShape();
        // We are a box, so makes sense, no?
        // Basically set the physics polygon to a box with the same dimensions as our sprite
        shape.setRadius(getWidth()/2);

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

    public Body getBody() {
        return body;
    }
}
