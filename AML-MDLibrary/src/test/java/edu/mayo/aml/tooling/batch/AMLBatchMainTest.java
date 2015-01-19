package edu.mayo.aml.tooling.batch;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit adl2aml for simple AMLBatchMain.
 */
public class AMLBatchMainTest
    extends TestCase
{
    /**
     * Create the adl2aml case
     *
     * @param testName name of the adl2aml case
     */
    public AMLBatchMainTest(String testName)
    {
        super( testName );
        System.out.println("in the test...");
        //AMLBatchMainExampleProject mainExampleProject = new AMLBatchMainExampleProject();
        //mainExampleProject.launch(null);
        System.out.println("done...");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AMLBatchMainTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
