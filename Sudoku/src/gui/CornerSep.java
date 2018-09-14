package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class CornerSep extends Separator {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon corner;
	
	public CornerSep(int a, int b, int c, int d) {
		int i = 0;
		if (a == SMALL_SEPARATOR) {
			i = 0;
		} else {
			i = 1;
		}
		if (b == SMALL_SEPARATOR) {
			i *= 2;
		} else {
			i = 2*i+1;
		}
		if (c == SMALL_SEPARATOR) {
			i *= 2;
		} else {
			i = 2*i+1;
		}
		if (d == SMALL_SEPARATOR) {
			i *= 2;
		} else {
			i = 2*i+1;
		}
		url = VSudoku.class.getResource("/resources/images/" + i + ".gif");
		corner = new ImageIcon(url);
		
		setPreferredSize(new Dimension(5,5));
	}
	
	public CornerSep(int i) {
		if (i > 15) {
			i = 15;
		}
		if (i < 0) {
			i = 0;
		}
		url = VSudoku.class.getResource("/resources/images/" + i + ".gif");
		corner = new ImageIcon(url);
		
		setPreferredSize(new Dimension(5,5));
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(corner.getImage(),0,0,this);
	}
}