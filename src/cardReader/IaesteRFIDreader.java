package cardReader;

/*
 * Denne readeren her er spesielt laget for den som er kjøpt inn i 2011.
 * Det er en IDETRONIC smart usb reader. http://idtronic-smartrfid.com/usb-readers/desktop-reader/
 * Grunnen til at denne er spesiell, er fordi den leser inn bytsene ved LSB(least significant bit) 
 * og vi må gjøre om til MSB(most significant bit). Derfor er eksisterer transelate.
 * 
 * 
 * 
 * 
 */

public class IaesteRFIDreader extends Reader {

	public IaesteRFIDreader(ReaderListener rl) {
		super(rl);
		readerName = "IAESTEreader";
		charReader = "I";
		syncCharFromReader = "S";
		connectChar = "C";
	}

	@Override
	protected void process(String message) {
		try {
			if(!connected) {
				
				if(message.indexOf(syncCharFromReader) != -1) {
					sendMessage(connectChar);
					connected = true;
				}
			}
			else if(message.charAt(0) == 85) {
				if(message.length() >= 11) {
					log.debug("Recieving data: "+message);
					notifyCardNumber(message.substring(1, 11));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	protected void notifyCardNumber(String message) {
		try {
			long cardNumber = Long.parseLong(message, 16);
			log.debug("Wrong cardnumber which is MSB: " +cardNumber);
			long rightNumber = translate(cardNumber);
			log.debug("Right cardnumber which is LSB: " +rightNumber);
			Rlistener.recieveRFIDCardNumber(rightNumber);
		} catch (Exception e) {
			log.error("Long parse error");
		}
		
	}
	
	public Long translate(Long oldValue) { 
		long newValue = 0;
		for (int part=0; part<8; part++) {
			long oldPart = ( (oldValue >> part*8) & 0xFF);
			long newPart = 0;
			for (int i=0; i<8; i++) {
				boolean bitSet = (((oldPart & (int) Math.pow(2, i)) & 0xFF) > 0 );
				if (bitSet) {
					newPart += (int) Math.pow(2, (7-i));
				} 
			}
			newValue += (newPart << part*8);
		}
		return newValue;
	}
	
	

}
