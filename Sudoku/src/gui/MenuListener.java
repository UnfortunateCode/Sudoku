package gui;

import java.awt.event.*;

public class MenuListener implements ActionListener {
	VBoard vb;
	
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("Clear")) {
			vb.clear();
			vb.clear();
			vb.repaint();
		} else if (ae.getActionCommand().equals("Highlight")) {
			vb.setHighable(!vb.highlight);
			vb.repaint();
		}
	}
	
	public void addVBoard(VBoard vboard) {
		vb = vboard;
	}

}
