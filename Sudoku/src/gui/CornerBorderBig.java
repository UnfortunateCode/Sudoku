package gui;

import javax.swing.*;
import java.awt.*;
import java.net.*;

public class CornerBorderBig extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int NORTHEAST = 0;
	public final static int SOUTHEAST = 1;
	public final static int SOUTHWEST = 2;
	public final static int NORTHWEST = 3;
	
	ImageIcon corner;
	
	public CornerBorderBig (int type) {
		URL url;
		switch (type) {
		case NORTHEAST:
			url = VSudoku.class.getResource("/resources/images/neCorner.gif");
			corner = new ImageIcon(url);
			break;
		case SOUTHEAST:
			url = VSudoku.class.getResource("/resources/images/seCorner.gif");
			corner = new ImageIcon(url);
			break;
		case SOUTHWEST:
			url = VSudoku.class.getResource("/resources/images/swCorner.gif");
			corner = new ImageIcon(url);
			break;
		case NORTHWEST:
			url = VSudoku.class.getResource("/resources/images/nwCorner.gif");
			corner = new ImageIcon(url);
			break;
		}
		
		//setBackground(Color.blue);
//System.err.println("Initializing a Corner");		
		setPreferredSize(new Dimension(10,10));
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(corner.getImage(),0,0,this);
	}
}
