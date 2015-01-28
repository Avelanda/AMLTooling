package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;

/**
 * Created by dks02 on 1/21/15.
 */
public abstract class MDProject
{
    protected Project mdProject = null;
    protected boolean inSession = false;

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

    public void setProject(Project project)
    {
        this.mdProject = project;
    }

    public void save()
    {
        Preconditions.checkNotNull(getProject());
        startSession("Saving Project");
        ProjectUtils.saveProject(mdProject);
        closeSession();
    }

    public void startSession(String task)
    {
        if (inSession)
            closeSession();

        SessionManager.getInstance().createSession(task);
        inSession = true;
    }

    public void closeSession()
    {
        if (!inSession)
            return;

        SessionManager.getInstance().closeSession();
        inSession = false;
    }
}
