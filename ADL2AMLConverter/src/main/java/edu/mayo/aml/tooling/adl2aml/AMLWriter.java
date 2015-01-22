package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeOntology;
import org.openehr.jaxb.am.TermBindingItem;
import org.openehr.jaxb.am.TermBindingSet;

import java.net.URI;
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
        // Start an AML Magic Draw Project which will contain all new UML entities
        amlProject = new AMLMDProject();
        if (amlProject.getProject() == null)
            logger.error("Failed to initialize the base project - to start AML Conversion !! Exiting...");

        amlProject.init();
        Preconditions.checkNotNull(amlProject.getRootPackages());

        this.convert();

        // Save Project at the end of conversion
        amlProject.save();

        amlProject.closeSession();
        return 0;
    }

    public void convert()
    {
        Preconditions.checkState(!archetypes.isEmpty());

        int total = archetypes.size();
        int failed = 0;
        List<String> failedFiles = new ArrayList<String>();

        AU.info("Converting " + archetypes.size() + " archetypes...");
        for (Archetype archetype : archetypes.values())
        {
            if (toFiler && (!helper.toProecess(archetype)))
                continue;

            AU.debug("##############################################################");
            AU.debug("Archetype: " + archetype.getArchetypeId().getValue());

            if ((archetype.getDescription() != null) && (archetype.getDescription().getDetails().size() > 0))
                AU.debug("Description: " + archetype.getDescription().getDetails().get(0).getPurpose());


            addArchetype(archetype);

        }

        AU.debug(" Total=" + total +
                 " Success=" + (total - failed) +
                 " Failed=" + failed);
    }

    public void addTerms(ArchetypeOntology ontology)
    {
        if (ontology.getTermBindings().isEmpty())
            return;

        for (TermBindingSet set : ontology.getTermBindings())
            for (TermBindingItem item : set.getItems())
                amlProject.createConceptReference(item);
    }

    public void addArchetype(Archetype archetype)
    {
            addTerms(archetype.getOntology());
            amlProject.createArchetype(archetype);
    }
}
