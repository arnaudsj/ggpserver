package de.tu_dresden.inf.ggp06_2.resolver.helper;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;

public class ResolverUtilsTest {

    final String baseOr =
        "(<= (legal player (move ?x ?y))     " +
        "    (true (cell ?u ?y b))           " +
        "    (or (succ ?x ?u) (pred ?x ?u)) )";

    final String flatOr =
        "(<= (legal player (move ?x ?y))     " +
        "    (true (cell ?u ?y b))           " +
        "    (succ ?x ?u) )                  " +
        "(<= (legal player (move ?x ?y))     " +
        "    (true (cell ?u ?y b))           " +
        "    (pred ?x ?u) )";

    @Test
    public void testOr() {
        ExpressionList expBaseOr = Parser.parseGDL(baseOr);
        ExpressionList expFlatOr = Parser.parseGDL(flatOr);
        assertEquals(new Theory(expBaseOr).flattenTheory().getAll(), expFlatOr);
    }

}
