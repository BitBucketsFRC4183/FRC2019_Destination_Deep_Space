package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import frc.robot.simulator.physics.MathConstants;

/**
 * Created by julienvillegas on 31/01/2017.
 */

public class DriveBaseSide extends Image {

    public Body body;
    private World world;
    private Wheel frontWheel;
    private Wheel rearWheel;
    private Arm arm;
    RevoluteJoint frontWheelJoint;
    RevoluteJoint rearWheelJoint;
    RevoluteJoint armJoint;
    WeldJoint mountJoint;
    Mount mount;


    public DriveBaseSide(World world, float x, float y) {
        super(new Texture("assets/Generic_Texture.png"));
        this.world = world;

        // make it 1m x .1m
        setSize(27f*MathConstants.INCHES_TO_METERS, 4f*MathConstants.INCHES_TO_METERS);
        setOrigin(getWidth() / 2, getHeight() / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(getX(), getY());

        // Create a body in the world using our definition
        body = this.world.createBody(bodyDef);

        // Now define the dimensions of the physics shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth()/2, getHeight()/2);

        // FixtureDef is a confusing expression for physical properties
        // Basically this is where you, in addition to defining the shape of the body
        // you also define it's properties like density, restitution and others we will see shortly
        // If you are wondering, density and area are used to calculate over all mass
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 1f;
        fixtureDef.restitution= 0f;
        Fixture fixture = body.createFixture(fixtureDef);

        // Shape is the only disposable of the lot, so get rid of it
        shape.dispose();

        addWheels(x, y);
        addMount(x, y);
        addArm(x, y);

        // move it to the x/y
        setTransform(x, y, 0);
    }

    public void addWheels(float x, float y) {
        rearWheel = new Wheel(world, x + getWidth()/3f, y + getY());
        frontWheel = new Wheel(world, x + getWidth()/3 + y + getWidth(), getY());

        RevoluteJointDef rearWheelJointDef = new RevoluteJointDef();
        rearWheelJointDef.bodyA = rearWheel.getBody();
        rearWheelJointDef.bodyB = this.getBody();
        rearWheelJointDef.localAnchorA.set(0, 0);
        rearWheelJointDef.localAnchorB.set(-1.0f/3.0f * this.getWidth(), 0);
        rearWheelJointDef.collideConnected = false;
        rearWheelJointDef.enableMotor = true;
        rearWheelJointDef.maxMotorTorque = 100;
        rearWheelJoint = (RevoluteJoint) world.createJoint(rearWheelJointDef);

        RevoluteJointDef frontWheelJointDef = new RevoluteJointDef();
        frontWheelJointDef.bodyA = frontWheel.getBody();
        frontWheelJointDef.bodyB = this.getBody();
        frontWheelJointDef.localAnchorA.set(0, 0);
        frontWheelJointDef.localAnchorB.set(1.0f/3.0f * this.getWidth(), 0);
        frontWheelJointDef.enableMotor = true;
        frontWheelJointDef.maxMotorTorque = 100;
        frontWheelJointDef.collideConnected = false;
        frontWheelJoint = (RevoluteJoint) world.createJoint(frontWheelJointDef);

    }
    public void addMount(float x, float y) {
        mount = new Mount(world, x, y + getHeight()/2f);
        WeldJointDef mountJointDef = new WeldJointDef();
        mountJointDef.bodyA = mount.getBody();
        mountJointDef.bodyB = this.getBody();
        mountJointDef.localAnchorA.set(0, mount.getHeight()/-2);
        mountJointDef.localAnchorB.set(0, getHeight()/2);
        mountJointDef.collideConnected = false;
        mountJoint = (WeldJoint) world.createJoint(mountJointDef);

    }
    public void addArm(float x, float y) {
        arm = new Arm(world, x, y + getHeight()/2f + mount.getHeight());

        RevoluteJointDef armJointDef = new RevoluteJointDef();
        armJointDef.bodyA = arm.getBody();
        armJointDef.bodyB = mount.getBody();
        armJointDef.localAnchorA.set(0, 0);
        armJointDef.localAnchorB.set(0, mount.getHeight()/2);
        armJointDef.collideConnected = false;
        armJointDef.enableMotor = true;
        armJointDef.maxMotorTorque = 100;
        armJoint = (RevoluteJoint) world.createJoint(armJointDef);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        frontWheel.draw(batch, parentAlpha);
        rearWheel.draw(batch, parentAlpha);
        mount.draw(batch,parentAlpha);
        // TODO: Turn back on arm drawing after texture is fixed
        // arm.draw(batch,parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle()*  MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x-this.getWidth()/2,body.getPosition().y-this.getHeight()/2);

        this.frontWheel.act(delta);
        this.rearWheel.act(delta);
        this.mount.act(delta);
        this.arm.act(delta);
    }

    public void setFrontMotorSpeed(float motorSpeed) {
        frontWheelJoint.setMotorSpeed(motorSpeed);
    }

    public void setRearMotorSpeed(float motorSpeed) {
        rearWheelJoint.setMotorSpeed(motorSpeed);
    }

    public Body getBody() {
        return body;
    }

    public void setTransform(float x, float y, float angle) {
        frontWheel.getBody().setTransform(x + frontWheelJoint.getLocalAnchorB().x, y + frontWheelJoint.getLocalAnchorB().y, 0);
        rearWheel.getBody().setTransform(x + rearWheelJoint.getLocalAnchorB().x, y + rearWheelJoint.getLocalAnchorB().y, 0);
        this.getBody().setTransform(x, y, 0);

    }

	public void setArmRotationSpeed(float motorSpeed) {
        armJoint.setMotorSpeed(motorSpeed);
	}
}
