package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.adl2aml.utils.UMLUtils;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.TermBindingItem;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject extends MDProject
{
    public Logger logger = Logger.getRootLogger();

    private Project mdProject = null;
    public String projectLocation = "AMLBaseProject.mdzip";
    public String defaultRootPackageName = "AML";

    public String defaultTermsPackageName = "Terms";
    public String sctTermsPackageName = "SNOMED-CT";
    public String loincTermsPackageName = "LOINC";
    public String otherTermsPackageName = "OtherTerms";

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

    String[] imports =
                {   "TerminologyProfile.mdzip",
                    "ReferenceModelProfile.mdzip"
                };

    HashMap<String, Object> projectElements = new HashMap<String, Object>();

    public AMLMDProject()
    {
        mdProject = ProjectUtils.getProjectAtLocation(projectLocation);

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
        return defaultRootPackageName;
    }

    public String getDefaultTermsPackageName()
    {
        return defaultTermsPackageName;
    }

    public void init()
    {
        startSession("Initializing Project");
        removeAllElements();
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        try
        {
            for (String imp : imports)
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
        snomedCTTermsPackage = ModelUtils.createPackage(sctTermsPackageName, termsPackage);
        loincTermsPackage = ModelUtils.createPackage(loincTermsPackageName, termsPackage);
        otherTermsPackage = ModelUtils.createPackage(otherTermsPackageName, termsPackage);

        HashMap<String, Object> tagValues = new HashMap<String, Object>();

        try
        {
            snomedctTermsDiag = ModelUtils.createDiagram(sctTermsPackageName,
                                           DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                           snomedCTTermsPackage);

            tagValues.put("identifierURIPattern", "snomed-uri-pattern");
            tagValues.put("uri", "http://snomed.org");

            snomedctIds = ModelUtils.createEnumeration(sctTermsPackageName +"-ID",
                    snomedCTTermsPackage,
                    "TerminologyProfile",
                    "ScopedIdentifier",
                    tagValues);

            DiagramPresentationElement sctdpe = mdProject.getDiagram(snomedctTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(snomedctIds, sctdpe);

            loincTermsDiag = ModelUtils.createDiagram(loincTermsPackageName,
                                            DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                            loincTermsPackage);

            tagValues.put("identifierURIPattern", "loinc-uri-pattern");
            tagValues.put("uri", "http://loinc.org");

            loincIds = ModelUtils.createEnumeration(loincTermsPackageName +"-ID",
                    loincTermsPackage,
                    "TerminologyProfile",
                    "ScopedIdentifier",
                    tagValues);

            DiagramPresentationElement loincdpe = mdProject.getDiagram(loincTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(loincIds, loincdpe);

            otherTermsDiag = ModelUtils.createDiagram(otherTermsPackageName,
                                            DiagramTypeConstants.UML_CLASS_DIAGRAM,
                                            otherTermsPackage);

            tagValues.put("identifierURIPattern", "other-uri-pattern");
            tagValues.put("uri", "http://other.org");

            otherIds = ModelUtils.createEnumeration(otherTermsPackageName +"-ID",
                    otherTermsPackage,
                    "TerminologyProfile",
                    "ScopedIdentifier",
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

            Package termPkg = getPackageForTermID(termId);

            startSession("Create Concept Reference");
            HashMap<String, Object> tagValues = new HashMap<String, Object>();
            tagValues.put("id", code);
            tagValues.put("uri", termId);

            cls = ModelUtils.createClass(code, termPkg, "TerminologyProfile", "ConceptReference", tagValues);

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

}
