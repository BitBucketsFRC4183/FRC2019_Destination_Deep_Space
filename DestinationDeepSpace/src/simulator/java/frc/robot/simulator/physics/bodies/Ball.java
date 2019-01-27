package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import frc.robot.simulator.physics.MathConstants;

public class Ball extends Image {

    private float radius = 13*MathConstants.INCHES_TO_METERS;

    private Body ball;
    private World world;
    private BodyDef ballBodyDef = new BodyDef();
    private FixtureDef ballFixtureDef = new FixtureDef();

    public Ball(World world, Texture texture, float x, float y) {
        super(texture);
        this.world = world;

        ballBodyDef.type = BodyType.DynamicBody;
        ball = world.createBody(ballBodyDef);// DropStudentTables

        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(radius/2);

        ballFixtureDef.friction = 1f;
        ballFixtureDef.restitution = 0.5f;
        ballFixtureDef.shape = ballShape;
        ballFixtureDef.density = 0.5f;
        ball.createFixture(ballFixtureDef);
        ball.setTransform(x, y, 0);
        ballShape.dispose();
        //setPosition(x, y);
        setSize(radius, radius);
        setOrigin(getWidth() / 2, getHeight() / 2);

    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(ball.getAngle()*  MathUtils.radiansToDegrees);
        setPosition(ball.getPosition().x-getWidth()/2,ball.getPosition().y-getHeight()/2);
    }

    /**
     * @return the body
     */
    public Body getBody() {
        return ball;
    }

    /**
     * @return the world
     */
    public World getWorld() {
        return world;
    }

    /**
     * @param world the world to set
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * @return the ballBodyDef
     */
    public BodyDef getBallBodyDef() {
        return ballBodyDef;
    }

    /**
     * @param ballBodyDef the ballBodyDef to set
     */
    public void setBallBodyDef(BodyDef ballBodyDef) {
        this.ballBodyDef = ballBodyDef;
    }

    /**
     * @return the ballFixtureDef
     */
    public FixtureDef getBallFixtureDef() {
        return ballFixtureDef;
    }

    /**
     * @param ballFixtureDef the ballFixtureDef to set
     */
    public void setBallFixtureDef(FixtureDef ballFixtureDef) {
        this.ballFixtureDef = ballFixtureDef;
    }
}