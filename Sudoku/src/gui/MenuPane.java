package gui;

import javax.swing.*;

public class MenuPane extends JPanel {
	private static final long serialVersionUID = 1L;
	VBoard vb;
	MenuListener ml;
	
	public MenuPane(VBoard vboard, MenuListener menul) {
		vb = vboard;
		ml = menul;
		ml.addVBoard(vb);
		
		JButton clear = new JButton("Restart");
		JButton highlight = new JButton("Highlight");
		
		clear.setActionCommand("Clear");
		highlight.setActionCommand("Highlight");
		
		clear.addActionListener(ml);
		highlight.addActionListener(ml);
		
		add(clear);
		add(highlight);
		
		setVisible(true);
	}
}
