package edu.mayo.aml.tooling.adl2aml;

import edu.mayo.aml.tooling.adl2aml.utils.AU;
import org.openehr.jaxb.am.Archetype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dks02 on 12/16/14.
 */
public class ADL2AML
{
    public static void main(String[] args)
    {
        int howManyToWrite = 100000;
        int howManyWritten = 0;

        ADLReader adlReader = new ADLReader();
        AMLWriter amlWriter = new AMLWriter();

        adlReader.setAdlFolderOfFilePath("/Users/dks02/A123/git/archetypes/miniCIMI");
        List<File> inputFiles = adlReader.getAllFiles(null);

        AU.debug("Loaded =" + inputFiles.size() + " Archetypes!!");

        for (File adlFile : inputFiles)
        {
            String canonicalPath = null;
            Archetype arch = null;
            try
            {
                canonicalPath = adlFile.getCanonicalPath();
                arch = adlReader.getADLArchetype(adlFile);

                if (howManyWritten < howManyToWrite)
                {
                    amlWriter.queue(arch);
                    howManyWritten++;
                }
                else
                    break;
            }
            catch (Exception e)
            {
                AU.debug("Failed:[" + canonicalPath + "] " + e.getMessage());
                continue;
            }

            if (arch == null)
                continue;
        }

        amlWriter.launch(args);

        AU.debug("DONE!!");
    }
}
