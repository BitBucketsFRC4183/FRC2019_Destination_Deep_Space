package frc.robot.simulator.physics.bodies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import frc.robot.simulator.physics.MathConstants;

/**
 * Abstract PhysicsBody class to handle all the glue of creating a physics body.
 * Override methods as necessary to create your custom physics body.
 * 
 * This class also inherits from Image so it can render a texture in the place of the body
 */
public abstract class AbstractPhysicsBody extends Image {
    protected Body body;
    protected World world;
    protected BodyDef bodyDef;
    protected FixtureDef fixtureDef;

    /**
     * Constructor requires a world, texture, coords and dimensions
     */
    public AbstractPhysicsBody(World world, Texture texture, float x, float y, float width, float height) {
        super(texture);
        this.world = world;
        
        // Create the BodyDef  We will fill them in with init fuctnions
        bodyDef = new BodyDef();
        initBodyDef();

        // now that we have a body def, create a body in the world
        body = world.createBody(bodyDef);

        // create a fixture def for our shapes
        fixtureDef = new FixtureDef();
        initFixtureDef();

        // create shapes and fixtures from them
        List<Shape> shapes = createShapes();
        createFixtures(shapes);

        // dispose the shapes
        for (Shape shape : shapes) {
            shape.dispose();
        }

        // move the body to x,y in world space
        body.setTransform(x, y, 0);

        // Set the texture world size and origin on the body
        setSize(width, height);
        initImageOrigin();
    }


    /**
     * Initialize the body def, override to do something other than setting it to a
     * DynamicBody
     */
    protected void initBodyDef() {
        bodyDef.type = BodyType.DynamicBody;
    }

        /**
     * Initialize the fixtureDef with friction, density and restitution values
     */
    protected abstract void initFixtureDef();

    /**
     * Create one or more shapes for this body's fixtures
     */
    protected List<Shape> createShapes() {
        List<Shape> shapes = new ArrayList<>();
        Shape shape = createShape();
        shapes.add(shape);
        return shapes;
    }

    protected abstract Shape createShape();

    /**
     * Create fixtures from every shape
     * @param shapes
     */
    protected void createFixtures(List<Shape> shapes) {
        for (Shape shape : shapes) {
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
        }
    }

    /**
     * Initialize the origin. Override this if you don't want the origin to be in the center of the shape
     */
    protected void initImageOrigin() {
        setOrigin(getWidth()/2, getHeight()/2);
    }
    
    /**
     * Called by stage every render loop. Override this to draw children of a PhysicsBody, such
     * as when the DriveBaseSide draws the mount, arm, wheels, etc.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    /**
     * Called by stage every render loop. This moves the texture in the world based on the physics body's 
     * translations.
     * 
     * Override this to call act on childre, such as when the DriveBaseSide acts on the mount, arm, wheels, etc.
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(body.getAngle()*  MathUtils.radiansToDegrees);
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);
    }

    /**
     * @return the body
     */
    public Body getBody() {
        return body;
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    /**
     * get the lateral velocity of a body as a new vector
     * @return
     */
    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1,0));
        float dot = currentRightNormal.dot(body.getLinearVelocity());
        return new Vector2(currentRightNormal.x * dot, currentRightNormal.y * dot);
    }

    /**
     * get the forward velocity of the body as a new vector
     * @return
     */
    Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        float dot = currentForwardNormal.dot(body.getLinearVelocity());
        return new Vector2(currentForwardNormal.x * dot, currentForwardNormal.y * dot);
    }

    public void setAngularVelocity(int angularVelocity) {
        body.setAngularVelocity(0);
    }

    public void setLinearVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    /**
     * Reset this object to a position in the world and set it's velocity and angular velocity to 0
     * @param x
     * @param y
     */
    public void resetToPosition(float x, float y) {
        setTransform(x, y, 0);
        setAngularVelocity(0);
        setLinearVelocity(0, 0);
    }   
}