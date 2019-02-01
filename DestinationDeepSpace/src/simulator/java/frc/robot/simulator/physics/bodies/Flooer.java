package frc.robot.simulator.physics.bodies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Flooer extends Image { // Yes "Flooer"...              ...Fight me.
    private Body flooer;
    private World world;
    private BodyDef ballBodyDef = new BodyDef();
    private FixtureDef ballFixtureDef = new FixtureDef();
    private Stage stage;

    public Flooer(World world){
        super(new Texture("assets/Flooer.png"));
        BodyDef flooerBodyDef = new BodyDef();
        flooerBodyDef.type = BodyType.StaticBody;
        PolygonShape flooerShape = new PolygonShape();
        flooerShape.setAsBox(5000, .5f);
        FixtureDef flooerFixtureDef = new FixtureDef();
        flooerFixtureDef.friction = 1;
        flooerFixtureDef.restitution = 0;
        flooerFixtureDef.shape = flooerShape;
        Body flooer = world.createBody(flooerBodyDef);
        flooer.createFixture(flooerFixtureDef);
        setPosition(-5000, -0.5f);
        setSize(10000, 1);
        setOrigin(5000, 0.5f);
    }
}