package gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * VTile handles all the GUI for a single tile, ie
 * the VTile holds a single value in the final grid.
 * Before the number is chosen, the number of choices
 * is presented.
 * 
 * @author Paul Heleen
 * @version 2011-09-03
 */
public class VTile extends JPanel {
	private static final long serialVersionUID = 1L;
	URL url;
	public final static int LARGE = 0;
	public final static int LARGE_HIGHLIGHT = 1;
	public final static int SMALL = 2;
	public final static int SMALL_HIGHLIGHT = 3;
	public final static int SMALL_DISABLE = 4;
	public final static int SMALL_DISABLE_HIGHLIGHT = 5;
	public final static int NUMOPTS = 6;
	
	ImageIcon imgs[];
	ImageIcon tiles[];
	int NumVals;
	int numX, numY;
	int val;
	boolean []choices;
	private boolean isSet, preSet, isHigh;
	Font f, fBig;
	BoardListener bl;
	
	VTile(int nv, boolean s, int v, Font font, BoardListener boardlistener) {
		f = font;
		bl = boardlistener;
		addMouseListener(bl);
		addMouseMotionListener(bl);
		imgs = new ImageIcon[NUMOPTS];
		NumVals = nv;
		numX = (int)Math.ceil(Math.sqrt(nv));
		numY = (int)Math.ceil((float)nv/(float)numX);
		choices = new boolean[NumVals];
		for (int i = 0; i < choices.length; ++i) {
			choices[i] = true;
		}
		setPreferredSize(new Dimension(16*numX, 16*numY));

		fBig = f.deriveFont((float)(f.getSize()* numY));
		
		if (s) {
			preSet = isSet = true;
			isHigh = false;
			val = v;
			tiles = null;
			url = VSudoku.class.getResource("/resources/images/lPreset"+numX + "" + numY + ".gif");
			imgs[LARGE] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/lPHighlight"+numX + "" + numY + ".gif");
			imgs[LARGE_HIGHLIGHT] = new ImageIcon(url);
		} else {
			preSet = isSet = isHigh = false;
			val = -1;
			tiles = new ImageIcon[numX * numY];
			url = VSudoku.class.getResource("/resources/images/small.gif");
			imgs[SMALL] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/sHighlight.gif");
			imgs[SMALL_HIGHLIGHT] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/sDisable.gif");
			imgs[SMALL_DISABLE] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/sDHighlight.gif");
			imgs[SMALL_DISABLE_HIGHLIGHT] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/large" + numX + "" + numY + ".gif");
			imgs[LARGE] = new ImageIcon(url);
			url = VSudoku.class.getResource("/resources/images/lHighlight" + numX + "" + numY + ".gif");
			imgs[LARGE_HIGHLIGHT] = new ImageIcon(url);
			
		}
//System.err.println(imgs[LARGE].getImageLoadStatus());
//System.err.println(val + " at " + 16*numX/2 + " and " + 16*numY/2);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int tempX, tempY;
		
		if (isSet) {
			if (isHigh) {
				g.drawImage(imgs[LARGE_HIGHLIGHT].getImage(),0,0, this);
			} else {
				g.drawImage(imgs[LARGE].getImage(),0,0,this);
			}
			g.setFont(fBig);
			FontMetrics met = getFontMetrics(g.getFont());
			tempY = numY*14 - (numY*16-g.getFont().getSize())/2;
			tempX = (numX*16-met.stringWidth(""+ (val+1))) / 2;
			g.drawString(""+(val+1), tempX, tempY);
		} else {
			int i, j;
			for (i = 0; i < numY; ++i) {
				for (j = 0; j < numX; ++j) {
					if (i*numX+j < NumVals) {
						if (isHigh) {
							g.drawImage(imgs[SMALL_HIGHLIGHT].getImage(), 16*j, 16*i, this);
						} else {
							g.drawImage(imgs[SMALL].getImage(), 16*j, 16*i, this);
						}
					} else {
						if (isHigh) {
							g.drawImage(imgs[SMALL_DISABLE_HIGHLIGHT].getImage(), 16*j, 16*i, this);
						} else {
							g.drawImage(imgs[SMALL_DISABLE].getImage(), 16*j, 16*i, this);
						}
					}
				}
			}
			// draw numerals
			/*
			 * Graphics Font *should* be set by container
			 * g.setFont(new Font("Serif", Font.BOLD, 12));
			 * ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			 *		RenderingHints.VALUE_ANTIALIAS_ON);
			 */
			g.setFont(f);
			FontMetrics met = getFontMetrics(g.getFont());
			tempY = 14- (16-g.getFont().getSize())/2;
			
			for (i = 0; i < numY; ++i) {
				for (j = 0; j < numX; ++j) {
					if (i*numX+j >= NumVals) {
						break;
					}
					if (choices[i*numX+j]) {

						tempX = (16-met.stringWidth(""+(i*numX+j+1))) / 2;
						g.drawString(""+(i*numX+j+1), tempX+j*16, tempY+i*16);
//System.err.println("Painting " + (i*numX+j) + " at " + (tempX+j*16) + ", " + (tempY+i*16));
					}
					/*
					else {
						System.out.println("Choice fail: " + (i*numX+j));
					}
					*/
				}
			}
		}
	}
	
	public void setHighlight(boolean tf) {
		isHigh = tf;
	}

	public boolean isSet() {
		return isSet;
	}
	
	public void setTo(int i) {
		if (!isSet && choices[i]) {
			setVal(i);
			set(true);
		}
	}
	
	public void unset() {
		if (!preSet) {
			isSet = false;
			val = -1;
		}
	}
	
	public void togglePos(int i) {
		choices[i] = !choices[i];
	}
	
	private void set(boolean tf) {
		isSet = tf;
	}
	
	private void setVal(int i) {
		if (i < 0) {
			val = 0;
		} else if (i >= NumVals) {
			val = NumVals-1;
		} else {
			val = i;
		}
	}
	
	public boolean isPreset() {
		return preSet;
	}
	
	public boolean isHigh() {
		return isHigh;
	}
	
	public void setHigh(boolean tf) {
		isHigh = tf;
	}

	public boolean getChoice(int i) {
		if (isSet) {
			return false;
		}
		return choices[i];
	}
	
	public void setChoice(int i, boolean tf) {
		if (isSet) {
			return;
		}
		if (i < 0 || i >= choices.length) {
			return;
		}
		choices[i] = tf;
	}
	
}
