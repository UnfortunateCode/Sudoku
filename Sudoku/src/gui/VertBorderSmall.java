package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class VertBorderSmall extends Separator {
	private static final long serialVersionUID = 1L;
	URL url;
	ImageIcon border;
	
	public VertBorderSmall () {
		url = VSudoku.class.getResource("/resources/images/vBorderSmall.gif");
		border = new ImageIcon(url);
		setPreferredSize(new Dimension(10,5));
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(border.getImage(),0,0,this);
	}
}
