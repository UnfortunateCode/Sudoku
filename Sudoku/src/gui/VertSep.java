package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;



public class VertSep extends Separator {
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon separator;
	int NumVals, numY;
	
	public VertSep(int numv, int size) {
		NumVals = numv;
		numY = (int)Math.ceil((float)NumVals / Math.ceil(Math.sqrt(NumVals)));
		
		if (size == SMALL_SEPARATOR) {
			url = VSudoku.class.getResource("/resources/images/svSep.gif");
		} else {
			url = VSudoku.class.getResource("/resources/images/lvSep.gif");
		}
		separator = new ImageIcon(url);
		setPreferredSize(new Dimension(5, 16*numY));
	}
	
	public void paintComponent(Graphics g) {
		for (int i = 0; i < numY; ++i) {
			g.drawImage(separator.getImage(), 0, 16*i, this);
		}		
	}
}
