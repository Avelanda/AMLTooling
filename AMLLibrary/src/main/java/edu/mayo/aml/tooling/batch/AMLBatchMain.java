package edu.mayo.aml.tooling.batch;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.impl.ElementsFactory;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import edu.mayo.aml.tooling.auxiliary.Utils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * AML Batch using MD OpenAPIs
 *
 */
public class AMLBatchMain extends CommandLine
{
    public Logger logger = Logger.getRootLogger();
    private AMLBatchAuxiliary aux = new AMLBatchAuxiliary(this);


    public static void main( String[] args )
    {
        AMLBatchMain abm = new AMLBatchMain();
        abm.log( "Batch STARTED" );
        abm.launch(args);
        abm.log("Batch ENDED");
    }

    public void log(String msg)
    {
        this.logger.info(msg);
    }

    @Override
    protected byte execute()
    {
        Project project = ProjectUtils.getProject();

        try
        {
            SessionManager.getInstance().createSession("Creating Stuff");
            // Clean existing Packages
            aux.removeExistingPackages(project);

            Package np = ModelUtils.createPackage("TestDeepak-" + Utils.getCurrentTimeStampAsSuffix(), project.getModel());
            Class clsA = ModelUtils.createClass("Person", np);
            Class clsB = ModelUtils.createClass("Address", np);

            Classifier stringType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "String", null);
            Classifier integerType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Integer", null);

            Property property11 = ModelUtils.createProperty("name", stringType, VisibilityKindEnum.PUBLIC, clsA);
            Property property12 = ModelUtils.createProperty("SSN", stringType, VisibilityKindEnum.PRIVATE, clsA);

            Property property21 = ModelUtils.createProperty("houseNumber", integerType, VisibilityKindEnum.PUBLIC, clsB);
            Property property22 = ModelUtils.createProperty("street", stringType, VisibilityKindEnum.PUBLIC, clsB);
            Property property23 = ModelUtils.createProperty("city", stringType, VisibilityKindEnum.PUBLIC, clsB);
            Property property24 = ModelUtils.createProperty("state", stringType, VisibilityKindEnum.PUBLIC, clsB);
            Property property25 = ModelUtils.createProperty("zip", integerType, VisibilityKindEnum.PUBLIC, clsB);

            Relationship rel = ModelUtils.createRelation("hasAddress", "addressOf", clsA, clsB, true);

            Diagram diag = ModelUtils.createDiagram(np.getName() + "_classDiagram", DiagramTypeConstants.UML_CLASS_DIAGRAM, np);

            SessionManager.getInstance().closeSession();

        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        ProjectUtils.saveProject(project);
        return 0;
    }
}
