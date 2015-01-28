package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.*;
import com.nomagic.magicdraw.uml.symbols.paths.ContainmentLinkView;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.InterfaceRealization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dks02 on 1/27/15.
 */
public class AMLMDProjectHelper
{
    public Logger logger = Logger.getRootLogger();
    private MDProject mdp = null;

    public String getDefaultRootPackageName()
    {
        return AMLConstants.defaultRootPackageName;
    }

    public AMLMDProjectHelper(MDProject mdProject)
    {
        this.mdp = mdProject;
    }

    public Package getRootPackage(String name, boolean create)
    {
        Collection<Package> roots = ModelUtils.findPackageForMatchingName(mdp.getProject(), name);

        if (!roots.isEmpty())
            roots.iterator().next();

        if (!create) return null;

        return ModelUtils.createPackage(getDefaultRootPackageName(), mdp.getProject().getModel());
    }

    public void removeAllElements()
    {
        Preconditions.checkNotNull(mdp.getProject());
        ModelUtils.removeAllPackages(mdp.getProject());
        ModelUtils.removeAllProfiles(mdp.getProject());
    }

    public void addUsedModules()
    {
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        try
        {
            for (String imp : AMLConstants.imports)
            {
                File file = new File(imp);
                ProjectDescriptor des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
                projectsManager.useModule(mdp.getProject(), des);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Package createArchetypePackage(Archetype archetype, Package parent)
    {
        // Archetype creation Begin
        String adlArchId = archetype.getArchetypeId().getValue();
        String amlArchId = AMLWriterHelper.getAMLArchetypeNameFromADLArchetypeName(adlArchId);
        AU.debug("Creating AML Archetype Package: " + amlArchId);

        return ModelUtils.createPackage(amlArchId,
                parent,
                AMLConstants.CONSTRAINT_PROFILE,
                AMLConstants.STEREOTYPE_ARCHETYPE,
                null);
    }

    public Diagram createArchetypeDiagram(Package archPkg) throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(archPkg);
        return ModelUtils.createDiagram(archPkg.getName(), DiagramTypeConstants.UML_CLASS_DIAGRAM, archPkg);
    }

    public void addElementToDiagram(Element element, Diagram diagram) throws ReadOnlyElementException
    {
        DiagramPresentationElement pe = mdp.getProject().getDiagram(diagram);
        PresentationElementsManager.getInstance().createShapeElement(element, pe);
    }

    public void displayRelatedInformation(Diagram diagram) throws ReadOnlyElementException
    {
        Set linkTypes = new HashSet();
        linkTypes.add(new LinkType(Generalization.class));
        linkTypes.add(new LinkType(InterfaceRealization.class));
        linkTypes.add(new LinkType(Association.class));

        DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(linkTypes);
        info.setDepthLimited(true);
        info.setCreateContainment(false);
        info.setDepthLimit(3);

        PresentationElement view = mdp.getProject().getDiagram(diagram);
        DisplayRelatedSymbols.displayRelatedSymbols(view, info);
    }
}
