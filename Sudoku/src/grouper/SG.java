package grouper;

import java.util.LinkedList;
import java.util.Random;

public class SG {
	static int NUMG = 0;
	static Random r = new Random();
	ST []b;
	Jigsaw j;
	LinkedList<ST> tl;
	int id, numVals, numFree;

	public SG(Jigsaw jig, int num) {
		j = jig;
		b = j.getBoard();
		id = NUMG++;
		numVals = num;
		numFree = num;
		tl = new LinkedList<ST>();
	}
	
	public void add(ST t) {
		if (numFree > 0 && !tl.contains(t)) {
			tl.add(t);
			--numFree;
			t.addG(this);
		}
	}

	public void rem(ST t) {
		if (tl.contains(t)) {
			tl.remove(t);
			++numFree;
			t.remG();
		}
	}
			
	
	public int numShared(SG g) {
		int count = 0;
		
		for (ST t : g.tl) {
			if (tl.contains(t)) {
				++count;
			}
		}
		return count;
	}
	
	public boolean mergeFrom(SG g) {
		if (g.numVals - g.numFree - numShared(g) <= numFree) {
			for (ST t : g.tl) {
				add(t);
			}
			return true;
		}
		return false;
	}
	
	public boolean expand() {
		if (tl.size() == 0) {
			return false;
		}
		if (numFree == 0) {
			return true;
		}
		LinkedList<ST> avails = new LinkedList<ST>();
		for (ST t : tl) {
			for (ST temp : t.getConnects()) {
				if (!avails.contains(temp)) {
					avails.add(temp);
				}
			}
		}
//System.out.println(id + ": " + tl.size() + "s " + numFree + "f " + avails.size() + "a.");
		if (avails.size() == 0) {
			return false;
		}
		int rChoice = (int)(avails.size() * r.nextFloat());
		int curr = rChoice;
		
		do {
			add(avails.get(curr));
			if (j.checkColors() && expand()) {
				return true;
			}
			rem(avails.get(curr));
			curr = (curr + 1) % avails.size();
		} while (curr != rChoice);
		
		return false;
	}
}
