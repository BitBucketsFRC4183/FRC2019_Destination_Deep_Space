package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import frc.robot.simulator.physics.MathConstants;

public class Arm extends Image {
    public Body body;
    private World world;
    BodyDef armBodyDef;
    FixtureDef armFixtureDef;
    PolygonShape armShape;

    public Arm(World world, float x, float y) {
        super(new Texture("assets/Arm.png"));
        this.world = world;    
        
        BodyDef armBodyDef = new BodyDef();
        FixtureDef armFixtureDef = new FixtureDef();
        armBodyDef.type = BodyType.DynamicBody;
        PolygonShape baseShape=new PolygonShape();
        baseShape.setAsBox(2f*MathConstants.INCHES_TO_METERS/2, 12f*MathConstants.INCHES_TO_METERS/2);
        setSize(2f * MathConstants.INCHES_TO_METERS,12f * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);
        armFixtureDef.friction = 1f;
        armFixtureDef.restitution = 0;
        armFixtureDef.density = 0.1f;
        armFixtureDef.shape = baseShape;
        body = world.createBody(armBodyDef);
        body.createFixture(armFixtureDef);
        body.setTransform(x, y, 0);
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

    /**
     * @return the body
     */
    public Body getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(Body body) {
        this.body = body;
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
     * @return the armBodyDef
     */
    public BodyDef getarmBodyDef() {
        return armBodyDef;
    }

    /**
     * @param armBodyDef the armBodyDef to set
     */
    public void setarmBodyDef(BodyDef armBodyDef) {
        this.armBodyDef = armBodyDef;
    }

    /**
     * @return the armFixtureDef
     */
    public FixtureDef getarmFixtureDef() {
        return armFixtureDef;
    }

    /**
     * @param armFixtureDef the armFixtureDef to set
     */
    public void setarmFixtureDef(FixtureDef armFixtureDef) {
        this.armFixtureDef = armFixtureDef;
    }

    /**
     * @return the armShape
     */
    public PolygonShape getarmShape() {
        return armShape;
    }

    /**
     * @param armShape the armShape to set
     */
    public void setarmShape(PolygonShape armShape) {
        this.armShape = armShape;
    }
    
}