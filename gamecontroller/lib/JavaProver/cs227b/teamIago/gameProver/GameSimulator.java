/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.gameProver;

import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.AndOp;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.DistinctOp;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Theory;
import cs227b.teamIago.resolver.Variable;
import cs227b.teamIago.util.GameState;

/**
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GameSimulator
{
	Theory theoryObj;

	// Constant values from GameMaster
	public static final int MAX_GOAL = 100;
	public static final int MIN_GOAL = 0;
	
	// 0-ary predicate
	protected static final Atom aTerm = new Atom("TERMINAL");

	// Variable, list containing only that var
	protected static final Variable vX = new Variable("X");
	protected static final ExpList varListX = new ExpList(new Expression[] {vX});

	// Atoms for unary predicates
	protected static final Atom aRole = new Atom("ROLE");
	protected static final Atom aInit = new Atom("INIT");
	protected static final Atom aTrue = new Atom("TRUE");
	protected static final Atom aNext = new Atom("NEXT");
	// Unary predicates (not dependent on Player) get fixed reps
	protected static final Predicate pRoles = new Predicate(aRole,varListX);
	protected static final Predicate pTrue = new Predicate(aTrue,varListX);
	protected static final Predicate pNext = new Predicate(aNext,varListX);
	protected static final Predicate pInit = new Predicate(aInit,varListX);
	protected boolean wasInterrupted = false;
	
	
	// binary predicates (player, expression)
	protected static final Atom aLegal = new Atom("LEGAL"); 
	protected static final Atom aDoes = new Atom("DOES");
	protected static final Atom aGoal = new Atom("GOAL");

	public GameSimulator(boolean wantDebugPrintouts, boolean useOpt)
	{
		// TODO: enable second param once optimization works
		theoryObj = new Theory(wantDebugPrintouts, useOpt);
	}

	// call after an axiom file has been stored in the theory object
	// returns a list of all the roles in the game (null if none exist)
	public ExpList GetRoles()
	{
		try {
			return theoryObj.finds(vX,pRoles);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public ExpList GetOtherRoles(Expression player)
	{
		ExpList roleVar = new ExpList();
		roleVar.add(player);
		roleVar.add(vX);
		try {
			return theoryObj.finds(vX,new AndOp(pRoles,new DistinctOp(vX,player)));
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public GameState GetGameState() {
		return theoryObj.getState();
	}
	
	public void SetGameState(GameState state) {
		theoryObj.setState(state);
	}
	
	// Returns all the legal moves for the given player in the current state
	// Now returns them in the format requested by the "SimulateStep" function 
	public ExpList GetLegalMoves(Expression player){
		ExpList roleVar = new ExpList();
		roleVar.add(player);
		roleVar.add(vX);
		try {
			return theoryObj.finds(new Predicate(aDoes, roleVar), new Predicate(aLegal,roleVar));
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public ExpList GetGoalValues(Expression player){
		ExpList roleVar = new ExpList();
		roleVar.add(player);
		roleVar.add(vX);
		ExpList ret = null;
		try {
			return theoryObj.finds(vX,new Predicate(aGoal,roleVar));
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public int GetGoalValue(Expression player) {
		ExpList roleVar = new ExpList();
		roleVar.add(player);
		roleVar.add(vX);
		Expression e;
		try {
			e = theoryObj.findx(vX,new Predicate(aGoal,roleVar));
		} catch (InterruptedException ie) {
			wasInterrupted = true;
			e  = null;
		}
		if (e == null) return MIN_GOAL; // not sure what to do in this case
		return Integer.parseInt(e.toString());
	}
	
	public ExpList CalcNextState(){
		ExpList ret = null;
		try {
			return theoryObj.finds(pTrue,pNext);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	// Calculates the initial state of the game from the axioms
	public ExpList CalcInitState() {
		ExpList ret = null;
		try {
			return theoryObj.finds(pTrue,pInit);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
//	 Sets the game into its initial state
	public void SimulateStart() {
		theoryObj.clearState();
		ExpList startState = CalcInitState();		
		theoryObj.setState(startState);
	}

	// Given a list of moves
	// specified with the "does" predicate,
	//    ie, (does white noop), (does black (mark 1 1 O))
	// shifts to the next game step according to the axioms.
	public void SimulateStep(ExpList moves) {
		theoryObj.add(moves);
		ExpList newState = CalcNextState();
		theoryObj.setState(newState);
	}
	
	// parses the file specified by the parameter into the theory object
	public void ParseFileIntoTheory(String fileName){
		ExpList expList = Parser.parseFile(fileName);
		theoryObj.add(expList);
		theoryObj.buildVolatile();
	}
	
	public void ParseDescIntoTheory(String gameDescription){
		ExpList expList = Parser.parseDesc(gameDescription);
		theoryObj.add(expList);
		theoryObj.buildVolatile();
	}
	
	// returns whether the current state stored in the theory object is
	// terminal
	public boolean IsTerminal(){
		try {
			return theoryObj.findp(aTerm);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return false;
		}
	}
	
	public boolean wasInterrupted() {
		return this.wasInterrupted;
	}
	
	public void reset() {
		wasInterrupted = false;
	}
	
	public void interrupt() {
		theoryObj.interrupt();
	}
	
	public Theory getTheory() {
		return theoryObj;
	}
	
}
