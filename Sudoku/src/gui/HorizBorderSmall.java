package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class HorizBorderSmall extends Separator {

	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon border;
	
	public HorizBorderSmall () {
		url = VSudoku.class.getResource("/resources/images/hBorderSmall.gif");
		border = new ImageIcon(url);
		setPreferredSize(new Dimension(5,10));
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(border.getImage(),0,0,this);
	}
}
