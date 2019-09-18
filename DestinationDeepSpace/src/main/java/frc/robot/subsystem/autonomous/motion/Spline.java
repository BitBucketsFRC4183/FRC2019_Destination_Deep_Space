/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package frc.robot.subsystem.autonomous.motion;
import java.util.TreeMap;

/**
 * Add your docs here.
 */
public class Spline
{//technically like a pair of splines and not a spline, i'm a liar forgive me for my 
    // polynomial of x with respect to t
    public double[] xcoef = new double[6];//pos 0 is c_0x^0, pos 1 is c_1x^1, etc... c_5x^5. Cubic -> c_5, c_4=0
    // polynomial of y with respect to t
    public double[] ycoef = new double[6];
    // polynomial of dx/dt with respect to t
    double[] xprimecoef = new double[5];
    // polynomial of dy/dt with respect to t
    double[] yprimecoef = new double[5];
    // polynomial of d^2 x/dt^2 with respect to t
    double[] xdoubleprimecoef = new double[4];
    // polynomial of d^2 y/dt^2 with respect to t
    double[] ydoubleprimecoef = new double[4];



    // s - normalized time
    // for example, same curve could happen from t=0 to t=1 or from t=0 to t=5
    // so we take all curves to go from s=0 to s=1

    // l - accumulated arc length as a function of s

    // maps accumulated arc length to normalized time parameter
    public TreeMap<Double, Double> ltos = new TreeMap<Double, Double>();// map is called l to s bc it's mapping l to s

    public double previousLength;
    public double arcLength;
    // time difference between "knots" aka points
    public double knot_Distance;

    public Spline()
    {
    }

    public void setArcLength()
    {
        // number of integration steps for arc length approximation
        double NUM_STEPS=10000;
        // delta s is going to be 1 divided by NUM_STEPS to split [0, 1] into NUM_STEPS intervals in s
        double ds=1/NUM_STEPS;
        // accumulated arc length
        arcLength=0;
        // approximate arc length of one small interval of size ds
        double integrand=0;
        double s=0;

        double xprime, yprime;

        // start at s=0, go to s=1
        for(int i=1; i<=NUM_STEPS; i++)
        {
            // ending point of interval at i*ds, bc i intervals so far of width ds
            s=i*ds;
            // derivatives of function at the point
            xprime = evaluateFunction(xprimecoef, s);
            yprime = evaluateFunction(yprimecoef, s);
            // sqrt((dx/ds)^2 + (dy/ds)^2) ds - pythagorean theorem
            integrand = Math.sqrt(Math.pow(xprime, 2) + Math.pow(yprime, 2));
            // add the integrand to the accumulated arc length
            arcLength+=integrand*ds;
            // add (accumulated arc length, s) pair to ltos
            ltos.put(arcLength,s);
        }
   }

   /**
    * Evaluate a polynomial at s
    *
    * @param coefficients coefficients in polynomial
    * @param s "time" parameter between 0 and 1
    * @return polynomial evaluated at s=0
    */
    public double evaluateFunction(double[] coefficients, double s)
    {
        double value=0;
        // just add all the terms to the return value
        for(int i=0; i<coefficients.length; i++)
            value+=Math.pow(s,i)*coefficients[i];
        return value;
    }
}