package de.tu_dresden.inf.ggp06_2.simulator;

import java.util.HashMap;
import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.AndOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.MovesScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.RuleScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

/**
 * 
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD 
 *
 */
public class GameSimulator {
    
    /* Stores the logger for this class */
    public static Logger logger = Logger.getLogger(GameSimulator.class);

    Theory      theoryObj;
    TheoryScope theoryScope;
    GameState   gameState;
    TimerFlag   flag;
    HashMap     <Expression, Predicate> roleTemplates;

	protected static final ExpressionList varListX = new ExpressionList(
                                                         new Expression[] {Const.vX}
                                                         );

	protected boolean wasInterrupted = false;
	
	

	public GameSimulator(boolean wantDebugPrintouts){
        flag          = new TimerFlag();
        roleTemplates = new HashMap<Expression, Predicate>();
	}

    /**
	 * Is called after an axiom file has been stored in the theory object.
	 * @return a list of all the roles in the game (null if none exist)
	 */
    public ExpressionList getRoles(){
        roleTemplates.clear();
		try {
            
            // retrieve role names
			ExpressionList roleNames = ResolutionHelper.resolveAndApply( 
                    Const.vX, Const.pRoles, theoryScope, flag);

            // create role templates
            for (Expression roleName : roleNames)
                roleTemplates.put( roleName, new Predicate( Const.aGoal, 
                                                            roleName, 
                                                            Const.vX ) );
            
            // return roleNames
            return roleNames;
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public ExpressionList getOtherRoles(Expression player){
		try {
			return ResolutionHelper.resolveAndApply( 
                    Const.vX,
                    new AndOperator(Const.pRoles,new DistinctOperator(Const.vX,player)),
                    theoryScope,
                    flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	public GameState getGameState() {
		return gameState;
	}
	
	public void setGameState(GameState state) {
		gameState = state;
	}
	
	public ExpressionList getLegalMove(Expression player) {
		ExpressionList fill = new ExpressionList();
		fill.add(new Predicate(Const.aDoes, player, Const.vX));
		try {
            Predicate toResolve = new Predicate(Const.aLegal, player, Const.vX);
            RuleScope scope    = new GameStateScope(theoryObj, gameState);

            // resolveAndApplyToMany
            Substitution sigma = toResolve.chainOne( new Substitution(), scope, flag );
            return ResolutionHelper.produceMultipleDerivativeFromOneSubstitution( fill, sigma );

        } catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	// Returns all the legal moves for the given player in the current state
	// Now returns them in the format requested by the "SimulateStep" function 
	public ExpressionList getLegalMoves(Expression player){
        Predicate pDoes = new Predicate(Const.aDoes, player, Const.vX);
        Predicate pLegal = new Predicate(Const.aLegal, player, Const.vX);
		try {
			return ResolutionHelper.resolveAndApply(
                    pDoes, 
                    pLegal,
                    new GameStateScope(theoryObj, gameState),
                    flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return new ExpressionList();
		}
	}
	
    /**
     * This method returns a list of goal values.
     * @param player
     * @return
     */
	public ExpressionList getGoalValues(Expression player){
		try {
			return ResolutionHelper.resolveAndApply( 
                    Const.vX, 
                    new Predicate( Const.aGoal, player, Const.vX ),
                    new GameStateScope(theoryObj, gameState),
                    flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
            return null;
		}
	}
	
	public int getGoalValue(Expression player) {
		Expression e;
		try {
            Expression   template = roleTemplates.get(player);
            Substitution sigma    = new Substitution();
            RuleScope    scope    = new GameStateScope(theoryObj, gameState);
            
            // resolveOneAndApply
            sigma = template.chainOne( sigma, scope, flag );
            e     = ResolutionHelper.produceDerivativeFromOneSubstitution( Const.vX, sigma );
		} catch (InterruptedException ie) {
			wasInterrupted = true;
			e  = null;
		}
		if (e == null) return Const.MIN_GOAL; // not sure what to do in this case
		return Integer.parseInt(e.toString());
	}
	
	public ExpressionList calcNextState(ExpressionList moves){
		try {
			return ResolutionHelper.resolveAndApply(
                    Const.pTrue,
                    Const.pNext,
                    new MovesScope(theoryObj, gameState, moves),
                    flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
	// Calculates the initial state of the game from the axioms
	public ExpressionList calcInitState() {
		try {
			return ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return null;
		}
	}
	
//	 Sets the game into its initial state
	public void simulateStart() {
		//theoryObj.clearState();
		ExpressionList startState = calcInitState();
        gameState = new GameState(startState);
		//theoryObj.setState(startState);
	}

	// Given a list of moves
	// specified with the "does" predicate,
	//    ie, (does white noop), (does black (mark 1 1 O))
	// shifts to the next game step according to the axioms.
	public void simulateStep(ExpressionList moves) {
		ExpressionList newState = calcNextState(moves);
		gameState = new GameState(newState);
	}
	
	/**
	 * This method parses the file specified by the parameter into the theory 
	 * object
	 * @param fileName
	 */
	public void parseFileIntoTheory(String fileName){
		theoryObj   = new Theory(Parser.parseFile(fileName));
		theoryScope = new TheoryScope(theoryObj);
	}
	
	/**
	 * This method parses the description file specified by the parameter into 
	 * the theory object
	 * @param fileName
	 */
	public void parseDescIntoTheory(String gameDescription){
		theoryObj   = new Theory(Parser.parseGDL(gameDescription));
		theoryScope = new TheoryScope(theoryObj);
	}
	
	/**
	 * 
	 * @return whether the current state stored in the theory object is terminal
	 */
	public boolean isTerminal(){
		try {
			return ResolutionHelper.isResolvable(
                    Const.aTerm, 
                    new GameStateScope(theoryObj, gameState),
                    flag);
		} catch (InterruptedException e) {
			wasInterrupted = true;
			return false;
		}
	}
	
	/**
	 * 
	 * @return the wasInterrupted flag
	 */
	public boolean wasInterrupted() {
		return this.wasInterrupted;
	}
	
	
	/**
	 * This method resets the wasInterrupted flag to false.
	 *
	 */
	public void reset() {
		wasInterrupted = false;
	}
	
	
	/**
	 * Calls the interrupt hook of the theory object.
	 *
	 */
	public void interrupt() {
		flag.interrupt();
	}
	
	/**
	 * 
	 * @return the theory object of the game simulator
	 */
	public Theory getTheory() {
		return theoryObj;
	}
	
}

