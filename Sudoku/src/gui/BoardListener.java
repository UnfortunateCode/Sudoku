package gui;

import java.awt.event.*;
import java.awt.*;
import sudokuBase.*;

public class BoardListener extends MouseAdapter implements MouseMotionListener {

	int xTile=-1, yTile=-1;
	boolean isDragging = false, invalidDrag = false;
	int dragVal, NumVals, nx, ny;
	Point hTile;
	VBoard vb;
	Board solved;
	boolean isFinished = false;
//	Point srcTile, endTile;
	
	public void setVBoard(VBoard vBoard) {
		vb = vBoard;
		NumVals = vb.NumVals;
		nx = vb.numX;
		ny = vb.numY;
	}
	public void addSolution(Board b) {
		solved = b;
	}
	private Point getTile(int x, int y) {
		return new Point(((x-10)/(5+nx*16)),((y-10)/(5+ny*16)));
	}
	
	private Point getPointFrom(int i) {
		return new Point(i % NumVals, i / NumVals);
	}
	
	private int getTileLocation(Point p) {
		return p.y * NumVals + p.x;
	}
	
	private int getChoice(Point p) {
		int x = ((p.x-10)%(5+nx*16))/(16);
		int y = ((p.y-10)%(5+ny*16))/(16);
//System.err.println(x + ", " + y + " smaller than " + nx + ", " + ny);
		if (y >= ny || x >= nx) {
			return -1;
		}
		return y*nx+x;
	}
	
	public void mouseClicked(MouseEvent me) {
		if (isFinished) {
			return;
		}
		Object src = me.getSource();
		Point p;
		int z;
		if (src instanceof VBoard) {
			p = getTile(me.getX(), me.getY());
			z = getChoice(new Point(me.getX(), me.getY()));
		} else if (src instanceof VTile) {
			p = getPointFrom(vb.vtList.indexOf((VTile)src));
			z = (me.getY()/16)*nx+(me.getX()/16);
		} else {
			return;
		}
		
		if (z < 0 || z > NumVals) {
			return;
		}
		VTile vt = vb.vtList.get(getTileLocation(p));
			
		if (me.getModifiers() == MouseEvent.BUTTON1_MASK) {
			if (vt.isSet() && !vt.isPreset()) {
				vt.unset();
			} else if (!vt.isSet()) {					
				vt.setTo(z);
				
				boolean flag = true;
				for (VTile v : vb.vtList) {
					flag &= (v.val == solved.tList.get(vb.vtList.indexOf(v)).val);
				}
				if (flag) {
					// all values are set properly
					isFinished = true;
					for (VTile v : vb.vtList) {
						v.setHighlight(true);
						v.repaint();
					}
				}
				
			}
		} else if (me.getModifiers() == MouseEvent.BUTTON3_MASK) {
			if (!vt.isSet()) {				
				vt.togglePos(z);
			}
		}
		vt.repaint();
	}
	
	public void mouseReleased(MouseEvent me) {
		if (isFinished) {
			return;
		}
		if (me.getModifiers() != MouseEvent.BUTTON3_MASK || isDragging == false) {
			invalidDrag = false;
			return;
		}
		if (invalidDrag) {
			invalidDrag = false;
			return;
		}
		

		boolean bVal = !vb.vtList.get(vb.tr.getSrc().y*NumVals+vb.tr.getSrc().x).getChoice(dragVal);
		for (int i = vb.tr.getTileY(); i <= vb.tr.getTileMaxY(); ++i) {
			for (int j = vb.tr.getTileX(); j <= vb.tr.getTileMaxX(); ++j) {
				vb.vtList.get(i*NumVals+j).setChoice(dragVal, bVal);
			}
		}
		isDragging = false;
		vb.tr = null;
			
		vb.repaint();
	}
	
	public void mouseMoved(MouseEvent me) {
		if (isFinished) {
			return;
		}
//System.out.println("moved to " + me.getX() + ", " + me.getY());
		Object src = me.getSource();
		Point p;
		if (src instanceof VBoard) {
			p = getTile(me.getX(), me.getY());
		} else if (src instanceof VTile) {
			p = getPointFrom(vb.vtList.indexOf((VTile)src));
		} else {
			return;
		}
		if (p.x < 0 || p.x >= NumVals || p.y < 0 || p.y >= NumVals) {
			return;
		}
		
		if (vb.isHighable() && !p.equals(hTile)) {
			if (hTile != null) {
				vb.unHighlight(hTile);
			}
			hTile = p;
			vb.highlight(hTile);
//System.out.println("Now over: " + p + " - (" + me.getX() + "," + me.getY() + ")");
		}
		vb.repaint();
	}
	
	public void mouseDragged(MouseEvent me) {
		if (isFinished) {
			return;
		}
		if (me.getModifiers() != MouseEvent.BUTTON3_MASK || invalidDrag) {
			return;
		}
		Object src = me.getSource();
		Point p;
		int z;
		if (src instanceof VBoard) {
			p = getTile(me.getX(), me.getY());
			z = getChoice(new Point(me.getX(), me.getY()));
		} else if (src instanceof VTile) {
			p = getPointFrom(vb.vtList.indexOf((VTile)src));
			p = getTile(nx*16*p.x+10+5*(p.x)+me.getX(), 
					ny*16*p.y+10+5*(p.y)+me.getY());
			z = (me.getY()/16)*nx+(me.getX()/16);
		} else {
			return;
		}
		if (!isDragging || vb.tr == null) {
			vb.tr = new TileRect(p, NumVals);
			dragVal = z;
			if (dragVal < 0 || dragVal >= NumVals || 
					vb.vtList.get(getTileLocation(vb.tr.getSrc())).isSet()) {
				vb.tr = null;
				invalidDrag = true;
				isDragging = false;
				return;
			}
			isDragging = true;
			invalidDrag = false;
		} else if (isDragging && invalidDrag) {
			return;
		}
		vb.tr.setEnd(p);
		
		vb.repaint();
	}
}
