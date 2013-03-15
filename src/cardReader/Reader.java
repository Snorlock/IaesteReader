/*
 * Author: Snorre Lothar von Gohren Edwin
 * 		   snorre.edwin@gmail.com
 * 
 * 
 * Se logs for å følge med prosessen til oppkoblingen.
 * Error og annet kommer der. Sys out er bare starten av oppkobling, og om det funket eller ikke.
 * Funket det så spytter den ut kortnummeret når du scanner kortet også.
 * For å teste, kjør testCard. Den står instilt på IaesteRFIDreader, det kan endres, men da
 * må du lage din egen classe. Les i IaesteRFIDreader for å se hvilken leser som brukes.
 * 
 * 
 * 
 */

package cardReader;

import gnu.io.*;
import java.lang.Exception;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.io.*;
import org.apache.log4j.*;

public abstract class Reader implements SerialPortEventListener {
	protected static Logger log = Logger.getLogger(Reader.class);
	
	
	InputStreamReader in;
	OutputStream out;
	CommPortIdentifier cpID;
	ReaderListener Rlistener;
	SerialPort sPort;
	
	String portname;
	String readerName = "DefaultReader";
	String charReader = "";
	String connectChar = "";
	String syncCharFromReader = "";
	
	boolean connected = false;
	boolean isReady = true;

	
	public Reader(ReaderListener rl) {
		Rlistener = rl;
	}
	
	public String getPortName(){
		return portname;
	}
	
	public String getReaderName() {
		return readerName;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setReaderListener(ReaderListener rl){
		Rlistener = rl;
	}
	
	/*
	 * This starts the whole process. This should be run when you start a new reader.
	 */
	public void start() {
		System.out.println("Starting to connect");
		log.debug("Starting to connect");
		getAvaileblePorts();
		Rlistener.notifyChangeStatus(this);
	}
	
	/*
	 * This stops the whole process. This should be run when you start a new reader.
	 */
	
	public void stop() {
    	if (!connected) return;
    	if (sPort != null) {
    		log.debug("Stopper leser: " + readerName);
    	    sPort.close();
    	}
    	connected = false;
    	Rlistener.notifyChangeStatus(this);
    }
	
	/*
	 * 
	 * Implemented by the SerlialPortEventListenere interface
	 * This reacts when the reader sends a event
	 *  
	 *  */
	public void serialEvent(SerialPortEvent event) {
		String connectMessage = "";
		byte[] b = new byte[1];
			try {
				isReady = false;
				while(in.ready()) {
					b[0] = (byte)in.read();
					connectMessage = connectMessage + new String(b,"ASCII");
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error in bytes recieved from reader");
			}
			isReady = true;
			if(connectMessage.charAt(0) == syncCharFromReader.charAt(0)) {
				log.debug("Recieved synkronize from " + this.getReaderName()+". Message: " +connectMessage);
			}
			else if(connectMessage.charAt(0) == 63) {
				System.out.println("Reader is corrupt - Message it is sending is: " + connectMessage + "Unplugg and plugg inn the reader again!");
				log.error("Reader is corrupt - Message it is sending is: " + connectMessage  + "Unplugg and plugg inn the reader again!");
			}
			else {
				log.debug("Data fra kortleser (" + this.getReaderName() + "): " + connectMessage);
			}
			process(connectMessage);
	}
	
	/*
	 * 
	 *This message is used to connect to reader.
	 *It first sends a string to say hello.
	 *And to send a connect string after it has recieved and sync message. 
	 * 
	 */
	
	protected boolean sendMessage(String s) {
        if (isReady) {
            byte[] b = s.getBytes();
            try {
            	if(s == charReader) {
	            	log.debug("Sending hello to "+this.getReaderName()+". Message: "+s);
	                out.write(b);
            	}
            	else if(s == connectChar) {
	            	log.debug("Sending connect to "+this.getReaderName()+". Message: "+s);
	                out.write(b);
            	}
            	else {
	            	log.debug("Sending message "+this.getReaderName()+". Message: "+s);
	                out.write(b);
            	}
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else {
            log.error("Error while sending: not ready to send message: " + s);
            try {
                Thread.sleep(200);
                sendMessage(s);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
	
	/*
	 * 
	 *This one is used to open the commport
	 *Starts the inputstream reader, based on the serialpors inputstream.
	 *It also starts the outputstream.
	 *It sets the comport id aswell. 
	 * 
	 */
	
	protected boolean setPort (CommPortIdentifier com) {
		try{
			CommPort commport = com.open(this.getReaderName(), 9600);
			if(commport instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commport;
				sPort = serialPort;
				serialPort.addEventListener(this);
				
				in = new InputStreamReader(serialPort.getInputStream());
				out = serialPort.getOutputStream();
				serialPort.notifyOnDataAvailable(true);
			}
		}
		catch(PortInUseException e){
			log.error("Error: "+com.getName()+" port already in use.");
			return false;
		}
		catch(TooManyListenersException ex) {
			log.error("Error: Too many listeners here? "+ ex);
			return false;
		}
		catch(IOException exc) {
			log.error("Error: IO exception: " +exc);
			return false;
		}
		cpID = com;
		return true;
		
	}
	
	/*
	 * 
	 *This observes the availeble commPorts
	 *From the rxtxCOMM librari it can be used enumuration over the ports
	 *It checks if it is a COM port, which basically checks if it is a seriall port.
	 *But had to write it this way so it was going to use processing power on sending other comports to setPort.
	 *Other comports can be LPT, or even virtual ports. 
	 *RIM(Research in motion - blackberry development) creates some COM virtual ports.
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public void getAvaileblePorts() {
		Enumeration<CommPortIdentifier> thePorts = null;
		try {
			thePorts = CommPortIdentifier.getPortIdentifiers();
		} catch (java.lang.UnsatisfiedLinkError e2) {
			log.error("Error: Dont find any comports. Drivers installed? (rxtxSeriall.dll)");
		} catch (Exception e) {
			log.error("Error: Dont find any comports. Drivers installed? (rxtxSeriall.dll)");
		}
		if(thePorts != null ){
			while(thePorts.hasMoreElements() && !connected){
				CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
				if(com.getName().indexOf("COM") != -1) {
					log.debug("Found comm port " +com.getName());
					if(!com.isCurrentlyOwned()){
						if(setPort(com)) {
							portname = com.getName();
							if(sendMessage(charReader)) {
								log.debug("Sent message, tries to connect to " +com.getName());
								/*
								 * ****VIKTIG****
								 * This thread sleep is added to let the system change the connected state.
								 * If this isnt here, it usually say its not connected, but still works.
								 */
								try {
									Thread.sleep(400);
								} catch (Exception e) {
									// TODO: handle exception
								}
								if(!connected) {
									log.error("Error: Could not connect to " +com.getName());
									sPort.close();
								}
							}
						}
					}
				}
				else {
					log.error("No COM element connected to the computer, please insert the reader");
				}
			}
		}
		else {
			log.error("Array empty and no ports found");
		}
		if(connected) {
			log.debug("Reader is now connected and ready to read");
		}
		
	}
	/*
	 * 
	 * This abstract method is there to send the process of recieving bits, to the current reader.
	 * All readers have different methods of sending and recieving bits. 
	 * The reader metod has been made as general as possible.
	 * 
	 */
	abstract protected void process (String message);
	
}
