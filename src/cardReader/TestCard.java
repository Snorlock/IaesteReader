package cardReader;

/*
 * Dette er bare en classe som skriver ut kortnummer og tilkoblet eller ikke.
 * Det er også er det er en main klasse som starter den riktige leseren.
 */

public class TestCard implements ReaderListener {
	
	private static Reader rfid;
	
	@Override
	public void recieveRFIDCardNumber(long number) {
		System.out.println("Kort nummeret er: " +number);
	}

	@Override
	public void notifyChangeStatus(Reader reader) {
		System.out.println(reader.getReaderName() + (reader.isConnected() ? " ble koblet til" : " ble ikke tilkoblet"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestCard ts = new TestCard();
		rfid = new IaesteRFIDreader(ts);
		rfid.start();
	}

}
