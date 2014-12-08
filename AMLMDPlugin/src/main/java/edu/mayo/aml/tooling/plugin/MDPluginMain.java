package edu.mayo.aml.tooling.plugin;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by dks02 on 12/4/14.
 */
public class MDPluginMain extends Plugin
{
    @Override
    public void init()
    {
        ActionsConfiguratorsManager manager = ActionsConfiguratorsManager.getInstance();
        manager.addMainMenuConfigurator(new MainMenuConfigurator(getProjectsActions()));
        manager.addMainMenuConfigurator(new MainMenuConfigurator(getAMLActions()));
    }

    private NMAction getAMLActions() {
        ActionsCategory amlcategory = new ActionsCategory(null, "AMLObjects");
        // this call makes submenu.
        amlcategory.setNested(true);

		/*
		 * Add new action to the category.
		 */
        amlcategory.addAction(new NMAction("Add Package", "Add Package", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                javax.swing.JOptionPane.showMessageDialog(null, "This will create a Package");
            }
        });

        return amlcategory;
    }

    private NMAction getProjectsActions()
    {
        ActionsCategory category = new ActionsCategory(null, "Projects");
        // this call makes submenu.
        category.setNested(true);

		/*
		 * Add new action to the category.
		 */
        category.addAction(new NMAction("New Project","New Project", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Application.getInstance().getProjectsManager().createProject();
            }
        });

        /*
         * Close project
         */
        category.addAction(new NMAction("Close Project","Close Project", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Application.getInstance().getProjectsManager().closeProject();
            }
        });

        /*
         * Change active project.
         */
        category.addAction(new NMAction("Change Active Project","Change Active Project", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ProjectsManager manager = Application.getInstance().getProjectsManager();
                for (Project project : manager.getProjects())
                {
                    if (!manager.isProjectActive(project))
                    {
                        manager.setActiveProject(project);
                        break;
                    }
                }
            }
        });

        /*
         *  Show project location.
         */
        category.addAction(new NMAction("Show Location","Show Location", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String locations = "";
                Project prj = Application.getInstance().getProjectsManager().getActiveProject();
                List<ProjectDescriptor> projectDescriptors = ProjectDescriptorsFactory.getAvailableDescriptorsForProject(prj);
                for (int i = projectDescriptors.size() - 1; i >= 0; --i)
                {
                    URI uri = projectDescriptors.get(i).getURI();
                    if (uri != null)
                    {
                        String location = uri.toString();
                        if (location != null)
                        {
                            locations += location + "\n";
                        }
                    }
                }
                System.out.println("locations = " + locations);
                JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider().getDialogParent(), locations, "Location",1);
            }
        });

        /*
         * Save all project.
         */
        category.addAction(new NMAction("Save All","Save All", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
                for (Project project : projectsManager.getProjects())
                {
                    for (ProjectDescriptor projectDescriptor : ProjectDescriptorsFactory.getAvailableDescriptorsForProject(project))
                    {
                        projectsManager.saveProject(projectDescriptor, false);
                    }
                }
            }
        });

        /*
         * Save all project in silent mode.
         */
        category.addAction(new NMAction("Silent Save All","Silent Save All", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
                for (Project project : projectsManager.getProjects())
                {
                    for (ProjectDescriptor projectDescriptor : ProjectDescriptorsFactory.getAvailableDescriptorsForProject(project))
                    {
                        projectsManager.saveProject(projectDescriptor, true);
                    }
                }
            }
        });

        /*
         * Load from location.
         */
        category.addAction(new NMAction("Load from Location","Load from Location", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String location = JOptionPane.showInputDialog("Enter location:");
                if (location != null)
                {
                    File file = new File(location);
                    if (file.exists())
                    {
                        ProjectDescriptor des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
                        Application.getInstance().getProjectsManager().loadProject(des, true);
                    }
                }
            }
        });

        /*
         * Save project to localtion.
         */
        category.addAction(new NMAction("Save into Location","Save into Location", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String location = JOptionPane.showInputDialog("Enter location:");
                if (location != null)
                {
                    ProjectDescriptor des = ProjectDescriptorsFactory.createLocalProjectDescriptor(Application.getInstance().getProjectsManager().getActiveProject(), new File(location));
                    Application.getInstance().getProjectsManager().saveProject(des, true);
                }
            }
        });

        /*
         * Reload project.
         */
        category.addAction(new NMAction("Reload current project","Reload current project", null, null)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
                ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.getDescriptorForProject(projectsManager.getActiveProject());
                projectsManager.loadProject(projectDescriptor,true);
            }
        });

        return category;
    }

    @Override
    public boolean close()
    {
        return true;
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }

    public class MainMenuConfigurator implements AMConfigurator
    {

        String AMLMenu="AML";

        /**
         * Action will be added to manager.
         */
        private NMAction action;

        /**
         * Creates configurator.
         * @param action action to be added to main menu.
         */
        public MainMenuConfigurator(NMAction action)
        {
            this.action = action;
        }

        /**
         * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
         *  Methods adds action to given manager Examples category.
         */
        @Override
        public void configure(ActionsManager mngr)
        {
            // searching for Examples action category
            ActionsCategory category = (ActionsCategory) mngr.getActionFor(AMLMenu);

            if( category == null )
            {
                // creating new category
                category = new MDActionsCategory(AMLMenu,AMLMenu);
                category.setNested(true);
                mngr.addCategory(category);
            }
            category.addAction(action);
        }

        @Override
        public int getPriority()
        {
            return AMConfigurator.MEDIUM_PRIORITY;
        }
    }
}