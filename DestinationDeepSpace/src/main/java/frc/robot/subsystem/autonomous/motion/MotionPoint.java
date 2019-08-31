/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous.motion;

public class MotionPoint {
    public double l_vel;
    public double l_pos;
    public double l_acc;

    public double r_pos;
    public double r_vel;
    public double r_acc;
    public MotionPoint(double l_pos, double l_vel, double l_acc, double r_pos, double r_vel)
    {
        //long story short: r_acc not in constructor bc it is calculated after entirety of other points
        //are calculated. But it shouldn't be null
        this.l_vel=l_vel;
        this.l_pos=l_pos;
        this.l_acc=l_acc;
        this.r_pos=r_pos;
        this.r_vel=r_vel;
    }
}
