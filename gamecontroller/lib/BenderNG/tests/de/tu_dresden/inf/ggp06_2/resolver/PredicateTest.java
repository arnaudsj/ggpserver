package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class PredicateTest {
    private Predicate aPredicate;
    private static final Atom aCell = new Atom("cell");
    private static final Atom a1 = new Atom("1");
    
    private static final Atom role = new Atom("a");
    private static final Atom pickedby = new Atom("pickedby");
    final Predicate pPickedByA1 = new Predicate(
            Const.aTrue, new Predicate(pickedby, role, a1));
    
    @Before
    public void setUp(){
        aPredicate = new Predicate(Const.aTrue, new Predicate(aCell, Const.vX));
    }
    
    @Test
    public void equalityAndHashCode(){
        Predicate anotherPredicate = new Predicate(
                Const.aTrue, 
                new Predicate(aCell, new Variable("?Y")));
        
        assertTrue(aPredicate.hashCode() != anotherPredicate.hashCode());
        assertTrue(!aPredicate.equals( anotherPredicate) );

        Predicate yetAnotherPredicate = new Predicate(
                Const.aTrue, 
                new Predicate(aCell, new Variable("?X")));        
        assertEquals(aPredicate, yetAnotherPredicate);
        assertEquals(aPredicate.hashCode(), yetAnotherPredicate.hashCode());
        

        assertEquals( pPickedByA1, new Predicate(
                Const.aTrue, new Predicate(pickedby, role, a1)) );
    }
}
