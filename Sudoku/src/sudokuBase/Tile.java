package sudokuBase;

import java.util.List;
import java.util.LinkedList;

public class Tile {
	public int val;
	boolean ready, set, forced;
	boolean []posBits;
	Board b;
	List<Group> gList;
	
	/**
	 * The basic constructor requires a board to 
	 * connect to.  All possibilities are set available.
	 * 
	 * @param board the board to connect to
	 */
	public Tile(Board board) {
		val = -1;
		ready = set = forced = false;
		b = board;
		posBits = new boolean[b.NUMVALS];
		for (int i = 0; i < b.NUMVALS; ++i) {
			posBits[i] = true;
		}
		gList = new LinkedList<Group>();
	}
	
	/**
	 * Copy constructor.  Creates a duplicate of the
	 * tile t2, that connects to b2 instead of the
	 * board t2 is connected to.
	 * 
	 * @param t2 the tile to copy
	 * @param b2 the new board to connect to
	 * @throws Exception if the new board and old are incompatible.
	 */
	public Tile(Tile t2, Board b2) throws Exception {
		if (b2.NUMVALS != t2.b.NUMVALS) {
			throw new Exception("Missized Boards");
		}
		b = b2;
		val = t2.val;
		ready = t2.ready;
		set = t2.set;
		forced = t2.forced;
		posBits = new boolean[b.NUMVALS];
		for (int i = 0; i < b.NUMVALS; ++i) {
			posBits[i] = t2.posBits[i];
		}
		gList = new LinkedList<Group>();
	}

	/**
	 * Displays the index of the tile
	 */
	public String toString() {
		return ("" + b.tList.indexOf(this));
	}
	
	/**
	 * Displays the value that the tile is set to,
	 * but increases it by one to make it easier
	 * for humans to read. (unset tiles are displayed
	 * as 0)
	 * 
	 * @return the displayable value
	 */
	public String sVal() {
		if (set) {
			return "" + (val+1);
		}
		return "0";
	}
	
	
	/**
	 * Displays the index, the possibilities, and the
	 * set value
	 * 
	 * @return a full info string of the tile
	 */
	public String disp() {
		String s = new String();
		s+=(toString() + ": [ ");
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (posBits[i]) {
				s+=((i+1)+" ");
			} else {
				s+=("0 ");
			}
		}
		s+=("] " + (val+1));
		return s;
	}
	
	
	/**
	 * AddGroup attaches a group to this tile, then
	 * sends this tile to the group to ensure that
	 * the group include this tile.
	 * 
	 * @param g the group to add into
	 */
	public void addGroup(Group g) {
		if (!gList.contains(g)) {
			gList.add(g);
			g.addTile(this);
		}
	}

	
	/**
	 * prepSet prepares the tile for setting purposes.
	 * The safe operations are to set the value and
	 * ready is set true.  These are not updated within
	 * the group until set() is called.
	 * 
	 * @param value the value to set to
	 * @return true if it can be set
	 */
	public boolean prepSet(int value) {
		if (!ready && !set && value >= 0 && 
				value < b.NUMVALS && posBits[value]) {
			val = value;
			ready = true;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Set performs the unsafe operations to set a tile.
	 * This includes removing this tile from all possibility
	 * lists of groups besides the value.  Furthermore, for
	 * all connected tiles, the value needs to be removed from
	 * their possibility lists, and they need to be removed from
	 * their groups' possibility lists for that value.
	 */
	public void set() {
		if (!ready || val < 0 || val > b.NUMVALS) {
			return;
		}
		set = true;
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (i != val) {
				prepRem(i);
				remove();
			}
		}
		
		for (Group g : gList) {
			for (Tile t : g.tList) {
				t.prepRem(val);
				t.remove();
			}
		}
	}
	
	/**
	 * Predicate for the set value
	 * @return the value of set
	 */
	public boolean isSet() {
		return set;
	}
	
	/**
	 * The value of val
	 * @return the value
	 */
	public int getVal() {
		return val;
	}
	
	/**
	 * sets the value in a non-safe way
	 * and sets forced true so that this
	 * can be determined
	 * @param value the value to set
	 */
	public void forceSet(int value) {
		val = value;
		forced = true;
		set = true;
	}
	
	/**
	 * unsets the value in a non-safe way
	 * and sets forced true so that this
	 * can be determined
	 */
	public void forceUnset() {
		set = false;
		forced = true;
		val = -1;
	}
	
	/**
	 * safely unsets the tile, though may
	 * make the board unsolvable. 
	 */
	public void unset() {
		if (set == false) {
			val = -1;
			return;
		}
		set = false;
		ready = false;
		
		int i;
		for (i = 0; i < b.NUMVALS; ++i) {
			posBits[i] = true;
		}
		for (Group g : gList) {
			g.posListArr[val].clear();
			g.posListArr[val].add(this);
			for (Tile t : g.tList) {
				if (t.isSet()) {
					prepRem(t.val);
				} else {
					t.setPos(val, true);
					for (Group gt : t.gList) {
						for (Tile tt : gt.tList) {
							if (tt.isSet() && tt.val == val) {
								t.prepRem(val);
								t.remove();
								break;
							}
						}
						if (!t.hasPos(val)) {
							break;
						}
					}
				}
			}
		}
		val = -1;
		remove();
	}
	
	/**
	 * sets the possibility to either true or false
	 * 
	 * @param i the value to set
	 * @param tf the value to set it to
	 */
	public void setPos(int i, boolean tf) {
		posBits[i] = tf;
		if (tf) {
			for (Group g : gList) {
				if (!g.posListArr[i].contains(this)) {
					g.posListArr[i].add(this);
				}
			}
		} else {
			for (Group g : gList) {
				g.posListArr[i].remove(this);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Magnitude returns the number of 
	 * possibilities this tile can take
	 * 
	 * @return the number of possibilities
	 */
	public int magnitude() {
		int num = 0;
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (posBits[i]) {
				++num;
			}
		}
		return num;
	}

	/**
	 * Gets the first possibility
	 * @return the first possibility
	 */
	public int getFirstPos() {
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (posBits[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * prepRem processes the safe operations
	 * in removing a possibility
	 * 
	 * @param value the value to remove
	 * @return true if it can be removed
	 */
	public boolean prepRem(int value) {
		if (posBits[value]) {
			posBits[value] = false;
			return true;
		}
		return false;
	}
	
	/**
	 * Performs the unsafe operations of removal.
	 * This removes the tile from the groups 
	 * possibility list.
	 */
	public void remove() {
		int i;
		for (Group g : gList) {
			for (i = 0; i < b.NUMVALS; ++i) {
				if (!posBits[i]) {
					g.posListArr[i].remove(this);
				}
			}
		}
	}

	/**
	 * predicate to get the value of a possibility
	 * @param value the value to find
	 * @return true if the value is possibility
	 */
	public boolean hasPos(int value) {
		if (value < 0 || value >= b.NUMVALS) {
			return false;
		}
		return posBits[value];
	}

	/**
	 * numSharedPos finds the number of possibilities
	 * that are shared between this and the given tile
	 * 
	 * @param t the tile to compare
	 * @return the number of shared possibilities
	 */
	public int numSharedPos(Tile t) {
		int num = 0;
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (hasPos(i) && t.hasPos(i)) {
				++num;
			}
		}
		return num;
	}
	
	/**
	 * numSharedG finds the number of groups that
	 * contain both tiles
	 * @param t the tile to compare
	 * @return the number of groups containing both of these
	 */
	public int numSharedG(Tile t) {
		int num = 0;
		for (Group g : gList) {
			if (t.gList.contains(g)) {
				++num;
			}
		}
		return num;
	}
	
	/**
	 * getGList is a method of getting access to the group list
	 * from outside the package.
	 */
	public List<Group> getGList() {
		return gList;
	}
}
