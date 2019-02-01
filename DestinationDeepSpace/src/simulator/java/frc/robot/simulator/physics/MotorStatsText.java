package frc.robot.simulator.physics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import frc.robot.subsystem.scoring.ScoringSubsystem;

public class MotorStatsText extends Actor {

    BitmapFont font;

    public MotorStatsText(float x, float y) {
        font = new BitmapFont();
        font.setColor(0.5f,0.4f,0,1);
//        this.setPosition(x, y);
//        this.setSize(5, 5);
//        this.setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /// TODO: Fix for position control
        /// font.draw(batch, "Arm Motor Output: " + ScoringSubsystem.instance().getRotationMotor1().getMotorOutputPercent() * 100.0f, getX(), getY());
    }

}
