package frc.robot.subsystem.drive;



import edu.wpi.first.wpilibj.command.Command;
import frc.robot.operatorinterface.OI;
import frc.robot.subsystem.autonomous.GuidanceAlgorithm;
import frc.robot.utils.CommandUtils;
import frc.robot.subsystem.vision.VisionSubsystem;
import frc.robot.subsystem.vision.VisionConstants;
import frc.robot.subsystem.drive.DriveSubsystem;
import frc.robot.subsystem.vision.CameraFeedback;
import frc.robot.subsystem.autonomous.AutonomousConstants;



public class PostAutoAlign extends Command {
    private OI oi = OI.instance();

    private VisionSubsystem vision = VisionSubsystem.instance();
    private DriveSubsystem drive = DriveSubsystem.instance();
    private GuidanceAlgorithm guidance;

    private boolean finished = false;

    // Called just before this Command runs the first time
    @Override
    protected void initialize() {
        System.out.println(this.getClass().getName() + " Start" + System.currentTimeMillis()/1000);

        // so we can see tape and align
        vision.enableAutonomousExposure();

        guidance = new GuidanceAlgorithm();
    }

    @Override
    protected void execute() {
        // copy pasted from auto aligning
        CameraFeedback feedback = vision.getClosestObjectData();

        double offAxis = feedback.getOffAxis();
        double parallax = feedback.getParallax();
        double distance = feedback.getDistance();

        //SmartDashboard.putNumber(getName()+"/offAxis",offAxis);
        //SmartDashboard.putNumber(getName()+"/parallax",parallax);
        //SmartDashboard.putNumber(getName()+"/distance",distance);

        // no need to keep guiding if in a certain distance
        if (distance <= AutonomousConstants.GUIDANCE_STOP) {
            guidance.setOffAxis(0.0);
            guidance.setParallax(0.0);
        } else {
            guidance.setOffAxis(offAxis);
            guidance.setParallax(parallax);
        }

        double turnRate_radps = guidance.getTurnRate(distance);

        // if within reasonable margin of error for angle to tape, done aligning
        if (Math.abs(offAxis * VisionConstants.HALF_FOV_RAD) <= AutonomousConstants.POST_AUTO_ALIGN_ANGLE_THRESHOLD) {
            finished = true;
        } else {
            // only turn towards tape
            drive.velocityDrive_auto(0, turnRate_radps);
        }
    }

    @Override
    protected boolean isFinished() {
        boolean forceIdle = oi.driverIdle();
        boolean forceExitAuto = oi.forceExitAuto();

        boolean exit = forceIdle || forceExitAuto || finished;

        if (exit) {
            return CommandUtils.stateChange(new Idle());
        }

        return false;
    }
}