package frc.robot.subsystem.scoring;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

        boolean forceIdle = oi.operatorIdle() || 
                            scoringSubsystem.exceededCurrentLimit();

        if (forceIdle) {
            SmartDashboard.putString("OrientationStatus","Forced Idle");
            return CommandUtils.stateChange(new Idle());
        }


        boolean areWeThereYet = (Math.abs(Math.toDegrees(scoringSubsystem.getTargetAngle_rad()) - 
                                         scoringSubsystem.getAngle_deg()) < ScoringConstants.ANGLE_TOLERANCE_DEG);

        boolean timeout = isTimedOut();
        
        // if you already reached the desired level that this is at, allow a change
        // if the arm can move fast enough, we want this behavior (always preferred)
        // if not, then we may want to let the driver change the state while the
        //      robot is still trying to move the scoring arm
        // is the robot sufficiently within this state's level?
        if ( !areWeThereYet) {
            if (timeout) 
            {
                /// TODO: Need to evaluate whether going Idle makes sense
                /// or should we just hold position and signal a problem
                /// some other way?
                SmartDashboard.putString("OrientationStatus","Timeout");
                return CommandUtils.stateChange(new Idle());
            }
        }

        // VERY IMPORTANT
        
        // Don't exit this command until they stop pressing the button
        // This prevents toggling back and forth.
        // The user must stop pressing this button to switch again
        // or change height
        boolean switchOrientation = oi.switchOrientation();

        if (switchOrientation) {
            // if (!releasedButton) {
            //     // don't change the state if button to switch
            //     // is pressed but hasn't been released in
            //     // the duration of this state
                SmartDashboard.putString("OrientationStatus","Holding Button");
                return false;
        //     }
        // } else {
        //     releasedButton = false; // it has been released at this point
        }

        // If we got this far then the user did release the button
        // So we can now evaluate an in route level change

        // get selected level on joystick (NONE if none selected, INVALID if multiple selected)
        ScoringConstants.ScoringLevel level = scoringSubsystem.getCommandedLevel();

        // if two levels are selected, there is uncertainty in what to do
		// so don't change the state
        if (level == ScoringConstants.ScoringLevel.INVALID) {
            SmartDashboard.putString("OrientationStatus","Invalid Level");
            return false;
        }

        if (level != ScoringConstants.ScoringLevel.NONE)
        {
            SmartDashboard.putString("OrientationStatus","New Level");
            return CommandUtils.stateChange(new ArmLevel(level));            
        }

        // boolean changeLevel = (level == ScoringConstants.ScoringLevel.NONE);

        // if (changeLevel && !switchOrientation) {
        //     SmartDashboard.putString("OrientationStatus","New Level");
        //     return CommandUtils.stateChange(new ArmLevel(level));
        // }

        // if (switchOrientation && !changeLevel) {
        //     SmartDashboard.putString("OrientationStatus","New Orientation Switch");
        //     return CommandUtils.stateChange(new OrientationSwitch());
        // }

        SmartDashboard.putString("OrientationStatus","End of Is Finished");
        return false;
    }
}