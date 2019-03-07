package frc.robot.utils;



public class Controller {
	private final double P;
	private final double I;
	private final double D;
	
	
	
	private double previous;
    private double integral;
    


    private final double FREQUENCY;
	
	
	
	public Controller(double p, double i, double d, double frequency) {
		P = p;
		I = i;
        D = d;
        
        FREQUENCY = frequency;
		
		previous = 0;
		integral = 0;
	}
	
	
	
	public double newValue(double error) {
		double p_contribution = P * error;
		
		integral += error / FREQUENCY;
		double i_contribution = I * integral;
		
		double deriv = (error - previous) * FREQUENCY;
		previous = error;
		double d_contribution = D * deriv;
		
		
		
		double ret = p_contribution + i_contribution + d_contribution;
		
		
		
		/*if (ret > Math.PI / 8) {
			ret = p_contribution;
		}*/
		
		
		
		return ret;
	}
	
	
	
	public void reset() {
		previous = 0;
		integral = 0;
	}
}
