package frc.robot.utils.autotuner;

public class Complex {
	private final double RE;
	private final double IM;





	public Complex(double re, double im) {
		RE = re;
		IM = im;
	}





	public double real() {
		return RE;
	}

	public double imag() {
		return IM;
	}



	public double norm() {
		return Math.sqrt(RE * RE + IM * IM);
	}

	public double angle() {
		return Math.atan2(IM, RE);
	}

	public Complex conj() {
		return new Complex(RE, -IM);
	}





	public Complex multiply(double re) {
		return new Complex(re * RE, re * IM);
	}

	public Complex multiply(Complex c) {
		return new Complex(
			RE * c.real() - IM * c.imag(),
			RE * c.imag() + IM * c.real()
		);
	}



	public Complex divide(double re) {
		return new Complex(RE / re, IM / re);
	}

	public Complex divide(Complex c) {
		return (multiply(c.conj())).divide(c.norm());
	}



	public Complex add(Complex c) {
		return new Complex(RE + c.real(), IM + c.imag());
	}

	public Complex add(double re) {
		return new Complex(RE + re, IM);
	}





	// e^(i * im) - Euler!
	public static Complex unit(double im) {
		return new Complex(
			Math.cos(im),
			Math.sin(im)
		);
	}





	public String toString() {
		return RE + " + " + IM + "i";
	}
}
