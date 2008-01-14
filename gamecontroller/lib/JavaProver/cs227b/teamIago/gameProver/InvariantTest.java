/*
 * Created on May 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.gameProver;

import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.*;
import java.util.ArrayList;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InvariantTest {

	public static void main(String[] args) {
		{
			String theoryFile, role;
			boolean debug = false;
			
			/* System.err.println("Started...");
			
			if ((args.length < 1) || (args.length > 1)) {
				System.err.println("Usage: InvTest datafile");
				System.exit(-1);
			}

			theoryFile = args[0].trim();
			
			String relPath = "data" + System.getProperty("file.separator");
			if (!theoryFile.startsWith(relPath)) theoryFile = relPath + theoryFile;
			*/
			theoryFile = "data/tictactoe.kif";
			// TODO: enable second param once optimization works
			Theory theory = new Theory(debug, false);
			
			ExpList axioms = Parser.parseFile(theoryFile);
			theory.add(axioms);
			
			ExpList board = new ExpList();
			for (int i = 1; i < 4; ++i)
				for (int j = 1; j < 4; ++j) {
					Variable ijVal = theory.generateVar();
					ExpList cellList = new ExpList();
					cellList.add(new Atom(Integer.toString(i)));
					cellList.add(new Atom(Integer.toString(j)));
					cellList.add(ijVal);
					Expression cellij = new Predicate("cell",cellList);
					ExpList trueListij = new ExpList();
					trueListij.add(cellij);
					Expression trueCellij = new Predicate("true",trueListij);
					board.add(trueCellij);
				}
			
			ExpList premises = board;
			
			System.out.println("Giving premise:");
			System.out.println(premises);
			Expression toProve = 
			new Predicate(
				"goal",new ExpList(new Expression[] {
					new Atom("White"), new Atom("100")	
				}));
			
			
			System.out.println("Trying to prove:");
			System.out.println(toProve);
			ArrayList retPremises = null;
			try {
				retPremises = theory.findsConditional(toProve,premises);
			} catch (InterruptedException e) {
				System.out.println(e);
				System.exit(0);
			}
			if (retPremises == null) System.out.println("Unproven.");
			else {
				System.out.println("Proven.");
				System.out.println("Matching premises:");
				for (int i = 0; i < retPremises.size(); ++i) {
					System.out.println(retPremises.get(i));
				}
				System.out.println("Done.");
			}
			
		}
	}
}
