package sudokuBase;

import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Reducer {

	Board b;
	Random r;
	Solver s;
	
	@SuppressWarnings("unchecked")
	private class BN implements Comparable {
		Board b;
		float n;
		BN(Board b2, int n2) {
			b = b2;
			n = n2;
		}
		
		boolean equals(BN bn) {
			return n == bn.n;
		}
		
		public int compareTo(Object o) {
			try {
				if (o.getClass() == this.getClass()) {
					if (n > ((BN)o).n) {
						return 1;
					} else if (n < ((BN)o).n) {
						return -1;
					} else {
						return 0;
					}
				}
			} catch (Exception e) {
				return 0;
			}
			return 0;
		}
	}
	LinkedList<BN> bList;
	
	private class Cycle {
		int []corners;
		
		public Cycle(int a, int b, int c, int d) {
			corners = new int [4];
			corners[0] = a;
			corners[1] = b;
			corners[2] = c;
			corners[3] = d;
		}
		
		public boolean equals(Object o) {
			if (!(o instanceof Cycle)) {
				return false;
			}
			Cycle c = (Cycle)o;
			int inCommon = 0;
			for (int i = 0; i < 4; ++i) {
				if (c.corners[i] == corners[0] ||
					c.corners[i] == corners[1] ||
					c.corners[i] == corners[2] ||
					c.corners[i] == corners[3]) {
					++inCommon;
				}
			}
			return inCommon == 4;
		}
		
		public boolean anySet(Board b) {
			return (b.tList.get(corners[0]).isSet() ||
				b.tList.get(corners[1]).isSet() ||
				b.tList.get(corners[2]).isSet() ||
				b.tList.get(corners[3]).isSet());
		}
		
		public String toString() {
			String s = "[" + corners[0] + ", " + corners[1] + ", ";
			s += corners[2] + ", " + corners[3] + "]";
			return s;
		}
	}
	
	private LinkedList<Cycle> findCycles(LinkedList<Cycle> cL, Board s) {
		Tile pA = null, pB = null;
		Cycle c;
		for (Tile a : s.tList) {
			for (Group g : a.gList) {
				for (Tile b : g.tList) {
					if (a != b && a.numSharedG(b) == 2) {
						// candidate side, now must find other 
						// side of cycle
						for (Group gFromA : a.gList) {
							for (Tile pairedA : gFromA.tList) {
								if (pairedA.val == b.val && pairedA.numSharedG(a) == 1) {
									pA = pairedA;
								}
							}
						}
						for (Group gFromB : b.gList) {
							for (Tile pairedB : gFromB.tList) {
								if (pairedB.val == a.val && pairedB.numSharedG(b) == 1) {
									pB = pairedB;
								}
							}
						}
						if (pA == null || pB == null || pA.numSharedG(pB) == 0) {
							// cannot connect to cycle
							break;
						} else if (pA.numSharedG(pB) == 2) {

							// end of cycle found
							c = new Cycle(s.tList.indexOf(a),
									      s.tList.indexOf(b),
									      s.tList.indexOf(pA),
									      s.tList.indexOf(pB));
							if (!cL.contains(c)) {
//System.out.println("Unique Cycle found " + c);
								cL.add(c);
							}
						}
					}
				}
			}
		}
		
		return cL;
	}
	/*
	 * To build a board, all the cycles need to be found, and
	 * the edge cycles should have one of their values set. Also
	 * all but one number needs to be set (if two are unset, they
	 * can be renamed without loss, and thus two solutions).
	 */
	public Board build (Board solved, int diffLimit, long seed) {
		Board b2 = new Board(solved);
		for (Tile t : b2.tList) {
			t.unset();
		}
		b2.resetPos();
//b2.display();
		r = new Random(seed);
		int ind, rev, val;
		s = new Solver();

//System.out.println("Searching Cycles");		
		LinkedList<Cycle> cL = findCycles(new LinkedList<Cycle>(), solved);
		for (Cycle c : cL) {
			if (!c.anySet(b2)) {
				ind = (int)(r.nextFloat() * 4);
				b2.tList.get(c.corners[ind]).prepSet(solved.tList.get(c.corners[ind]).val);
				b2.tList.get(c.corners[ind]).set();
				b2.tList.get(b2.tList.size() - c.corners[ind] - 1).prepSet(
						solved.tList.get(solved.tList.size() - c.corners[ind] - 1).val);
				b2.tList.get(b2.tList.size() - c.corners[ind] - 1).set();
			}
		}
//b2.display();
//System.out.println("Ensuring N-1 Vals");
		// All cycles have been fixed, in a symmetric way, now
		// all but one possible numbers must exist at least once
		BitSet bs = new BitSet(b2.NUMVALS);
		for (Tile t : b2.tList) {
			if (t.isSet()) {
				bs.set(t.val);
			}
		}
//System.out.println("Set so far: " + bs);
		while (bs.cardinality() < b2.NUMVALS - 1) {
			val = bs.nextClearBit(0);
			ind = (int)(r.nextFloat() * b2.NUMVALS);
			for (Tile t : solved.tList) {
				if (t.val == val) {
					if (ind == 0) {
						ind = solved.tList.indexOf(t);
						rev = b2.tList.size() - ind - 1;
						b2.tList.get(ind).prepSet(val);
						b2.tList.get(ind).set();
						bs.set(val);
						b2.tList.get(rev).prepSet(solved.tList.get(rev).val);
						b2.tList.get(rev).set();
						bs.set(b2.tList.get(rev).val);
						break;
					} else {
						--ind;
					}
				}
			}
		}
//b2.display();
		// board restrictions should be placated, and a solution val
		// needs to be achieved
//System.out.println("UnDifficulting");
		val = s.solve(b2);
		while (val < 0 || val > diffLimit) {
//System.out.println("Adding due to val = " + val + " not within [0," + diffLimit + "]");
			s = new Solver();
			s.solve(b2);
			ind = getUnset(s.b, r.nextFloat());
			if (ind < 0) {
				break;
			}
			rev = b2.tList.size() - ind - 1;
			b2.tList.get(ind).prepSet(solved.tList.get(ind).val);
			b2.tList.get(ind).set();
			b2.tList.get(rev).prepSet(solved.tList.get(rev).val);
			b2.tList.get(rev).set();
/*b2.display();
byte[] readb = new byte[10];
try{
//System.in.read(readb);
}catch(Exception e){}*/
			s = new Solver();
			val = s.solve(b2);
		}
		
		return b2;
	}
	
	/*
	 * Reduce takes a completed board (or one that 
	 * is solvable, and with help removes numbers 
	 * from the board that will lead to a puzzle
	 * of the proper difficulty.  diffLimit is the
	 * rating that should not be exceeded.  
	 * diffPercent ranges from 0 and 1.0, which
	 * moderates how easy the puzzle is from each
	 * stage.  An easy puzzle with a high percentage
	 * may have very few steps but uses higher 
	 * strategies.  On the other hand, a hard puzzle
	 * with a low percentage will find many many easy
	 * steps.  diffPrecision is the number of separate
	 * attempts that should be made to find the
	 * percentage to use. Seed is the seed for the 
	 * random numbers.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Board reduce (Board b2, int diffLimit, float diffPercent, int diffPrecision, long seed) {
//System.out.println(b2.numUnset() + " vs " + b2.numSet());
		r = new Random(seed);
		
		s = new Solver();
		int n = s.solve(b2);
		
		if (n < 0 || n > diffLimit) {
			return b2;
		}
		
		bList = new LinkedList<BN>();
		int v, i, index;
		int nS = Math.min(diffPrecision,b2.numSet());
		
		index = get(b2,r.nextFloat());
//System.out.println("Initial Ind: " + index);		
		for (i = 0; i < nS; ++i) {
			b = new Board(b2);
//System.out.print("About to loop :");
			do {
			     index = (index+1)%b2.tList.size();
//System.out.print(" " + index);
			} while (!b.tList.get(index).isSet());
//System.out.println();			
			b.tList.get(index).unset();
			b.tList.get(b.tList.size() - index - 1).unset();
//System.out.print("Ind: " + index);
			v = s.solve(b);
			if (v > 0 && v < diffLimit) {
				bList.add(new BN(b,v));
//System.out.println(" added");
			}
//else { System.out.println();}
		}
		
		// bList now has all the options that will make
		// a solvable board and keep within the limit.
		
		if (bList.isEmpty()) {
			return b2;
		}
		
		// sort these boards from easiest to hardest
		Collections.sort(bList);
		
		// resort these from the closest to the furthest
		// away from the diffPercent.
		for (i = 0; i < bList.size(); ++i) {
			bList.get(i).n = Math.abs(diffPercent - ((float)i/(float)bList.size()));
		}
		Collections.sort(bList);
		
		// Attempt the list in order, if any option comes back
		// short of the minimum, move to the next.
		// If the Limit is too large, the entire search space
		// will be explored, and the hardest puzzle found will
		// be returned. (Note, a beam search of diffPrecision is
		// being used here.)
		int maxSoFar = n;
		Board upperB = null;
		for (i = 0; i < bList.size(); ++i) {
			b = reduce(bList.get(i).b, diffLimit, diffPercent, diffPrecision, r.nextLong());
			v = s.solve(b);
			if (v > 0.75*diffLimit) {
				return b;
			}
			if (v > maxSoFar) {
				maxSoFar = v;
				upperB = b;
			}
		}
		if (maxSoFar >= 0) {
			return upperB;
		}
		return b2;
	}
	
	/**
	 * Gets a equally distributed random tile that is
	 * currently set.
	 * 
	 * @param b the board to pick from
	 * @param r the random percent
	 * @return an integer of the position of the tile
	 */
	public int get(Board nb, float nr) {
//System.out.println("R=" + nr);		
		
		// The list of set tiles must be obtained, otherwise
		// advancing to the next set tile would result in
		// set tiles right after large patches of unset tiles
		// to have a higher chance of being chosen.
		LinkedList<Tile> tL = new LinkedList<Tile> ();
		for (Tile t : nb.tList) {
			if (t.isSet()) {
				tL.add(t);
			}
		}
		return nb.tList.indexOf(tL.get((int)(nr*tL.size())));
	}
	
	public int getUnset(Board nb, float nr) {
		//System.out.println("R=" + nr);		
				
				// The list of set tiles must be obtained, otherwise
				// advancing to the next set tile would result in
				// set tiles right after large patches of unset tiles
				// to have a higher chance of being chosen.
				LinkedList<Tile> tL = new LinkedList<Tile> ();
				for (Tile t : nb.tList) {
					if (!t.isSet()) {
						tL.add(t);
					}
				}
				if (tL.size() == 0) {
					return -1;
				}
				return nb.tList.indexOf(tL.get((int)(nr*tL.size())));
			}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Solver.main(null);

	}

}
