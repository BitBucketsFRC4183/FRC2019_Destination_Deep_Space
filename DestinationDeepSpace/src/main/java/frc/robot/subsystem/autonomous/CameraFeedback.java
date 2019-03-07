package frc.robot.subsystem.autonomous;



public class CameraFeedback {
	private final boolean IN_AUTO_ASSIST_REGION;
	private final double PARALLAX;
	private final double OFF_AXIS;
	
	
	
	public CameraFeedback(boolean inAutoAssistRegion, double parallax, double offAxis) {
		IN_AUTO_ASSIST_REGION = inAutoAssistRegion;
		PARALLAX = parallax;
		OFF_AXIS = offAxis;
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
}
