package logic;

import cardReader.*;
import gui.*;

/*
* Dette er den logiske h�ndtereren
* */
public class logicHandler implements ReaderListener {

	private Reader reader;
    private pusherConnection pub;
	private cardStarter gui;
	private INRegistreringPanel inReg;
	private cardStarter cardStarter;

	public logicHandler(cardStarter cardStarter,String channel) {
        reader = new IaesteRFIDreader(this);
		reader.start();
        pub = new pusherConnection(channel);

	}

	@Override
	public void recieveRFIDCardNumber(long number) {
		inReg.sendNumber(number);
	}

	@Override
	public void notifyChangeStatus(Reader reader) {
		System.out.println(reader.getReaderName()+ (reader.isConnected() ? " ble koblet til." : " ble ikke tilkoblet"));
	}
	
	public void setInReg(INRegistreringPanel inReg) {
		this.inReg = inReg;
	}

    public void sendMessage(String message){
        pub.sendMessage(message);
    }
	
}
