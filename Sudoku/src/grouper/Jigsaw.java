package grouper;

import java.util.LinkedList;
import java.util.Random;

public class Jigsaw {
	static final int PUZZLE = 1;
	static final int SQUARE = 2;
	public ST []b;
	int numVals;
	
	public Jigsaw(int n, int t) {
		numVals = n;
		if (t == PUZZLE || t == SQUARE) {
			b = new ST[(int)Math.pow(n,t)];
			
			for (int i = 0; i < numVals; ++i) {
				for (int j = 0; j < numVals; ++j) {
					b[numVals*i+j] = new ST();
					if (i > 0) {
						b[numVals*i+j].attach(b[numVals*(i-1)+j]);
					}
					if (j > 0) {
						b[numVals*i+j].attach(b[numVals*i+j-1]);
					}
				}
			}
		} else {
			Jigsaw j = new Jigsaw(n, SQUARE);
			b = j.b;
			numVals = j.numVals;
		}
	}
	
	public ST[] getBoard() {
		return b;
	}
	
	public void makeGroups() {
		SG g;
		for (int i = 0; i < b.length; ++i) {
			if (!b[i].belongs()) {
				g = new SG(this, numVals);
				g.add(b[i]);
//System.err.println(g.id + ": " + g.tl.size() + "s -> " + g.numFree + " of " + g.numVals + " left.");
				g.expand();
			}
//display();			
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkColors() {
		int i;
		int currColor = 0;
		for (i = 0; i < b.length; ++i) {
			b[i].resetColor();
		}
		for (i = 0; i < b.length; ++i) {
			if (!b[i].belongs() && b[i].isBlank()) {
				b[i].setColor(currColor++);
			}
		}
		if (currColor == 1) {
			return true;
		}
		LinkedList<ST> [] colListArr = new LinkedList [currColor];
		for (i = 0; i < colListArr.length; ++i) {
			colListArr[i] = new LinkedList<ST> ();
		}
		for (i = 0; i < b.length; ++i) {
			if (!b[i].belongs() && !b[i].isBlank() &&
					!colListArr[b[i].getColor()].contains(b[i])) {
				colListArr[b[i].getColor()].add(b[i]);
			}
		}
		int col, nf;
		LinkedList<SG> gl = new LinkedList<SG>();
		for (col = 0; col < currColor; ++col) {
			if (colListArr[col].size() % numVals == 0) {
				continue;
			}
			gl = new LinkedList<SG>();
			for (ST t : colListArr[col]) {
				for ( ST t2 : t.allConnects()) {
					if (t2.belongs() && !gl.contains(t2.g)) {
						gl.add(t2.g);
					}
				}
				nf = 0;
				for (SG g : gl) {
					nf += g.numFree;
				}
				if (colListArr[col].size() % numVals - nf <= 0) {
					break;
				}
			}
			nf = 0;
			for (SG g : gl) {
				nf += g.numFree;
			}
			if (colListArr[col].size() % numVals - nf > 0) {
				return false;
			}
		}
		
		
		return true;
	}
	
	public void display() {
		System.out.println("Jigsaw Display: ");
		int i, j;
		for (i = 0; i < numVals; ++i) {
			for (j = 0; j < numVals; ++j) {
				System.out.print(b[i*numVals+j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SG.r = new Random(2);
		SG.NUMG = 0;
		Jigsaw j;// = new Jigsaw(9, SQUARE);
		j = squared(5);
		//j.makeGroups();
		
		/*j.display();
		
		int i, num = 100;
		long s;
		int[] iarr = new int[81];
		for (i = 0; i < 81; ++i) {
			iarr[i] = 0;
		}
		for (i = 0; i < num; ++i) {
			s = SG.r.nextLong();
			SG.r = new Random(s);
			SG.NUMG = 0;
			j = new Jigsaw(9, SQUARE);
			j.makeGroups();
			++iarr[SG.NUMG];
			//j.display();
			//System.out.println();
		}
		for (i = 0; i < 81; ++i) {
			if (iarr[i] > 0) {
				System.out.println(i + ": " + iarr[i] + " or " + iarr[i] / (float)num + "%");
			}
		}
		*/j.display();
	}
	
	public static Jigsaw jigIsUp(int numV, Long seed) {
		SG.r = new Random (seed);
		SG.NUMG = 0;
		
		Jigsaw j = new Jigsaw(numV, SQUARE);
		j.makeGroups();
		while (SG.NUMG != numV) {
			SG.r = new Random(SG.r.nextLong());
			SG.NUMG = 0;
			j = new Jigsaw(numV, SQUARE);
			j.makeGroups();
		}
		
		return j;
	}
	
	public static int midfactor(int num) {
		double n = Math.floor(Math.sqrt(num));
		for (int i = (int)n; i >= n/2; --i) { // find somewhere between a
			                            // square and 1x4 rectangle
//System.err.println("i = " + i);
			if (Math.floor(num / (double)i) == Math.ceil(num / (double) i)) {

//System.err.println("F: " + Math.floor(num/(double)i));
//System.err.println("C: " + Math.ceil(num/(double)i));
				return i;
			}
		}
		return 1;
	}
	
	public static Jigsaw squared(int numV) {
		SG.NUMG = 0;
		int y = midfactor(numV);
//System.err.println("y = " + y);
		if (y == 1) {
			return jigIsUp(numV, 0L);
		}
		int x = numV / y;
		Jigsaw jig = new Jigsaw(numV, SQUARE);
		SG [] gL = new SG[numV];
		int i;
		for (i = 0; i < numV; ++i) {
			gL[i] = new SG(jig, numV);
		}
		for (i = 0; i < numV*numV; ++i) {
			gL[y*(i/(y*numV))+(i%numV)/x].add(jig.b[i]);
		}
		return jig;
		
	}

}
