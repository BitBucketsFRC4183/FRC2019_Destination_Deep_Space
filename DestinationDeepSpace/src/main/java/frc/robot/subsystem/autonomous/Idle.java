package frc.robot.subsystem.autonomous;

import frc.robot.operatorinterface.OI;
import frc.robot.utils.CommandUtils;

import edu.wpi.first.wpilibj.command.Command;



public class Idle extends Command {
    private static OI oi = OI.instance();
    private static AutonomousSubsystem autonomousSubsystem = AutonomousSubsystem.instance();



    public Idle() {
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

        // TODO: exit condition for AutoAssist

        return false;
    }
}