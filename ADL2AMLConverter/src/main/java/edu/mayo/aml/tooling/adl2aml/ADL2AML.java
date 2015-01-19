package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.magicdraw.core.Project;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
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
        int howManyToWrite = 1;
        int howManyWritten = 0;
        int total = 0;
        int failed = 0;
        List<String> failedFiles = new ArrayList<String>();

        ADLReader adlReader = new ADLReader();
        AMLWriter amlWriter = new AMLWriter();

        //adlReader.setAdlFolderOfFilePath("./ADL2AMLConverter/adl15");
        adlReader.setAdlFolderOfFilePath("/Users/dks02/A123/git/archetypes/miniCIMI");

        List<File> inputFiles = adlReader.getAllFiles(null);

        AU.debug("Loaded =" + inputFiles.size() + " Archetypes!!");
        total = inputFiles.size();

        Project project = ProjectUtils.getProject();

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
                    amlWriter.convertToAML(arch);
                    howManyWritten++;
                }
                else
                    break;
            }
            catch (Exception e)
            {
                AU.debug("Failed:[" + canonicalPath + "] " + e.getMessage());
                failedFiles.add(canonicalPath);
                failed++;
                continue;
            }

            if (arch == null)
                continue;

            AU.debug("##############################################################");
            AU.debug("File: " + canonicalPath);
            AU.debug("Archetype: " + arch.getArchetypeId().getValue());

            if ((arch.getDescription() != null)&&(arch.getDescription().getDetails().size() > 0))
                AU.debug("Description: " + arch.getDescription().getDetails().get(0).getPurpose());
        }

        AU.debug("Total=" + total + "  Success=" + (total - failed) + " Failed=" + failed + " Converted to AML=" + howManyWritten);
        AU.debug("DONE!!");
    }
}
