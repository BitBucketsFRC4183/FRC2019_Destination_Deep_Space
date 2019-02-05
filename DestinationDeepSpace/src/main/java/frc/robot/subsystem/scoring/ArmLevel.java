package frc.robot.subsystem.scoring;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

public class ArmLevel extends Command {
    private static OI oi = OI.instance();
    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();

    private final ScoringConstants.ScoringLevel LEVEL;



    public ArmLevel(ScoringConstants.ScoringLevel level) {
        requires(scoringSubsystem);
        setTimeout(ScoringConstants.LEVEL_CHANGE_TIMEOUT_SEC);

        LEVEL = level;
    }





    @Override
    protected void initialize() {
        System.out.println(this.getClass().getName() + " " + LEVEL.toString() + " START " + System.currentTimeMillis()/1000);

        scoringSubsystem.goToLevel(LEVEL);
    }



    @Override
    protected boolean isFinished() {
        boolean forceIdle = oi.operatorIdle();

        if (forceIdle) {
            return CommandUtils.stateChange(new Idle());
        }



        boolean timeout = isTimedOut();



        // if you already reached the desired level that this is at, allow a change
        // if the arm can move fast enough, we want this behavior (always preferred)
        // if not, then we may want to let the driver change the state while the
        //      robot is still trying to move the scoring arm
        int err = scoringSubsystem.getArmLevelTickError();
        // is the robot sufficiently within this state's level?
        if (err > ScoringConstants.ROTATION_MOTOR_ERROR_DEADBAND_TICKS) {
            if (timeout) {
                return CommandUtils.stateChange(new Idle());
            }

            return false;
        }



        // whether or not the button to switch the orientation is pressed
        boolean switchOrientation = oi.switchOrientation();

        // get selected level on joystick
        ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();

        // if multiple levels are selected or this level is still selected,
        // don't allow any state changes
        if (level == ScoringConstants.ScoringLevel.INVALID || level == LEVEL) {
            return false;
        }

        // if one level button is pressed
        boolean differentLevel = level != ScoringConstants.ScoringLevel.NONE;
        

        // if trying to change the level and orientation, then don't allow it
        if (differentLevel && switchOrientation) {
            return false;
        }

        // check if a different level is selected
        if (differentLevel) {
            // previous checks ensure robot is sufficiently within this state's level
            // so we can change it

            return CommandUtils.stateChange(new ArmLevel(level));
        }

        // check if user trying to switch the orientation of the arm
        if (switchOrientation) {
            return CommandUtils.stateChange(new OrientationSwitch());
        }

        return false;
    }
}