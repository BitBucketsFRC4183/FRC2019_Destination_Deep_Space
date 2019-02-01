
package frc.robot.simulator.physics;

import com.badlogic.gdx.Game;
import frc.robot.Robot;

public class PhysicsSimulation extends Game {

	private final Robot robot;

	public PhysicsSimulation(Robot robot) {
		this.robot = robot;
	}

	@Override
	public void create () {
		this.setScreen(new DriveBaseSideScreen(this, robot));
//		 this.setScreen(new DriveBaseTopDownScreen(this, robot));
	}

	@Override
	public void render () {
		super.render();
	}

	public void dispose () {
	}

}
