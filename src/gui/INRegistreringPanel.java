package gui;

import javax.swing.*;
import cardReader.*;
import logic.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class INRegistreringPanel extends JPanel {
		
	JLabel staticText, cardNumber;
	JButton testButton;
    logicHandler logic;

	
	public INRegistreringPanel(final logicHandler logic) {
        this.logic = logic;
		staticText = new JLabel("Scann kortet ditt");
        testButton = new JButton("TRYKK MEG");
        add(testButton);
        add(staticText);

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        }

        );
	}
	
	public void sendNumber(long number) {
        logic.sendMessage(""+number);
	}

}
