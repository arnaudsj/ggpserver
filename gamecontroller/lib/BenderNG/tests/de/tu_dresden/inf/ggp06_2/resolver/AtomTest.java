/**
 *
 */
package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ingo Keller
 *
 */
public class AtomTest {
    Atom              anAtom;
    Map<Atom, String> collection;

    @Before
    public void setUp() throws Exception {
        anAtom     = new Atom("role");
        collection = new HashMap<Atom, String>();
    }

    @Test
    public final void testGetVariables() {
        assertEquals(anAtom.getVariables(), new ArrayList<Variable>());

        // malformed atom - just a check
        assertEquals(new Atom("?X").getVariables(), new ArrayList<Variable>());
    }

    @Test
    public final void testEqualsObject() {

        // equality holds for two distinct created literals which are the same
        assertEquals( new Atom("X"), new Atom("X") );

        // equality holds for key matching in collections
        collection.put( anAtom, "A" );
        assertEquals( collection.get( new Atom("role") ), "A" );

    }
}
