package sudokuBase;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Solver {
	
	public static int SVAL_ONLYPOSING = 1;
	public static int SVAL_ONLYPOSINT = 1;
	public static int SVAL_SHAREDGROUP = 3;
	public static int SVAL_NINN = 2;
	public static int SVAL_HOOK = 20;
	public static int SVAL_SWORDFISH = 25;
	
	
	public Board b;
	private List<Tile> settable;
	private List<Tile> removable;
	private boolean updated, noSolution;
	public int stepCount;
	
	public int OnlyPosInGUsed;
	public int OnlyPosInTUsed;
	public int GroupsUsed;
	public int SharedGroupUsed;
	public int NInNUsed;
	public int HookUsed;
	public int SwordfishUsed;
	
	
	/**
	 * The only constructor sets up a new solver
	 * with no set board.
	 */
	public Solver() {
		settable = new LinkedList<Tile>();
		removable = new LinkedList<Tile>();
		updated = false;
		noSolution = false;
		stepCount = 0;
		setBoard(null);
		
		OnlyPosInGUsed = 0;
		OnlyPosInTUsed = 0;
		GroupsUsed = 0;
		SharedGroupUsed = 0;
		NInNUsed = 0;
		HookUsed = 0;
		SwordfishUsed = 0;
	}
	
	/**
	 * Solve creates a new board based on
	 * the input, then uses Solve In Place
	 * to find the solution value
	 * 
	 * @param board the board that needs solving
	 * @return the value of the easiest solution, small being easy
	 */
	public int solve(Board board) {
		setBoard(new Board(board));
		return solveInPlace(getBoard());
	}
	
	
	/**
	 * Solve In Place uses the Board argument and
	 * solves it in the easiest way possible. The
	 * function uses the easiest strategy first, 
	 * and if nothing changes uses a higher strategy,
	 * and if there is a change, starts over from
	 * the easiest strategy.  Awarding less points to
	 * easier strategies will create a score that is
	 * based on how hard the strategies used are, as
	 * well as how many steps are taken in the solution.
	 * 
	 * Note that an unsolvable board creates a negative
	 * number; -1 being exactly 1 solution that couldn't
	 * be found with existing strategies, -2 being more
	 * than one solution, and -3 meaning no solution.
	 * 
	 * An already solved board will return 0, as 0 steps
	 * are needed to solve it.
	 * 
	 * @param board the board that needs solving
	 * @return the value of the easiest solution, negative if unsolvable
	 */
	public int solveInPlace(Board board) {
		
		setBoard(board);
		stepCount = 0;
		noSolution = false;
		settable = new LinkedList<Tile>();
		getBoard().resetPos();
		
		OnlyPosInGUsed = 0;
		OnlyPosInTUsed = 0;
		GroupsUsed = 0;
		SharedGroupUsed = 0;
		NInNUsed = 0;
		HookUsed = 0;
		SwordfishUsed = 0;
		
		while (!getBoard().completed() && !noSolution) {
			updated = false;
			settable = new LinkedList<Tile>();
			removable = new LinkedList<Tile>();

			/*
			 * OnlyPosInG looks at a covering group, and
			 * if a number can only appear in one tile of
			 * the group, then that tile must be that 
			 * number.  StepCount is updated based on the
			 * set number, not the clear number.
			 */
			onlyPosInG();
			if (updated) {
				++OnlyPosInGUsed;
			}
			
			/*
			 * OnlyPosInT looks at every tile on the board
			 * and if a tile has only one possible value,
			 * then that value is set.  StepCount is
			 * updated based on the set number, not the
			 * clear number.
			 */
			if (!updated) {
				onlyPosInT();
			
			if (updated) {
				++OnlyPosInTUsed;
			}}
			
			/*
			 * GStrategies are solving strategies unique
			 * to each group type.  Based on what type of
			 * group it is, it will look at its own tiles
			 * and decide if an update is possible.
			 */
			if (!updated) {
				for (Group g : getBoard().gList) {
					// No system for gStrategies to update stepcount yet
					updated |= g.gStrategy();
				}
			
			if (updated) {
				++GroupsUsed;
			}}
			
			/*
			 * SharedGroup determines if a set of tiles
			 * share at least two groups, and that at
			 * least one group the values can only appear
			 * in the intersection.
			 */
			if (!updated) {
				//System.out.println("Entering Shared Group: ");
				//b.display();
				sharedGroup();
			
			if (updated) {
				//System.out.println("Used Shared Group: ");
				//b.display();
				++SharedGroupUsed;
			}}
			
			/*
			 * NInN is a generalization of naked twins or
			 * triplets.  A Naked N-uplet is the same as a
			 * hidden M-uplet if N+M=NUMVALS.  A Naked 
			 * N-uplet is a set of N Tiles whose union of
			 * possibilities has a size of N.  This 
			 * N-uplet covers, and thus all groups that 
			 * share these N Tiles can only have these 
			 * values within the N-uplet.
			 */
			for (int i=2; !updated && i < getBoard().NUMVALS - 1; ++i) {
				//System.out.println("Entering " + i + " in " + i + ":");
				//b.display();
				NInN(i);
			
			if (updated) {
				//System.out.println("Used " + i + " in " + i + ":");
				//b.display();
				++NInNUsed;
			}}

			/*
			 * Hook takes a covering triplet chain, each Tile
			 * only having a different subset of two of the
			 * possibilities, and for each pair of tiles that
			 * the shared number is a hook.  For all pairings
			 * of groups from the distinct tiles, the 
			 * intersection tiles cannot have the hook as a
			 * possibility. 
			 */
			if (!updated) {
				hook();
			
			if (updated) {
				++HookUsed;
			}}
			
			/*
			 * Swordfish is a generalization of the X_Wing
			 * and Swordfish strategies.  If there is an 
			 * even circuit of groups where every other group
			 * contains only 2 possible tiles that can have
			 * the value i, and that all groups are covering,
			 * then for every pair in the circuit, all groups
			 * that share those two tiles can only have the
			 * value i appear within those two tiles.
			 */
			if (!updated) {
				swordfish();
			
			if (updated) {
				++SwordfishUsed;
			}}
			

			/*
			 * Brute force method forces every cell to have
			 * a value from its posList continuing past the
			 * first solution.  If a second solution exists
			 * then stepCount is set to -2, if there is no
			 * solution then -3, and if there is only one 
			 * the return is -1.
			 */
			if (!updated) {
				stepCount = -1;//bruteForce();
				noSolution = true;
			}


			/*
			 * For all settable and removable tiles, process.
			 * These tiles were not set, nor had possibilities
			 * removed during the strategies so as not to cause
			 * concurrent modification, messing up iterators.
			 */
			for (Tile t : settable) {	
				t.set();
			}
			for (Tile t : removable) {
				t.remove();
			}
		}
		
		return stepCount;
	}
	

	/*
	 * OnlyPosInG looks at a covering group, and
	 * if a number can only appear in one tile of
	 * the group, then that tile must be that 
	 * number.  StepCount is updated based on the
	 * set number, not the clear number.
	 */
	public void onlyPosInG() {	
		for (Group g : getBoard().gList) {
			if (g.covers()) {
				for (int i = 0; i < getBoard().NUMVALS; ++i) {
					if (g.posListArr[i].size() == 1) {
						// The number of possibilities for value i is one tile
						if (g.posListArr[i].get(0).prepSet(i)) {
							updated = true;
							settable.add(g.posListArr[i].get(0));
							stepCount += SVAL_ONLYPOSING;
							break;
						}
					}
				}
			}
		}
	}
	

	/*
	 * OnlyPosInT looks at every tile on the board
	 * and if a tile has only one possible value,
	 * then that value is set.  StepCount is
	 * updated based on the set number, not the
	 * clear number.
	 */
	public void onlyPosInT() {
		for (Tile t : getBoard().tList) {
			if (t.magnitude() == 1) {
				// The number of possibilities in t is one value
				if (t.prepSet(t.getFirstPos())) {
					updated = true;
					settable.add(t);
					stepCount += SVAL_ONLYPOSINT;
				}
			}
		}
	}
	

	/*
	 * SharedGroup determines if a set of tiles
	 * share at least two groups, and that at
	 * least one group the values can only appear
	 * in the intersection.
	 */
	public void sharedGroup() {
		for (Group g : getBoard().gList) {
			if (g.covers()) {
				for (int i = 0; i < getBoard().NUMVALS; ++i) {
					if (g.posListArr[i].size() > 0) {
						for (Group tileG : g.posListArr[i].get(0).gList) {
							// tileG is all the groups that contain the first possible
							// tile that has i as a possibility in the group g
							if (tileG != g && tileG.containsAll(g.posListArr[i])) {
								// tileG and g are unique, and the intersection contains
								// all tiles with possibility i within g
								for (Tile t1 : tileG.tList) {
									if (!g.posListArr[i].contains(t1)) {
										// remove i as a possibility from all other tiles
										// in tileG that are not in the intersection
										if (t1.prepRem(i)) {
											updated = true;
											removable.add(t1);
											stepCount += SVAL_SHAREDGROUP;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	

	/*
	 * NInN is a generalization of naked twins or
	 * triplets.  A Naked N-uplet is the same as a
	 * hidden M-uplet if N+M=NUMVALS.  A Naked 
	 * N-uplet is a set of N Tiles whose union of
	 * possibilities has a size of N.  This 
	 * N-uplet covers, and thus all groups that 
	 * share these N Tiles can only have these 
	 * values within the N-uplet.
	 */
	public void NInN(int num) {
		if (num < 2 || num > getBoard().NUMVALS - 1) {
			return;
		}
		Tile []tarr; // The tile list of a group converted to an array
		boolean []posBits = new boolean[getBoard().NUMVALS];
		boolean flag;
		int []ind = new int[num]; // The indicies of where in tarr to look,
                                  // these will cover every combination of
		                          // num tiles of tarr
		int curr; // The value that can be updated, for 2 out of 3
                  // it starts 1,2 curr points at 2. Updates to 
		          // 1,3, 3 being the max value, so curr would then
		          // point to the 1, updating it and lowering all
		          // following resulting in 2,3
		int i, j, count;
	
		for (Group g : getBoard().gList) {
			if (g.covers() && g.tList.size() >= num) {
				tarr = g.tList.toArray(new Tile[0]);
				
				// start with the first num tiles
				for (i = 0; i < num; ++i) {
					ind[i] = i;
				}
				
				curr = num-1;
				while (curr >= 0) { // curr < 0 means all combinations have been tried
					
					// reset the possibilities
					for (i = 0; i < num; ++i) {
						posBits[i] = false;
					}
					
					// sets the indicies to the next combination
					if (setInd(ind, curr, tarr.length)) {
						
						// if any of the combination is set, then the
						// process can be skipped over
						flag = false;
						for (i = 0; i < num; ++i) {
							if (tarr[ind[i]].isSet()) {
								flag = true;
								break;
							}
						}
						if (flag) {
							continue;
						}
						
						// Find all the possibilities in the combined tiles
						count = 0;
						for (i = 0; i < getBoard().NUMVALS; ++i) {
							posBits[i] = false;
						}
						for (i = 0; i < getBoard().NUMVALS; ++i) {
							for (j = 0; j < num; ++j) {				
								if (tarr[ind[j]].hasPos(i)) {
									posBits[i] = true;
									++count;
									break;
								}
							}
						}
						
						// If the number of possibilities equals the number
						// of tiles, then each possibility must exist in 
						// this combination of tiles.
						if (count == num) {

							for (Tile t : g.tList) {
								if (t.isSet()) {
									continue;
								}
								flag = false;
								for (j = 0; !flag && j < num; ++j) {
									flag |= (t == tarr[ind[j]]);
								}
								
								if (!flag) {
									// For every tile in the overlying group
									// if it is not part of the combination
									// then it cannot have any of the possibilities
									// that must exist within the combination
									for (i = 0; i < getBoard().NUMVALS; ++i) {
										if (posBits[i]) {
											if (t.prepRem(i)) {
												updated = true;
												removable.add(t);
												stepCount += SVAL_NINN * num;
											}
										}
									}
								}
							}
						}
					} else { // SetInd has returned false, indicating the last
						     // possible combination has been tried.
						curr=-1;
					}
				}
			}
		}
	}
	
	/* Only to be used by NInN.  Sets the index at the
	 * specified position to the next highest value,
	 * then resets all following positions to the min
	 * values greater than the previous position.
	 * 
	 * If this cannot be done, it attempts to call this
	 * function on the previous position
	 */
	private boolean setInd(int []arr, int p, int maxV) {
		if (p >=  arr.length) {
			return true;
		}
		if (p < 0) {
			return false;
		}
		if (arr[p]+1 < maxV) {
			++arr[p];
			if (resetInd(arr, p+1, maxV)) {
				return true;
			} else {
				return setInd(arr, p-1, maxV);
			}
		}
		return setInd(arr, p-1, maxV);
	}
	
	/* Only to be used by setInd.  Sets the position
	 * specified to the previous position plus one, then
	 * calls itself on the following position 
	 */
	private boolean resetInd(int []arr, int p, int maxV) {
		if (p >= arr.length) {
			return true;
		}
		if (p < 0) {
			return false;
		}
		if (arr[p-1]+1 < maxV) {
			arr[p] = arr[p-1]+1;
			return resetInd(arr, p+1, maxV);
		}
		return false;
	}
	

	/*
	 * Swordfish is a generalization of the X_Wing
	 * and Swordfish strategies.  If there is an 
	 * even circuit of groups where every other group
	 * contains only 2 possible tiles that can have
	 * the value i, and that all groups are covering,
	 * then for every pair in the circuit, all groups
	 * that share those two tiles can only have the
	 * value i appear within those two tiles.
	 * 
	 * The swordfish function just sets up the proper
	 * variables then sends it to the sword function to
	 * do the heavy recursive work.
	 */
	public void swordfish() {
		Tile t, s;
		List<Tile> TL;
		for (int i = 0; i < getBoard().NUMVALS; ++i) {
			for (Group g : getBoard().gList) {
				if (g.covers() && g.posListArr[i].size() == 2) {
					t = g.posListArr[i].get(0);
					s = g.posListArr[i].get(1);
					TL = new LinkedList<Tile> ();
					TL.add(s);
//System.out.println("Calling sword [");
//System.out.println(s + " /");
					if (sword(g, t, s, i, TL)) {
						return;
					}
				}
			}
		}
	}
	
	/*
	 * Sword takes a Group in which there are only two Tiles that
	 * have the possibility of value.  The first of these Tiles is
	 * the tile to spring board off of to try and find another 
	 * "parallel" group that has only two possible tiles of value.
	 * 
	 * If the second tile (that does not share a group with the noUse
	 * tile) shares a group with the lookFor tile, then a circuit is
	 * complete and the necessary removals can be made.  Otherwise
	 * that second tile becomes the new noUse tile and recurses.
	 */
	private boolean sword(Group twoG, Tile noUse, Tile lookFor,
			int value, List<Tile> TL) {
		Tile nNoUse;
		List<Tile> nTL;
		
		for (Group g : noUse.gList) {
			if (g != twoG) {	
				// find a group connected to noUse
				
				for (Tile nNoMatch : g.posListArr[value]) {
					if (!twoG.tList.contains(nNoMatch)) {						
						for (Group nTwoG : nNoMatch.gList) {
							// Find a tile in the group and see if it contains
							// a new group that only has one other tile that has
							// the value possibility.
							
							if (nTwoG.posListArr[value].size() == 2 &&
									nTwoG.covers() && nTwoG != g &&
									!nTwoG.tList.contains(nNoMatch)&&
									!nTwoG.tList.contains(lookFor)) {
								// A new twoG exists
								
								// find the new noUse
								if (nTwoG.posListArr[value].get(0) == nNoMatch) {
									nNoUse = nTwoG.posListArr[value].get(1);
								} else {
									nNoUse = nTwoG.posListArr[value].get(0);
								}
								
								// add the tiles for eventual removal process
								nTL = new LinkedList<Tile> (TL);
								nTL.add(nNoMatch);
								nTL.add(nNoUse);
								
								// if the new noUse is in a group with lookFor
								// the circuit is complete, clean up and get out
								for (Group g2 : nNoUse.gList) {
									if (g2.posListArr[value].contains(lookFor)) {
										nTL.add(lookFor);
										cleanBlade(nTL, value);
										return true;
									}
								}
								
								// the new noUse was not in a group with lookFor
								// so recurse using the new values.
//	System.out.println(nNoMatch);
//	System.out.println(nNoUse + " /");
								if (sword(nTwoG, nNoUse, lookFor, value, nTL)) {
									return true;
								}

							}
							// the new group did not have exactly 2 tiles with
							// value as a possibility
						}
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * Clean Blade is to be called only by sword, and is
	 * used to take a Tile list, and which value is being
	 * cleaned, and make sure that all groups that contain
	 * each sequential pair of tiles only contain those two
	 * tiles for that possible value.
	 * 
	 * Note, each alternate pair was one of the previous twoG's
	 * meaning that for that group those two tiles *were* the 
	 * only ones with value as a possibility, however those two
	 * tiles may also share more groups in common, so must still
	 * be cleaned.
	 */
	private void cleanBlade(List<Tile> TL, int value) {
		Tile t, s;
		Iterator<Tile> it = TL.iterator();
		s = it.next();

		while (it.hasNext()) { 
			t = s;
			s = it.next();
			
			for (Group g : t.gList) {
				if (g.posListArr[value].contains(s)) {
					// Get each group that contains t & s
					
					for (Tile r : g.posListArr[value]) {
						// remove value as a possibility from
						// every other tile in the group.
						if (r != t && r != s) {
							if (r.prepRem(value)) {
								updated = true;
								removable.add(r);
								stepCount += SVAL_SWORDFISH;
							}
						}
					}
				}
			}
		}
	}
	

	/*
	 * Hook takes a covering triplet chain, each Tile
	 * only having a different subset of two of the
	 * possibilities, and for each pair of tiles that
	 * the shared number is a hook.  For all pairings
	 * of groups from the distinct tiles, the 
	 * intersection tiles cannot have the hook as a
	 * possibility. 
	 */
	private void hook() {
		int i, bait = -1, hook = -1;
		boolean flag;
		for (Tile t : getBoard().tList) {
			if (t.magnitude() == 2) {
				// Get a tile with only 2 possibilities
				
				for (Group g : t.gList) {
					for (Tile t2 : g.tList) {
						if (t2.magnitude() == 2 && t.numSharedPos(t2) == 1) {
							// Get a connected tile that shares exactly one
							// of those possibilities, and only has one other
							// possibilities. (see NinN(2) if they share both)
							
							// Find the bait (the third number in this pair 
							// of tiles)
							for (i = 0; i < getBoard().NUMVALS; ++i) {
								if (t2.hasPos(i) && !t.hasPos(i)) {
									bait = i;
								}
							}
							
							// Find the hook (the possibility in the first
							// tile that was not shared in the second, but
							// the third tile needs to only have the bait
							// and the hook as possibilities.
							for (Group g2 : t2.gList) {
								for (Tile t3 : g2.tList) {
									if (t3.magnitude() == 2 &&
											t.numSharedPos(t3) == 1 &&
											t2.numSharedPos(t3) == 1 && 
											t3.hasPos(bait)) {
										for (i = 0; i < getBoard().NUMVALS; ++i) {
											if (i != bait && t3.hasPos(i)) {
													hook = i;
											}
										}
									
									
										// Throw two nets, the first from the first tile
										// the second from the third tile. The intersection
										// of these two groups will be all the tiles that
										// need to have the hook removed.
										flag = false;
										for (Group net1 : t.gList) {
											for (Group net2 : t3.gList) {
												for (Tile fish : net1.posListArr[hook]) {
													if (net2.posListArr[hook].contains(fish) &&
															fish != t && fish != t3) {
														if (fish.prepRem(hook)) {
															flag = true;
															updated = true;
															removable.add(fish);
															stepCount += SVAL_HOOK;
														}
													}
												}
											}
										}
										if (flag) {
											// If there was an update, return
											// and let an easier strategy handle
											// the rest
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	

	/*
	 * Brute force method forces every cell to have
	 * a value from its posList continuing past the
	 * first solution.  If a second solution exists
	 * then stepCount is set to -2, if there is no
	 * solution then -3, and if there is only one 
	 * the return is -1.
	 * 
	 * This function breaks the functionality of 
	 * Tiles by forced sets and unsets, so this is
	 * only an applicable function when no other
	 * strategy works, and a determination on the
	 * type of unsolvability is needed.
	 * 
	 * Returns the type of unsolvability, as well 
	 * as sets the board to a completed state.
	 */
	public void bruteForce() {
//System.out.println("Entering Brute Force");
		int stype = 0, maxP = -1;
		int [] vals = new int[b.tList.size()];
		boolean [] preset = new boolean[b.tList.size()];
		BitSet [] posibs = new BitSet[b.tList.size()];
		int ptr = 0;
		for (int j = 0; j < b.tList.size(); ++j) {
			if (b.tList.get(j).isSet()) {
				preset[j] = true;
				vals[j] = b.tList.get(j).val;
			} else {
				preset[j] = false;
				vals[j] = -1;
			}
		}
/*		for (int j = 0; j < b.NUMVALS; ++j) {
			for (int k = 0; k < b.NUMVALS; ++k) {
				System.out.print(vals[j*b.NUMVALS+k] + " ");
			}
			System.out.println();
		}*/
		while (ptr >= 0) {
			try {
//System.in.read();
			} catch (Exception e) {}
			/*if (ptr > maxP) {
				maxP = ptr;
				System.out.println("\nGot up to: " + maxP);
			} else {
				for (int i = 0; i < ptr; ++i) {
					System.out.print(" ");
				}
				System.out.println(ptr);
			}*/
			if (ptr >= b.tList.size()) {
//System.out.print("EOL: ");
				if (stype == 0) {
					stype = -1;
					--ptr;
//System.out.println("First Solution found");
					for (int i = 0; i < b.tList.size(); ++i) {
						/*if (i % b.NUMVALS == 0) {
							System.out.println();
						}
						System.out.print(vals[i] + " ");*/
						b.tList.get(i).prepSet(vals[i]);
						b.tList.get(i).set();
					}
//b.display();
				} else {
//System.out.println("Second Solution found, breaking");
					stype = -2;
					break;
				}
			} else if (preset[ptr]) {
//System.out.print("PRE: ");
				if (posibs[ptr] == null) {
//System.out.println("Moving Forward");
					posibs[ptr] = new BitSet(b.NUMVALS);
					++ptr;
				} else {
//System.out.println("Moving Backward");
					posibs[ptr] = null;
					--ptr;
				}
			} else if (posibs[ptr] == null) {
//System.out.print("FWD: ");
				posibs[ptr] = new BitSet(b.NUMVALS);
				posibs[ptr].set(0, b.NUMVALS);
				
				for (Group g : b.tList.get(ptr).gList) {
					for (Tile t : g.tList) {
						if (vals[b.tList.indexOf(t)] >= 0) {
							posibs[ptr].clear(vals[b.tList.indexOf(t)]);
						}
					}
				}
				
/*
for (int errInd = 0; errInd < b.NUMVALS; ++errInd) {
	if (posibs[ptr].get(errInd)) {
		System.out.print("1");
	} else {
		System.out.print("0");
	}
}
*/
				vals[ptr] = posibs[ptr].nextSetBit(0);
				if (vals[ptr] < 0) {
//System.out.println(" none set, Moving Backward");				
					posibs[ptr] = null;
					--ptr;
				} else {
//System.out.println(" " + vals[ptr] + " chosen, Moving Forward");
					posibs[ptr].clear(vals[ptr]);
					++ptr;
				}
			} else { //Not preset, posibs already set
/*System.out.print("BKD: ");

for (int errInd = 0; errInd < b.NUMVALS; ++errInd) {
	if (posibs[ptr].get(errInd)) {
		System.out.print("1");
	} else {
		System.out.print("0");
	}
}
*/
				vals[ptr] = posibs[ptr].nextSetBit(0);
				if (vals[ptr] >= 0) {
//System.out.println(" " + vals[ptr] + " chosen, Moving Forward");
					posibs[ptr].clear(vals[ptr]);
					++ptr;
				} else {
//System.out.println(" none set, Moving Backward");
					posibs[ptr] = null;
					--ptr;
				}
			}
			//if (ptr == 0) {
			//	System.out.println("trying" + vals[0] + " at origin");
			//}
		}
		/*
		int stype = 0;
		int ptr = 0;
		int i;
		int []solved = new int[getBoard().tList.size()];
		Tile t;
		
		while (ptr >= 0) {
			
			// Check completion case if all tiles have been filled
			if (ptr == getBoard().tList.size()) {
				if (getBoard().completed() && getBoard().check() && stype == 0) {
					// There is a solution, there may be more so 
					// mark it and continue.
					stype = -1;
					for (i = 0; i < solved.length; ++i) {
						solved[i] = getBoard().tList.get(i).getVal();
					}
					--ptr;
				} else if (getBoard().completed() && getBoard().check() && stype < 0){
					// There is a second solution, return -2
					// to show that there are multiple solutions
					stype = -2;
					ptr = -1;
					break;
				} else {
					--ptr;
				}
			}
			
			
			// End case not found, so get to work.
			
			// get the next tile that isn't naturally set
			t = getBoard().tList.get(ptr);
			while (t.isSet() && !t.forced && ++ptr < getBoard().tList.size()) {
				t = getBoard().tList.get(ptr);
			}
			if (ptr == getBoard().tList.size()) {
				// the last tile was the last open tile, so continue
				// and the end case will be determined.
				continue;
			}
			
			// get all possibilities at this stage
			for (i = 0; i < getBoard().NUMVALS; ++i) {
				t.setPos(i, true);
			}
			for (Group g : t.gList) {
				for (Tile t2 : g.tList) {
					if (t2.isSet()) {
						t.setPos(t2.getVal(), false);
					}
				}
			}
			
			// based on the current value, set this tile to the next
			// possibility if existent and continue with the iteration.
			for (i = 0; (!t.hasPos(i) || i <= t.getVal()) && i < getBoard().NUMVALS; ++i);
			if (i == getBoard().NUMVALS) {
				t.forceUnset();
				--ptr;
				while (ptr >= 0 && !getBoard().tList.get(ptr).forced) {
					--ptr;
				}			
			} else {
				t.forceSet(i);
				++ptr;
			}
			
		}*/
		
		// ptr < 0
		if (stype == 0) {
			// no solution
			stepCount = -3;
			noSolution = true;
		} else {
			stepCount = stype;
		}
//b.display();
//System.out.println("Exiting Brute Force" + stype);
	}

	public boolean check() {
//System.out.println("Entering Check");
		// Setup
		BitSet []match = new BitSet[b.tList.size()];
		int i, j;
		for (i = 0; i < match.length; ++i) {
			match[i] = new BitSet(b.tList.size());
			match[i].set(0, b.tList.size());
		}
		LinkedList<BitSet> bsGList = new LinkedList<BitSet>();
		BitSet bs;
//System.out.println("Setting up bsGList");
		for (Group g : b.gList) {
			bs = new BitSet(b.tList.size());
			for (Tile t : g.tList) {
				bs.set(b.tList.indexOf(t));
			}
			bsGList.add(bs);
		}
/*
for (BitSet bits : bsGList) {
	System.out.println(bits);
}
System.out.println("\nRemoving Groups from Matches");
*/
		for (i = 0; i < match.length; ++i) {
			for (Group g : b.tList.get(i).gList) {
				match[i].andNot(bsGList.get(b.gList.indexOf(g)));
			}
			match[i].set(i);
		}
/*
for (int errInd = 0; errInd < match.length; errInd += 3) {
	for (int errLnd = 0; errLnd < b.NUMVALS; ++errLnd) {
		for (int errJnd = 0; errJnd < 3; ++errJnd) {
			for (int errKnd = 0; errKnd < b.NUMVALS; ++errKnd) {
				if (match[errInd*3+errJnd].get(errLnd*b.NUMVALS+errKnd)) {
					System.out.print("1 ");
				} else {
					System.out.print("0 ");
				}
			}
			System.out.print("\t");
		}
		System.out.println();
	}
	System.out.println();
}
*/
//System.out.println("Entering loop");
		// Logically assign equalities where necessary
		boolean updated = true;
		int card;
		BitSet bsOld1, bsOld2;
		while (updated) {
			updated = false;
//System.out.println("Loop entered: updated now " + updated);
			for (i = 0; i < match.length; ++i) {
//System.out.println("\tMatch[" + i + "] " + match[i]);
				for (BitSet group : bsGList) {
//System.out.println("\t\tGroup " + group);
					if (group.get(i)) {
						continue;
					}
					bs = (BitSet)match[i].clone();
					bs.and(group);
					card = bs.cardinality();
//System.out.println("\t\tLeft " + bs + " (Card: " + card + ")");
					if (card <= 0) {
						// Contradiction, tile cannot match values
						// with any tile in the group
//						System.out.println("Contradiction: T" + i + " G" + bsGList.indexOf(group));
//						b.display();
						return false;
					} else if (card == 1) {
						// Tile at said position must have the same
						// value as the tile at i
						card = bs.nextSetBit(0);
						bs = (BitSet)match[i].clone();
						bs.and(match[card]);
//System.out.println("\t\tCard = 1");
//System.out.println("\t\t\tMatch[" + i + "] " + match[i]);
//System.out.println("\t\t\tMatch[" + card + "] " + match[card]);
//System.out.println("\t\t\tRemains: " + bs);
						if (bs.cardinality() < b.NUMVALS) {
							// Contradiction, no other tile can
							// match values with this set of tiles
							// and this set is less than the number
							// of times a coloring must appear
//							System.out.println("Contradiction: " + bs);
//							b.display();
							return false;
						}
//System.out.println("\t\t\tResetting all");
						bsOld1 = match[i];
						bsOld2 = match[card];
						for (BitSet ibs : match) {
							if (ibs == bsOld1 || ibs == bsOld2) {
								ibs = bs;
							}
						}
						updated = true;
//System.out.println("\t\t\tUpdated now " + updated);
						break;
					}
				}

				if (updated) {
					break;
				}
				
			}
		}
		//System.out.println("No contradiction found");
//		b.display();
		return true;
	}
	
	public LinkedList<Integer> resetPos(BitSet []p, int[]v) {
		LinkedList<Integer> least = new LinkedList<Integer>();
		int leastMin = b.NUMVALS+1;
		for (int i = 0; i < b.tList.size(); ++i) {
			if (v[i] >= 0) {
				continue;
			}
			p[i] = new BitSet(b.NUMVALS);
			p[i].set(0, b.NUMVALS);
			for (Group g : b.tList.get(i).gList) {
				for (Tile t : g.tList) {
					if (v[b.tList.indexOf(t)] >= 0) {
						p[i].clear(v[b.tList.indexOf(t)]);
					}
				}
			}
			if (p[i].cardinality() < leastMin) {
				leastMin = p[i].cardinality();
				least = new LinkedList<Integer> ();
				least.add(i);
			} else if (p[i].cardinality() == leastMin) {
				least.add(i);
			}
		}
		return least;
	}
	
	public int seedForce(Long seed, boolean firstOnly) {
		Random r = new Random(seed);
		boolean cont = false, fast = firstOnly;
		int stype = 0, leastPos = b.NUMVALS+1;
		int ptr;
		int []vals = new int[b.tList.size()];
		boolean []preset = new boolean[b.tList.size()];
		BitSet []posibs = new BitSet[b.tList.size()];
		LinkedList<Integer> least = new LinkedList<Integer>();
		LinkedList<Integer> hist = new LinkedList<Integer>();
		
		for (int i = 0; i < b.tList.size(); ++i) {
			if (b.tList.get(i).isSet()) {
				preset[i] = true;
				vals[i] = b.tList.get(i).val;
			} else {
				preset[i] = false;
				vals[i] = -1;
			}
		}
		
		least = resetPos(posibs, vals);
		if (least.size() == 0) {
//System.out.println("Least size 0");
			return -1;
		} else if (posibs[least.get(0)].cardinality() == 0) {
//System.out.println("posibs["+least.get(0)+"].card == 0");
			return -3;
		} else {
			ptr = least.get((int)(r.nextFloat() * least.size()));
			hist.push(ptr);
		}
		while (hist.size() > 0) {
			ptr = hist.pop();
//System.out.println("Starting vals[" + ptr + "] = " + vals[ptr]);	
			if (vals[ptr] == b.NUMVALS-1) {
				vals[ptr] = -1;
			} else {
				vals[ptr] = posibs[ptr].nextSetBit(vals[ptr]+1);
			}

/*
for (int j = 0; j < hist.size(); ++j) {
	System.out.print(" ");
}
System.out.println(hist.size() + " " + ptr + ": " + posibs[ptr] + " > " + vals[ptr]);
*/			
			
			
			if (vals[ptr] < 0) {
				// no more options, go back
				least = resetPos(posibs, vals);
			} else {
				// had another value, move forward
				least = resetPos(posibs, vals);
				
				if (least.size() == 0) {
					// solution found
					if (cont || fast) {
						// finished, exit out
						for (int i = 0; i < b.tList.size(); ++i) {
							if (vals[i] >= 0) {
								b.tList.get(i).prepSet(vals[i]);
								b.tList.get(i).set();
							}
						}
						return -2;
					} else {
						// first solution found, find next
						cont = true;
						hist.push(ptr);
//System.out.println(ptr + " being pushed back in, vals = " + vals[ptr]);
					}
				} else if (posibs[least.get(0)].cardinality() == 0) {
					// some tiles can't have values, try next value
					// on this tile
					hist.push(ptr);
//System.out.println("no follower to " + ptr + " back in, vals = " + vals[ptr]);
				} else {
					// a tile exists that can still be set, move
					// forward to process it
					hist.push(ptr);
					ptr = least.get((int)(r.nextFloat() * least.size()));
					hist.push(ptr);
				}
			}
		}
		
		if (cont) {
			// one solution was found, was in process of looking
			// for the second which doesn't exist
			return -1;
		} else {
			// never even found the first solution
			return -3;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Program Start");
		
		Board myB = new Board(9);
		
		Group []cols;
		Group []rows;
		Group []spaces;
		
		cols = new Group[9];
		rows = new Group[9];
		spaces = new Group[9];
		
		for (int i = 0; i < 9; ++i) {
			cols[i] = new Group(myB);
			rows[i] = new Group(myB);
			spaces[i] = new Group (myB);
			
			myB.addGroup(cols[i]);
			myB.addGroup(rows[i]);
			myB.addGroup(spaces[i]);
		}
		
		Tile t;
		int x;
		
		for (int i = 0; i < 81; ++i) {
			t = new Tile(myB);
			x = i / 9;
			rows[x].addTile(t);
			x = i % 9;
			cols[x].addTile(t);
			x = 3 * (i / 27) + (i % 9) / 3;
			spaces[x].addTile(t);
			
			myB.addTile(t);
		}
		
		int []arr = {0,0,5, 0,0,9, 4,2,0,
			     0,0,0, 0,0,0, 0,8,1,
			     0,0,8, 0,0,4, 9,0,0,
			     
			     6,3,0, 0,0,1, 0,0,0,
			     0,9,0, 0,0,0, 0,5,0,
			     0,0,0, 7,0,0, 0,3,9,
			     
			     0,0,6, 1,0,0, 5,0,0,
			     5,8,0, 0,0,0, 0,0,0,
			     0,7,9, 3,0,0, 6,0,0};
		
		for (int i = 0; i < 81; ++i) {
			if (arr[i] > 0) {
				t = myB.tList.get(i);
				t.prepSet(arr[i]-1);
				t.set();
			}
		}
		
		System.out.println("Start:");
		myB.display();/*
		Solver s = new Solver();
		s.solve(myB);
		System.out.println("Point Val: " + s.stepCount);
		
		/*quickPosDisp(myB);

		System.out.println();
		for (Group g : myB.gList) {
			System.out.print(g);
			for (Tile r : g.tList) {
				System.out.print(" " + r.num());
			}
			for (int i = 0; i < myB.NUMVALS; ++i) {
				System.out.print("\n" + (i+1) + ": ");
				for (Tile s : g.posListArr[i]) {
					System.out.print(s.num() + " ");
				}
			}
			System.out.println(g.disp());
			
		}*/
		
		
		Solver s = new Solver();
		s.solve(myB);
		
		System.out.println();
		System.out.println("Solved:");
		s.getBoard().display();
		System.out.println("Point value: " + s.stepCount);
		System.out.println("OnlyPosInG: " + s.OnlyPosInGUsed);
		System.out.println("OnlyPosInT: " + s.OnlyPosInTUsed);
		System.out.println("Groups: " + s.GroupsUsed);
		System.out.println("SharedGroups: " + s.SharedGroupUsed);
		System.out.println("NInN: " + s.NInNUsed);
		System.out.println("Hook: " + s.HookUsed);
		System.out.println("Swordfish: " + s.SwordfishUsed);
		/*
		Reducer red = new Reducer();
		Board nb = red.reduce(s.getBoard(), 120, 1.0f, 11, 4);
		System.out.println();
		System.out.println("Reduced:");
		nb.display();
		
		s.solve(nb);
		System.out.println();
		//System.out.println("Solved again:");
		//s.b.display();
		System.out.println("Point value: " + s.stepCount);
		System.out.println("OnlyPosInG: " + s.OnlyPosInGUsed);
		System.out.println("OnlyPosInT: " + s.OnlyPosInTUsed);
		System.out.println("Groups: " + s.GroupsUsed);
		System.out.println("SharedGroups: " + s.SharedGroupUsed);
		System.out.println("NInN: " + s.NInNUsed);
		System.out.println("Hook: " + s.HookUsed);
		System.out.println("Swordfish: " + s.SwordfishUsed);
		*/

	}
	
	// Quickly and messily displays the board in a 9x9 grid
	public static void quickDisplay(Board myB) {
		for (int i = 0; i < 9; ++i) {
			if (i % 3 == 0) {
				System.out.println();
			}
			for (int j = 0; j < 9; ++j) {
				if (j % 3 == 0) {
					System.out.print(" ");
				}
				System.out.print((myB.tList.get(i*9+j).sVal()) + " ");
			}
			System.out.println();
		}
	}
	
	// Quickly shows all the possibilities for each tile
	// including the tile number, the possibilities, and
	// what the tile is currently set to.
	public static void quickPosDisp(Board myB) {
		for (Tile t : myB.tList) {
			System.out.println(t.disp());
		}
	}

	public void setBoard(Board b) {
		this.b = b;
	}

	public Board getBoard() {
		return b;
	}

}
 