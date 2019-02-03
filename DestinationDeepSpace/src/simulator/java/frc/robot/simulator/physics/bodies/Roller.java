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

public class Roller extends Image {

    private float diameter = 3.5f*MathConstants.INCHES_TO_METERS;

    private Body roller;
    private World world;
    private BodyDef rollerBodyDef = new BodyDef();
    private FixtureDef rollerFixtureDef = new FixtureDef();

    public Roller(World world, float x, float y) {
        super(new Texture("assets/Roller.png"));
        this.world = world;

        rollerBodyDef.type = BodyType.DynamicBody;
        roller = world.createBody(rollerBodyDef);

        CircleShape rollerShape = new CircleShape();
        rollerShape.setRadius(diameter/2);

        rollerFixtureDef.friction = 1f;
        rollerFixtureDef.restitution = 0.0f;
        rollerFixtureDef.shape = rollerShape;
        rollerFixtureDef.density = 0.5f;
        roller.createFixture(rollerFixtureDef);
        roller.setTransform(x, y, (float)Math.PI);
        rollerShape.dispose();
        setSize(diameter, diameter);
        setOrigin(getWidth() / 2, getHeight() / 2);

    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(roller.getAngle()*  MathUtils.radiansToDegrees);
        setPosition(roller.getPosition().x-getWidth()/2,roller.getPosition().y-getHeight()/2);
    }

    /**
     * @return the body
     */
    public Body getBody() {
        return roller;
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
     * @return the rollerBodyDef
     */
    public BodyDef getRollerBodyDef() {
        return rollerBodyDef;
    }

    /**
     * @param rollerBodyDef the rollerBodyDef to set
     */
    public void setRollerBodyDef(BodyDef rollerBodyDef) {
        this.rollerBodyDef = rollerBodyDef;
    }

    /**
     * @return the rollerFixtureDef
     */
    public FixtureDef getRollerFixtureDef() {
        return rollerFixtureDef;
    }

    /**
     * @param rollerFixtureDef the rollerFixtureDef to set
     */
    public void setRollerFixtureDef(FixtureDef rollerFixtureDef) {
        this.rollerFixtureDef = rollerFixtureDef;
    }
}