package edu.mayo.aml.tooling.adl2aml;

import edu.mayo.aml.tooling.adl2aml.utils.AU;
import org.openehr.jaxb.am.Archetype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by dks02 on 12/16/14.
 */
public class ADL2AML
{
    private Vector<String> targetArchetypeIds = new Vector<String>();

    public static void main(String[] args)
    {
        ADL2AML adl2AML = new ADL2AML();
        adl2AML.prepareSelected();

        int howManyToWrite = 100000;
        int howManyWritten = 0;

        ADLReader adlReader = new ADLReader();
        AMLWriter amlWriter = new AMLWriter();

        adlReader.setAdlFolderOfFilePath("/Users/dks02/A123/git/archetypes/miniCIMI");
        List<File> inputFiles = adlReader.getAllFiles(null);

        AU.debug("Loaded =" + inputFiles.size() + " Archetypes!!");

        for (File adlFile : inputFiles) {
            String canonicalPath = null;
            Archetype arch = null;
            try {
                canonicalPath = adlFile.getCanonicalPath();
                arch = adlReader.getADLArchetype(adlFile);

                if (howManyWritten < howManyToWrite)
                {
                    if (adl2AML.inTheList(arch))
                    {
                        amlWriter.queue(arch);
                        howManyWritten++;
                    }
                } else
                    break;
            } catch (Exception e) {
                AU.debug("Failed:[" + canonicalPath + "] " + e.getMessage());
                continue;
            }

            if (arch == null)
                continue;
        }

        amlWriter.launch(args);

        AU.debug("DONE!!");
    }


    public void prepareSelected() {
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.cluster.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.compound_clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.indivisible_clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_document.v2.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_report_header.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.action.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.author_action.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.issue_action.v1.0.0");
    }

    public boolean inTheList(Archetype archetype)
    {
        if (archetype == null)
            return false;

        if (targetArchetypeIds.contains(archetype.getArchetypeId().getValue()))
            return true;

        return false;
    }
}
