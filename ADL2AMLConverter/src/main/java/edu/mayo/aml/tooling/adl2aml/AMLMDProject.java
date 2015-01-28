package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.CAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject extends MDProject
{
    public Logger logger = Logger.getRootLogger();

    public Package rootPackage = null;

    private AMLMDProjectHelper ph = null;
    private AMLMDProjectTerms pt = null;
    private AMLMDProjectCommons pc = null;

    private HashMap<String, Archetype> adlArchetypes = new HashMap<String, Archetype>();
    private HashMap<String, Class> processed = new HashMap<String, Class>();

    public AMLMDProject()
    {
        if (getProject() == null)
        {
            Project proj = ProjectUtils.getProjectAtLocation(AMLConstants.projectLocation);
            if (proj == null)
            {
                proj = ProjectUtils.createProject();
                ProjectUtils.setActiveProject(proj);
                ProjectDescriptorsFactory.createLocalProjectDescriptor(proj, ProjectUtils.getDefaultProjectFile());
                save();
            }

            setProject(proj);
        }

        ph = new AMLMDProjectHelper(this);
        pt = new AMLMDProjectTerms(this);
        pc = new AMLMDProjectCommons(this);
    }

    public void init()
    {
        checkSession("Initializing Project");
        // Reinitialize project contents by deleteing any previously generated elements.
        ph.removeAllElements();

        // Import "Use Module" elements
        ph.addUsedModules();

        rootPackage = ph.getRootPackage(AMLConstants.defaultRootPackageName, true);
        //AU.info("Root Package:" + UMLUtils.printUMLNamedElement(rootPackage));

        // Initialize Project Terminology Structure under the root package.
        // This will create folder and sub-folders for terminology/concept references
        // that are used for this set of archetypes.
        pt.initialize(rootPackage);
    }


    public void loadADLArchetypes(HashMap<String, Archetype> archetypes)
    {
        Preconditions.checkNotNull(archetypes);

        this.adlArchetypes = archetypes;

        if (archetypes.isEmpty())
        {
            AU.warn("No Archetype found for loading into AML Project!! Exiting...");
            return;
        }

        int total = archetypes.size();
        int failed = 0;
        List<String> failedFiles = new ArrayList<String>();

        AU.info("Converting " + archetypes.size() + " archetypes...");

        for (Archetype archetype : archetypes.values())
        {
            try
            {
                addArchetype(archetype);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                failed++;
            }
        }

        AU.debug(" Total=" + total +
                " Success=" + (total - failed) +
                " Failed=" + failed);
    }

    public Class addArchetype(Archetype archetype)
    {
        if (archetype == null)
            return null;

        // Check if already processed or not
        String currentArchId = AMLWriterHelper.getArchetypeIdWithoutMinorVersion(archetype);
        Class archCls = processed.get(currentArchId);

        if (archCls != null)
        {
            AU.debug("Archetype " + currentArchId + " Already processed!");
            return archCls;
        }

        AU.debug("####################### BEGIN #######################################");
        AU.debug("Archetype: " + archetype.getArchetypeId().getValue());
        if ((archetype.getDescription() != null) && (archetype.getDescription().getDetails().size() > 0))
            AU.debug("Description: " + archetype.getDescription().getDetails().get(0).getPurpose());

        // create terminological elements referenced in the archetype
        pt.addTerms(archetype.getOntology());

        try
        {
            Package archPkg = ph.createArchetypePackage(archetype, rootPackage);
            Diagram archDiag = ph.createArchetypeDiagram(archPkg);

            ph.addElementToDiagram(archPkg, archDiag);

            if (archetype.getDefinition() == null)
                return archCls;

            String archVersionName = ADLHelper.getTermDefinitionText(archetype,
                                                archetype.getDefinition().getNodeId(),
                                                archetype.getOriginalLanguage().getCodeString());

            archCls = ModelUtils.createClass(archVersionName,
                                                    archPkg,
                                                    AMLConstants.CONSTRAINT_PROFILE,
                                                    AMLConstants.STEREOTYPE_ARCHETYPE_VERSION,
                                                    null);

            ph.addElementToDiagram(archCls, archDiag);

            List<CAttribute> attributes = archetype.getDefinition().getAttributes();
            ph.displayRelatedInformation(archDiag);

            processed.put(currentArchId, archCls);

            String parentArchId = "";

            if (archetype.getParentArchetypeId() != null)
                parentArchId = AMLWriterHelper.removeMinorVersion(archetype.getParentArchetypeId().getValue());

            if((!AU.isNull(parentArchId))&&
               (!currentArchId.equals(parentArchId)))
            {
                Archetype parent = adlArchetypes.get(parentArchId);

                if (parent == null)
                    AU.debug("Archetype Parent " + parentArchId +" not found in given list of archetypes. Skipping...");
                else
                {
                    AU.debug("Adding Parent:" + parentArchId);
                    addArchetype(adlArchetypes.get(parentArchId));
                }
            }
        }
        catch(ReadOnlyElementException roe)
        {
            roe.printStackTrace();
        }

        AU.debug("######################## END ######################################");

        return archCls;
    }
}
