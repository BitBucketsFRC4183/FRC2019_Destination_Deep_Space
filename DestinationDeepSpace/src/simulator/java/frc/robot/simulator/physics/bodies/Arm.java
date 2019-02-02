package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
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
        PolygonShape rightArmLowerShape=new PolygonShape();
        PolygonShape rightArmMidShape=new PolygonShape();
        PolygonShape rightArmUpperShape=new PolygonShape();
        PolygonShape leftArmLowerShape=new PolygonShape();
        PolygonShape leftArmMidShape=new PolygonShape();
        PolygonShape leftArmUpperShape=new PolygonShape();
//        baseShape.setAsBox(halfWidth, halfHeight);
        /* Build a box with vertices, hw and hh are halfWidth, halfHeight

        (-hw,hh)  *-------* (hw,hh)
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
        (-hw,-hh) *-------* (hw,-hh)

         */        

        Vector2[] baseVertices =  new Vector2[] {
            new Vector2(-1, 0),
            new Vector2(1,  0),
            new Vector2(1,   14),
            new Vector2(-1,  14)
        };

        Vector2[] rightArmLowerVertices =  new Vector2[] {
            new Vector2(1,   12),
            new Vector2(5, 12),
            new Vector2(4.665f, 14),
            new Vector2(1, 14),
        };

        Vector2[] rightArmMidVertices =  new Vector2[] {
            new Vector2(5, 12),
            new Vector2(8.665f, 16),
            new Vector2(6.665f, 16),
            new Vector2(4.665f, 14),
        };

        Vector2[] rightArmUpperVertices =  new Vector2[] {
            new Vector2(8.665f, 16),
            new Vector2(8.665f, 21),
            new Vector2(6.665f, 21),
            new Vector2(6.665f, 16),
        };

        Vector2[] leftArmLowerVertices =  new Vector2[] {
            new Vector2(-5,      12),
            new Vector2(-1,      12),
            new Vector2(-1,      14),
            new Vector2(-4.665f, 14),
        };

        Vector2[] leftArmMidVertices =  new Vector2[] {
            new Vector2(-4.665f, 14),
            new Vector2(-6.665f, 16),
            new Vector2(-8.665f, 16),
            new Vector2(-5,      12),
        };

        Vector2[] leftArmUpperVertices =  new Vector2[] {
            new Vector2(-6.665f, 16),
            new Vector2(-6.665f, 21),
            new Vector2(-8.665f, 21),
            new Vector2(-8.665f, 16),
        };

        transformInchesToMeters(baseVertices);
        baseShape.set(baseVertices);

        transformInchesToMeters(rightArmLowerVertices);
        rightArmLowerShape.set(rightArmLowerVertices);

        transformInchesToMeters(rightArmMidVertices);
        rightArmMidShape.set(rightArmMidVertices);

        transformInchesToMeters(rightArmUpperVertices);
        rightArmUpperShape.set(rightArmUpperVertices);

        transformInchesToMeters(leftArmLowerVertices);
        leftArmLowerShape.set(leftArmLowerVertices);

        transformInchesToMeters(leftArmMidVertices);
        leftArmMidShape.set(leftArmMidVertices);

        transformInchesToMeters(leftArmUpperVertices);
        leftArmUpperShape.set(leftArmUpperVertices);        

        setSize(8.665f * 2 * MathConstants.INCHES_TO_METERS, 21 * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth()/2, 0);

        body = world.createBody(armBodyDef);

        // Create a composite fixture for the arm
        armFixtureDef.friction = 1f;
        armFixtureDef.restitution = 0;
        armFixtureDef.density = 0.1f;

        armFixtureDef.shape = baseShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = rightArmLowerShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = rightArmMidShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = rightArmUpperShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = leftArmLowerShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = leftArmMidShape;
        body.createFixture(armFixtureDef);

        armFixtureDef.shape = leftArmUpperShape;
        body.createFixture(armFixtureDef);
        
        body.setTransform(x, y, 0);
    }

    private void transformInchesToMeters(Vector2[] vertices) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].x = vertices[i].x * MathConstants.INCHES_TO_METERS;
            vertices[i].y = vertices[i].y * MathConstants.INCHES_TO_METERS;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        setRotation(body.getAngle()*  MathUtils.radiansToDegrees);
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y);
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