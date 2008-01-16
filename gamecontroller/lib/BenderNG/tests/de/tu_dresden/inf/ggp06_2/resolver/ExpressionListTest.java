package de.tu_dresden.inf.ggp06_2.resolver;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.helper.ResolverUtils;

public class ExpressionListTest {
    
    /* Testing the flattenExpresionList() method:
     * the first (one lelement) expressionList should be equal
     * to the second one after the flattening
     */
    private ExpressionList expressionList1 = Parser.parseGDL("" +
            "(<= (A ?X ?Y) " +
            "(NOT (AND (OR P (Q ?X L)) (NOT (AND (R ?X ?Y) (S ?X ?Y))))))");
    private ExpressionList expressionList2 = Parser.parseGDL(
            "( <= (A ?X ?Y ) (NOT P) (NOT (Q ?X L ))  ) " +
            "( <= (A ?X ?Y ) (R ?X ?Y ) (S ?X ?Y )  ) ");
    private ExpressionList expressionList3;
    
    @Before
    public void setUp() throws Exception {
      expressionList3 = ResolverUtils.flattenExpressionList(expressionList1);        
    }

     @Test
    public void testFlatteningExpressionList() {
        assertTrue(expressionList3.equals( expressionList2 ));
    }
}
