package frc.robot.simulator.physics.bodies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import frc.robot.simulator.physics.MathConstants;

public class Arm extends AbstractPhysicsBody {

    public Arm(World world, float x, float y) {
        super(world, new Texture("assets/ArmTexture.png"), x, y, 8.665f * 2 * MathConstants.INCHES_TO_METERS, 21 * MathConstants.INCHES_TO_METERS);
    }

    private void transformInchesToMeters(Vector2[] vertices) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].x = vertices[i].x * MathConstants.INCHES_TO_METERS;
            vertices[i].y = vertices[i].y * MathConstants.INCHES_TO_METERS;
        }
    }

    @Override
    protected void initFixtureDef() {
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0;
        fixtureDef.density = 0.1f;
    }

    /**
     * This is a bit of a hack, but composite shapes override createShapes() and don't call
     * createShape
     */
    @Override
    protected Shape createShape() {
        throw new IllegalArgumentException("Composite shapes don't use createShape");
    }

    @Override
    protected List<Shape> createShapes() {
        PolygonShape baseShape = new PolygonShape();
        PolygonShape rightArmLowerShape = new PolygonShape();
        PolygonShape rightArmMidShape = new PolygonShape();
        PolygonShape rightArmUpperShape = new PolygonShape();
        PolygonShape leftArmLowerShape = new PolygonShape();
        PolygonShape leftArmMidShape = new PolygonShape();
        PolygonShape leftArmUpperShape = new PolygonShape();
        /* Build a box with vertices

          (-1,14) *-------* (1,14)
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
                  |       |
           (-1,0) *-------* (1,0)

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
        
        List<Shape> shapes = new ArrayList<>();
        shapes.add(baseShape);
        shapes.add(rightArmLowerShape);
        shapes.add(rightArmMidShape);
        shapes.add(rightArmUpperShape);
        shapes.add(leftArmLowerShape);
        shapes.add(leftArmMidShape);
        shapes.add(leftArmUpperShape);

        return shapes;
    }

    @Override
    protected void initImageOrigin() {
        setOrigin(getWidth() / 2, 0);
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
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y);
    }

    
}