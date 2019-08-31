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
    public static double[] takeDerivative(double[] function)
    {
        double[] result = new double[function.length-1];
        for(int i=1; i<=result.length; i++)
            result[i-1]=i*function[i];
        return result;
    }

    public static double d2r(double angle)//prob already exists, i was impatient
    {
        return angle*Math.PI/180;
    }
}
