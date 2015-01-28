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

    public boolean applyFilter = true;

    public void queue(Archetype archetype)
    {
        if ((archetype == null)||(archetype.getArchetypeId() == null))
            return;

        if ((applyFilter)&&(!helper.toProecess(archetype)))
            return;

        archetypes.put(AMLWriterHelper.getArchetypeIdWithoutMinorVersion(archetype), archetype);
    }

    @Override
    protected byte execute()
    {
        // Start an AML Magic Draw Project which will contain all new UML entities
        amlProject = new AMLMDProject();
        if (amlProject.getProject() == null)
            logger.error("Failed to initialize the base project - to start AML Conversion !! Exiting...");

        amlProject.checkSession("<<<<<<<<<<<<   Starting transform...");

        amlProject.init();
        Preconditions.checkNotNull(amlProject.getRootPackages());

        amlProject.loadADLArchetypes(archetypes);

        // Save Project at the end of conversion
        amlProject.save();

        // Closing the session
        amlProject.closeSession();
        AU.debug("<<<<<<<<<<<<   Finished transform...");
        return 0;
    }
}
