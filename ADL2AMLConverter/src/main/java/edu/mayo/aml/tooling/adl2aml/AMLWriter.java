package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import edu.mayo.aml.tooling.batch.AMLBatchAuxiliary;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dks02 on 12/16/14.
 */
public class AMLWriter extends CommandLine
{
    public Logger logger = Logger.getRootLogger();
    public HashMap<String, Archetype> archetypes = new HashMap<String, Archetype>();

    private AMLMDProject amlProject = null;
    private AMLWriterHelper helper = new AMLWriterHelper();

    boolean toFiler = true;

    public void queue(Archetype archetype)
    {
        if ((archetype == null)||(archetype.getArchetypeId() == null))
            return;

        archetypes.put(archetype.getArchetypeId().getValue(), archetype);
    }

    @Override
    protected byte execute()
    {
        amlProject = new AMLMDProject();
        if (amlProject.getAMLMDProject() == null)
            logger.error("Failed to initialize the base project - to start AML Conversion !! Exiting...");

        this.convert();
        return 0;
    }

    public void convert()
    {
        int total = archetypes.size();
        int failed = 0;
        List<String> failedFiles = new ArrayList<String>();

        if (archetypes.isEmpty())
            AU.warn("No Archetype to convert!!");

        AU.info("Converting " + archetypes.size() + " archetypes...");

        for (Archetype archetype : archetypes.values())
        {
            if (toFiler && (!helper.toProecess(archetype)))
                continue;

            AU.debug("##############################################################");
            AU.debug("Archetype: " + archetype.getArchetypeId().getValue());

            if ((archetype.getDescription() != null) && (archetype.getDescription().getDetails().size() > 0))
                AU.debug("Description: " + archetype.getDescription().getDetails().get(0).getPurpose());

        }

        AU.debug(" Total=" + total +
                 " Success=" + (total - failed) +
                 " Failed=" + failed);
    }
}
