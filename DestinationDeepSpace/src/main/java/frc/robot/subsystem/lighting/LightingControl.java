package frc.robot.subsystem.lighting;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.SerialPortManager;

import jssc.SerialPort;
import jssc.SerialPortException;


public class LightingControl 
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
	
	public LightingControl() 
	{
		
		SmartDashboard.putString("LightingControl/Status",  "Starting BucketLights");
	
		// Had to get at least one Lambda expression in the code somewhere! -tjw
		serialPort = SerialPortManager.findPort( 
				(input) -> input.contains("BucketLights"), 
				SerialPort.BAUDRATE_38400);
		
		if( serialPort == null)
		{
			SmartDashboard.putString("LightingControl/Status", "No BucketLights board found!");
			return;
		}		
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


	public void set(int lightingObject, String function, String color, int nspace, int period_msec)
	{
		set(lightingObject, function, color, nspace, period_msec, prefBrightness);
	}	
	public void set(int lightingObject, String function, String color, int nspace, int period_msec, int brightness)
	{
		if (serialPort != null)
		{
			try
			{
				String command = String.format(FORMAT,
											   lightingObject,
											   function,
											   color,
											   nspace,
											   brightness,
											   period_msec);
				
				//SmartDashboard.putString("LightingControl/Status", command);
				serialPort.writeString(command);
			}
			catch (SerialPortException e) 
			{
				// pass
			}
		}		
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
