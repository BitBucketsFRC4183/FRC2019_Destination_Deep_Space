/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous.motion;
/**
 * Add your docs here.
 */
public class PathFinder {

    private Waypoint[] waypoints;
    public PathType pathType;

    public Spline[] splines; 
    public enum PathType{
        CUBIC_HERMITE,
        QUINTIC_HERMITE,
        B_SPLINE;
    }

    public PathFinder(Waypoint[] waypoints, PathType pathType)
    {
        copy(waypoints);
        this.pathType=pathType;
        generateAllSplines();
    }

    private void copy(Waypoint[] waypoints)
    {
        this.waypoints = new Waypoint[waypoints.length];
        for(int i=0; i<waypoints.length; i++)
            this.waypoints[i] = new Waypoint(waypoints[i].x, waypoints[i].y, waypoints[i].deg);
    }

    private boolean generateAllSplines()
    {
        splines = new Spline[waypoints.length-1];
        for(int i=0; i<splines.length; i++)
        {
            splines[i] = new Spline();
            splines[i]=generateASpline(waypoints[i], waypoints[i+1], splines[i]);
            if(i>0) splines[i].previousLength = splines[i-1].previousLength+splines[i-1].arcLength;
        }
        return true;
    }

    private Spline generateASpline(Waypoint start, Waypoint end, Spline temp)
    {
        return generateASpline(start.x, start.y, start.deg, end.x, end.y, end.deg, temp);
    }

    private Spline generateASpline(double x0, double y0, double theta0, double x1, double y1, double theta1, Spline temp)
    {
        temp.knot_Distance = Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0));
        
        double cc=temp.knot_Distance*1.25;//tangent vector magnitude, Matlab investigates this a little more.
        //tangent magnitude doesnt "mean" much. Matlab experimentation shows different values and why they're chosen
        double a0x=0;//Second Derivative variable, there are only so many letters of the alphabet.
        double m0x=cc*Math.cos(SabaMath.d2r(theta0));

        double a0y=0;
        double m0y=cc*Math.sin(SabaMath.d2r(theta0));

        double k0 = Math.abs(m0x*a0y-m0y*a0x)/Math.pow(m0x*m0x+m0y*m0y,1.5);//for ease of change in future, this is 0 anyway

        double a1x=0;
        double m1x = cc*Math.cos(SabaMath.d2r(theta1));

        double a1y=0;
        double m1y=cc*Math.sin(SabaMath.d2r(theta1));

        double k1 = Math.abs(m1x*a1y-m1y*a1x)/Math.pow(m1x*m1x+m1y*m1y, 1.5);

        if (pathType==PathType.CUBIC_HERMITE)//Equations come from my heart and soul, and totally not basis functions
        {
            temp.xcoef[5]=0;
            temp.xcoef[4]=0;
            temp.xcoef[3]=-2*x1+2*x0+m0x+m1x;
            temp.xcoef[2]=-3*x0-2*m0x+3*x1-m1x;
            temp.xcoef[1]=m0x;
            temp.xcoef[0]=x0;

            temp.ycoef[5]=0;
            temp.ycoef[4]=0;
            temp.ycoef[3]=-2*y1+2*y0+m0y+m1y;
            temp.ycoef[2]=-3*y0-2*m0y+3*y1-m1y;
            temp.ycoef[1]=m0y;
            temp.ycoef[0]=y0;
        }
        if(pathType==PathType.QUINTIC_HERMITE)//Curvature is 0, so splines can be line-ish near the end. :) if this can change, we'll figure out later
        {
            temp.xcoef[5]=a1x/2-a0x/2-3*m1x-3*m0x+6*x1-6*x0;
            temp.xcoef[4]=-a1x+1.5*a0x+7*m1x+8*m0x-15*x1+15*x0;
            temp.xcoef[3]=a1x/2-1.5*a0x-4*m1x-6*m0x+10*x1-10*x0;
            temp.xcoef[2]=a0x/2;
            temp.xcoef[1]=m0x;
            temp.xcoef[0]=x0;

            temp.ycoef[5]=a1y/2-a0y/2-3*m1y-3*m0y+6*y1-6*y0;
            temp.ycoef[4]=-a1y+1.5*a0y+7*m1y+8*m0y-15*y1+15*y0;
            temp.ycoef[3]=a1y/2-1.5*a0y-4*m1y-6*m0y+10*y1-10*y0;
            temp.ycoef[2]=a0y/2;
            temp.ycoef[1]=m0y;
            temp.ycoef[0]=y0;
        }
        initializeSpline(temp);
        return temp;
    }

    private void initializeSpline(Spline temp)
    {
        temp.xprimecoef = SabaMath.takeDerivative(temp.xcoef);
        temp.xdoubleprimecoef = SabaMath.takeDerivative(temp.xprimecoef);

        temp.yprimecoef = SabaMath.takeDerivative(temp.ycoef);
        temp.ydoubleprimecoef = SabaMath.takeDerivative(temp.yprimecoef);
        //knot distance was done in generating spline method :)
        temp.setArcLength();
    }

    public double[] getSplineNo(double d)
    {
        double[] result = new double[2];//first element is spline #, 2nd is how far in the spline it is
        for(int i=0; i<splines.length; i++)
        {
            if(d>=splines[i].previousLength& d<=(splines[i].previousLength+splines[i].arcLength))
            {
                result[0]=i;
                result[1]=d-splines[i].previousLength;
                return result;
            }
        }
        return result;
    }
}
