package gui;

import javax.swing.*;

import java.awt.*;
import java.net.URL;

public class VertBorderBig extends JPanel {
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon border;
	int NumVals, numY;
	
	public VertBorderBig (int nv) {
		NumVals = nv;
		numY = (int)Math.ceil((float)nv/Math.ceil(Math.sqrt(nv)));
		url = VSudoku.class.getResource("/resources/images/vBorderBig.gif");
		border = new ImageIcon(url);
		
		setPreferredSize(new Dimension(10, 16*numY));
	}
	
	public void paintComponent(Graphics g) {
		for (int i = 0; i < numY; ++i) {
			g.drawImage(border.getImage(), 0, 16*i, this);
		}
	}
}
