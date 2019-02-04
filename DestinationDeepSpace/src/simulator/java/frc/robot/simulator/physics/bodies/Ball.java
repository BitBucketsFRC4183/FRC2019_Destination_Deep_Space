package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import frc.robot.simulator.physics.MathConstants;

public class Ball extends AbstractPhysicsBody {

    private static float diameter = 13 * MathConstants.INCHES_TO_METERS;

    public Ball(World world, Texture texture, float x, float y) {
        super(world, texture, x, y, diameter, diameter);
    }

    @Override
    protected void initFixtureDef() {
        fixtureDef.friction = 1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.density = 0.5f;
    }

    @Override
    protected Shape createShape() {
        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(diameter/2);
        return ballShape;
    }


}