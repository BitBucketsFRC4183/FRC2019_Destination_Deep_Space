package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import frc.robot.simulator.physics.MathConstants;

public class TopDownField extends Image {

    private final float ftPerPixelWidth;
    private final float inchesPerPixelWidth;
    private final float ftPerPixelHeight;
    private final float inchesPerPixelHeight;

    public TopDownField() {
        super(new Texture("assets/2019-field.jpg"));

        // dimensions from https://github.com/wpilibsuite/PathWeaver/blob/master/src/main/resources/edu/wpi/first/pathweaver/2019-deepspace.json
        Vector2 imageSize = new Vector2(1592, 656);
        Vector2 topLeft = new Vector2(217, 40);
        Vector2 bottomRight = new Vector2(1372, 615);

        // the inner field part in ft is 54x27. We use this to compute our entire field size, in meters
        Vector2 fieldSize = new Vector2(54  * 12 * MathConstants.INCHES_TO_METERS, 27 * 12 * MathConstants.INCHES_TO_METERS);

        // feet per pixel is width in feet divided by width of inner field pixels
        ftPerPixelWidth = 54f / (bottomRight.x - topLeft.x);
        ftPerPixelHeight = 27f / (bottomRight.y - topLeft.y);
        inchesPerPixelWidth = ftPerPixelWidth * 12;
        inchesPerPixelHeight = ftPerPixelHeight * 12;

        setSize(inchesPerPixelWidth * imageSize.x * MathConstants.INCHES_TO_METERS, inchesPerPixelHeight * imageSize.y * MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(0, 0);
    }

    // 1220x400 is the robot start for red
    public Vector2 getFieldCoordsForPixel(int x, int y) {
        return new Vector2(x * inchesPerPixelWidth * MathConstants.INCHES_TO_METERS, y * inchesPerPixelHeight * MathConstants.INCHES_TO_METERS);
    }
}
