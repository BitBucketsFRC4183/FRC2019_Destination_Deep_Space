package frc.robot.utils;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class SerialPortManager {
	
	public interface PortTester {
		/**
		 * Must return true iff you recognize your device in the input
		 * @param input
		 * @return
		 */
		boolean test( String input);
	}
	
	public static SerialPort findPort( PortTester tester, int baudRate) {
		return findPort( tester, baudRate, 3000);
	}

	public static SerialPort findPort( PortTester tester, int baudRate, int msecs) {
		
		String[] portNames = SerialPortList.getPortNames();

		if (portNames.length >= 1)
		{
			System.out.print("Port list:");
			for(String portName : portNames)
				System.out.print(" " + portName);
			System.out.println();		
			
			for( String portName : portNames) {
				
				try {
					
					System.out.println("Trying port:" + portName);
					SerialPort port = new SerialPort(portName);
					port.openPort();

					port.setParams(baudRate,
							SerialPort.DATABITS_8, 
							SerialPort.STOPBITS_1, 
							SerialPort.PARITY_NONE);
					

					String inBuff = "", inStr;				
					long tQuit = System.currentTimeMillis() + msecs;
					while( System.currentTimeMillis() < tQuit) {
						
						// Get input & append to inBuff
						if( (inStr = port.readString()) != null) {
							inBuff += inStr;
						}
						
						if( tester.test(inBuff) ) {
							System.out.format( "Success on port %s\n", portName);
							return port;
						}
					}
					
					port.closePort();				
				}
				catch( SerialPortException ex) {}			
			}
		}		
		// Not found
		return null;
	}

}
