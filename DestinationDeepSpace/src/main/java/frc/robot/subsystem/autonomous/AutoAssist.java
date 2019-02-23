package frc.robot.subsystem.autonomous;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;



public class AutoAssist extends Command {
    private static OI oi = OI.instance();
    private static AutonomousSubsystem autonomousSubsystem = AutonomousSubsystem.instance();



    public AutoAssist() {
        requires(autonomousSubsystem);
        setRunWhenDisabled(true); // Idle command
    }



    @Override
    protected void initialize() {
        System.out.println(this.getClass().getName() + " AUTO START " + " " + System.currentTimeMillis()/1000);
        autonomousSubsystem.disable();
    }

    @Override
    protected boolean isFinished() {
        boolean forceIdle = oi.driverIdle();

        if (forceIdle) {
            return false;
        }

        // TODO: exit condition for Idle

        return false;
    }
}