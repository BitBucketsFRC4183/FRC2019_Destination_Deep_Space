/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystem.autonomous.motion;

import frc.robot.subsystem.autonomous.AutonomousConstants;

import frc.robot.subsystem.drive.DriveConstants;



public class TrajectoryFinder {//deceleration is NEGATIVE* remember that pls
    MotionProfile profile;
    PathFinder pathfinder;

    // total distance to be travelled
    double totalDistance;
    // acceleration
    double acc;
    // deceleration
    double dec;
    // cruise velocity
    double v_cruise;
    // starting velocity
    double v_start;
    // ending velocity
    double v_end;

    // time spent accelerating
    double t_acc;
    // distance moved by accelerating
    double d_acc;
    // time spent in cruise state
    double t_cruise;
    // distance moved in cruise state
    double d_cruise;
    // time spent decelerating
    double t_dec;
    // distance moved by decelerating
    double d_dec;

    // total duration of path
    public double t_total;

    // motion points to cross
    private MotionPoint[] mPoints;

    public enum MotionProfile{
        TRIANGULAR,
        TRAPEZOIDAL,
        S_CURVE;//this isn't programmed in, idk how to do that. Using trapezoidal most often
    }

    /**
     * Create a TrajectoryFinder
     * 
     * @param profile type of motion profile to use between points
     * @param pathtype type of spline to generate
     * @param waypoint waypoints to cross
     * @param acc acceleration to get up to cruise velocity
     * @param v_cruise cruise velocity
     * @param dec deceleration to get to end velocity from cruise
     * @param v_start starting velocity
     * @param v_end ending velocity
     */
    public TrajectoryFinder(MotionProfile profile, PathFinder.PathType pathtype, Waypoint[] waypoint, double acc, double v_cruise, double dec, double v_start, double v_end)
    {
        this.pathfinder = new PathFinder(waypoint, pathtype);
        this.profile = profile;
        this.acc=acc;
        this.v_cruise=v_cruise;
        this.dec=dec;
        this.v_start=v_start;
        this.v_end=v_end;
        initializeTraj();
        generateMotionProfile();
    }
    
    public PathFinder getPathFinder() {
    	return this.pathfinder;
    }

    /**
     * Set total arc length and calculate details about the path
     * (time spent and distance travelled in each phase)
     */
    private void initializeTraj()
    {
        // calculate total distance travelled by adding individual spline arc lengths
        for(int i=0; i<pathfinder.splines.length; i++) totalDistance+=pathfinder.splines[i].arcLength;
        // sometimes, given the acceleration and velocity restrictions, no trapezoidal path
        // can be made that has the specified cruise velocity, and the best that can be done
        // is to increase at acceleration to some maximum velocity then decrease to the ending velocity
        // this equation determines said maximum velocity
        // any lower velocity would let you have a period of cruise velocity
        double vtemp=Math.sqrt((totalDistance+v_start*v_start*0.5/acc-v_end*v_end*0.5/dec)/(0.5/acc-0.5/dec));
        // if cruise velocity is too high, lower it and make a triangular profile
        // aka accelerate to cruise then instantly start decelerating to ending velocity
        if(v_cruise>vtemp) 
        {
            v_cruise=vtemp;
            profile=MotionProfile.TRIANGULAR;
        }

        // distance travelled in acceleration phase
        d_acc=(v_cruise*v_cruise-v_start*v_start)/2/acc;
        // time spent accelerating to cruise
        t_acc=(v_cruise-v_start)/acc;

        // distance travelled in deceleration phase
        d_dec=(v_end*v_end-v_cruise*v_cruise)/2/dec;
        // time spent decelerating to ending velocity
        t_dec=(v_end-v_cruise)/dec;

        if(profile==MotionProfile.TRIANGULAR)
        {
            // no cruise period, instantly start decelerating
            d_cruise=0;
            t_cruise=0;
        }
        else if(profile==MotionProfile.TRAPEZOIDAL)
        {
            // subtract acceleration and deceleration phases' distances
            // from total to get distance remaining to travel in
            // cruise phase
            d_cruise=totalDistance-d_acc-d_dec;
            // time spent in cruise phase
            t_cruise=d_cruise/v_cruise;
        }

        // total time it would take to travel along the path
        t_total=t_cruise+t_acc+t_dec;
    }

    /**
     * Get travelled distance, velocity, and acceleration at time t
     */
    public double[] getPointInfo(double t)
    {
        double[] result = new double[3];//contains position, velocity, acceleration in that order

        // consider cases for trapezoidal and triangular profiles
        // equivalent bc triangular is a special case of trapezoidal
        if(profile==MotionProfile.TRAPEZOIDAL)
        {
            // check if in accelerating stage
            if(t<=t_acc)
            {
                // get distance travelled so far up to t
                result[0]=v_start*t+0.5*acc*t*t;
                // and velocity
                result[1]=v_start+acc*t;
                // and acceleration
                result[2]=acc;
                return result;
            }
            // check if in cruise phase
            if(t>=t_acc && t<=(t_cruise+t_acc))
            {
                // get distance travelled so far in cruise stage + distance that was travelled in acceleration phase
                result[0]=d_acc+(t-t_acc)*v_cruise;
                // constant cruise velocity
                result[1]=v_cruise;
                // no acceleration b/c constant velocity
                result[2]=0;
                return result;
            }
            // check if in deceleration phase
            if(t>=(t_acc+t_cruise))
            {
                // distance travelled in last two phases + distance travelled up to time t in deceleration phase
                result[0]=d_acc+d_cruise+((t-t_acc-t_cruise)*v_cruise+(t-t_acc-t_cruise)*(t-t_acc-t_cruise)*dec*0.5);
                // velocity robot is at at time=t
                result[1]=v_cruise+dec*(t-t_acc-t_cruise);
                // deceleration in deceleration phase
                result[2]=dec;
                return result;
            }
        }
        // same comments apply
        if(profile==MotionProfile.TRIANGULAR)//this entire thing is unnecessary. It's the same as previous (trapezoidal) code, but with t_cruise and d_cruise trivialized to 0, which already happened. somehow it makes me less nervous to have this though
        {
            if(t>=0&t<=t_acc)
            {
                result[0]=v_start*t+0.5*acc*t*t;
                result[1]=v_start+acc*t;
                result[2]=acc;
                return result;
            }
            if(t>=(t_acc)&t<(t_acc+t_dec))
            {
                result[0]=d_acc+((t-t_acc)*v_cruise+(t-t_acc)*(t-t_acc)*dec*0.5);
                result[1]=v_cruise+dec*(t-t_acc);
                result[2]=dec;
                return result;
            }
        }
        return result;
    }

    public void generateMotionProfile()
    {
        // spline generating between waypoints
        // extrapolate points at in between times
        // specifically, at times when the motion profile loop starts an iteration

        // always at least starting point -> +1
        // total time * frequency = number of times it will run in the total time interval
    	// +1 for start
    	// +1 for end (basically impossible total time is a multiple of period, and can't hurt since 0 acceleration)
        mPoints = new MotionPoint[(int) ((t_acc + t_cruise + t_dec)*AutonomousConstants.LOOP_HERTZ + 2)];
        // go through each point loop will run at
        // and get the motion point at that time
        double time;
        for(int i=0; i<mPoints.length - 1; i++)
        {
            // time into path so far -> i-th iteration in loop of LOOP_HERTZ frequency
            // time = #/(# per second)
            time=i/AutonomousConstants.LOOP_HERTZ;
            // generate i-th motion point
            mPoints[i] = getMotionPoint(time);
        }
        mPoints[mPoints.length - 1] = getMotionPoint(t_total);
        
        for(int i=0; i<mPoints.length; i++)//sets their rotational acclerations
        {
            // calculate time at i-th MP
            time=i/AutonomousConstants.LOOP_HERTZ;
            // want it to have no angular acceleration at the end
            if(i==0||i==mPoints.length-1) mPoints[i].r_acc=0;
            // approximate angular acceleration with symmetric derivative approximation
            else mPoints[i].r_acc=(mPoints[i+1].r_vel-mPoints[i-1].r_vel)*2/AutonomousConstants.LOOP_HERTZ;
        }
    }

    public MotionPoint getMotionPoint(double t)
    {
        // get point info at t, set data as variables
        double linvel=getPointInfo(t)[1];
        double linpos=getPointInfo(t)[0];
        double linacc=getPointInfo(t)[2];

        // polynomial number in which the distance linpos along spline is at
        int splno= (int) (pathfinder.getSplineNo(linpos)[0]);
        // how far into that polynomial the distance is
        double d=pathfinder.getSplineNo(linpos)[1];
        // get parameter value for polynomial at distance d into it
        double sval=pathfinder.splines[splno].ltos.ceilingEntry(d).getValue();
        // calculate x at point
        double x = pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].xcoef, sval);
        // calculate y at point
        double y = pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].ycoef, sval);
        // calculate x derivative at point
        double xp=pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].xprimecoef, sval);
        // calculate x double derivative at point
        double xpp=pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].xdoubleprimecoef, sval);
        // calculate y derivative at point
        double yp=pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].yprimecoef, sval);
        // calculate y double derivative at point
        double ypp=pathfinder.splines[splno].evaluateFunction(pathfinder.splines[splno].ydoubleprimecoef, sval);

        // fancy equation for curvature of path at point
        // follows right hand coordinate system: x in front of robot, y to the right, z down
        // allow negative curvature so we can get negative angular velocity
        double curv=(ypp*xp-xpp*yp)/Math.pow((xp*xp + yp*yp), 1.5);// curvature is 1/radius

        // angle robot is facing
        double rotpos=Math.atan2(yp, xp);
        // angular velocity
        double rotvel=linvel*curv;//cuz curvature is 1/radius

        double diffSpeed = rotvel * DriveConstants.WHEEL_TRACK_INCHES / 2.0;

        // Compute, report, and limit lateral acceleration
		if (Math.abs(rotvel * linvel) > DriveConstants.MAX_LAT_ACCELERATION_IPSPS)
		{
			linvel = Math.signum(linvel)*DriveConstants.MAX_LAT_ACCELERATION_IPSPS/Math.abs(rotvel);
        }
        
        double left_speed = linvel + diffSpeed;
		double right_speed = linvel - diffSpeed;
        
        // create the MP given data of motion at the point
        MotionPoint mp = new MotionPoint(linpos, linvel, linacc,rotpos, rotvel, left_speed, right_speed);
        mp.setT(t);
        
        mp.setX(x);
        mp.setY(y);

        return mp;
    }       

    public MotionPoint[] getMotionPoints() {
        return mPoints;
    }
}
