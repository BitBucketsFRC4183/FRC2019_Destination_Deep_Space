package frc.robot.subsystem.autonomous;

import frc.robot.utils.Controller;



public class GuidanceAlgorithm {
	private static Controller controlLoop_parallax = new Controller(
        AutonomousConstants.PARALLAX_KP,
        AutonomousConstants.PARALLAX_KI,
        AutonomousConstants.PARALLAX_KD,
        AutonomousConstants.CAMERA_FPS
    );
	private static Controller controlLoop_offAxis = new Controller(
        AutonomousConstants.OFF_AXIS_KP,
		AutonomousConstants.OFF_AXIS_KI,
        AutonomousConstants.OFF_AXIS_KD,
        AutonomousConstants.CAMERA_FPS
    );
	
	private static double parallax;
    private static double offAxis;

    private static double parallax_contribution;
    private static double offAxis_contribution;
	
	
	
	public static double getTurnRate(double distance) {
		parallax_contribution = controlLoop_parallax.newValue(parallax);
		offAxis_contribution  = controlLoop_offAxis .newValue(offAxis);
		
		if (distance < AutonomousConstants.MIN_PARALLAX_DISTANCE)
		{
			parallax_contribution = 0;
			offAxis_contribution *= AutonomousConstants.OFF_AXIS_GAIN_BOOST;
		}
		
		double omega = parallax_contribution + offAxis_contribution;
		
		return omega;
	}
	
	
	
	public static void setParallax(double p) {
		parallax = p;
	}
	
	public static void setOffAxis(double o) {
		offAxis = o;
    }
    
    public static double getParallaxContribution() {
        return parallax_contribution;
    }

    public static double offAxisContribution() {
        return offAxis_contribution;
    }
    
    
    
	public static void reset() {
		parallax = 0;
		offAxis = 0;
		
		controlLoop_parallax.reset();
		controlLoop_offAxis.reset();
	}
}
