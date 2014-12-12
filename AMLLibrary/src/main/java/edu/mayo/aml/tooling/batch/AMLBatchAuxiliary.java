package edu.mayo.aml.tooling.batch;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;

import java.util.Collection;

/**
 * Created by dks02 on 12/11/14.
 */
public class AMLBatchAuxiliary
{
    AMLBatchMain mainClass = null;

    public AMLBatchAuxiliary(AMLBatchMain main)
    {
        mainClass = main;
    }

    private void log(String msg)
    {
        mainClass.log(msg);
    }

    public void removeExistingPackages(Project project)
    {
        try
        {
            ModelElementsManager mm = ModelElementsManager.getInstance();
            Collection<Package> allPkgs = ModelUtils.getAllPackages(project);
            for (Package pkg : allPkgs)
                if (pkg.canBeDeleted()) {
                    log("\n####################\nRemoving:[" + pkg.canBeDeleted() + "] " + pkg.getName());
                    mm.removeElement(pkg);
                }

            Collection<Profile> allProfiles = ModelUtils.getAllProfiles(project);
            for (Profile profile : allProfiles)
                if (profile.canBeDeleted()) {
                    log("\n####################\nRemoving:[" + profile.canBeDeleted() + "] " + profile.getName());
                    mm.removeElement(profile);
                }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}
