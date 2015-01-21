package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.adl2aml.utils.UMLUtils;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject extends MDProject
{
    public Logger logger = Logger.getRootLogger();

    private Project mdProject = null;
    public String projectLocation = "AMLBaseProject.mdzip";
    public String defaultRootPackageName = "AML";

    public Package rootPackage = null;

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

    public void init()
    {
        startSession("Initializing Project");
        //removeAllElements();

        Collection<Package> roots = ModelUtils.findPackageForMatchingName(getProject(), getDefaultRootPackageName());

        if (roots.isEmpty())
            rootPackage = ModelUtils.createPackage(getDefaultRootPackageName(), getProject().getModel());
        else
            rootPackage = roots.iterator().next();

        AU.info("Root Package:" + UMLUtils.printUMLNamedElement(rootPackage));
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
}
