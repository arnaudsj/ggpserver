package de.tu_dresden.inf.ggp06_2.connection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;


public class MessageTest {

    Atom    aMove = new Atom("MOVE");
    Message test;

    @Before
    public void setUp() throws Exception {}

    @Test
    public final void testStartMessage() {

        test = new Message("(START match.test robot ((ROLE ROBOT) (role alice)) 10 10)");
        assertEquals( test.getType(),    Message.START );
        assertEquals( test.getMatchId(), "match.test"  );
        assertEquals( test.getRole(),    "robot"       );
    
    }

    @Test
    public final void testPlayMessage() {

        test = new Message("(PLAY match.test (MOVE))");
        assertEquals( test.getType(),         Message.PLAY                );
        assertEquals( test.getMatchId(),      "match.test"                );
        assertEquals( test.getMoves().size(), 1                           );
        assertEquals( test.getMoves(),        new ExpressionList( aMove ) );
    
        test = new Message("(PLAY match.test (NOOP))");
        assertEquals( test.getType(),         Message.PLAY                );
        assertEquals( test.getMatchId(),      "match.test"                );
        assertEquals( test.getMoves().size(), 1                           );
    
        test = new Message("(PLAY match.test ((MOVE) NOOP))");
        assertEquals( test.getType(),         Message.PLAY                );
        assertEquals( test.getMatchId(),      "match.test"                );
        assertEquals( test.getMoves().size(), 2                           );
    }

    @Test
    public final void testStopMessage() {

        test = new Message("(STOP match.test (MOVE))");        
        assertEquals( test.getType(),         Message.STOP );
        assertEquals( test.getMatchId(),      "match.test" );
        assertEquals( test.getMoves().size(), 1            );
        assertEquals( test.getMoves(),        new ExpressionList( aMove ) );
    }

}
