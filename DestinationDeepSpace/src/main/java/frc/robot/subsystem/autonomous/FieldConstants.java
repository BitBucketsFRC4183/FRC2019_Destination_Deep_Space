package frc.robot.subsystem.autonomous;

import frc.robot.subsystem.scoring.ScoringConstants;

// x = across field
// y = perpendicular to x
// all values in inches
public class FieldConstants {
    // starting position on hab 1
    public static final double START_X = 4 * 12 + (3*12)/2;
    // difference in y along different starting positions on hab 2
    // add for right, subtract for left, nothing for center
    public static final double DIF_Y = 02 * 12 + (3*12 + 4.0) / 2;

    // diagonal length of robot (30x28 in^2)
    private static final double ROBOT_DIAG = Math.sqrt(30 * 30 + 28 * 28);

    // length along 15 deg ramp
    public static final double RAMP_LENGTH = Math.sqrt(Math.pow(11.25, 2) + Math.pow(3, 2));

    // distance from starting point to start of ramp
    public static final double CENTER_TO_RAMP = 3.0*12/2;

    // x from wall to once robot at ground level
    public static final double MID_X = START_X + CENTER_TO_RAMP + RAMP_LENGTH + ROBOT_DIAG/2;

    public static final double HALF_FIELD_X = 27.0 * 12;
    public static final double FIELD_CENTER_TO_CARGO = 9;
    public static final double CARGO_LENGTH = 7*12 + 11.75;

    // robot in front of cargo bay with some space to spare, even with arm at cargo level
    // if arm was at 90 deg, there would be theoretically no space, but its above that
    // allowing for a few inches of freedom
    public static final double END_X = HALF_FIELD_X - FIELD_CENTER_TO_CARGO - CARGO_LENGTH - ScoringConstants.ARM_LENGTH_INCH;
    // y coord of hatch placing area
    public static final double END_Y = 0.25*(4*12 + 7.75);
}