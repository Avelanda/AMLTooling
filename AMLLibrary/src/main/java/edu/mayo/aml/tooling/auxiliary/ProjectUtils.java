package edu.mayo.aml.tooling.auxiliary;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import edu.mayo.aml.tooling.batch.AMLBatchEnvironment;

import java.io.File;
import java.io.IOException;

/**
 * Created by dks02 on 12/11/14.
 */
public class ProjectUtils
{
    public static Project getProjectAtLocation(String location)
    {
        ProjectsManager pm = Application.getInstance().getProjectsManager();
        File file = new File(location);
        Project project = null;
        ProjectDescriptor des = null;
        if (file.exists())
        {
            des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
            pm.loadProject(des, true);
            project = pm.getActiveProject();
        }
        else
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            project = pm.createProject();
        }

        return project;
    }

    public static Project getProject()
    {
        AMLBatchEnvironment env = new AMLBatchEnvironment();
        String fileName = env.getProjectFileName();
        return getProjectAtLocation(fileName);
    }

    public static void saveProject(Project project)
    {
        if (project == null)
            return;

        ProjectsManager pm = Application.getInstance().getProjectsManager();
        ProjectDescriptor des = ProjectDescriptorsFactory.getDescriptorForProject(project);
        pm.saveProject(des, false);
    }
}
