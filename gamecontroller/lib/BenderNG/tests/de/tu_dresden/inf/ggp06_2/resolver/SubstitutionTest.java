package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SubstitutionTest {
    
    Substitution sub1, sub2;
    
    @Before
    public void setUp(){
        Variable vL = new Variable("?l");
        Atom aL = new Atom("l");
        sub1 = new Substitution();
        sub1.addAssociation( vL, aL );

        vL = new Variable("?l");
        aL = new Atom("l");
        sub2 = new Substitution();
        sub2.addAssociation( vL, aL );
    }
    
    @Test
    public void hashCodes(){
        assertEquals(sub1.hashCode(), sub2.hashCode());
        assertEquals(sub1, sub2);
    }
}
