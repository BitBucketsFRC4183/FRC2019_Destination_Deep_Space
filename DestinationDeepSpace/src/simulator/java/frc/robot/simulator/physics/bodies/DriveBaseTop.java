package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import frc.robot.simulator.physics.MathConstants;

/**
 * Top down drive base for testing drive subsystem
 */

public class DriveBaseTop extends AbstractPhysicsBody {

    private float maxDriveForce = 2f;

    public DriveBaseTop(World world, float x, float y) {
        super(world, new Texture("assets/drive_base_top_down.png"), x, y, 30 * MathConstants.INCHES_TO_METERS,
                30 * MathConstants.INCHES_TO_METERS);
    }

    @Override
    protected void initFixtureDef() {
        fixtureDef.density = 1;
        fixtureDef.friction = 1;
        fixtureDef.restitution = .2f;
    }

    @Override
    protected Shape createShape() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(30 * .92f * MathConstants.INCHES_TO_METERS / 2, 30 * MathConstants.INCHES_TO_METERS / 2);
        return shape;
    }

    private Vector2 getForceVector(float factor) {
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        Vector2 forceVector = currentForwardNormal.scl(maxDriveForce);
        forceVector = forceVector.scl(factor);
        return forceVector;
    }

    public void setFrontLeftMotorOutput(float motorOutput) {
        if (motorOutput != 0) {
            Vector2 forceVector = getForceVector(motorOutput);
            body.applyForce(forceVector, body.getWorldPoint(new Vector2(-getWidth() / 2, getHeight() / 2)), true);
        }
    }

    public void setFrontRightOutput(float motorOutput) {
        if (motorOutput != 0) {
            Vector2 forceVector = getForceVector(motorOutput);
            body.applyForce(forceVector, body.getWorldPoint(new Vector2(getWidth() / 2, getHeight() / 2)), true);
        }
    }

    public void setRearLeftMotorOutput(float motorOutput) {
        if (motorOutput != 0) {
            Vector2 forceVector = getForceVector(motorOutput);
            body.applyForce(forceVector, body.getWorldPoint(new Vector2(-getWidth() / 2, -getHeight() / 2)), true);
        }
    }

    public void setRearRightMotorOutput(float motorOutput) {
        if (motorOutput != 0) {
            Vector2 forceVector = getForceVector(motorOutput);
            body.applyForce(forceVector, body.getWorldPoint(new Vector2(getWidth() / 2, -getHeight() / 2)), true);
        }
    }

    void updateFriction() {
        //lateral linear velocity
        float maxLateralImpulse = 3f;
        Vector2 lateralVelocity = getLateralVelocity().scl(-1f);
        Vector2 impulse = lateralVelocity.scl(body.getMass());
        if (impulse.len() > maxLateralImpulse)
            impulse.scl(maxLateralImpulse / impulse.len());
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        //angular velocity
        body.applyAngularImpulse(0.1f * body.getInertia() * -body.getAngularVelocity(), true);

        //forward linear velocity
        Vector2 currentForwardNormal = getForwardVelocity();
        float currentForwardSpeed = currentForwardNormal.len();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        body.applyForce(currentForwardNormal.scl(dragForceMagnitude), body.getWorldCenter(), true);
    }

    @Override
    public void act(float delta) {
        updateFriction();
        super.act(delta);
    }


}
