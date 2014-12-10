package edu.mayo.aml.tooling.batch;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
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

import java.io.File;
import java.io.IOException;

/**
 * AML Batch using MD OpenAPIs
 *
 */
public class AMLBatchMain extends CommandLine
{
    public String projectFile = "/Users/dks02/A123/MagicDrawWS/TestUMLByDeepak.mdzip";
    public static void main( String[] args )
    {
        System.out.println( "Batch STARTED" );
        new AMLBatchMain().launch(args);
    }

    @Override
    protected byte execute()
    {
        ProjectsManager pm = Application.getInstance().getProjectsManager();
        File file = new File(projectFile);
        Project project = null;
        ProjectDescriptor des = null;
        System.out.println( "Batch 1" );
        if (file.exists())
        {
            System.out.println( "Batch 2" );
            des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
            pm.loadProject(des, true);
            project = pm.getActiveProject();
        }
        else
        {
            System.out.println("Batch 3");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            project = pm.createProject();
        }
            SessionManager.getInstance().createSession("Creating Stuff");
            ElementsFactory ef = project.getElementsFactory();
            ModelElementsManager mm = ModelElementsManager.getInstance();

//            try
//            {
//                for (Package e : ModelHelper.findInParent(project.getModel(), );)
//                    mm.removeElement(e);
//            }
//            catch (ReadOnlyElementException e1)
//            {
//                    e1.printStackTrace();
//            }

            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package np = ef.createPackageInstance();
            np.setName("TestDeepak");
            np.setOwner(project.getModel());

            com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class clsA = ef.createClassInstance();
            clsA.setName("Person");

            Property property1 = ef.createPropertyInstance();
            property1.setName("name");
            property1.setVisibility(VisibilityKindEnum.PUBLIC);

            Property property2 = ef.createPropertyInstance();
            property2.setName("id");
            property2.setVisibility(VisibilityKindEnum.PRIVATE);

            Property property3 = ef.createPropertyInstance();
            property3.setName("enrolledIn");
            property3.setVisibility(VisibilityKindEnum.PUBLIC);

            Classifier stringType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "String", null);
            Classifier integerType = ModelHelper.findDataTypeFor(Application.getInstance().getProject(), "Integer", null);

            property1.setType(stringType);
            property2.setType(integerType);
            property3.setType(stringType);

            Class clsB = ef.createClassInstance();
            clsB.setName("ClassB");

            Relationship rel = ef.createAssociationInstance();
            rel.set_representationText("AdjacentTo");

            rel.setOwner(clsA);
            ModelHelper.setClientElement(rel, clsB);
            ModelHelper.setSupplierElement(rel, clsA);


            try
            {
                mm.addElement(clsA, np);
                mm.addElement(property1, clsA);

                mm.addElement(property2, clsB);
                mm.addElement(property3, clsB);

                mm.addElement(rel, np);

                Diagram cd = mm.createDiagram(DiagramTypeConstants.UML_CLASS_DIAGRAM, np);
                cd.setName(np.getName() + "_classDiagram");

            }
            catch (ReadOnlyElementException e1)
            {
                e1.printStackTrace();
            }

            clsB.setOwner(np);

            SessionManager.getInstance().closeSession();

            des = ProjectDescriptorsFactory.createLocalProjectDescriptor(project, file);

            pm.saveProject(des, false);


        System.out.println( "Batch ENDED" );
        return 0;
    }
}
