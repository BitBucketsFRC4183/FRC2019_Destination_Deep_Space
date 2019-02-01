package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public abstract class PhysicsImage extends Image {

    protected Body body;
    protected final World world;

    public PhysicsImage(World world) {
        super();

        this.world = world;
    }

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle()*  MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x-this.getWidth()/2,body.getPosition().y-this.getHeight()/2);
    }

    /**
     * get the lateral velocity of a body as a new vector
     * @return
     */
    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1,0));
        float dot = currentRightNormal.dot(body.getLinearVelocity());
        return new Vector2(currentRightNormal.x * dot, currentRightNormal.y * dot);
    }

    /**
     * get the forward velocity of the body as a new vector
     * @return
     */
    Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        float dot = currentForwardNormal.dot(body.getLinearVelocity());
        return new Vector2(currentForwardNormal.x * dot, currentForwardNormal.y * dot);
    }

    public void setAngularVelocity(int angularVelocity) {
        body.setAngularVelocity(0);
    }

    public void setLinearVelocity(float x, float y) {
        body.setLinearVelocity(x, y);
    }

    /**
     * Reset this object to a position in the world and set it's velocity and angular velocity to 0
     * @param x
     * @param y
     */
    public void resetToPosition(float x, float y) {
        setTransform(x, y, 0);
        setAngularVelocity(0);
        setLinearVelocity(0, 0);
    }

    public Body getBody() {
        return body;
    }

    protected Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return texture;
    }

}
