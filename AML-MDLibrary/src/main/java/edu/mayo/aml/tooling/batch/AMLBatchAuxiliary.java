package edu.mayo.aml.tooling.batch;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by dks02 on 12/11/14.
 */
public class AMLBatchAuxiliary
{
    private CommandLine mainClass = null;
    public Logger logger = Logger.getRootLogger();

    public AMLBatchAuxiliary(CommandLine main)
    {
        mainClass = main;
    }

    public void removeExistingPackages(Project project)
    {
        try
        {
            ModelElementsManager mm = ModelElementsManager.getInstance();
            Collection<Package> allPkgs = ModelUtils.getAllPackages(project);
            for (Package pkg : allPkgs)
                if (pkg.canBeDeleted()) {
                    logger.info("\n####################\nRemoving:[" + pkg.canBeDeleted() + "] " + pkg.getName());
                    mm.removeElement(pkg);
                }

            Collection<Profile> allProfiles = ModelUtils.getAllProfiles(project);
            for (Profile profile : allProfiles)
                if (profile.canBeDeleted()) {
                    logger.info("\n####################\nRemoving:[" + profile.canBeDeleted() + "] " + profile.getName());
                    mm.removeElement(profile);
                }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}
