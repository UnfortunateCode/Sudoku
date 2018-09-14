package sudokuBase;

import java.util.List;
import java.util.LinkedList;

public class Group {
	public List<Tile> tList;
	List<Tile> []posListArr;
	Board b;
	
	
	/**
	 * Constructor takes in a board to attach, and
	 * sets up the group
	 * 
	 * @param board the board to attach
	 */
	@SuppressWarnings("unchecked")
	public Group(Board board) {
		b = board;
		tList = new LinkedList<Tile>();
		posListArr = new List[b.NUMVALS];
		for (int i = 0; i < b.NUMVALS; ++i) {
			posListArr[i] = new LinkedList<Tile>();
		}
	}
	
	/**
	 * Copy constructor makes a full copy of another
	 * group, and attaches the board argument to this
	 * group.  It then attaches tiles from the argument
	 * board to match the tiles from the other board that
	 * the argument group is attached to.
	 * 
	 * @param g2 the group to copy
	 * @param b2 the board to attach
	 * @throws Exception if the board types are different
	 * @throws Exception if the boards tiles are not initialized
	 */
	@SuppressWarnings("unchecked")
	public Group(Group g2, Board b2) throws Exception {
		if (b2.NUMVALS != g2.b.NUMVALS) {
			throw new Exception ("Missized Boards");
		}
		if (b2.tList.size() != g2.b.tList.size()) {
			throw new Exception ("Tiles uninitiated");
		}
		b = b2;
		tList = new LinkedList<Tile>();
		posListArr = new List[b.NUMVALS];
		for (int i = 0; i < b.NUMVALS; ++i) {
			posListArr[i] = new LinkedList<Tile>();
		}
		for (Tile t : g2.tList) {
			addTile(b2.tList.get(t.b.tList.indexOf(t)));
		}
	}
	
	/**
	 * Quick display to separate groups from each other
	 */
	public String toString() {
		return "G" + b.gList.indexOf(this);
	}
	
	/**
	 * Displays the board with the attached tiles
	 * 
	 * @return the string displayed
	 */
	public String disp() {
		String s = toString() + ": [ ";
		for (Tile t : tList) {
			s += t + " ";
		}
		s += "]\n";
		int i;
		for (i = 0; i < b.NUMVALS; ++i) {
			s += (i+1) + ": ";
			for (Tile t : posListArr[i]) {
				s += (t + " ");
			}
			s+=("\n");
		}
		return s;
	}
	
	/**
	 * Adds a tile to the group, and then attempts
	 * to add this group to the tile.
	 * 
	 * @param t the tile to add
	 */
	public void addTile(Tile t) {
		if (!tList.contains(t)) {
			tList.add(t);
			for (int i = 0; i < b.NUMVALS; ++i) {
				if (t.hasPos(i)) {
					posListArr[i].add(t);
				}
			}
			t.addGroup(this);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Predicate to determine whether the group
	 * covers. Meaning that every possibility in
	 * the group MUST be in the group, or rather
	 * the number of possibilities matches the 
	 * number of open spaces in the group
	 * 
	 * @return true if the group covers
	 */
	public boolean covers() {
		int num = 0;
		for (int i = 0; i < b.NUMVALS; ++i) {
			if (posListArr[i].size() > 0) {
				++num;
			}
		}
		return (num == numUnset());
	}
	
	/**
	 * Determines the number of unset tiles
	 * 
	 * @return the number of unset tiles
	 */
	public int numUnset() {
		int num = 0;
		for (Tile t : tList) {
			if (!t.set) {
				++num;
			}
		}
		return num;
	}

	/**
	 * Checks if the group contains all the tiles
	 * from the list
	 * 
	 * @param list the tiles to check
	 * @return true if all the tiles are in the group
	 */
	public boolean containsAll(List<Tile> list) {
		for (Tile t : list) {
			if (!tList.contains(t)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Predicate for specific strategies unique to
	 * a the group.  For example, a sums group must
	 * have all tiles add up to the sum, so the strategy
	 * must make sure that any possibility in the group
	 * must be able to match other possibilities to make
	 * the sum.
	 * 
	 * @return true if there was a change
	 */
	public boolean gStrategy() {
		return false;
	}
	
	/**
	 * Predicate for groups to make sure any of their
	 * unique rules are not being followed.  Multiple
	 * tiles being the same is a generic rule that does
	 * not need to be checked.
	 * 
	 * @return true if all unique rules are being followed
	 */
	public boolean gcheck() {
		return true;
	}
	
	/**
	 * getTList is a method for classes outside the package
	 * to get access to the tList
	 */
	public List<Tile> getTList() {
		return tList;
	}
}
