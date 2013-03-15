package cardReader;

/*
 * Dette er lytteren.
 * Viktige momenter som er verdt å lytte på er:
 * recieveRFIDCardNumber
 * notifyChangeStatus(litt usikker på denne bruken)
 */

public interface ReaderListener {
	public void recieveRFIDCardNumber(long number);
	public void notifyChangeStatus(Reader reader);
	

}
