package frc.robot.subsystem.scoring;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;

public class OrientationSwitch extends Command {
    private static OI oi = OI.instance();
    private static ScoringSubsystem scoringSubsystem = ScoringSubsystem.instance();



    private boolean releasedButton = false;



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
        // whether or not the button to switch the orientation is pressed
        boolean switchOrientation = oi.switchOrientation();

        if (!switchOrientation) {
            releasedButton = true;
        }



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

        // VERY IMPORTANT
        // If the user has not released the button while in this state
        // then allowing a state change is BAD
        // It may end up in an infinite loop of state changes of
        // OrientationSwitch
        if (switchOrientation && !releasedButton) {
            return false;
        }



        // get selected level on joystick (NONE if none selected, INVALID if multiple selected)
        ScoringConstants.ScoringLevel level = scoringSubsystem.getSelectedLevel();

        // if two levels are selected, there is uncertainty in what to do
		// so don't change the state
        if (level == ScoringConstants.ScoringLevel.INVALID) {
            return false;
        }

        if (level != ScoringConstants.ScoringLevel.NONE ^ switchOrientation) {
            // if some level is selected, go to it
            if (level != ScoringConstants.ScoringLevel.NONE) {
                return CommandUtils.stateChange(new ArmLevel(level));
            } else {
                return CommandUtils.stateChange(new OrientationSwitch());
            }
        }

        return false;
    }
}