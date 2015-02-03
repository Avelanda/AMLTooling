package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.Archetype;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by dks02 on 1/21/15.
 */
public abstract class MDProject
{
    protected Project mdProject = null;
    protected boolean inSession = false;

    private HashMap<String, Vector<String>> rmClassesInDiag = new HashMap<String, Vector<String>>();
    private HashMap<String, Class> rmClasses = new HashMap<String, Class>();

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

    public boolean isContainedInDiagram(String diagName, String elementId)
    {
        if (AU.isNull(diagName))
            return false;

        if (AU.isNull(elementId))
            return false;

        Vector<String> rms = rmClassesInDiag.get(diagName);

        if (rms == null)
            return false;

        return rms.contains(elementId);
    }

    public void registerRMClassUsageInDiagram(String diagName, String rmName)
    {
        if (AU.isNull(diagName))
            return;

        if (AU.isNull(rmName))
            return;

        Vector<String> rms = rmClassesInDiag.get(diagName);

        if (rms == null)
            rms = new Vector<String>();

        if (!rms.contains(rmName))
            rms.add(rmName);

        rmClassesInDiag.put(diagName, rms);
    }

    public void registerRMClass(String name, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class rmClass)
    {
        if (AU.isNull(name))
            return;

        if (rmClass == null)
            return;

        rmClasses.put(name, rmClass);
    }

    public Class getRMClass(String name)
    {
        if (AU.isNull(name))
            return null;

        return rmClasses.get(name);
    }

    public void setProject(Project project)
    {
        this.mdProject = project;
    }

    public void save()
    {
        Preconditions.checkNotNull(getProject());
        checkSession("Saving Project");
        ProjectUtils.saveProject(mdProject);
    }

    public void checkSession(String task)
    {
        if (!SessionManager.getInstance().isSessionCreated())
            SessionManager.getInstance().createSession(task);
    }

    public void closeSession()
    {
        if (!inSession)
            return;

        SessionManager.getInstance().closeSession();
        inSession = false;
    }

    public abstract Package getArchetypeLibraryPackage();
    public abstract Package getReferenceModelPackage();
    public abstract Class getOrCreateAMLArchetype(String archetypeId);
    public abstract Class getOrCreateAMLArchetype(Archetype archetype);
}
