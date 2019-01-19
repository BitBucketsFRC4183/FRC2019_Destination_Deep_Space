package frc.robot.subsystem.scoring;

public enum TestWheelPositions {
    DEG_0   (0),
    DEG_90  (90),
    DEG_180 (180),
    DEG_270 (270);

    private final double DEG;
    private TestWheelPositions(double deg) {
        DEG = deg;
    }

    public double getDeg() {
        return DEG;
    }
}