package edu.mayo.aml.tooling.batch;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import edu.mayo.aml.tooling.auxiliary.ProjectUtils;
import edu.mayo.aml.tooling.auxiliary.Utils;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * AML Batch using MD OpenAPIs
 *
 */
public class AMLBatchMainExampleProject extends CommandLine
{
    public Logger logger = Logger.getRootLogger();

    public static void main( String[] args )
    {
        AMLBatchMainExampleProject abm = new AMLBatchMainExampleProject();
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
            ModelUtils.removeAllPackages(project);
            ModelUtils.removeAllProfiles(project);

            Package np = ModelUtils.createPackage("TestDeepak-" + Utils.getCurrentTimeStampAsSuffix(), project.getModel());

            Class clsExists = createAClass("Exists", np);
            Class clsHuman = createAClass("Human", np);
            Class clsFish = createAClass("Fish", np);
            Class clsPerson = createPersonClass(np);
            Class clsAddress = createAddressClass(np);

            Generalization generalization = ModelUtils.createGeneralization(clsHuman, clsPerson);

            // Operation for Person Class
            Classifier integerType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Integer", null);
            Parameter idParam = ModelUtils.createParameter("id", integerType, ParameterDirectionKindEnum.IN);
            Parameter returnParam = ModelUtils.createParameter("address", clsAddress,ParameterDirectionKindEnum.RETURN);
            Operation getAddressOp = ModelUtils.createOperation("getAddressById",Arrays.asList(idParam, returnParam), clsPerson);

            Association comp_assoc = ModelUtils.createAssociation("hasAddress", "addressOf", clsPerson, clsAddress, true, AggregationKindEnum.COMPOSITE);
            Association self_assoc = ModelUtils.createAssociation("hasRelatives", "relativesOf", clsPerson, clsPerson, true, AggregationKindEnum.SHARED);

            Relationship dep = ModelUtils.createDependency("exists", "dependsOn", clsExists, clsPerson, np);

            Profile popProfile = ModelUtils.createProfile("Population", project.getModel());

            Classifier booleanType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Boolean", null);

            Stereotype stereotypeMammal = ModelUtils.createStereotype("Mammal", "Class", project, popProfile);
            ModelUtils.addStereotypeTag(stereotypeMammal, "doesFemaleFeedMilk", booleanType);

            // Apply Stereotype
            if (StereotypesHelper.canApplyStereotype(clsHuman, stereotypeMammal))
            {
                StereotypesHelper.addStereotype(clsHuman, stereotypeMammal);
                StereotypesHelper.setStereotypePropertyValue(clsHuman, stereotypeMammal, "doesFemaleFeedMilk", "true");
            }

            // Apply Stereotype
            if (StereotypesHelper.canApplyStereotype(clsFish, stereotypeMammal))
            {
                StereotypesHelper.addStereotype(clsFish, stereotypeMammal);
                StereotypesHelper.setStereotypePropertyValue(clsFish, stereotypeMammal, "doesFemaleFeedMilk", "false");
            }

            Diagram clsDiag = ModelUtils.createDiagram(np.getName() + "_classDiagram", DiagramTypeConstants.UML_CLASS_DIAGRAM, np);
            DiagramPresentationElement cdpe = project.getDiagram(clsDiag);

            PresentationElementsManager.getInstance().createShapeElement(clsExists, cdpe);
            PresentationElementsManager.getInstance().createShapeElement(clsHuman, cdpe);
            PresentationElementsManager.getInstance().createShapeElement(clsPerson, cdpe);
            PresentationElementsManager.getInstance().createShapeElement(clsFish, cdpe);
            PresentationElementsManager.getInstance().createShapeElement(clsAddress, cdpe);

            Diagram profileDiag = ModelUtils.createDiagram(np.getName() + "_profileDiagram", DiagramTypeConstants.UML_PROFILE_DIAGRAM, np);
            DiagramPresentationElement dpe = project.getDiagram(profileDiag);

            PresentationElementsManager.getInstance().createShapeElement(stereotypeMammal, dpe);

            SessionManager.getInstance().closeSession();

        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        ProjectUtils.saveProject(project);
        return 0;
    }

    private Class createAClass(String name, Package pkg)
    {
        try
        {
            Class cls = ModelUtils.createClass(name, pkg);

            return cls;
        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        return null;
    }

    private Class createPersonClass(Package pkg)
    {
        try
        {
            Classifier stringType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "String", null);
            Classifier integerType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Integer", null);

            Class clsP = ModelUtils.createClass("Person", pkg);
            Property property11 = ModelUtils.createProperty("name", stringType, VisibilityKindEnum.PUBLIC, clsP);
            Property property12 = ModelUtils.createProperty("SSN", stringType, VisibilityKindEnum.PRIVATE, clsP);
            Property property13 = ModelUtils.createProperty("id", integerType, VisibilityKindEnum.PRIVATE, clsP);

            return clsP;
        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        return null;
    }

    private Class createAddressClass(Package pkg)
    {
        try
        {
            Class clsA = ModelUtils.createClass("Address", pkg);

            Classifier stringType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "String", null);
            Classifier integerType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Integer", null);


            Property property20 = ModelUtils.createProperty("id", integerType, VisibilityKindEnum.PUBLIC, clsA);
            Property property21 = ModelUtils.createProperty("houseNumber", integerType, VisibilityKindEnum.PUBLIC, clsA);
            Property property22 = ModelUtils.createProperty("street", stringType, VisibilityKindEnum.PUBLIC, clsA);
            Property property23 = ModelUtils.createProperty("city", stringType, VisibilityKindEnum.PUBLIC, clsA);
            Property property24 = ModelUtils.createProperty("state", stringType, VisibilityKindEnum.PUBLIC, clsA);
            Property property25 = ModelUtils.createProperty("zip", integerType, VisibilityKindEnum.PUBLIC, clsA);

            return clsA;
        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        return null;
    }

}
