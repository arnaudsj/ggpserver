package de.tu_dresden.inf.ggp06_2.resolver;

import static de.tu_dresden.inf.ggp06_2.resolver.Expression.tNorm;
import static de.tu_dresden.inf.ggp06_2.resolver.Expression.tConorm;
import static de.tu_dresden.inf.ggp06_2.resolver.Expression.t;
import org.apache.log4j.Logger;
import org.junit.Test;

public class TNormsTest {

    private static final Logger logger = Logger.getLogger( TNormsTest.class );

    @Test
    public void tNorms(){
        double a = tNorm(0.999876543211, 1.2345678900005375E-4);
        double b = tNorm( a,  1.2345678900005375E-4);
        logger.info( "a: " +a);
        logger.info( "b: "+b );
        //assertEquals(0.01911717327880691, a);
        //assertEquals( 0.008984949172212264, b );
    }

    @Test
    public void tConorms(){
        // ROW \/ COLUMN
        double a = tConorm( 0.04527627776800591, 0.04527627776800591 );
        // DIAGONAL1 \/ DIAGONAL2
        double b = tConorm( 0.03096741450355034, 0.03096741450355034 );

        tConorm( a, b );
        tConorm( 0.01911717327880691, 0.008984949172212264);

    }

    @Test
    public void tTest(){
        double a = t(0.999876543211, 1.2345678900005375E-4);
        double b = t( a,  1.2345678900005375E-4);
        logger.info( "\n\n\n" );
        logger.info( "a: " +a);
        logger.info( "b: "+b );


    }

   /* @Test
    public void sTest(){
        // ROW \/ COLUMN
        double a = s( 0.04527627776800591, 0.04527627776800591 );
        // DIAGONAL1 \/ DIAGONAL2
        double b = s( 0.03096741450355034, 0.03096741450355034 );
        double c = s( a, b );
        double d1 = s(0.01911717327880691, 0.008984949172212264);

        logger.info( "\n\n\n" );
        logger.info( " a: " +a);
        logger.info( " b: "+b);
        logger.info( " c: "+c);
        logger.info( "corner:"+d1+" center: "+ b);
        //assertEquals( 0.04527627776800591, b );
        //assertEquals( 0.04527627776800591, c );

        //assertEquals( b, d1 );

    }*/

}
