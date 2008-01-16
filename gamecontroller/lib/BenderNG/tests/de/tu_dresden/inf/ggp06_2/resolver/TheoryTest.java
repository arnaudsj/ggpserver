package de.tu_dresden.inf.ggp06_2.resolver;

import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.apache.log4j.Logger;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.MovesScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;
import de.tu_dresden.inf.ggp06_2.simulator.Game;
import de.tu_dresden.inf.ggp06_2.gamedb.logic.GameManager;

public class TheoryTest {
    private static final Logger logger  = Logger.getLogger( TheoryTest.class );

    private Theory      theory;
    private TheoryScope theoryScope;
    private TimerFlag   flag;
    private ExpressionList  expressionList;
    private ExpressionList  legalMoves;
    private ExpressionList  legalMovesFlattened;
    
    private GameManager gameManeger1 = new GameManager();
    private Game        game1;
    private Game        game2;
    private TimerFlag   timerFlag = new TimerFlag();

    private static final Atom     aMark   = new Atom( "mark" );

    private static final Atom     aA      = new Atom( "a" );

    private static final Atom     aCell   = new Atom( "cell" );

    private static final Atom     aColour = new Atom( "colour" );

    private static final Atom     aWhite  = new Atom( "white" );

    private static final Atom     aBlack  = new Atom( "black" );

    private static final Atom     aEmpty  = new Atom( "empty" );

    private static final Variable vX      = new Variable( "?x" );

    private static final Variable vY      = new Variable( "?y" );
    
    private static String gameRulesGDL;

    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== TheoryTest ===" );
    }    

    @Before
    public void setUp() {
        gameRulesGDL = "(role a)            " + // UNIVERSALS
                       "(colour white)      " +
                       "(colour black)      " +
                       "(init (cell empty)) " + // TRANSPOSITIONS
                       "";

        ExpressionList gameRules = Parser.parseGDL(gameRulesGDL);
        
        /* RULES */
        gameRules.add( new Implication( Const.aTerm,
                new NotOperator( new Predicate( Const.aTrue, new Predicate(
                        aCell, aEmpty ) ) ) ) );
        gameRules.add( new Implication(  new Predicate( Const.aGoal, aA,
                new Atom( "100" ) ), new Predicate( Const.aTrue, new Predicate(
                aCell, aWhite ) ) ) );
        gameRules.add( new  Implication( new Predicate( Const.aGoal, aA,
                new Atom( "0" ) ), new Predicate( Const.aTrue, new Predicate(
                aCell, aBlack ) ) ) );
        
        ExpressionList nextStateRule = new ExpressionList();
        nextStateRule.add( new Predicate( Const.aDoes, aA, new Predicate( aMark, vX ) ) );
        nextStateRule.add( new Predicate( aColour, vX ) );
        nextStateRule.add( new Predicate( Const.aTrue, new Predicate( aCell, aEmpty ) ) );
        
        gameRules.add( new Implication(new Predicate( Const.aNext, new Predicate( aCell, vX ) ),
                nextStateRule ) );
        
        ExpressionList legalRules = new ExpressionList();
        legalRules.add(new Predicate( Const.aTrue, new Predicate( aCell, aEmpty ) ));
        legalRules.add(new Predicate( aColour, vY ) );
        
        gameRules.add( new Implication( new Predicate( Const.aLegal, aA, new Predicate( aMark, vY ) ),
                legalRules ) );
        
        theory      = new Theory( gameRules );
        theoryScope = new TheoryScope(theory);
        flag        = new TimerFlag();
        logger.info( theory );
        
        expressionList = Parser.parseFile( "./testdata/games/Chess.kif" );
        game1 = gameManeger1.getGameByGDL( expressionList.toString() );
        game2 = gameManeger1.getGameByGDL(game1.getTheory().flattenTheory().getAll().toString());
        try {
            legalMoves = game1.getLegalMoves( game1.getRoleNames().get( 0 ), 
                game1.getInitialNode().getState(), timerFlag );
            legalMovesFlattened = game2.getLegalMoves( game1.getRoleNames().get( 0 ), 
                game1.getInitialNode().getState(), timerFlag );
        }
        catch ( InterruptedException e1 ) {
            logger.error("error during initialization!");
            e1.printStackTrace();
            fail();
            return;
        }
        
    }
    @Test
    public void flattenedReport() {
        assertTrue(legalMovesFlattened.equals( legalMoves ));
    }

    @Test
    public void justReport() {
        ExpressionList gameRules = Parser.parseGDL(gameRulesGDL);
        
        /* RULES */
        gameRules.add( new Implication( Const.aTerm,
                new NotOperator( new Predicate( Const.aTrue, new Predicate(
                        aCell, aEmpty ) ) ) ) );
        gameRules.add( new Implication(  new Predicate( Const.aGoal, aA,
                new Atom( "100" ) ), new Predicate( Const.aTrue, new Predicate(
                aCell, aWhite ) ) ) );
        gameRules.add( new  Implication( new Predicate( Const.aGoal, aA,
                new Atom( "0" ) ), new Predicate( Const.aTrue, new Predicate(
                aCell, aBlack ) ) ) );
        
        ExpressionList nextStateRule = new ExpressionList();
        nextStateRule.add( new Predicate( Const.aDoes, aA, new Predicate( aMark, vX ) ) );
        nextStateRule.add( new Predicate( aColour, vX ) );
        nextStateRule.add( new Predicate( Const.aTrue, new Predicate( aCell, aEmpty ) ) );
        
        gameRules.add( new Implication(new Predicate( Const.aNext, new Predicate( aCell, vX ) ),
                nextStateRule ) );
        
        ExpressionList legalRules = new ExpressionList();
        legalRules.add(new Predicate( Const.aTrue, new Predicate( aCell, aEmpty ) ));
        legalRules.add(new Predicate( aColour, vY ) );
        
        gameRules.add( new Implication( new Predicate( Const.aLegal, aA, new Predicate( aMark, vY ) ),
                legalRules ) );
        
        theory      = new Theory( gameRules );
        theoryScope = new TheoryScope(theory);
        flag        = new TimerFlag();
        
    }

    @Test
    public void noteOnFindS() {
        ExpressionList colours = null;
        try {
            colours = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate( aColour, vX ), 
                    theoryScope, 
                    flag );
        }
        catch ( InterruptedException e ) {}
        
        assertNotNull( colours );
        assertTrue( 0 != colours.size() );
        for ( Expression aResult : colours )
            assertTrue( aResult instanceof Atom );
    }

    @Test
    public void sampleUsageOfFindS() {
        /**
         * First of all let's find out, who is the player.
         */
        ExpressionList players = null;
        try {
            players = ResolutionHelper.resolveAndApply( 
                    vX, 
                    new Predicate( Const.aRole, vX ), 
                    theoryScope,
                    flag);
        }
        catch ( InterruptedException e ) {}
        
        assertNotNull( players );
        assertTrue( 0 != players.size() );
        /*So player is stored in 'player' variable.*/
        Expression player = players.get( 0 );
        
        ExpressionList initialState = null;
        try {
            initialState = ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
        }
        catch ( InterruptedException e1 ) {}
        
        assertNotNull(initialState);
        assertTrue(0 != initialState.size());        
        GameState state0 = new GameState( initialState );
        GameStateScope scope0 = new GameStateScope(theory, state0);
        /* OK. Initial state is there.*/
        
        /**
         * What are the legal moves now?
         */
        ExpressionList moves = null;
        Expression answerTemplate = new Predicate( Const.aDoes, player, vX );
        Expression predicateToResolve = new Predicate( Const.aLegal, player, vX );
        try {
            moves = ResolutionHelper.resolveAndApply( 
                    answerTemplate, predicateToResolve, scope0, flag );
        }
        catch ( InterruptedException e ) {}
        assertNotNull( moves );

        assertTrue( 0 != moves.size() );
        for ( Expression aMove : moves ) {
            assertTrue( aMove instanceof Predicate );
            Predicate thisMove = (Predicate) aMove;
            assertEquals( Const.aDoes, thisMove.firstOperand() );
            assertEquals( player, thisMove.secondOperand() );
        }
    }

    @Test
    public void noteOnMakingStep() {

        ExpressionList initialState = null;
        try {
            initialState = ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
        }
        catch ( InterruptedException e1 ) {}
        
        assertNotNull(initialState);
        assertTrue(0 != initialState.size());
        GameState state0 = new GameState( initialState );
        
        ExpressionList moves = new ExpressionList(
                new Predicate( 
                        Const.aDoes, 
                        aA, new Predicate( 
                                aMark,
                                aWhite ) 
                        ));
        MovesScope scope0 = new MovesScope( theory, state0, moves);
        
        ExpressionList nextState = null;
        try {
            /**
             * There is simple idea behind this statement.
             *  o Find all 'next' statements that hold after move was done
             *  o Turn those statements into 'true' statements
             */
            nextState = ResolutionHelper.resolveAndApply( 
                    Const.pTrue, Const.pNext, scope0, flag );
        }
        catch ( InterruptedException e ) {}

        assertNotNull( nextState );
        assertTrue( 0 != nextState.size() );
        for ( Expression aTrue : nextState ) {
            assertTrue( aTrue instanceof Predicate );
            Predicate currentFluent = (Predicate) aTrue;
            assertEquals( Const.aTrue, currentFluent.firstOperand() );
        }
    }

    @Test
    public void noteOnChangingState() {

        ExpressionList initialState = null;
        try {
            initialState = ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
        }
        catch ( InterruptedException e1 ) {}
        
        assertNotNull(initialState);
        assertTrue(0 != initialState.size());
        GameState state0 = new GameState( initialState );
        GameStateScope scope0 = new GameStateScope( theory, state0 );
        
        Expression answerTemplate = new Predicate( aCell, vX );
        Expression predicateToResolve = new Predicate( Const.aTrue,
                new Predicate( aCell, vX ) );
        
        
        ExpressionList cells = null;
        try {
            cells = ResolutionHelper.resolveAndApply( 
                    answerTemplate, predicateToResolve, scope0, flag );
        }
        catch ( InterruptedException e ) {}
        
        assertNotNull( cells );
        assertTrue( 1 == cells.size() );
        Predicate cell = (Predicate) cells.get( 0 );
        assertEquals( aCell, cell.firstOperand() );
        assertEquals( aEmpty, cell.secondOperand() );
        
        
        Expression whiteCellState = new Predicate( Const.aTrue, new Predicate(
                aCell, aWhite ) );
        ExpressionList nextState = new ExpressionList();
        nextState.add( whiteCellState );
        GameState state1 = new GameState( nextState );
        GameStateScope scope1 = new GameStateScope(theory, state1);
        try {
            cells = ResolutionHelper.resolveAndApply( 
                    answerTemplate, predicateToResolve, scope1, flag );
        }
        catch ( InterruptedException e ) {}
        
        assertNotNull( cells );
        assertTrue( 1 == cells.size() );
        cell = (Predicate) cells.get( 0 );
        assertEquals( aCell, cell.firstOperand() );
        assertEquals( aWhite, cell.secondOperand() );
    }
    
    /**
     * This test method loads a couple of game descriptions and tests if they 
     * were identified correctly.
     *
     */
    @Test
    public void isValid() {
        for (String gameFile : GameManager.gameFiles) {
            logger.info( "Test file " + gameFile);
            assertTrue( new Theory( Parser.parseFile(gameFile) ).isValid() );
        }
        
        Parser.parseFile(GameManager.gameFiles[2]);
        Theory theory = Parser.getFullTheory();
        
        System.err.println(theory);
        System.err.println(theory.symbolsToString());
    
    }
}