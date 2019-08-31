/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous.motion;

//the closest I will get to credibility in mathematics
public class SabaMath 
{
    /**
     * Take derivative of a polynomial function
     * i.e. y = (c_n) x^n + (c_n-1) x^(n - 1) + ... + c_2 x^2 + c_1 x + c_0
     *      y' = n (c_n) x^(n - 1) + (n - 1) (c_n-1) x^(n - 2) + ... 2 c_2 x + c_1
     * 
     * @param function coefficients of polynomial, i-th entry is coefficient on x^i
     */
    public static double[] takeDerivative(double[] function)
    {
        // d/dx (c_n x^n) is (n)(c_n) x^(n - 1)
        // (coefficient on x^n)*(n) = coefficient on x^(n - 1) in derivative

        // n-th order polynomial's derivative is (n-1)th order
        // coefficients in new polynomial that is derivative of function
        double[] result = new double[function.length-1];

        // go through every coefficient but the one on x^0, derivative of constant is 0 so doesn't affect polynomial
        for(int i=1; i<=result.length; i++)
            // coefficient on x^(n - 1)
            result[i-1]=i*function[i];
        return result;
    }

    /**
     * Degree to radians
     */
    public static double d2r(double angle)//prob already exists, i was impatient
    {
        return angle*Math.PI/180;
    }
}
