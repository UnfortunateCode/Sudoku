package sudokuBase;

import java.util.List;
import java.util.LinkedList;

public class Board {

	public List<Tile> tList;
	public List<Group> gList;
	public int NUMVALS;
	
	/**
	 * Constructor creates a board of numValues
	 * Tiles.  
	 * 
	 * @param numValues the number of tiles used
	 */
	public Board (int numValues) {
		NUMVALS = numValues;
		tList = new LinkedList<Tile>();
		gList = new LinkedList<Group>();
	}
	
	/**
	 * Copy constructor, creates a new board based
	 * on another.  All the tiles and groups are 
	 * copied, as well as all interrelations.
	 * 
	 * This copy is completely unique compared to
	 * the old board.
	 * 
	 * @param b2 the board to be copied
	 */
	public Board (Board b2) {
		NUMVALS = b2.NUMVALS;
		tList = new LinkedList<Tile>();
		gList = new LinkedList<Group>();
		
		try {
			for (Tile t : b2.tList) {
				tList.add(new Tile(t, this));
			}
			for (Group g : b2.gList) {
				gList.add(new Group(g, this));
			}
			resetPos();
		} catch(Exception e) {
			// Exception comes from NUMVALS != b2.NUMVALS
			// which has been set to equal.
		}
		
	}
	
	/**
	 * getter for NUMVALS
	 */
	public int getNumVals() {
		return NUMVALS;
	}
	
	/**
	 * getter for tiles
	 */
	public Tile getTile(int x, int y) {
		if (y*NUMVALS+x < 0 || y*NUMVALS+x >= tList.size()) {
			return null;
		}
		return tList.get(y*NUMVALS+x);
	}
	
	/**
	 * index finder for a tile
	 */
	public int getTileIndex(Tile t) {
		return tList.indexOf(t);
	}
	
	/**
	 * ResetPos goes through all the Tiles and Groups
	 * and ensures that all possibilities except those
	 * that are set within the group are available.
	 */
	public void resetPos() {
		int i;
		
		// Set all tile possibilities to true
		for (Tile t : tList) {
			if (t.isSet()) {
				continue;
			}
			for (i = 0; i < NUMVALS; ++i) {
				t.posBits[i] = true;
			}
		}
		
		// Set all group posLists to avail tiles
		for (Group g : gList) {
			for (i = 0; i < NUMVALS; ++i) {
				g.posListArr[i].clear();
				g.posListArr[i].addAll(g.tList);
			}
		}
		
		// Remove possibilities due to a tile being
		// set within the group.
		for (Tile t : tList) {
			if (t.isSet()) {
				t.ready = t.set = false;
				t.posBits[t.val] = true;
				t.prepSet(t.val);
				t.set();
			}
		}
	}
	
	/**
	 * Adds a tile to the board, and ensures 
	 * that the board attached to the tile is
	 * this board.
	 * 
	 * @param t the tile to add
	 */
	public void addTile(Tile t) {
		if (t.b == (this) && !tList.contains(t)) {
			tList.add(t);
		}
	}
	
	/**
	 * Adds a group to the board, and ensures
	 * that the board attached to the group
	 * is this board.
	 * 
	 * @param g the group to add
	 */
	public void addGroup(Group g) {
		if (g.b == (this) && !gList.contains(g)) {
			gList.add(g);
		}
	}
	

	/**
	 * Checks to see if there are any non-set
	 * tiles within this board.
	 * 
	 * @return true if all tiles are set
	 */
	public boolean completed() {
		for (Tile t : tList) {
			if (!t.isSet()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks to make sure that all rules of
	 * the board are being followed.  This in
	 * combination with completed() will show
	 * if a board is solved.
	 * 
	 * @return true if all rules are being followed.
	 */
	public boolean check() {
		for (Tile t : tList) {
			if (t.isSet()) {
				for (Group g : t.gList) {
					for (Tile t2 : g.tList) {
						if (t2.isSet()) {
							if (t2 != t && t2.getVal() == t.getVal()) {
								return false;
							}
						}
					}
					if (!g.gcheck()) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * NumUnset gives a count of the number of unset
	 * tiles in the board
	 */
	public int numUnset() {
		int count = 0;
		for (Tile t : tList) {
			if (!t.isSet()) {
				++count;
			}
 		}
		return count;
	}
	
	/**
	 * NumSet gives a count of the number of set
	 * tiles in the board
	 */
	public int numSet() {
		int count = 0;
		for (Tile t : tList) {
			if (t.isSet()) {
				++count;
			}
		}
		return count;
	}
	
	/**
	 * Attempts to make a good looking general display
	 */
	public void display() {
		int i, j, n;
		for (i = 0; i*NUMVALS < tList.size(); ++i) {
			for (j = 0; j < NUMVALS; ++j) {
				if (j > 0) {

					n = tList.get(i*NUMVALS+j).numSharedG(tList.get(i*NUMVALS + j - 1));
					if (n == 1) {
						System.out.print("|");
					} else if (n == 2) {
						System.out.print(" ");
					} else {
						System.out.print(" ");
					}
				}

				System.out.print(tList.get(i*NUMVALS + j).sVal());
			}
			System.out.println();
			if (i < NUMVALS-1) {
				for (j = 0; j < NUMVALS; ++j) {
					n = tList.get(i*NUMVALS+j).numSharedG(tList.get((i+1)*NUMVALS+j));
					if (n == 1) {
						System.out.print("-");
					} else if (n == 2) {
						System.out.print(" ");
					} else {
						System.out.print(" ");
					}
					if (j != NUMVALS-1) {
						System.out.print("+");
					}
				}
			}
			System.out.println();
		}
	}

}
