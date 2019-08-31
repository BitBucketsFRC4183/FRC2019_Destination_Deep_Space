/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous.motion;

public class Waypoint {
    public double x;
    public double y;
    public double deg;

    public Waypoint(double x, double y, double deg)
    {
        this.x=x;
        this.y=y;
        this.deg=deg;
    }
}
