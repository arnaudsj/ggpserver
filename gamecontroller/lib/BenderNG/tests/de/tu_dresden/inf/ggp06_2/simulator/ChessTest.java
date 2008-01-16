package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolutionHelper;
import de.tu_dresden.inf.ggp06_2.resolver.scope.GameStateScope;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.GameState;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;
import de.tu_dresden.inf.ggp06_2.simulator.flags.TimerFlag;

public class ChessTest {

    private Theory theory;
    private TheoryScope theoryScope;
    private TimerFlag flag;
    private static final Logger logger = Logger.getLogger( ChessTest.class );

    @Before
    public void setUp(){
        
        ExpressionList expList = Parser.parseFile("./testdata/games/Chess.kif");
        theory = new Theory( expList );
        theoryScope = new TheoryScope(theory);
        flag = new TimerFlag();
    }
    
    @Test
    public void legalMoves(){
        /*----------- initializing the game -----------*/
        ExpressionList initials = null;
        try {
            initials = ResolutionHelper.resolveAndApply(
                    Const.pTrue, Const.pInit, theoryScope, flag);
        }
        catch ( InterruptedException e1 ) {
            logger.error( "interrupted !" );
        }
        
        assertNotNull(initials);
        assertTrue(0 < initials.size());
        
        /*----------- starting the game -----------*/
        Expression aRole = null;
        try {
            Substitution sigma = Const.pRole.chainOne( new Substitution(), theoryScope, flag );
            aRole = ResolutionHelper.produceDerivativeFromOneSubstitution( Const.vX, sigma );
        }
        catch ( InterruptedException e ) {
            logger.error(" interrupted !");
        }
        
        assertNotNull(aRole);
        logger.debug("palying as: "+aRole);
        GameState initialState = new GameState( initials );
        GameStateScope scope0 = new GameStateScope(theory, initialState);        

        Predicate legal = new Predicate(Const.aLegal, aRole, Const.vX);
        Expression aLegalMove = null;
        try {
            Substitution sigma = legal.chainOne( new Substitution(), scope0, flag );
            aLegalMove = ResolutionHelper.produceDerivativeFromOneSubstitution( legal, sigma );
        }
        catch ( InterruptedException e ) {
            logger.error( " interrupted !" );
        }
        
        logger.debug("palying as: "+aRole);
        assertNotNull(aLegalMove);
        
        ExpressionList legalMoves = null;
        try {
            legalMoves = ResolutionHelper.resolveAndApply(
                    legal, legal, scope0, flag );
        }
        catch ( InterruptedException e ) {
            logger.error( " interrupted !" );
        }
        
        assertNotNull(legalMoves);
        assertTrue( 0 < legalMoves.size());
        
        for(Expression aMove : legalMoves){
            logger.info(" move: "+aMove);
        }
    }
}
