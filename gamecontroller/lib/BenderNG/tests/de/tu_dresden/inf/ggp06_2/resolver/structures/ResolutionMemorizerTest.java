package de.tu_dresden.inf.ggp06_2.resolver.structures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;

public class ResolutionMemorizerTest {
    
    private static final Logger logger = Logger.getLogger( ResolutionMemorizerTest.class );

    // Terms
    private final static Atom aStep = new Atom("STEP");
    private static final Atom a1 = new Atom("1");
    private static final Atom a2 = new Atom("2");

    final static Variable vY = new Variable("?Y");
    final static Variable vX = new Variable("?X");

    // Predicates
    Predicate  trueStepX;
    List<Substitution> allPovenSigmas;
    //GameState gameState;
    ResolutionMemorizer memorizer;

    @Before
    public void setUp(){
        trueStepX = new Predicate( Const.aTrue,
                new Predicate(aStep, vX));
        
        Substitution sigmaStep = new Substitution();
        sigmaStep.addAssociation( vX, a1 );
        allPovenSigmas = new ArrayList<Substitution>();
        allPovenSigmas.add( sigmaStep );
        
        memorizer = new ResolutionMemorizer();        
        memorizer.setProven( trueStepX, allPovenSigmas );        
    }
    
    @Test
    public void intermediateProvenStates(){
        Predicate trueStepY = new Predicate( Const.aTrue,
                new Predicate(aStep, vY));
        
        assertTrue(memorizer.isProven( trueStepY ));
        
        List<Substitution> received = memorizer.getProven( 
                new Substitution(), trueStepY );
        
        assertNotNull(received);
        assertTrue(!received.isEmpty());
        assertTrue(1 == received.size());
        
        Substitution shortCut = received.get( 0 );
        
        assertNotNull(shortCut);
        assertTrue(!shortCut.isEmpty());
        
        if (logger.isDebugEnabled()){
            logger.debug("shortcut: "+shortCut);
        }
        assertTrue(1 == shortCut.entrySet().size());
        assertTrue(shortCut.containsKey( vY ));
        assertEquals(a1, shortCut.get( vY ));
    }
    
    /**
     * This one is quite important. Let's take a closer
     * look. <ol>
     * <li> original gameState maps (true (step ?X)) to (?X->1)</li>
     * <li> we try to find a proof for (true (step ?Y)) with current substitution
     *      (?Y -> 2)</li>
     * <li> BUT we get current value here (?Y->2), because we
     *      do not consider having contravertial informationn for same state!</li>
     * </ol>
     *
     */
    @Test
    public void intermediateControvertialState(){
        //logger.info( " === intermediateControvertialState === " );
        Predicate trueStepY = new Predicate( Const.aTrue,
                new Predicate(aStep, vY));
        Substitution currentProofState = new Substitution();
        currentProofState.addAssociation( vY, a2 );
        
        //logger.info( " === isProven() === " );
        
        assertTrue(memorizer.isProven( trueStepY ));
        
        //logger.info( " === getProven() === " );
        
        List<Substitution> received = memorizer.getProven( 
                currentProofState, trueStepY );
        
        assertNotNull(received);
        assertFalse(received.isEmpty());
        assertEquals(1, received.size());
        
        Substitution theProof = received.get( 0 );
        
        assertTrue(theProof.containsKey( vY ));
        assertEquals(a2, theProof.get( vY ));
    }

    @Test
    public void complexProof(){
        //logger.info( " === complexProof === " );
        Substitution complexProof = new Substitution();
        complexProof.addAssociation( vX, vY );
        complexProof.addAssociation( vY, a1 );
        
        allPovenSigmas.add( complexProof );
        memorizer.setProven(trueStepX, allPovenSigmas);
        
        //logger.info( " === complex proof added === " );
        
        Predicate trueStepY = new Predicate( Const.aTrue,
                new Predicate(aStep, vY));
        
        assertTrue(memorizer.isProven( trueStepY ));
        
        //logger.info( " === isProven() tested. === " );
        
        List<Substitution> results = memorizer.getProven( new Substitution(), trueStepY );
        
        //logger.info( " === getProven(): "+results );
        
        assertNotNull(results);
        assertEquals(1, results.size());
        
        Substitution theProof = results.get( 0 );
        
        assertTrue(theProof.containsKey( vY ));
        assertEquals(a1, theProof.get( vY ));
    }
}
