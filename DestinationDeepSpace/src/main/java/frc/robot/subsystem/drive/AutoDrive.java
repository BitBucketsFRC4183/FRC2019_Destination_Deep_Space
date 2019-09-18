package frc.robot.subsystem.drive;



import edu.wpi.first.wpilibj.command.Command;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.autonomous.AutoPeriod;
import frc.robot.utils.CommandUtils;



public class AutoDrive extends Command {
    // creates the AutoPeriod instance, starting loop
    private AutoPeriod auto = AutoPeriod.instance();
    private OI oi = OI.instance();

    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println(this.getClass().getName() + " Start" + System.currentTimeMillis()/1000);

        
    }

    protected boolean isFinished() {
        boolean forceIdle = oi.driverIdle();
        boolean forceExitAuto = oi.forceExitAuto();
        boolean isFinished = auto.isItFinished();

        boolean exit = forceIdle || forceExitAuto || isFinished;

        if (exit) {
            auto.stop();
            
            return CommandUtils.stateChange(new Idle());
        }

        return false;
    }
}