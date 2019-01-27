package frc.robot.operatorinterface;

public class ControllerMapper {
    private static ControllerMapper xboxInstance;
    private static ControllerMapper ps4Instance;

    private int leftStickX;
    private int leftStickY;
    private int rightStickX;
    private int rightStickY;
    private int leftTrigger;
    private int rightTrigger;

    private int square;
    private int cross;
    private int circle;
    private int triangle;

    private int l1;
    private int r1;
    private int l2;
    private int r2;

    private int lStickButton;
    private int rStickButton;

    private int brandButton;

    public static ControllerMapper xbox() {
        if (xboxInstance == null) {
            xboxInstance = new ControllerMapper();
            xboxInstance.leftStickX = XboxConstants.LEFT_STICK_X.getValue();
            xboxInstance.leftStickY = XboxConstants.LEFT_STICK_Y.getValue();
            xboxInstance.leftTrigger = XboxConstants.LEFT_TRIGGER.getValue();
            xboxInstance.rightTrigger = XboxConstants.RIGHT_TRIGGER.getValue();
            xboxInstance.rightStickX = XboxConstants.RIGHT_STICK_X.getValue();
            xboxInstance.rightStickY = XboxConstants.RIGHT_STICK_Y.getValue();

            xboxInstance.square = XboxConstants.X_BUTTON.getValue();
            xboxInstance.cross = XboxConstants.A_BUTTON.getValue();
            xboxInstance.circle = XboxConstants.B_BUTTON.getValue();
            xboxInstance.triangle = XboxConstants.Y_BUTTON.getValue();

            xboxInstance.l1 = XboxConstants.LB_BUTTON.getValue();
            xboxInstance.r1 = XboxConstants.RB_BUTTON.getValue();
            // xbox has triggers, not buttons
//            xboxInstance.l2 = XboxConstants.something.getValue();
//            xboxInstance.r2 = XboxConstants.something.getValue();

            xboxInstance.lStickButton = XboxConstants.L_STICK.getValue();
            xboxInstance.rStickButton = XboxConstants.R_STICK.getValue();
            xboxInstance.brandButton = XboxConstants.XBOX_BUTTON.getValue();
        }
        return xboxInstance;

    }

    public static ControllerMapper ps4() {
        if (ps4Instance == null) {
            ps4Instance = new ControllerMapper();
            ps4Instance.leftStickX = PS4Constants.LEFT_STICK_X.getValue();
            ps4Instance.leftStickY = PS4Constants.LEFT_STICK_Y.getValue();
            ps4Instance.leftTrigger = PS4Constants.LEFT_TRIGGER.getValue();
            ps4Instance.rightTrigger = PS4Constants.RIGHT_TRIGGER.getValue();
            ps4Instance.rightStickX = PS4Constants.RIGHT_STICK_X.getValue();
            ps4Instance.rightStickY = PS4Constants.RIGHT_STICK_Y.getValue();

            ps4Instance.square = PS4Constants.SQUARE.getValue();
            ps4Instance.cross = PS4Constants.CROSS.getValue();
            ps4Instance.circle = PS4Constants.CIRCLE.getValue();
            ps4Instance.triangle = PS4Constants.TRIANGLE.getValue();

            ps4Instance.l1 = PS4Constants.L1.getValue();
            ps4Instance.r1 = PS4Constants.R1.getValue();
            ps4Instance.l2 = PS4Constants.L2.getValue();
            ps4Instance.r2 = PS4Constants.R2.getValue();

            ps4Instance.lStickButton = PS4Constants.L_STICK.getValue();
            ps4Instance.rStickButton = PS4Constants.R_STICK.getValue();
            ps4Instance.brandButton = PS4Constants.PS4.getValue();

        }
        return ps4Instance;
    }

    public int getLeftStickX() {
        return leftStickX;
    }

    public int getLeftStickY() {
        return leftStickY;
    }

    public int getRightStickX() {
        return rightStickX;
    }

    public int getRightStickY() {
        return rightStickY;
    }

    public int getLeftTrigger() {
        return leftTrigger;
    }

    public int getRightTrigger() {
        return rightTrigger;
    }

    public int getSquare() {
        return square;
    }

    public int getCross() {
        return cross;
    }

    public int getCircle() {
        return circle;
    }

    public int getTriangle() {
        return triangle;
    }

    public int getL1() {
        return l1;
    }

    public int getR1() {
        return r1;
    }

    public int getL2() {
        return l2;
    }

    public int getR2() {
        return r2;
    }

    public int getLStickButton() {
        return lStickButton;
    }

    public int getRStickButton() {
        return rStickButton;
    }

    public int getBrandButton() {
        return brandButton;
    }

}
