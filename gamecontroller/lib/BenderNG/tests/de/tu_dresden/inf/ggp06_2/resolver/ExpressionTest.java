package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertTrue;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolverUtils;


public class ExpressionTest {

    Atom aX1   = new Atom("X");
    Atom aX2   = new Atom("X");
    Atom aY1   = new Atom("Y");
    Atom aY2   = new Atom("Y");
    Atom aA    = new Atom("A");
    Atom aB    = new Atom("B");
    Atom aC    = new Atom("C");
    Atom aF    = new Atom("F");
    Atom aG    = new Atom("G");
    Atom aH    = new Atom("H");
    Atom aSucc = new Atom("SUCC");

    Variable vX1 = new Variable("?X");
    Variable vX2 = new Variable("?X");
    Variable vY1 = new Variable("?Y");
    Variable vZ1 = new Variable("?Z");

    Predicate    pSucc    = new Predicate( aSucc, vX1, vY1 );
    Predicate    pG1      = new Predicate( aG,    aA,  aB  );
    Predicate    pG2      = new Predicate( aG,    aA,  vX1 );
    Predicate    pG3      = new Predicate( aG,    aA  );
    Predicate    pH1      = new Predicate( aH,    aC  );
    Predicate    pH2      = new Predicate( aH,    vY1 );
    Predicate    pF1      = new Predicate( aF,    aA,  vX1 );
    Predicate    pF2      = new Predicate( aF,    vY1, aB  );
    Predicate    pF3      = new Predicate( aF,    pG1, vY1 );
    Predicate    pF4      = new Predicate( aF,    aC,  vX1 );
    Predicate    pF5      = new Predicate( aF,    pG2, pH1 );
    Predicate    pF6      = new Predicate( aF,    pG1, vY1 );
    Predicate    pF7      = new Predicate( aF,    pG3 );
    Predicate    pF8      = new Predicate( aF,    vX1 );
    Predicate    pF9      = new Predicate( aF,    pG2, pH2 );
    Predicate    pF0      = new Predicate( aF,    pG1, vY1 );
    Predicate    pH3      = new Predicate( aH,    vY1, pF7 );
    Predicate    pH4      = new Predicate( aH,    vX1, pF8 );

    Substitution sEmpty   = new Substitution();
    Substitution sVarAtom = new Substitution();
    Substitution sVarPred = new Substitution();
    Substitution sF1F2    = new Substitution();
    Substitution sF5F6    = new Substitution();
    Substitution sH3H4    = new Substitution();

    /* Testing the moveNegationToAtoms() method:
     * premises of the two following implications should be the same
     * after the moving of negation is applied to the first premise
    */
    ExpressionList expressionList1 = Parser.parseGDL("" +
            "(<= (A ?X ?Y) " +
            "(NOT (AND (OR P (Q ?X L)) (NOT (AND (R ?X ?Y) (S ?X ?Y))))))");
    ExpressionList expressionList2 = Parser.parseGDL(
            "(<= (A ?X ?Y) " +
            "(OR (AND (NOT P) (NOT (Q ?X L )) ) (AND (R ?X ?Y ) (S ?X ?Y ) ) ) )");

    Expression expression1;
    Expression expression2;

    private static final Logger logger  = Logger.getLogger( ExpressionTest.class );


    @Before
    public void setUp() throws Exception {
        pH3.operands.add( pG3 );
        pH4.operands.add( vY1 );

        sVarAtom.addAssociation( vY1, aX1   );
        sVarPred.addAssociation( vZ1, pSucc );
        sF1F2.addAssociation( vX1, aB  );
        sF1F2.addAssociation( vY1, aA  );
        sF5F6.addAssociation( vX1, aB  );
        sF5F6.addAssociation( vY1, pH1 );
        sH3H4.addAssociation( vX1, pG3 );
        sH3H4.addAssociation( vY1, pG3 );

        expression1 = ResolverUtils.moveNegationToAtoms( ((Implication)expressionList2.get( 0 )).
                        getPremises().get( 0 ));
        expression2 = ((Implication)expressionList2.get( 0 )).
                        getPremises().get( 0 );
    }

    /**
     * This test method shows the correctness of our most general unifiyer
     * calculation.
     */
    @Test
    public void mgu() {

        // Atom1 x Atom2 -> {} if Atom1 == Atom2 -> empty substitution
        assertTrue( aX1.mgu( aX2, new Substitution() ) != null );
        assertTrue( aX1.mgu( aX2, new Substitution() ).equals( sEmpty ) );

        // Atom1 x Atom2 -> null if Atom1 != Atom2 -> fail
        assertTrue( aX1.mgu( aY1, new Substitution() ) == null );

        // Var1 x Atom1 -> {Var1/Atom1}
        assertTrue( vY1.mgu( aX1, new Substitution() ) != null );
        assertTrue( vY1.mgu( aX1, new Substitution() ).equals( sVarAtom ) );

        // Atom1 x Var1 -> {Var1/Atom1}
        assertTrue( aX1.mgu( vY1, new Substitution() ) != null );
        assertTrue( aX1.mgu( vY1, new Substitution() ).equals( sVarAtom ) );

        // Var1 x Var2 -> {Var1/Var_, Var2/Var_}
        assertTrue( vX1.mgu( vY1, new Substitution() ) != null );
        assertTrue( vX1.mgu( vY1, new Substitution() ).size() == 2 );

        // Atom x Predicate -> null -> fail
        assertTrue( aX1.mgu( pSucc, new Substitution() ) == null );

        // Predicate x Atom -> null -> fail
        assertTrue( pSucc.mgu( aX1, new Substitution() ) == null );

        // Var x Predicate -> {Var/Pred}
        assertTrue( pSucc.mgu( vZ1, new Substitution() ) != null );
        assertTrue( pSucc.mgu( vZ1, new Substitution() ).equals( sVarPred ) );

        // Predicate x Var -> {Var/Pred}
        assertTrue( vZ1.mgu( pSucc, new Substitution() ) != null );
        assertTrue( vZ1.mgu( pSucc, new Substitution() ).equals( sVarPred ) );

        // f(a,?x) x f(?y,b) -> {y/a,x/b}
        assertTrue( pF1.mgu( pF2, new Substitution() ) != null );
        assertTrue( pF1.mgu( pF2, new Substitution() ).equals( sF1F2 ) );

        // f(g(a,b),?y) x f(c,?x) -> fail
        assertTrue( pF3.mgu( pF4, new Substitution() ) == null );

        // f(g(a,?x),h(c)) x f(g(a,b),?y) -> {x/b,y/h(c)}
        assertTrue( pF5.mgu( pF6, new Substitution() ) != null );
        assertTrue( pF5.mgu( pF6, new Substitution() ).equals( sF5F6 ) );

        // p(?y,f(g(a)),?y) x p(?x,f(?x),g(a)) -> {x/g(a),y/g(a)}
        assertTrue( pH3.mgu( pH4, new Substitution() ) != null );
        System.err.println(pH3.mgu( pH4, new Substitution() ));
//        assertTrue( pH3.mgu( pH4, new Substitution() ).equals( sH3H4 ) );

        // f(g(a,?x),h(y)) x f(g(a,b),?y) -> fail -> because of ?y/h(?y) mapping
        assertTrue( pF9.mgu( pF0, new Substitution() ) == null );
    }

    @Test
    public void testMovingNegationToAtoms() {
        logger.info( expression1.toString() );
        logger.info( expression2.toString() );
        assertTrue(expression1.equals( expression2 ));
    }
}
