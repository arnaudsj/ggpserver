package de.tu_dresden.inf.ggp06_2.resolver.structures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzyResolution;
import de.tu_dresden.inf.ggp06_2.resolver.fuzzy.FuzzySubstitution;

public class FuzzyResolutionMemorizerTest {

    private static final Atom aCell = new Atom("cell");
    private static final Atom aB = new Atom("b");
    private static final Atom a1 = new Atom("1");
    private static final Variable vX = new Variable("?x");

    FuzzyResolutionMemorizer fuzzyMemorizer;
    Predicate pCell;
    FuzzySubstitution resolution;
    FuzzyResolution resols;

    @Before
    public void setUp(){
        fuzzyMemorizer = new FuzzyResolutionMemorizer();
        pCell = new Predicate(aCell, vX, aB);
        resolution = new FuzzySubstitution();
        resols = new FuzzyResolution();
        resols.add( resolution );
    }

    @Test
    public void usefullSubstitutions(){
        resolution.addAssociation( vX, a1 );
        resolution.setFuzzyValue( 0.66 );
        resols.setFuzzyValue( 0.66 );
        fuzzyMemorizer.setFuzzyEvaluationStage( pCell, resols );

        Predicate pCell2 = new Predicate(aCell, new Variable("?new"), aB);
        assertTrue(fuzzyMemorizer.isFuzzylyEvaluated( pCell2 ));

        FuzzyResolution stage = fuzzyMemorizer.getFuzzyResolutionStage(
                new FuzzySubstitution(), pCell2 );
        assertTrue( null != stage );
        assertEquals(0.66, stage.getFuzzyValue());
        assertEquals( 1, stage.size() );

        Expression groundedPCell2 = pCell2.apply( stage.iterator().next() );
        Predicate expectedPCell2 = new Predicate(aCell, a1, aB);
        assertEquals(expectedPCell2, groundedPCell2);
    }

    @Test
    public void uselessSubstitutions(){
        resolution.addAssociation( new Variable("?somevar"), a1 );
        double d = 0.002;
        resolution.setFuzzyValue( d );
        resols.setFuzzyValue( d );
        fuzzyMemorizer.setFuzzyEvaluationStage( pCell, resols );

        Variable newVar = new Variable("?new");
        Predicate pCell2 = new Predicate(aCell, newVar, aB);
        assertTrue(fuzzyMemorizer.isFuzzylyEvaluated( pCell2 ));

        FuzzyResolution stage = fuzzyMemorizer.getFuzzyResolutionStage(
                new FuzzySubstitution(), pCell2 );

        assertTrue( null != stage );
        assertEquals(d, stage.getFuzzyValue());

    }
}
