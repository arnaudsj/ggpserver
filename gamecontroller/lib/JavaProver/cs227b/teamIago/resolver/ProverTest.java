/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;

import cs227b.teamIago.parser.Parser;

/**
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProverTest
{
	public ProverTest(){}

	// this main reads in an axiom file, a state file, and a moves file, and
	// prints out output according to the format specified in assignment 3
	public static void main(String args[])
	{
		try {
		int maxIndex;
		boolean wantDebugPrintouts = false;
		if (args.length % 3 == 1) wantDebugPrintouts = true;
		if (args.length >= 3) maxIndex = args.length / 3;
		else maxIndex = 4;
		
		for (int testfile = 1; testfile <=  maxIndex ; testfile++)
		{
		System.out.println("**************** Game " + testfile + " *****************");
		String axiomFile = "data/axioms" + testfile + ".kif";
		String stateFile = "data/state" + testfile + ".kif";
		String movesFile = "data/moves" + testfile + ".kif";

		if (args.length > 3*(testfile-1) + 2)
		{
			int offset;
			if (wantDebugPrintouts) offset = 1;
			else offset = 0;
			axiomFile = args[3*(testfile-1) + 0 + offset];
			stateFile = args[3*(testfile-1) + 1 + offset];
			movesFile = args[3*(testfile-1) + 2 + offset];
		}

		ExpList axiomList = Parser.parseFile(axiomFile);
		ExpList stateList = Parser.parseFile(stateFile);
		ExpList movesList = Parser.parseFile(movesFile);

		// TODO: enable second param once optimization works
		// ...or rather, once it actually improves speed.  d'oh.
		Theory theoryObj = new Theory(wantDebugPrintouts, false);

		for (int i = 0; i < axiomList.size(); i++)
		{
			theoryObj.add(axiomList.get(i));
		}
		for (int i = 0; i < stateList.size(); i++)
		{
			theoryObj.add(stateList.get(i));
		}
		for (int i = 0; i < movesList.size(); i++)
		{
			theoryObj.add(movesList.get(i));
		}
		
		if (wantDebugPrintouts) 
			System.err.println("Current value of topVar: " + theoryObj.getTopVar());
		
		// Atoms
		
		Atom legal = new Atom("LEGAL");
		Atom role = new Atom("Role");
		Atom goal = new Atom("GOAL");
		Atom terminal = new Atom("Terminal");
		Atom next = new Atom("next");

		// Variables
		
		Variable v1 = theoryObj.generateVar();
		Variable v2 = theoryObj.generateVar();
		Variable v3 = theoryObj.generateVar();
		
		// Lists of Variables
		ExpList v1only = new ExpList();
		v1only.add(v1);
		
		// Collections of answer expressions
		ExpList roles;
		ExpList legalMoves;
		ExpList goalValues;
		ExpList nextState;
		
		boolean isTerminal;


		// Get all the available roles
		 roles = theoryObj.finds(v1,new Predicate(role,v1only));
	
		if (roles == null) {
				System.out.println("No playable roles found: aborting.");
			System.exit(-1);
		}
		
		int i;
		for (i = 0; i < roles.size(); i++)
		{
			
			Expression player = roles.get(i);
			ExpList roleV2 = new ExpList();
			roleV2.add(player);
			roleV2.add(v2);
			System.out.println("Player " + player.toString());
			legalMoves =  theoryObj.finds(v2,new Predicate(legal,roleV2));
			if (legalMoves == null)
				System.out.println("No legal moves found.");
			else
				System.out.println("Legal moves: " + legalMoves.toString());
		        
			goalValues = theoryObj.finds(v2,new Predicate(goal,roleV2));
			if (goalValues == null) 
				System.out.println("No goal values found.");
			else
				System.out.println("Goal values: " +
				goalValues.toString()); 
		}
	     
		isTerminal = theoryObj.findp(terminal);
		if (isTerminal) {
			System.out.println("Game is in terminal state.");
			System.out.println("(Not calculating next state.)");
		}
		else 
		{
			System.out.println("Game is in non-terminal state.");
			nextState = theoryObj.finds(v1,new Predicate(next,v1only));
			if (nextState == null) 
				System.out.println("Could not calculate next state.");
			else System.out.println("Next state: " + nextState);
		}
		
	       
		System.out.println("************* End of Game " + testfile + " *************");
		}
		} catch (InterruptedException e) {
			System.out.println("how did we get an interruption here? " + e);
		}
	}

}
