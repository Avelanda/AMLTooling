package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeOntology;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.rm.CodePhrase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject extends MDProject
{
    public Logger logger = Logger.getRootLogger();

    public Package rootPackage = null;
    public Package rmPackage = null;
    public Diagram rootPackageDiagram = null;

    private AMLMDProjectHelper ph = null;
    private AMLMDProjectTerms pt = null;

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
    }

    public Package getArchetypeLibraryPackage()
    {
        return rmPackage;
    }

    public Package getReferenceModelPackage()
    {
        return rmPackage;
    }


    public void init()
    {
        checkSession("Initializing Project");
        // Reinitialize project contents by deleteing any previously generated elements.
        ph.removeAllElements();

        // Import "Use Module" elements
        ph.addUsedModules();

        rmPackage = ph.getReferenceModelPackage(AMLConstants.referenceModelPackageName);

        if (rmPackage == null)
        {
            AU.warn("No Reference Model FOUND !! Exiting...");
            return;
        }

        rootPackage = ph.getRootPackage(AMLConstants.rootPackageName, true, rmPackage);

        try
        {
            rootPackageDiagram = ModelUtils.createDiagram(AMLConstants.rootPackageName,
                                         DiagramTypeConstants.UML_PACKAGE_DIAGRAM,
                                         rootPackage);

            ph.addElementToDiagram(rootPackage, rootPackageDiagram);
            ph.addElementToDiagram(rmPackage, rootPackageDiagram);

        } catch (ReadOnlyElementException e)
        {
            e.printStackTrace();
        }

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

        try
        {
            Package archPkg = ph.createArchetypePackage(archetype, rootPackage);
            ph.addElementToDiagram(archPkg, rootPackageDiagram);

            Diagram archDiag = ph.createArchetypeDiagram(archPkg);
            ph.addElementToDiagram(archPkg, archDiag);

            // create terminological elements referenced in the archetype
            Enumeration localIdentifiers = pt.addLocalTerms(archetype.getOntology(), archPkg);
            ph.addElementToDiagram(localIdentifiers, archDiag);

            if (archetype.getDefinition() == null)
                return null;

            archCls = processArchetype(archetype, archPkg, archDiag, localIdentifiers);

            // Add to the list of processed archetypes
            processed.put(currentArchId, archCls);
            addParentArchetype(archetype, archCls, currentArchId, archDiag);
            ph.displayRelatedInformation(archDiag);
        }
        catch(ReadOnlyElementException roe)
        {
            roe.printStackTrace();
        }

        AU.debug("######################## END ######################################");

        return archCls;
    }

    private Class processArchetype(Archetype archetype,
                                   Package pkg,
                                   Diagram diag,
                                   Enumeration localIds)
                        throws ReadOnlyElementException
    {
        String language = archetype.getOriginalLanguage().getCodeString();
        EnumerationLiteral archLang = AMLMDProjectCommons.getThisOrEnglish(getProject(),
                                                            language);

        if (archLang == null)
            archLang = AMLMDProjectCommons.getEnglishLanguage(getProject());

        Class archCls = ph.addConstraint(archetype.getDefinition(),
                archLang, pkg, diag,
                archetype.getOntology(),
                AMLConstants.CONSTRAINT_PROFILE,
                AMLConstants.STEREOTYPE_ARCHETYPE_VERSION,
                localIds);

        return archCls;
    }


    private void addParentArchetype(Archetype archetype,
                                    Class archClass,
                                    String archId, Diagram diagram)
            throws ReadOnlyElementException
    {
        // Find and add parent if there is one (using Specialization with "constrains" stereotype)
        String parentArchId = "";
        Class parentCls = null;
        if (archetype.getParentArchetypeId() != null)
            parentArchId = AMLWriterHelper.removeMinorVersion(archetype.getParentArchetypeId().getValue());

        if(!AU.isNull(parentArchId))
        {
            if (!archId.equals(parentArchId))
            {
                Archetype parent = adlArchetypes.get(parentArchId);

                if (parent == null)
                    AU.debug("Archetype Parent " + parentArchId + " not found in given list of archetypes. Skipping...");
                else
                {
                    AU.debug("Adding Parent:" + parentArchId);
                    parentCls = addArchetype(adlArchetypes.get(parentArchId));
                }
            }
            else
                parentCls = archClass;
        }

        if (parentCls != null)
        {
            Generalization generalization = ModelUtils.createGeneralization(parentCls, archClass);
            ModelUtils.findAndApplyStereotype(generalization, AMLConstants.CONSTRAINT_PROFILE, AMLConstants.STEREOTYPE_CONSTRAINS, null);
            ph.addElementToDiagram(parentCls, diagram);
        }
    }
}
