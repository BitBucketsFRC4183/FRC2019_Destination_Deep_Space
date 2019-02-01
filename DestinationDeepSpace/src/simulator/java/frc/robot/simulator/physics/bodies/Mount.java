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

public class Mount extends Image {
    public Body body;
    private World world;
    BodyDef mountBodyDef;
    FixtureDef mountFixtureDef;
    PolygonShape mountShape;
    public  Mount(World world, float x, float y) {
        super(new Texture("assets/Generic_Texture.png"));
        this.world = world;    
        
        BodyDef mountBodyDef = new BodyDef();
        FixtureDef mountFixtureDef = new FixtureDef();
        mountBodyDef.type = BodyType.DynamicBody;
        PolygonShape baseShape=new PolygonShape();
        baseShape.setAsBox(2f*MathConstants.INCHES_TO_METERS/2, 15f*MathConstants.INCHES_TO_METERS/2);
        setSize(2f * MathConstants.INCHES_TO_METERS,15f * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);
        mountFixtureDef.friction = 1f;
        mountFixtureDef.restitution = 0;
        mountFixtureDef.density = 0.1f;
        mountFixtureDef.shape = baseShape;
        body = world.createBody(mountBodyDef);
        body.createFixture(mountFixtureDef);
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
     * @return the mountBodyDef
     */
    public BodyDef getMountBodyDef() {
        return mountBodyDef;
    }

    /**
     * @param mountBodyDef the mountBodyDef to set
     */
    public void setMountBodyDef(BodyDef mountBodyDef) {
        this.mountBodyDef = mountBodyDef;
    }

    /**
     * @return the mountFixtureDef
     */
    public FixtureDef getMountFixtureDef() {
        return mountFixtureDef;
    }

    /**
     * @param mountFixtureDef the mountFixtureDef to set
     */
    public void setMountFixtureDef(FixtureDef mountFixtureDef) {
        this.mountFixtureDef = mountFixtureDef;
    }

    /**
     * @return the mountShape
     */
    public PolygonShape getMountShape() {
        return mountShape;
    }

    /**
     * @param mountShape the mountShape to set
     */
    public void setMountShape(PolygonShape mountShape) {
        this.mountShape = mountShape;
    }
    
}