///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.test.prover;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 */
public class ProverTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite("Test for stanfordlogic.test.prover");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestSubstitution.class);
        suite.addTestSuite(TestTerms.class);
        suite.addTestSuite(TestProver.class);
        suite.addTestSuite(TestUnifier.class);
        suite.addTestSuite(TestFacts.class);
        //$JUnit-END$
        return suite;
    }

}
