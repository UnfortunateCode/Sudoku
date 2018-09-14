package gui;

import grouper.Jigsaw;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

import sudokuBase.*;

public class VSudoku extends JApplet{
	
	private static final long serialVersionUID = 1L;
	private static final int SQUARE = 0;
	private static final int JIGSAW = 1;

	Container c;
	MenuPane mp;
	Screen scr;
	int itype, isize;
	
	public void init() {
		
		
		try {
			
			String stype = getParameter("type");
			//System.err.println("Type: " + stype);
			if (stype == null) {
				itype = SQUARE;
			} else if (stype.equals("Jigsaw")){
				itype = JIGSAW;
			} else {
				itype = SQUARE;
			}
			String ssize = getParameter("size");
//System.err.println("Size: " + ssize);			
			try {
				isize = Integer.parseInt(ssize);
			} catch (Exception e) {
				isize = 9;
			}
		} catch (Exception e) {
			System.err.println(e);
			itype = SQUARE;
			isize = 9;
		}
		
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					scr = new Screen(itype, isize);
					scr.setOpaque(true);
					setContentPane(scr);
				}
			});
		} catch (Exception e) {
		}
		//VSudoku vs = new VSudoku(itype, isize);
		setVisible(true);
	}
	
}
