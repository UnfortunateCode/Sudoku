package grouper;

import java.util.LinkedList;

public class ST {

	SG g;
	LinkedList<ST> conn;
	int color;

	public ST() {
		g = null;
		conn = new LinkedList<ST>();
		color = -1;
	}

	public void attach(ST t) {
		if (!conn.contains(t)) {
			conn.add(t);
			t.attach(this);
		}
	}
	
	public int getGroupID() {
		if (g == null) {
			return -1;
		}
		return g.id;
	}
	
	public void addG(SG ng) {
		g = ng;
		g.add(this);
	}
	
	public void remG() {
		g = null;
	}
	
	public boolean belongs() {
		return g != null;
	}
	
	public void resetColor() {
		color = -1;
	}
	
	public boolean isBlank() {
		return (color < 0);
	}
	
	public void setColor(int col) {
		if (color == col) {
			return;
		}
		color = col;
		for (ST surr : conn) {
			if (!surr.belongs()) {
				surr.setColor(col);
			}
		}
	}
	
	public int getColor() {
		return color;
	}
	
	public LinkedList<ST> getConnects() {
//System.out.println("conn size: " + conn.size());
		LinkedList<ST> nconn = new LinkedList<ST>();
		for (ST t : conn) {
			if (!t.belongs()) {
				nconn.add(t);
			}
		}
//System.out.println("nconnsize: " + nconn.size());
		return nconn;
	}

	public LinkedList<ST> allConnects() {
		return conn;
	}
	
	public String toString() {
		if (g == null) {
			return "-1";
		}
		return "" + g.id;
	}
}
