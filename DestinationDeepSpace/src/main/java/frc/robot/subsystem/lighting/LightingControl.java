package frc.robot.subsystem.lighting;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.SerialPortManager;

import jssc.SerialPort;
import jssc.SerialPortException;


public class LightingControl extends Thread
{
	public static final int MIN_BRIGHTNESS = 2;
	public static final int SAFE_BRIGHTNESS = 32;
	public static final int FIELD_BRIGHTNESS = 100;
	public static final int MAX_BRIGHTNESS = 255;	// NOT EYE SAFE AT CLOSE RANGE, BUT MAY BE NEEDED FOR FIELD
	
	private static int prefBrightness = SAFE_BRIGHTNESS;
		
	private SerialPort serialPort;

//	String format is simple: 'NFCnbbbpppp'
//	    N - Strip Number 0..9
//	    F - Function
//	        0 (zero) = off
//	        1      = Solid ON
//	        S      = Snore on 3 second period
//	        B      = Blink with period pppp msec
//	        F      = Forward Chase n pixels (n >= 2, 1 on and n-1 off) with period pppp msec
//	        R      = Reverse Change n pixels (n >= 2, 1 on and n-1 off) with period pppp msec
//	        C      = Cylon (1-light side-to-side) with update period pppp msec
//			*      = Sparkles (random colors) changing at period pppp msec
//	    C - Color Code
//	        0 = black (OFF)
//	        W = white
//	        R = red
//	        G = green
//	        B = blue
//	        C = cyan
//	        M = magenta
//	        Y = yellow
//	        O = orange
//	        V = violet
//	    n - Used only in F and R functions to space the pixels (ignored in all other modes)
//	    bbb - Brightness from 000 to 255
//	    pppp - Period in milliseconds between transitions
	
	public static final String FUNCTION_OFF = "0";
	public static final String FUNCTION_ON = "1";
	public static final String FUNCTION_SNORE = "S";
	public static final String FUNCTION_BLINK = "B";
	public static final String FUNCTION_FORWARD = "F";
	public static final String FUNCTION_REVERSE = "R";
	public static final String FUNCTION_CYLON = "C";
	public static final String FUNCTION_SPARKLES = "*";
	
	public static final String COLOR_BLACK = "0";
	public static final String COLOR_WHITE = "W";
	public static final String COLOR_RED = "R";
	public static final String COLOR_GREEN = "G";
	public static final String COLOR_BLUE = "B";
	public static final String COLOR_CYAN = "C";
	public static final String COLOR_MAGENTA = "M";
	public static final String COLOR_YELLOW = "Y";
	public static final String COLOR_ORANGE = "O";
	public static final String COLOR_VIOLET = "V";
	
	private static final String FORMAT = "%d%s%s%d%03d%04d\r";

	private static int connectionAttempts = 0;
	
	// Singleton method; use LightingControl.instance() to get the LightingControl instance.
	public static LightingControl instance() {
		if(inst == null)
			inst = new LightingControl();
		return inst;
	}
	private static LightingControl inst;

	/**
	 * run - thread function to poll the serial port until we find the Lighting board
	 * Allows the search to run in the background with minimal interference to the
	 * other robot system startups. The polling will continue until 
	 */
	public void run()
	{
		long startTime_ms = System.currentTimeMillis();

		while ((serialPort == null) && 
		       ((System.currentTimeMillis() - startTime_ms) <= LightingConstants.POLLING_TIMEOUT_MSEC))
		{
			SmartDashboard.putNumber("LightingControl/ConnectionAttempts", connectionAttempts++);
			serialPort = SerialPortManager.findPort( 
					(input) -> input.contains("BucketLights"), 
					LightingConstants.BAUDRATE);
		
			if( serialPort == null)
			{
				SmartDashboard.putString("LightingControl/Status", "No BucketLights board found!");
				try {
					sleep(500, 0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (serialPort == null)
		{
			SmartDashboard.putString("LightingControl/Status", "No BucketLights board found! (Gave up)");
		}
		else
		{
			SmartDashboard.putString("LightingControl/Status", "BucketLights RUNNING!");
		}		
	}
	private LightingControl() 
	{
		SmartDashboard.putString("LightingControl/Status",  "Starting BucketLights");
	
		// Start the background polling that will find the serial port
		// This prevents the construction and initialization sequences from
		// delaying anything else in the system as we search for the lighting board
		start();
	}
	
	public void setOff(int lightingObject)
	{
		if (serialPort != null)
		{
			try
			{
				String command = String.format(FORMAT,
											   lightingObject,
											   FUNCTION_OFF,
											   COLOR_BLACK,
											   0,
											   0,
											   0);
				
				//SmartDashboard.putString("LightingControl/Status", command);
				serialPort.writeString(command);
			}
			catch (SerialPortException e) 
			{
				// pass
			}
		}		
	}	

	public String computeCommand(int lightingObject, String function, String color, int nspace, int period_msec, int brightness)
	{
		return String.format(FORMAT,
							lightingObject,
							function,
							color,
							nspace,
							brightness,
							period_msec);
	}

	public boolean isReady()
	{
		return (serialPort != null);
	}

	public String set(int lightingObject, String function, String color, int nspace, int period_msec)
	{
		return set(lightingObject, function, color, nspace, period_msec, prefBrightness);
	}	
	public String set(int lightingObject, String function, String color, int nspace, int period_msec, int brightness)
	{
		String command = "";
		if (serialPort != null)
		{
			try
			{
				command = computeCommand(lightingObject, function, color, nspace, period_msec, brightness);				
				SmartDashboard.putString("LightingControl/Status", command);
				serialPort.writeString(command);
			}
			catch (SerialPortException e) 
			{
				SmartDashboard.putString("LightingControl/Status", "Exception!");
			}
		}
		
		return command;
	}	
	
	public void setSleeping(int lightingObject)
	{
		set(lightingObject,
		    FUNCTION_SNORE,
		    COLOR_VIOLET,
		    0,	// nspace - don't care
		    0);	// period_msec - don't care
		
	}

}
