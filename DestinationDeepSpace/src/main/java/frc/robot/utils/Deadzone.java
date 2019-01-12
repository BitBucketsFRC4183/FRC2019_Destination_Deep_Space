package frc.robot.utils;

public class Deadzone {
	
	/**
	 * Simple deadzone function
	 * s
	 * @param x Input
	 * @param deadZone The deadzone 
	 * @return
	 */
	public static double f(double x, double deadZone) {
		double sign = Math.signum(x);
		x = Math.abs(x);
		if( x < deadZone ) return 0.0;
		return sign * (x - deadZone);
	}
}