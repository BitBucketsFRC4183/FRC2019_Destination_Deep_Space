package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BallTop extends Ball {

    public BallTop(World world, Texture texture, float x, float y) {
        super(world, texture, x, y);
    }

    // void updateFriction() {
    //     //lateral linear velocity
    //     float maxLateralImpulse = 1f;
    //     Vector2 lateralVelocity = getLateralVelocity().scl(-1f);
    //     Vector2 impulse = lateralVelocity.scl(body.getMass());
    //     if (impulse.len() > maxLateralImpulse)
    //         impulse.scl(maxLateralImpulse / impulse.len());
    //     body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

    //     //angular velocity
    //     body.applyAngularImpulse(0.1f * body.getInertia() * -body.getAngularVelocity(), true);

    //     //forward linear velocity
    //     Vector2 currentForwardNormal = getForwardVelocity();
    //     float currentForwardSpeed = currentForwardNormal.len();
    //     float dragForceMagnitude = -2 * currentForwardSpeed;
    //     body.applyForce(currentForwardNormal.scl(dragForceMagnitude), body.getWorldCenter(), true);
    // }

    // @Override
    // public void act(float delta) {
    //     updateFriction();
    //     super.act(delta);
    // }


}