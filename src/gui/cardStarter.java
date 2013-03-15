package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import logic.logicHandler;
import cardReader.*;
import gui.*;


public class cardStarter extends JFrame implements ActionListener {
	
	private JPanel jpanel;
	private IaesteRFIDreader reader;
	private ReaderListener readerListener;
	private logicHandler logic;
	private JLayeredPane layer;
    private JTextField channel;
	
	public cardStarter () {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle("IAESTE reader");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0,0,screenSize.width, screenSize.height);
		jpanel = new JPanel();
		add(jpanel);
		layer = new JLayeredPane();
		
		
		JButton inRegistrering = new JButton("IN Registrering");
		JButton inKveld = new JButton("IN Kvelds Arrangement");
		JButton iaesteArrangement = new JButton("Annet IAESTE arrangement");
        channel = new JTextField(30);
		
		inKveld.addActionListener(this);
		inRegistrering.addActionListener(this);
		iaesteArrangement.addActionListener(this);
		
		jpanel.add(inRegistrering);
		jpanel.add(inKveld);
		jpanel.add(iaesteArrangement);
        jpanel.add(channel);

        setSize(600,600);
        setLocation(700,100);
	}
	
	public static void main(String[] args) {
		new  cardStarter();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("ACtion is "+e.getActionCommand());
		if(e.getActionCommand() == "IN Registrering") {
			logic = new logicHandler(this, channel.getText());
			INRegistreringPanel inReg = new INRegistreringPanel(logic);
			logic.setInReg(inReg);
			this.add(inReg);
			inReg.setVisible(true);
			jpanel.setVisible(false);
			
		}
		else if(e.getActionCommand() == "IN Kvelds Arrangement"){
			
		}
		
		else if(e.getActionCommand() == "Annet IAESTE arrangement"){
			
		}
 		
	}

}
