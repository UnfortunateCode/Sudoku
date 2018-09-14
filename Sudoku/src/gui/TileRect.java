package gui;

import java.awt.*;

public class TileRect {
	private Point p, q, nums;
	int NumVals;
	
	public TileRect(Point a, int numV) {
		NumVals = numV;
		p = new Point(a);
		q = new Point(a);
		nums = new Point();
		nums.x = (int)Math.ceil(Math.sqrt(numV));
		nums.y = (int)Math.ceil((float)numV/(float)nums.x);
		setInBounds();
	}
	
	public TileRect(Point a, Point b, int numV) {
		NumVals = numV;
		p = new Point(a);
		q = new Point(b);
		nums = new Point();
		nums.x = (int)Math.ceil(Math.sqrt(numV));
		nums.y = (int)Math.ceil((float)numV/(float)nums.x);
		setInBounds();
	}
	
	public void setInBounds() {
		if (p.x < 0) { p.x = 0; }
		if (p.x >= NumVals) { p.x = NumVals-1; }
		if (p.y < 0) { p.y = 0; }
		if (p.y >= NumVals) { p.y = NumVals-1; }
		

		if (q.x < 0) { q.x = 0; }
		if (q.x >= NumVals) { q.x = NumVals-1; }
		if (q.y < 0) { q.y = 0; }
		if (q.y >= NumVals) { q.y = NumVals-1; }
	}
	
	public void setEnd(Point a) {
		q = new Point(a);
		setInBounds();
	}
	
	public Point getEnd() {
		return q;
	}
	
	public void setSrc(Point a) {
		p = new Point(a);
		setInBounds();
	}
	
	public Point getSrc() {
		return p;
	}
	
	public int getTileX() {
		return Math.min(p.x, q.x);
	}
	
	public int getTileY() {
		return Math.min(p.y, q.y);
	}
	
	public int getTileMaxX() {
		return Math.max(p.x, q.x);
	}
	
	public int getTileMaxY() {
		return Math.max(p.y, q.y);
	}
	
	public int getX() {
		return (nums.x*16*getTileX())+10 + 5*getTileX();
	}
	
	public int getY() {
		return (nums.y*16*getTileY())+10+5*getTileY();
	}
	
	public int getW() {
		return ((nums.x*16*getTileMaxX())+10+nums.x*16-1+5*getTileMaxX()) - getX();
	}
	
	public int getH() {
		return ((nums.y*16*getTileMaxY())+10+nums.y*16-1+5*getTileMaxY()) - getY();
	}
	
}
