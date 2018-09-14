package gui;

import grouper.Jigsaw;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JPanel;

import sudokuBase.Board;
import sudokuBase.Group;
import sudokuBase.Reducer;
import sudokuBase.Solver;
import sudokuBase.Tile;

public class Screen extends JPanel {
	private static final int SQUARE = 0;
	private static final int JIGSAW = 1;
	Container c;
	MenuPane mp;
	
	public Screen(int itype, int isize) {
		Font f = new Font("Serif", Font.BOLD, 12);
		BoardListener bl = new BoardListener();

		Board b = getBoard(itype, isize);
		Solver s = new Solver();
		//s.solve(b);
		//bl.addSolution(s.getBoard());
		VBoard vb = new VBoard(b, f, bl);
		
		c = new Container();
		c.setBounds(vb.getBounds());
		c.setPreferredSize(vb.getPreferredSize());
		c.add(vb);
		
		MenuListener ml = new MenuListener();
		mp = new MenuPane(vb, ml);
		mp.setBounds(0,0,vb.getWidth(),30);
		mp.setPreferredSize(new Dimension(vb.getWidth(), 30));
		mp.setVisible(true);

		setLayout(new BorderLayout());
		add(c, BorderLayout.CENTER);
		add(mp, BorderLayout.SOUTH);
		
		setSize(100+(vb.numX * 16 + 5) * b.NUMVALS+35,
				mp.getPreferredSize().height+c.getPreferredSize().height);
		/*System.out.println((100+(vb.numX * 16 + 5) * b.NUMVALS+35) + ", " + 
				(mp.getPreferredSize().height+c.getPreferredSize().height));
		System.out.println(vb.getWidth() + ", " + vb.getHeight());*/
		setVisible(true);
	}
	
	public Board getBoard(int t, int s) {
		//TODO
		/*
		 * Get today's puzzle from server,
		 * and return the board received.
		 * Failing that, get today's date
		 * from server. Use today's date to
		 * create a new puzzle.
		 */
		return tempBoard(t, s);
	}
	
	private Board permute(Board b, long seed) {
		Board pB = new Board(b);
		Random r = new Random(seed);
		LinkedList<Integer> avails = new LinkedList<Integer>();
		int i;
		for (i = 0; i < pB.getNumVals(); ++i) {
			avails.add(new Integer(i));
		}
		int []perm = new int[pB.getNumVals()];
		for (i = 0; i < perm.length; ++i) {
			perm[i] = avails.remove((int)(r.nextFloat()*avails.size()));
		}
		for (Tile pT : pB.tList) {
			if (pT.getVal() >= 0) {
				pT.val = perm[pT.val];
			}
		}
		pB.resetPos();
		return pB;
	}
	
	private Board tempBoard(int type, int size) {
//System.err.println("Type " + type + " and Size " + size);
		Board myB;
		Board mySolvedB;
		Board empty;
		Solver s;
		int i;

		Tile t;
		int x;
		Long seed = (System.currentTimeMillis() / 86400000);
		
		Jigsaw jig;
		
		Group []cols;
		Group []rows;
		Group []spaces;
	do {	
/**/System.out.println("Start:");
		myB = new Board(size);
		cols = new Group[myB.NUMVALS];
		rows = new Group[myB.NUMVALS];
		spaces = new Group[myB.NUMVALS];
		
		for ( i = 0; i < myB.NUMVALS; ++i) {
			cols[i] = new Group(myB);
			rows[i] = new Group(myB);
			spaces[i] = new Group (myB);
			
			myB.addGroup(cols[i]);
			myB.addGroup(rows[i]);
			myB.addGroup(spaces[i]);
		}
		
		if (type == JIGSAW) {
			jig = Jigsaw.jigIsUp(myB.NUMVALS, seed);
		} else {
			jig = Jigsaw.squared(myB.NUMVALS);
		}
//jig.display();
		
		
		
		for ( i = 0; i < myB.NUMVALS * myB.NUMVALS; ++i) {
			t = new Tile(myB);
			x = i / myB.NUMVALS;
			rows[x].addTile(t);
			x = i % myB.NUMVALS;
			cols[x].addTile(t);
			x = jig.b[i].getGroupID();
			/*x = 3 * (i / 27) + (i % 9) / 3;*/
			spaces[x].addTile(t);
			
			myB.addTile(t);
		}
		empty = new Board(myB);
		i = 0;
		for (Tile myT : myB.gList.get((int)((new Random(seed)).nextFloat() * myB.gList.size())).tList) {
			myT.prepSet(i++);
			myT.set();
		}
//		myB.display();
		mySolvedB = new Board(myB);
//		mySolvedB.display();
		s = new Solver();
		s.b = mySolvedB;
/**/System.out.println("About to check") ;
		if (s.check()) {
/**/System.out.println("Check good, brute forcing");
			s.stepCount = s.seedForce(seed, true);
		} else {
/**/System.out.println("Check bad, redoing");
			s.stepCount = -3;
		}
		
		mySolvedB = s.getBoard();
//mySolvedB.display();
//System.out.println("SC: " + s.stepCount);
		seed = (new Random(seed)).nextLong();
	} while (s.stepCount < -2);

		mySolvedB = permute(mySolvedB, seed);
//mySolvedB.display();
/**/System.out.println("Reducing");
		Reducer red = new Reducer();
		Board reduced = red.build(mySolvedB, 10000, seed);
/**/System.out.println("Reduced");		
		//myB.display();
		/*
		s = new Solver();
		s.solve(reduced);

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
		System.out.println("Swordfish: " + s.SwordfishUsed);*/
		return reduced;
	}

}
