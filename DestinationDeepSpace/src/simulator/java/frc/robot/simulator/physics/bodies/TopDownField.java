package frc.robot.simulator.physics.bodies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import frc.robot.simulator.physics.MathConstants;

public class TopDownField extends AbstractPhysicsBody {

    // dimensions from
    // https://github.com/wpilibsuite/PathWeaver/blob/master/src/main/resources/edu/wpi/first/pathweaver/2019-deepspace.json
    private static final Vector2 imageSize = new Vector2(1592, 656);
    private static final Vector2 topLeft = new Vector2(217, 40);
    private static final Vector2 bottomRight = new Vector2(1372, 615);

    // the inner field part in ft is 54x27. We use this to compute our entire field
    // size, in meters
    private static final Vector2 fieldSize = new Vector2(54 * 12 * MathConstants.INCHES_TO_METERS,
            27 * 12 * MathConstants.INCHES_TO_METERS);

    // feet per pixel is width in feet divided by width of inner field pixels
    private static final float ftPerPixelWidth = 54f / (bottomRight.x - topLeft.x);
    private static final float inchesPerPixelWidth = ftPerPixelWidth * 12;
    private static final float metersPerPixelWidth = inchesPerPixelWidth * MathConstants.INCHES_TO_METERS;
    private static final float ftPerPixelHeight = 27f / (bottomRight.y - topLeft.y);
    private static final float inchesPerPixelHeight = ftPerPixelHeight * 12;
    private static final float metersPerPixelHeight = inchesPerPixelHeight * MathConstants.INCHES_TO_METERS;

    // set the size of the field, in meters, to the size of the image, based on what
    // we know of the inner size in feet. yikes
    private static final float width = inchesPerPixelWidth * imageSize.x * MathConstants.INCHES_TO_METERS;
    private static final float height = inchesPerPixelHeight * imageSize.y * MathConstants.INCHES_TO_METERS;

    public TopDownField(World world) {
        super(world, new Texture("assets/2019-field.jpg"), 0, 0, width, height);
    }

    // 1220x400 is the robot start for red
    public Vector2 getFieldCoordsForPixel(int x, int y) {
        return new Vector2(x * metersPerPixelWidth, y * metersPerPixelHeight);
    }

    /**
     * Override body def to be a static. We don't want our walls or structures to
     * move (this time)
     */
    protected void initBodyDef() {
        bodyDef.type = BodyType.StaticBody;
    }

    @Override
    protected void initFixtureDef() {
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0;
    }

    /**
     * This is a bit of a hack, but composite shapes override createShapes() and
     * don't call createShape
     */
    @Override
    protected Shape createShape() {
        throw new IllegalArgumentException("Composite shapes don't use createShape");
    }

    /**
     * The various borders are all in pixels (from the image), then translated into
     * meters
     */
    @Override
    protected List<Shape> createShapes() {
        PolygonShape topBorder = new PolygonShape();
        float topBorderHeight = topLeft.y * metersPerPixelHeight;
        Vector2 topCenter = new Vector2(0, height / 2 - topBorderHeight / 2);
        topBorder.setAsBox(width / 2, topBorderHeight / 2, topCenter, 0);

        PolygonShape bottomBorder = new PolygonShape();
        float bottomBorderHeight = (imageSize.y - bottomRight.y) * metersPerPixelHeight;
        Vector2 bottomCenter = new Vector2(0, -height / 2 + bottomBorderHeight / 2);
        bottomBorder.setAsBox(width / 2, bottomBorderHeight / 2, bottomCenter, 0);

        PolygonShape leftBorder = new PolygonShape();
        float leftBorderWidth = topLeft.x * metersPerPixelWidth;
        Vector2 leftCenter = new Vector2(-width / 2 + leftBorderWidth / 2, 0);
        leftBorder.setAsBox(leftBorderWidth / 2, height / 2, leftCenter, 0);

        PolygonShape leftPlatform = new PolygonShape();
        float leftPlatformWidth = 80 * metersPerPixelWidth;
        Vector2 leftPlatformCenter = new Vector2(-width / 2 + leftBorderWidth + leftPlatformWidth / 2, 0);
        leftPlatform.setAsBox(leftPlatformWidth / 2, 230.0f / 2 * metersPerPixelHeight, leftPlatformCenter, 0);

        PolygonShape rightBorder = new PolygonShape();
        float rightBorderWidth = (imageSize.x - bottomRight.x) * metersPerPixelWidth;
        Vector2 rightCenter = new Vector2(width / 2 - rightBorderWidth / 2, 0);
        rightBorder.setAsBox(rightBorderWidth / 2, height / 2, rightCenter, 0);

        PolygonShape cargoShip = new PolygonShape();
        rightBorder.setAsBox(366.0f / 2 * metersPerPixelWidth, 90.0f / 2 * metersPerPixelHeight);

        PolygonShape rightPlatform = new PolygonShape();
        float rightPlatformWidth = 80 * metersPerPixelWidth;
        Vector2 rightPlatformCenter = new Vector2(width / 2 - rightBorderWidth - rightPlatformWidth / 2, 0);
        rightPlatform.setAsBox(rightPlatformWidth / 2, 230.0f / 2 * metersPerPixelHeight, rightPlatformCenter, 0);

        PolygonShape upperLeftRocket = new PolygonShape();
        Vector2[] upperLeftRocketVerices = new Vector2[] { 
            new Vector2(590, 55), 
            new Vector2(605, 90),
            new Vector2(640, 90), 
            new Vector2(655, 55) 
        };
        transformPixelVerticesToLocalMeters(upperLeftRocketVerices);
        upperLeftRocket.set(upperLeftRocketVerices);

        PolygonShape upperRightRocket = new PolygonShape();
        Vector2[] upperRightRocketVerices = new Vector2[] { 
            new Vector2(930, 55), 
            new Vector2(945, 90),
            new Vector2(980, 90), 
            new Vector2(995, 55) 
        };
        transformPixelVerticesToLocalMeters(upperRightRocketVerices);
        upperRightRocket.set(upperRightRocketVerices);

        PolygonShape lowerLeftRocket = new PolygonShape();
        Vector2[] lowerLeftRocketVerices = new Vector2[] { 
            new Vector2(590, 600), 
            new Vector2(605, 565),
            new Vector2(640, 565), 
            new Vector2(655, 600) 
        };
        transformPixelVerticesToLocalMeters(lowerLeftRocketVerices);
        lowerLeftRocket.set(lowerLeftRocketVerices);

        PolygonShape lowerRightRocket = new PolygonShape();
        Vector2[] lowerRightRocketVerices = new Vector2[] { 
            new Vector2(930, 600), 
            new Vector2(945, 565),
            new Vector2(980, 565), 
            new Vector2(995, 600) 
        };
        transformPixelVerticesToLocalMeters(lowerRightRocketVerices);
        lowerRightRocket.set(lowerRightRocketVerices);

        List<Shape> shapes = new ArrayList<>();
        shapes.add(topBorder);
        shapes.add(bottomBorder);
        shapes.add(leftBorder);
        shapes.add(leftPlatform);
        shapes.add(rightBorder);
        shapes.add(rightPlatform);
        shapes.add(cargoShip);
        shapes.add(upperLeftRocket);
        shapes.add(upperRightRocket);
        shapes.add(lowerLeftRocket);
        shapes.add(lowerRightRocket);

        return shapes;
    }

    /**
     * Transform vertices in image pixels to local coordinate meters
     * The image is 1592x656
     * The origin is the center of the image, or (796, 328)
     * 
     * @param vertices
     */
    public void transformPixelVerticesToLocalMeters(Vector2[] vertices) {
        for (Vector2 v : vertices) {
            v.x = -(imageSize.x / 2 - v.x);
            v.y = imageSize.y / 2 - v.y;
            v.scl(metersPerPixelWidth, metersPerPixelHeight);
        }
    }

}
