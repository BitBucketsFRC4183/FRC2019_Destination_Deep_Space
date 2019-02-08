
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
		if ("top".equals(System.getProperty("sim", "side").toLowerCase())) {
			this.setScreen(new DriveBaseTopDownScreen(this, robot));
		} else {
			this.setScreen(new DriveBaseSideScreen(this, robot));
		}
	}

	@Override
	public void render () {
		super.render();
	}

	public void dispose () {
	}

}
