package frc.robot.subsystem.autonomous;

import frc.robot.utils.Controller;



public class GuidanceAlgorithm {
	private Controller controlLoop_parallax = new Controller(
        AutonomousConstants.PARALLAX_KP,
        AutonomousConstants.PARALLAX_KI,
        AutonomousConstants.PARALLAX_KD,
        AutonomousConstants.CAMERA_FPS
    );
	private Controller controlLoop_offAxis = new Controller(
        AutonomousConstants.OFF_AXIS_KP,
		AutonomousConstants.OFF_AXIS_KI,
        AutonomousConstants.OFF_AXIS_KD,
        AutonomousConstants.CAMERA_FPS
    );
	
	private double parallax;
    private double offAxis;

    private double parallax_contribution;
    private double offAxis_contribution;
	
	
	
	public double getTurnRate(double distance) {
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
	
	
	
	public void setParallax(double p) {
		parallax = p;
	}
	
	public void setOffAxis(double o) {
		offAxis = o;
    }
    
    public double getParallaxContribution() {
        return parallax_contribution;
    }

    public double offAxisContribution() {
        return offAxis_contribution;
    }
    
    
    
	public void reset() {
		parallax = 0;
		offAxis = 0;
		
		controlLoop_parallax.reset();
		controlLoop_offAxis.reset();
	}
}
