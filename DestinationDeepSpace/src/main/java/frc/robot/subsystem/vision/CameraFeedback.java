package frc.robot.subsystem.vision;



public class CameraFeedback {
	private final boolean IN_AUTO_ASSIST_REGION;
	private final double PARALLAX;
	private final double OFF_AXIS;
	private final double DISTANCE;
	
	
	
	public CameraFeedback(boolean inAutoAssistRegion, double parallax, double offAxis, double distance) {
		IN_AUTO_ASSIST_REGION = inAutoAssistRegion;
		PARALLAX = parallax;
		OFF_AXIS = offAxis;
		DISTANCE = distance;
	}
	
	
	
    
    
	public boolean isInAutoAssistRegion() {
		return IN_AUTO_ASSIST_REGION;
	}
	
	public double getParallax() {
		return PARALLAX;
	}
	
	public double getOffAxis() {
		return OFF_AXIS;
	}
	
	public double getDistance() {
		return DISTANCE;
	}
}
