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
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.adl2aml.utils.UMLUtils;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerm;
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.openehr.jaxb.am.TermBindingItem;
import org.openehr.jaxb.rm.StringDictionaryItem;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject extends MDProject
{
    public Logger logger = Logger.getRootLogger();

    private Project mdProject = null;

    public Package rootPackage = null;
    public Package termsPackage = null;

    public Package snomedCTTermsPackage = null;
    public Package loincTermsPackage = null;
    public Package otherTermsPackage = null;

    public Diagram snomedctTermsDiag = null;
    public Diagram loincTermsDiag = null;
    public Diagram otherTermsDiag = null;

    public Enumeration snomedctIds = null;
    public Enumeration loincIds = null;
    public Enumeration otherIds = null;

    HashMap<String, Object> projectElements = new HashMap<String, Object>();

    public AMLMDProject()
    {
        mdProject = ProjectUtils.getProjectAtLocation(AMLConstants.projectLocation);

        if (mdProject == null)
        {

            mdProject = ProjectUtils.createProject();
            ProjectUtils.setActiveProject(mdProject);
            ProjectDescriptorsFactory.createLocalProjectDescriptor(mdProject, ProjectUtils.getDefaultProjectFile());
            save();
        }
    }

    public String getDefaultRootPackageName()
    {
        return AMLConstants.defaultRootPackageName;
    }

    public String getDefaultTermsPackageName()
    {
        return AMLConstants.defaultTermsPackageName;
    }

    public void init()
    {
        startSession("Initializing Project");
        removeAllElements();
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        try
        {
            for (String imp : AMLConstants.imports)
            {
                File file = new File(imp);
                ProjectDescriptor des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
                projectsManager.useModule(mdProject, des);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Collection<Package> roots = ModelUtils.findPackageForMatchingName(getProject(), getDefaultRootPackageName());

        if (roots.isEmpty())
            rootPackage = ModelUtils.createPackage(getDefaultRootPackageName(), getProject().getModel());
        else
            rootPackage = roots.iterator().next();

        AU.info("Root Package:" + UMLUtils.printUMLNamedElement(rootPackage));

        termsPackage = ModelUtils.createPackage(getDefaultTermsPackageName(), rootPackage);
        snomedCTTermsPackage = ModelUtils.createPackage(AMLConstants.sctTermsPackageName, termsPackage);
        loincTermsPackage = ModelUtils.createPackage(AMLConstants.loincTermsPackageName, termsPackage);
        otherTermsPackage = ModelUtils.createPackage(AMLConstants.otherTermsPackageName, termsPackage);

        HashMap<String, Object> tagValues = new HashMap<String, Object>();

        try
        {
            snomedctTermsDiag = ModelUtils.createDiagram(AMLConstants.sctTermsPackageName,
                                           DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                           snomedCTTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "snomed-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://snomed.org");

            snomedctIds = ModelUtils.createEnumeration(AMLConstants.sctTermsPackageName +"-ID",
                    snomedCTTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement sctdpe = mdProject.getDiagram(snomedctTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(snomedctIds, sctdpe);

            loincTermsDiag = ModelUtils.createDiagram(AMLConstants.loincTermsPackageName,
                                            DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                            loincTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "loinc-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://loinc.org");

            loincIds = ModelUtils.createEnumeration(AMLConstants.loincTermsPackageName +"-ID",
                    loincTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement loincdpe = mdProject.getDiagram(loincTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(loincIds, loincdpe);

            otherTermsDiag = ModelUtils.createDiagram(AMLConstants.otherTermsPackageName,
                                            DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                            otherTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "other-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://other.org");

            otherIds = ModelUtils.createEnumeration(AMLConstants.otherTermsPackageName +"-ID",
                    otherTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement othdpe = mdProject.getDiagram(otherTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(otherIds, othdpe);
        }
        catch (ReadOnlyElementException e)
        {
            e.printStackTrace();
        }

        closeSession();
    }

    public void save()
    {
        Preconditions.checkNotNull(getProject());
        startSession("Saving");
        ProjectUtils.saveProject(mdProject);
        closeSession();
    }

    public Collection<Package> getRootPackages()
    {
        Preconditions.checkNotNull(getProject());
        Collection<Package> nested = getProject().getModel().getNestedPackage();

        return nested;
    }

    public Project getProject()
    {
        return mdProject;
    }

    public void removeAllElements()
    {
        Preconditions.checkNotNull(getProject());
        ModelUtils.removeAllPackages(getProject());
        ModelUtils.removeAllProfiles(getProject());
    }

    private Package getPackageForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedCTTermsPackage;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincTermsPackage;

        return otherTermsPackage;
    }

    private Enumeration getEnumerationContainerForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedctIds;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincIds;

        return otherIds;
    }

    private Diagram getDiagramForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedctTermsDiag;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincTermsDiag;

        return otherTermsDiag;
    }

    public void createArchetype(Archetype archetype)
    {
        startSession("Creating Archetype");
        try
        {
            // Archetype creation Begin
            String adlArchId = archetype.getArchetypeId().getValue();
            String amlArchId = AMLWriterHelper.getAMLArchetypeNameFromADLArchetypeName(adlArchId);
            AU.debug("Creating AML Archetype : " + amlArchId);

            Package archPkg = ModelUtils.createPackage(amlArchId,
                                                       rootPackage,
                                                       AMLConstants.CONSTRAINT_PROFILE,
                                                       AMLConstants.STEREOTYPE_ARCHETYPE,
                                                       null);

            Diagram archDiag = ModelUtils.createDiagram(amlArchId, DiagramTypeConstants.UML_CLASS_DIAGRAM, archPkg);
            DiagramPresentationElement pe = getProject().getDiagram(archDiag);
            PresentationElementsManager.getInstance().createShapeElement(archPkg, pe);

            String archNameKey = archetype.getDefinition().getNodeId();
            String archVersionName = getTermDefinitionText(archetype.getOntology().getTermDefinitions(), archNameKey);

            Class archCls = ModelUtils.createClass(archVersionName,
                                                    archPkg,
                                                    AMLConstants.CONSTRAINT_PROFILE,
                                                    AMLConstants.STEREOTYPE_ARCHETYPE_VERSION,
                                                    null);
            PresentationElementsManager.getInstance().createShapeElement(archCls, pe);

        }
        catch(ReadOnlyElementException roe)
        {
            roe.printStackTrace();
        }

        closeSession();
    }

    public Class createConceptReference(TermBindingItem item)
    {
        Class cls = null;
        try
        {

            String ontId = item.getValue().getTerminologyId().getValue();
            String code = item.getValue().getCodeString();

            // if code has any non-alphanumeric
            if (code.matches("^.*[^a-zA-Z0-9 ].*$"))
                return null;

            String termId = ontId + code;

            Object obj = projectElements.get(termId);

            if ((obj != null)&&(obj instanceof Class))
                return ((Class)cls);

            startSession("Create Concept Reference");

            Package termPkg = getPackageForTermID(termId);

            HashMap<String, Object> tagValues = new HashMap<String, Object>();
            tagValues.put(AMLConstants.TAG_ID, code);
            tagValues.put(AMLConstants.TAG_URI, termId);

            cls = ModelUtils.createClass(code, termPkg,
                                        AMLConstants.TERMINOLOGY_PROFILE,
                                        AMLConstants.STEREOTYPE_CONCEPT_REFERENCE,
                                        tagValues);

            projectElements.put(termId, cls);

            Diagram termDiag = getDiagramForTermID(termId);
            DiagramPresentationElement cdpe = mdProject.getDiagram(termDiag);
            PresentationElementsManager.getInstance().createShapeElement(cls, cdpe);

            ModelUtils.createEnumerationLiteral(code, getEnumerationContainerForTermID(termId));

            closeSession();
        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        return cls;
    }

    public String getTermDefinitionText(List<CodeDefinitionSet> termDefinitions, String key)
    {
        Preconditions.checkNotNull(termDefinitions);
        Preconditions.checkNotNull(key);

        for (CodeDefinitionSet cds : termDefinitions)
            for (ArchetypeTerm term : cds.getItems())
            {
                if (term.getCode().equals(key))
                    for (StringDictionaryItem dict : term.getItems())
                        if (dict.getId().equalsIgnoreCase(AMLConstants.ATTRIBUTE_TEXT))
                            return dict.getValue();
            }
        return key;
    }
}
