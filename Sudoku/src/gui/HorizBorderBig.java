package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class HorizBorderBig extends JPanel {
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon border;
	int NumVals, numX;
	
	public HorizBorderBig (int nv) {
		NumVals = nv;
		numX = (int)Math.ceil(Math.sqrt(nv));
		url = VSudoku.class.getResource("/resources/images/hBorderBig.gif");
		border = new ImageIcon(url);
		
		setPreferredSize(new Dimension(16*numX, 10));
		//setBackground(Color.red);
	}
	
	public void paintComponent(Graphics g) {
		for (int i = 0; i < numX; ++i) {
			g.drawImage(border.getImage(), 16*i, 0, this);
		}
	}
}
