package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class VariableTest {

    Variable              aVar;
    Map<Variable, String> collection;

    @Before
    public void setUp() throws Exception {
        aVar       = new Variable("?X");
        collection = new HashMap<Variable, String>();        
    }

    @Test
    public final void testGetVariables() {

        ArrayList<Variable> dummy = new ArrayList<Variable>();
        dummy.add( aVar );
        
        assertTrue  ( aVar.getVariables().size() == 1 );
        assertEquals( aVar.getVariables(), dummy      );

    }

    @Test
    public final void testEqualsObject() {

        // Equality for different constructors
        assertEquals( new Variable("?X"), aVar );
        
        // equality holds for key matching in collections
        collection.put( aVar, "X" );
        assertEquals( collection.get( new Variable("?X") ), "X" );
    
    }

}
