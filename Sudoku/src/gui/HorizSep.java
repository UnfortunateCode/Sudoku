package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;



public class HorizSep extends Separator {
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon separator;
	int NumVals, numX;
	
	public HorizSep(int numv, int size) {
		NumVals = numv;
		numX = (int)Math.ceil(Math.sqrt(NumVals));
		
		if (size == SMALL_SEPARATOR) {
			url = VSudoku.class.getResource("/resources/images/shSep.gif");
		} else {
			url = VSudoku.class.getResource("/resources/images/lhSep.gif");
		}
		separator = new ImageIcon(url);
		setPreferredSize(new Dimension(16*numX, 5));
//System.err.println(separator.getImageLoadStatus());
	}
	
	public void paintComponent(Graphics g) {
		for (int i = 0; i < numX; ++i) {
			g.drawImage(separator.getImage(), 16*i, 0, this);
		}		
	}
}
