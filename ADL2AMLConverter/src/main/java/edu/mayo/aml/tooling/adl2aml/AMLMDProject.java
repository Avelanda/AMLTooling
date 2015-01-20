package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.magicdraw.core.Project;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLMDProject
{
    public Logger logger = Logger.getRootLogger();

    private Project mdProject = null;
    private String projectLocation = "AMLBaseProject.mdzip";

    public AMLMDProject()
    {
        mdProject = ProjectUtils.getProjectAtLocation(projectLocation);

        if (mdProject == null)
        {
/*
            mdProject = ProjectUtils.createProject();
            ProjectUtils.setActiveProject(mdProject);
            ProjectDescriptorsFactory.createLocalProjectDescriptor(mdProject, ProjectUtils.getDefaultProjectFile());
            ProjectUtils.saveProject(mdProject);
*/
            try
            {
                logger.error("Project does not exists at location:" + (new File(projectLocation)).getCanonicalPath());
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public Project getAMLMDProject()
    {
        return mdProject;
    }
}
