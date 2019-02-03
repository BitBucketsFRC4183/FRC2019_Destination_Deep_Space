package frc.robot.subsystem.scoring;

import edu.wpi.first.wpilibj.command.Command;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

public class OrientationSwitch extends Command {
    private static OI oi = OI.instance();
    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();



    public OrientationSwitch() {
        requires(scoringSubsystem);
        setTimeout(ScoringConstants.LEVEL_CHANGE_TIMEOUT_SEC);
    }



    @Override
    protected void initialize() {
        System.out.println(this.getClass().getName() + " START " + System.currentTimeMillis()/1000);

        scoringSubsystem.switchOrientation();
    }



    @Override
    protected boolean isFinished() {
        if (oi.operatorIdle()) {
            return CommandUtils.stateChange(new Idle());
        }



        if (isTimedOut()) {
            return CommandUtils.stateChange(new Idle());
        }


        
        // if you already reached the desired level that this is at, allow a change
        // if the arm can move fast enough, we want this behavior (always preferred)
        // if not, then we may want to let the driver change the state while the
        //      robot is still trying to move the scoring arm
        int err = scoringSubsystem.getArmLevelTickError();
        // is the robot sufficiently within this state's level?
        if (Math.abs(err) > ScoringConstants.ROTATION_MOTOR_ERROR_DEADBAND_TICKS) {
            return false;
        }



        // whether or not the button to switch the orientation is pressed
        boolean switchOrientation = oi.switchOrientation();

        // VERY IMPORTANT
        // If the button to switch the orientation is pressed and the robot is
        // in this state, we do not want any state changes
        // If we allow state changes, the next state may detected a request
        // to change back to this state, resulting in an infinite loop
        if (switchOrientation) {
            return false;
        }



        // get selected level on joystick (NONE if none selected, INVALID if multiple selected)
        ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();

        // if two levels are selected, there is uncertainty in what to do
		// so don't change the state
        if (level == ScoringConstants.ScoringLevel.INVALID) {
            return false;
        }

        // if some level is selected, go to it
        if (level != ScoringConstants.ScoringLevel.NONE) {
            return CommandUtils.stateChange(new ArmLevel(level));
        }

        return false;
    }
}