package gui;

import javax.swing.*;

import sudokuBase.*;

import java.awt.*;
import java.util.LinkedList;

public class VBoard extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int NumVals, numX, numY;
	Board b;
	Font f;
	BoardListener bl;
	LinkedList<VTile> vtList;
	TileRect tr;
	boolean highlight;
	
	public VBoard(Board board, Font font, BoardListener boardlistener) {
		b = board;
		f = font;
		bl = boardlistener;
		vtList = new LinkedList<VTile>();
		tr = null;
		highlight = false;
		
		NumVals = b.getNumVals();
		numX = (int)Math.ceil(Math.sqrt(NumVals));
		numY = (int)Math.ceil((float)NumVals / (float)numX);
		bl.setVBoard(this);
		Solver s = new Solver();
		s.solve(b);
		bl.addSolution(s.b);
		
		int wx, wy;
		wx = 16 * numX * NumVals + 5 * (NumVals - 1) + 20;
		wy = 16 * numY * NumVals + 5 * (NumVals - 1) + 20;
		
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		fl.setHgap(0);
		fl.setVgap(0);
		
		setLayout(fl);
		setBounds(0,0,wx,wy);
		
		addMouseListener(bl);
		addMouseMotionListener(bl);
		
		int i, j, k;
		Tile t, te, ts, tse;
		VTile vt;
		
		add(new CornerBorderBig(CornerBorderBig.NORTHWEST));

		for (j = 0; j < NumVals; ++j) {
			add (new HorizBorderBig(NumVals));
			if (j < NumVals - 1) {
				add (new HorizBorderSmall());
			}
		}
		add(new CornerBorderBig(CornerBorderBig.NORTHEAST));
		
		for (i = 0; i < NumVals; ++i) {
			add(new VertBorderBig(NumVals));
			for (j = 0; j < NumVals; ++j) {
				t = b.getTile(j, i);
				if (t.isSet()) {
					vt = new VTile(NumVals, true, t.getVal(), f, bl);
					vtList.add(vt);
					add(vt);
				} else {
					vt = new VTile(NumVals, false, -1, f, bl);
					vtList.add(vt);
					add(vt);
				}
				
				if (j < NumVals - 1) {
					if (t.numSharedG(b.getTile(j+1,i)) < 2) {
						add(new VertSep(NumVals, Separator.LARGE_SEPARATOR));
					} else {
						add(new VertSep(NumVals, Separator.SMALL_SEPARATOR));
					}
				}
			}
			add(new VertBorderBig(NumVals));
			
			if (i < NumVals - 1) {
				add(new VertBorderSmall());
				for (j = 0; j < NumVals; ++j) {
					if (b.getTile(j,i).numSharedG(b.getTile(j,i+1)) < 2) {
						add(new HorizSep(NumVals, Separator.LARGE_SEPARATOR));
					} else {
						add(new HorizSep(NumVals, Separator.SMALL_SEPARATOR));
					}
					
					if (j < NumVals - 1) {
						t = b.getTile(j, i);
						te = b.getTile(j+1, i);
						ts = b.getTile(j, i+1);
						tse = b.getTile(j+1, i+1);
						if (t.numSharedG(te) < 2) {
							k = 1;
						} else {
							k = 0;
						}
						if (te.numSharedG(tse) < 2) {
							k = 2*k+1;
						} else {
							k = 2*k;
						}
						if (tse.numSharedG(ts) < 2) {
							k = 2*k+1;
						} else {
							k = 2*k;
						}
						if (ts.numSharedG(t) < 2) {
							k = 2*k+1;
						} else {
							k = 2*k;
						}
						add(new CornerSep(k));
					}
				}
				add(new VertBorderSmall());
			}
			
		}
		
		add(new CornerBorderBig(CornerBorderBig.SOUTHWEST));

		for (j = 0; j < NumVals; ++j) {
			add (new HorizBorderBig(NumVals));
			if (j < NumVals - 1) {
				add (new HorizBorderSmall());
			}
		}
		add(new CornerBorderBig(CornerBorderBig.SOUTHEAST));
//System.err.println(this.getComponentCount());
	}
	
	public void paintChildren(Graphics g) {
		super.paintChildren(g);
//System.err.println("repainting vb");
		if (tr != null && !tr.getSrc().equals(tr.getEnd())) {
			((Graphics2D)g).setStroke(new BasicStroke(5.0F));
			g.setColor(Color.black);
			g.drawRect(tr.getX(),tr.getY(),tr.getW(),tr.getH());
		}
	}


	public boolean isHighable() {
		return highlight;
	}
	
	public void setHighable(boolean tf) {
		highlight = tf;
		if (!highlight) {
			for (VTile vt : vtList) {
				vt.setHighlight(false);
			}
			this.repaint();
		}
	}
	public void highlight(Point p) {
		int i;
		for (Group g : b.getTile(p.x, p.y).getGList()) {
			for (Tile t : g.getTList()) {
				i = b.getTileIndex(t);
				if (i < 0 || i >= vtList.size()) {
					continue;
				}
				vtList.get(i).setHighlight(true);
			}
		}
	}
	
	public void unHighlight(Point p) {
		int i;
		if (b.getTile(p.x, p.y) == null) {
/**/System.err.println("No such tile at " + p);
			return;
		}
		for (Group g : b.getTile(p.x, p.y).getGList()) {
			for (Tile t : g.getTList()) {
				i = b.getTileIndex(t);
				if (i < 0 || i >= vtList.size()) {
/**/System.err.println("i out of bounds: " + i);
					continue;
				}
				vtList.get(i).setHighlight(false);
			}
		}
	}
	
	public void clear() {
		int i;
		for (VTile vt : vtList) {
			for (i = 0; i < NumVals; ++i) {
				if (vt.isSet() && !vt.isPreset()) {
					vt.unset();
				} else if (!vt.isPreset()) {
					vt.choices[i] = true;
				}
			}
		}
		bl.isFinished = false;
		setHighable(false);
		repaint();
	}
}
