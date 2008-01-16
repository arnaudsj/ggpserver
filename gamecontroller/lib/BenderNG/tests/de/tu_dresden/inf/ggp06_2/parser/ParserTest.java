package de.tu_dresden.inf.ggp06_2.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.scope.TheoryScope;
import de.tu_dresden.inf.ggp06_2.resolver.structures.Theory;

import java_cup.runtime.Symbol;

public class ParserTest {
    
    private final static Logger logger = Logger.getLogger(ParserTest.class);

    @BeforeClass
    public static void putTitle(){
        logger.info( "\n=== ParserTest ===" );
    }
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
    @Test
	public void parsingAxioms() throws FileNotFoundException {

		String          filename   = "./testdata/axioms1.kif";
		FileInputStream gdlFile    = new FileInputStream( filename );
		preKIFParser    parser_obj = new preKIFParser( new preKIFScanner( gdlFile ));
 
		/* open input files, etc. here */
		Symbol  parse_tree     = null;
		boolean do_debug_parse = false;
		
		try {
			if (do_debug_parse)
				parse_tree = parser_obj.debug_parse();
			else
				parse_tree = parser_obj.parse();
		} catch (Exception e) {
            e.printStackTrace();
        }
        
        //logger.info( "\n---------------------\n" );
		
        if ( parse_tree != null ) {
            if ( parse_tree.value != null ){
                //logger.info( ( (ExpressionList) parse_tree.value ).size() + "\n" + parse_tree.value );
            } else {
                //logger.info( "no value" );
            }
        } else {
            //logger.info( "no parse_tree" );
        }
    }
    
    @Test
    public void classCheck(){
        ExpressionList expList = Parser.parseFile("./testdata/maze.kif");
        Theory theory = new Theory( expList);
        TheoryScope scope = new TheoryScope(theory);
        ExpressionList impls = scope.getSimilarExpressions( 
                new Implication(new Predicate(Const.aNext, Const.vX), Const.varListX) );
        
        assertNotNull(impls);
        assertTrue(0 < impls.size());
        for (Expression impl : impls){
            assertTrue(impl instanceof Implication);
        }
    }

}
