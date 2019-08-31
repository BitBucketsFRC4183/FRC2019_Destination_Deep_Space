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
{//technically like a pair of splines and not a spline, i'm a liar forgive me for my sins
    public double[] xcoef = new double[6];//pos 0 is c_0x^0, pos 1 is c_1x^1, etc... c_5x^5. Cubic -> c_5, c_4=0
    public double[] ycoef = new double[6];
    double[] xprimecoef = new double[5];
    double[] yprimecoef = new double[5];
    double[] xdoubleprimecoef = new double[4];
    double[] ydoubleprimecoef = new double[4];

    public TreeMap<Double, Double> ltos = new TreeMap<Double, Double>();// map is called l to s bc it's mapping l to s

    public double previousLength;
    public double arcLength;
    public double knot_Distance;

    public Spline()
    {
    }

    public void setArcLength()
    {
        double NUM_STEPS=10000;
        double ds=1/NUM_STEPS;
        arcLength=0;
        double integrand=0;
        double s=0;
        for(int i=1; i<=NUM_STEPS; i++)
        {
            s=i*ds;
            integrand=Math.sqrt(Math.pow(evaluateFunction(xprimecoef, s), 2)+ Math.pow(evaluateFunction(yprimecoef, s), 2));
            arcLength+=integrand*ds;
            ltos.put(arcLength,s);
        }
   }

    public double evaluateFunction(double[] coefficients, double s)
    {
        double value=0;
        for(int i=0; i<coefficients.length; i++)
            value+=Math.pow(s,i)*coefficients[i];
        return value;
    }
}