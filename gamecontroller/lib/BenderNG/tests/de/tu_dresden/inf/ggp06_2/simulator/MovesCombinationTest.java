package de.tu_dresden.inf.ggp06_2.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

public class MovesCombinationTest {
    
    private static final Logger logger = Logger.getLogger(MovesCombinationTest.class);
    
    private static final Expression blackMove1 = new Atom("1");
    private static final Expression blackMove2 = new Atom("2");
    private static final Expression whiteMoveA = new Atom("a");
    private static final Expression whiteMoveB = new Atom("b");
    List<ExpressionList> playerMoves = null;
    ExpressionList turnA1;
    ExpressionList turnA2;
    ExpressionList turnB1;
    ExpressionList turnB2;
    
    
    @Before
    public void setUp(){
        ExpressionList blackMoves = new ExpressionList();
        blackMoves.add( blackMove1 );
        blackMoves.add( blackMove2 );
        ExpressionList whiteMoves = new ExpressionList();
        whiteMoves.add( whiteMoveA );
        whiteMoves.add( whiteMoveB );
        playerMoves = new ArrayList<ExpressionList>();
        playerMoves.add( whiteMoves );
        playerMoves.add( blackMoves );
        
        turnA1 = new ExpressionList();
        turnA1.add( whiteMoveA );turnA1.add( blackMove1 );
        
        turnA2 = new ExpressionList();
        turnA2.add( whiteMoveA );turnA2.add( blackMove2 );
        
        turnB1 = new ExpressionList();
        turnB1.add( whiteMoveB );turnB1.add( blackMove1 );
        
        turnB2 = new ExpressionList();
        turnB2.add( whiteMoveB );turnB2.add( blackMove2 );
    }
    
    @Test
    public void testCombinedMoves(){
        List<ExpressionList> combined = Game.combine( playerMoves );
        assertNotNull( combined );
        logger.info( combined );
        assertEquals(4, combined.size());
        for (ExpressionList oneEdge : combined){
            assertEquals( 2, oneEdge.size() );
        }
        assertTrue(combined.contains( turnA1 ));
        assertTrue(combined.contains( turnA2 ));
        assertTrue(combined.contains( turnB1 ));
        assertTrue(combined.contains( turnB2 ));
        
    }
}
