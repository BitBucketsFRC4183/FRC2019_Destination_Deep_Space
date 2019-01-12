package frc.robot.operatorinterface;


import java.util.HashSet;
import java.util.Set;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class OI {

	// Each Modal must add itself to this Set so we can notify them appropriately
	private Set<Modal> modals = new HashSet<>();
	
	private final PhysicalController driverController = new PhysicalController( new Joystick(0));
	private final PhysicalController operatorController = new PhysicalController( new Joystick(1));
	
	
	//****************************
	// BUTTON DEFINITIONS
	//****************************
	
	//public final LogicalButton btnExample = new ModalButton(exampleController.bExample);
	
	// Drive
	public final LogicalButton btnLowSensitiveDrive = new ModalButton(driverController.bR1);  
	public final LogicalButton btnInvertAxis = new ModalButton(driverController.bR2);
	public final LogicalButton btnAlignLock = new ModalButton(driverController.bL1);
	public final LogicalButton btnDriveLock = new ModalButton(driverController.bL2);	
	public final LogicalButton btnStatus = new ModalButton(driverController.bTrackpad);
	public final LogicalButton btnMoveByTest = new ModalButton(driverController.bTriangle); 

	public final LogicalButton btn180 = new ModalButton(driverController.bPovDown);
	
	// Scoring Subsystem
	public final LogicalButton btnIntake = new ModalButton(operatorController.bCross);
	
	// Climber Buttons
	public final LogicalButton btnUp   = new ModalButton(operatorController.bPovUp);
	public final LogicalButton btnDown = new ModalButton(operatorController.bPovDown);
	
	
	
	//****************************
	// AXIS DEFINITIONS
	//****************************
	public final LogicalAxis axisForward = new ModalAxis(driverController.aLeftY);
	public final LogicalAxis axisTurn = new ModalAxis(driverController.aRightX);
		
	
	//****************************
	// Permanent SoftButtons (used for inter-State-Machine communications)
	//****************************
	public final LogicalButton sbtnShake = new SoftButton();

	/*
	 * I removed the driver/operator specific remappings because we didn't use them 
	 * and they clutter up this already too-big class.
	 * But if you need to remap a button or axis, here's the right way to do it.
	 * Take axisTurn as example; above, axisTurn is mapped to driverController.aRightX:
	 *   public final LogicalAxis axisTurn = new ModalAxis(driverController.aRightX)
	 * 
	 * If you wanted to change this so axisTurn was mapped to aRightX for driver A
	 * and aLeftX for driver B, change the above so that axisTurn is by default mapped to nothing:
	 *   public final LogicalAxis axisTurn = new ModalAxis(null)
	 *   
	 * Then add appropriate re-mapping methods to be called from Robot.teleopInit(), in those methods assign
	 * the appropriate Physical value to the ModalButton's teleopAxis member, e.g:
	 * In method map_driver_A() do
	 *   ((ModalAxis)axisTurn).teleopAxis = driverController.aRightX;
	 * and in method map_driver_B() do
	 *   ((ModalAxis)axisTurn).teleopAxis = driverController.aLeftX;
	 *   
	 * Don't remap by overwriting axisTurn, ie. don't (in a re-mapping method) do something like
	 *   axisTurn = new ModalAxis(driverController.aLeftX);
	 *   
	 * Note: axisTurn (and all the other Buttons/Axis) are declared "final" to keep you from doing this.
	 */
	
	/**
	 * If your Command needs rising or falling edge detect on a button,
	 * use this method to get a ButtonEvent for that purpose.
	 * In initialize(), get your ButtonEvent: oi.ButtonEvent btnShoot = oi.getBtnEvt( oi.btnShoot).
	 * In isFinished(), test the ButtonEvent: btnShoot.onPressed() or btnShoot.onReleased().
	 * 
	 * @param btn The Logical button 
	 * @return The ButtonEvent that wraps the button
	 */
	public ButtonEvent getBtnEvt( LogicalButton btn) { return new ButtonEvent(btn); }

		
	// Singleton method; use OI.instance() to get the OI instance.
	public static OI instance() {
		if(inst == null)
			inst = new OI();
		return inst;		
	}
	private static OI inst;	
	private OI() {}
	
	
	// Represents all the physical buttons & axis on one controller.
	// Lotta ugly typing in here but fortunately no need to change anything. 
	private class PhysicalController {

		@SuppressWarnings("unused")
		private final PhysicalButton 
			bSquare, bCross, bCircle,  bTriangle, 
			bL1, bR1, bL2, bR2,
			bShare, bOptions, bLstick, bRstick,
			bPS4, bTrackpad;
		
		private final PhysicalPovButton
			bPovUp, bPovRight, bPovDown, bPovLeft;
				
		@SuppressWarnings("unused")
		private final PhysicalAxis 
			aLeftX, aLeftY, aRightX, aRightY, aL2, aR2;
		
		private PhysicalController( Joystick controller) {
			bSquare = new PhysicalButton(controller, PS4Constants.SQUARE.getValue());
			bCross =  new PhysicalButton(controller, PS4Constants.CROSS.getValue());
			bCircle = new PhysicalButton(controller, PS4Constants.CIRCLE.getValue());
			bTriangle = new PhysicalButton(controller, PS4Constants.TRIANGLE.getValue());
			bL1 = new PhysicalButton(controller, PS4Constants.L1.getValue());
			bR1 = new PhysicalButton(controller, PS4Constants.R1.getValue());
			bL2 = new PhysicalButton(controller, PS4Constants.L2.getValue());
			bR2 = new PhysicalButton(controller, PS4Constants.R2.getValue());
			bShare = new PhysicalButton(controller, PS4Constants.SHARE.getValue());
			bOptions = new PhysicalButton(controller, PS4Constants.OPTIONS.getValue());
			bLstick = new PhysicalButton(controller, PS4Constants.L_STICK.getValue());
			bRstick = new PhysicalButton(controller, PS4Constants.R_STICK.getValue());
			bPS4 = new PhysicalButton(controller, PS4Constants.PS4.getValue());
			bTrackpad = new PhysicalButton(controller, PS4Constants.TRACKPAD.getValue());
			bPovUp = new PhysicalPovButton(controller, POV_BUTTON.UP);
			bPovRight = new PhysicalPovButton(controller, POV_BUTTON.RIGHT);
			bPovDown = new PhysicalPovButton(controller, POV_BUTTON.DOWN);
			bPovLeft = new PhysicalPovButton(controller, POV_BUTTON.LEFT);

			aLeftX = new PhysicalAxis( controller, PS4Constants.LEFT_STICK_X.getValue(), false);
			aLeftY = new PhysicalAxis( controller, PS4Constants.LEFT_STICK_Y.getValue(), true);
			aRightX = new PhysicalAxis( controller, PS4Constants.RIGHT_STICK_X.getValue(), false);
			aRightY = new PhysicalAxis( controller, PS4Constants.RIGHT_STICK_Y.getValue(), true);
			aL2 = new PhysicalAxis( controller, PS4Constants.L2_AXIS.getValue(), false);
			aR2 = new PhysicalAxis( controller, PS4Constants.R2_AXIS.getValue(), false);
		}
	}
	
	
	// Modal is a thing that needs to be informed of Robot mode changes.
	private interface Modal {
		default void setDisabledMode() {}
		default void setTeleopMode() {}
		default void setAutoMode() {}
	}
	
	
	// Following 3 functions called from Robot on mode changes
	public void setDisabledMode() {
		for( Modal m : modals)
			m.setDisabledMode();
	}
	
	public void setTeleopMode() {
		for( Modal m : modals)
			m.setTeleopMode();		
	}
	
	public void setAutoMode() {
		for( Modal m : modals)
			m.setAutoMode();		
	}

	
	// Represents a generic button.
	// get() is meaningful for all buttons;
	// the other methods are only meaningful for Soft buttons.
	public interface LogicalButton {
		public boolean get();
		public default void push() {}
		public default void release() {}
		public default void hit() { hit(300); }
		public default void hit( long msecs) {}  // "hit" means push, then release later
	}
	
	
	// ModalButton delegates to NadaButton, PhysicalButton, or SoftButton
	// depending on mode Disabled, Teleop, Autonomous, respectively.
	private class ModalButton implements LogicalButton, Modal {
		private LogicalButton nadaButton = new NadaButton();
		private LogicalButton teleopButton;
		private LogicalButton softButton = new SoftButton();
		private LogicalButton activeButton = nadaButton;
		
		private ModalButton( LogicalButton teleopButton) {
			this.teleopButton = teleopButton;
			modals.add(this);
		}
		
		@Override
		public void setDisabledMode() { activeButton = nadaButton; }
		@Override
		public void setTeleopMode() { activeButton = teleopButton; }
		@Override
		public void setAutoMode() { activeButton = softButton; }
		
		@Override
		public boolean get() { return activeButton.get(); }
		@Override
		public void push() { activeButton.push(); }
		@Override
		public void release() { activeButton.release(); }
		@Override
		public void hit( long msecs) { activeButton.hit( msecs); }
	}
	
	// NadaButton does nothing
	private class NadaButton implements LogicalButton {
		@Override
		public boolean get() { return false; }
	}
	
	// A LogicalButton operated by software (for Autonomous)
	private class SoftButton implements LogicalButton, Modal {
		volatile boolean state = false;		

		private SoftButton() { modals.add(this); }		
		
		// Put into initial state on Disabled
		@Override
		public void setDisabledMode() { release(); }
		
		@Override
		public boolean get() { return state; }
		@Override
		public void push() { state = true; }
		@Override
		public void release() { state = false; }
		@Override
		public void hit( long msecs) {
			// push() this, 
			// then start a thread to release() after a short while
			push();
			new Thread() {
				public void run() {
					try {
						Thread.sleep(msecs);
					} catch (InterruptedException e) {}
					release();
				}
			}.start();
		}
	}
	
	// A button on a controller
	private class PhysicalButton implements LogicalButton {
		Button btn;
		private PhysicalButton( Joystick controller, int btnNum) { 
			btn = new JoystickButton( controller, btnNum); 
		}
		
		@Override
		public boolean get() { return btn.get(); }		
	}
	
	// Allows you to use a controller axis as a button.
	// Not used yet but could come in handy someday I guess.
	@SuppressWarnings("unused")
	private class PhysicalAxisButton implements LogicalButton {
		private PhysicalAxis physAxis;
		private PhysicalAxisButton( Joystick controller, int axisNum, boolean invert) {
			physAxis = new PhysicalAxis( controller, axisNum, invert);
		}
		@Override
		public boolean get() { return physAxis.get() > 0.5; }
	}

	private enum POV_BUTTON { 
		UP(0), RIGHT(90), DOWN(180), LEFT(270);
		private int value;
		private POV_BUTTON(int value) {
			this.value = value;
		}
		private int getValue() { return value; }
	}
	
	// Allows you to use a POV button as a button
	private class PhysicalPovButton implements LogicalButton {		
		private Joystick controller;
		private POV_BUTTON whichPov;
		
		private PhysicalPovButton( Joystick controller, POV_BUTTON whichPov) {
			this.controller = controller;
			this.whichPov = whichPov;
		}
		
		@Override
		public boolean get() { return controller.getPOV() == whichPov.getValue(); }		
	}
	
	// Wraps a LogicalButton & makes it easy to catch rising/falling edges
	public class ButtonEvent {

		private final LogicalButton m_button;
		private boolean m_wasPressed;

		private ButtonEvent( LogicalButton button) {
			m_button = button;
			m_wasPressed = isPressed();
		}

		 // Returns true if the button is currently pressed
		public boolean isPressed() { return m_button.get(); }

		 // Returns true only once when the button is pressed
		 // (rising edge detect)
		public boolean onPressed() {	
			boolean rtn = false;
			boolean isPressedNow = isPressed();
			if( isPressedNow && !m_wasPressed)
				rtn = true;
			m_wasPressed = isPressedNow;
			return rtn;
		}

		 // Returns true only once when the button is released
		 // (falling edge detect)
		public boolean onReleased() {
			boolean rtn = false;
			boolean isPressedNow = isPressed();
			if( !isPressedNow && m_wasPressed)
				rtn = true;
			m_wasPressed = isPressedNow;
			return rtn;		
		}
	}
	
	
	// Represents a generic Axis.
	// get() is meaningful for all axis;
	// set() is meaningful only for soft axis.
	public interface LogicalAxis {
		public double get();
		public default void set( double value) {}
	}

	// ModalAxis delegates to NadaAxis, PhysicalAxis, or SoftAxis
	// according to mode Disabled, Teleop, Autonomous respectively.
	private class ModalAxis implements LogicalAxis, Modal {
		private LogicalAxis nadaAxis = new NadaAxis();
		private LogicalAxis teleopAxis;
		private LogicalAxis softAxis = new SoftAxis();
		private LogicalAxis activeAxis = nadaAxis;

		private ModalAxis( LogicalAxis teleopAxis) {
			this.teleopAxis = teleopAxis;
			modals.add(this);
		}
		
		@Override
		public void setDisabledMode() { activeAxis = nadaAxis; }
		@Override
		public void setTeleopMode() { activeAxis = teleopAxis; }
		@Override
		public void setAutoMode() { activeAxis = softAxis; }
		
		@Override
		public double get() { return activeAxis.get(); }
		@Override
		public void set( double value) { activeAxis.set(value); }		
	}
	
	// NadaAxis does nothing
	private class NadaAxis implements LogicalAxis {
		@Override
		public double get() { return 0.0; }
	}
	
	// An Axis operated by software (for Autonomous)
	private class SoftAxis implements LogicalAxis, Modal {
		volatile double value = 0.0;
		
		private SoftAxis() { modals.add(this);	}
		
		// Put in initial state when Disabled
		@Override
		public void setDisabledMode() { set(0.0); }
		
		@Override
		public double get() { return value; }
		@Override
		public void set( double value) { this.value = value; }
	}
	
	
	// An Axis on a controller
	private class PhysicalAxis implements LogicalAxis {
		Joystick controller;
		int axisNum;
		boolean invert;
		private PhysicalAxis( Joystick controller, int axisNum, boolean invert) {
			this.controller = controller;
			this.axisNum = axisNum;
			this.invert = invert;
		}

		@Override
		public double get() {
			return (invert ? -1.0 : 1.0 ) * controller.getRawAxis(axisNum);
		}
	}

}



