package edu.mayo.aml.tooling.adl2aml.test;

import edu.mayo.aml.tooling.adl2aml.ADLReader;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class ADLReaderTest extends TestCase
{

    public void testGetAllFiles() throws Exception
    {
        assertNotNull(new ADLReader().getAllFiles(null));
    }

    public void testGetADLArchetype() throws Exception
    {
        List<File> adls = new ADLReader().getAllFiles(null);
        assertFalse(adls.isEmpty());
        assertNotNull(adls.get(0));
        assertNotNull(new ADLReader().getADLArchetype(adls.get(0)));
    }
}