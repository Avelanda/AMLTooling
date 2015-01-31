package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.*;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.InterfaceRealization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.adl2aml.utils.UMLUtils;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import org.apache.log4j.Logger;
import org.openehr.jaxb.am.*;

import java.io.File;
import java.util.*;

/**
 * Created by dks02 on 1/27/15.
 */
public class AMLMDProjectHelper
{
    public Logger logger = Logger.getRootLogger();
    private MDProject mdp = null;

    public String getDefaultRootPackageName()
    {
        return AMLConstants.rootPackageName;
    }

    public AMLMDProjectHelper(MDProject mdProject)
    {
        this.mdp = mdProject;
    }

    public Package getRootPackage(String name, boolean create, Package rm)
    {
        if (AU.isNull(name))
            return null;

        mdp.checkSession("Creating Root Package if needed...");
        Collection<Package> roots = ModelUtils.findPackageForMatchingName(mdp.getProject(), name);

        if (!roots.isEmpty())
            return roots.iterator().next();

        if (!create) return null;

        // Create Root package aka 'Archetype Library'
        Package root = ModelUtils.createPackage(getDefaultRootPackageName(),
                                        mdp.getProject().getModel(),
                                        AMLConstants.CONSTRAINT_PROFILE,
                                        AMLConstants.STEREOTYPE_ARCHETYPE_LIBRARY,
                                        null);


        // Add reference model package to show what reference model this
        // archetype library is talking about.
        ModelUtils.addPackageImport(root,
                                    rm,
                                    AMLConstants.CONSTRAINT_PROFILE,
                                    AMLConstants.STEREOTYPE_REFERENCE_MODEL,
                                    null);

        return root;
    }

    public Package getReferenceModelPackage(String name)
    {
        if (AU.isNull(name))
            return null;

        mdp.checkSession("Finding Reference Model Package with Name:" + name);
        Collection<Package> roots = ModelUtils.findPackageForMatchingName(mdp.getProject(), name);

        if (!roots.isEmpty())
            return roots.iterator().next();

        return null;
    }

    public void removeAllElements()
    {
        Preconditions.checkNotNull(mdp.getProject());
        mdp.checkSession("Removing All Elements...");
        ModelUtils.removeAllPackages(mdp.getProject());
        ModelUtils.removeAllProfiles(mdp.getProject());
    }

    public void addUsedModules()
    {
        mdp.checkSession("Adding Use Module...");
        ProjectsManager projectsManager = Application.getInstance().getProjectsManager();
        try
        {
            for (String imp : AMLConstants.imports)
            {
                File file = new File(imp);
                ProjectDescriptor des = ProjectDescriptorsFactory.createProjectDescriptor(file.toURI());
                projectsManager.useModule(mdp.getProject(), des);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Package createArchetypePackage(Archetype archetype, Package parent)
    {
        mdp.checkSession("Creating Archetype Package...");
        // Archetype creation Begin
        String amlArchId = AMLWriterHelper.getArchetypeIdWithoutMinorVersion(archetype);
        AU.debug("Creating AML Archetype Package: " + amlArchId);

        return ModelUtils.createPackage(amlArchId,
                parent,
                AMLConstants.CONSTRAINT_PROFILE,
                AMLConstants.STEREOTYPE_ARCHETYPE,
                null);
    }

    public Diagram createArchetypeDiagram(Package archPkg) throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(archPkg);
        mdp.checkSession("Creating Archetype Diagram...");
        return ModelUtils.createDiagram(archPkg.getName(), DiagramTypeConstants.UML_CLASS_DIAGRAM, archPkg);
    }

    public void addElementToDiagram(Element element, Diagram diagram) throws ReadOnlyElementException
    {
        mdp.checkSession("Adding to the diagram...");
        DiagramPresentationElement pe = mdp.getProject().getDiagram(diagram);
        PresentationElementsManager.getInstance().createShapeElement(element, pe);
    }

    public void displayRelatedInformation(Diagram diagram) throws ReadOnlyElementException
    {
        mdp.checkSession("Display Related Information...");
        Set linkTypes = new HashSet();
        linkTypes.add(new LinkType(Generalization.class));
        linkTypes.add(new LinkType(InterfaceRealization.class));
        //linkTypes.add(new LinkType(Association.class));

        DisplayRelatedSymbolsInfo info = new DisplayRelatedSymbolsInfo(linkTypes);
        info.setDepthLimited(true);
        info.setCreateContainment(false);
        info.setDepthLimit(3);

        PresentationElement view = mdp.getProject().getDiagram(diagram);
        DisplayRelatedSymbols.displayRelatedSymbols(view, info);
    }

    public Class addConstraint(CComplexObject cComplexObject,
                                     EnumerationLiteral language,
                                     Package archPackage,
                                     Diagram diagram,
                                     ArchetypeOntology ontology,
                                     String profile,
                                     String stereotype,
                                     Enumeration localIds)
            throws ReadOnlyElementException
    {
        String nodeId = cComplexObject.getNodeId();
        String name = ADLHelper.getTermDefinitionText(ontology,
                                                    nodeId,
                                                    language.toString());

        HashMap<String, Object> tagValues = new HashMap<String, Object>();
        tagValues.put(AMLConstants.TAG_ID,
                ModelUtils.findEnumerationLiteralInEnumeration(localIds, nodeId));
        tagValues.put(AMLConstants.TAG_LANGUAGE, language);
        tagValues.put(AMLConstants.TAG_DESCRIPTION,
                AMLConstants.DEFAULT_DESCRIPTION);
        tagValues.put(AMLConstants.TAG_SIGN, name);

        Class constCls = ModelUtils.createClass(name,
                archPackage,
                profile,
                stereotype,
                tagValues);

        if (constCls != null)
            addElementToDiagram(constCls, diagram);

        // Add constraints here
        convertComplexDefinition(cComplexObject, language, archPackage, constCls, diagram, ontology, localIds);

        return constCls;
    }

    public void convertComplexDefinition(CComplexObject complexConstraint,
                                         EnumerationLiteral language,
                                         Package archPackage,
                                         Class currentClass,
                                         Diagram diagram,
                                         ArchetypeOntology ontology,
                                         Enumeration localIds)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(complexConstraint);
        Preconditions.checkNotNull(currentClass);

        // If this constraint constraint any RM Class
        // Add "constrains" relationship to the RM Class
        // To find the RM Class, we create a Regex
        // which starts with package name and ends at class name
        String rmClassName = complexConstraint.getRmTypeName();
        if (!AU.isNull(rmClassName))
        {
            //String pathRegex = mdp.getReferenceModelPackage().getQualifiedName() + ".*" +
            //        complexConstraint.getRmTypeName();

            //String pathRegex =  complexConstraint.getRmTypeName();

            String pr = rmClassName;
            if (rmClassName.indexOf("<") != -1)
                pr = rmClassName.split("<")[0];

            Class rmClass = mdp.getRMClass(pr);

            if (rmClass == null)
                rmClass = ModelUtils.findClassWithName(mdp.getReferenceModelPackage(), pr);

            if (rmClass == null)
                AU.warn("Could not find RM Class with names " + pr);
            else
                mdp.registerRMClass(pr, rmClass);

            Generalization generalization = ModelUtils.createGeneralization((Class) rmClass, currentClass);
            ModelUtils.findAndApplyStereotype(generalization,
                                        AMLConstants.CONSTRAINT_PROFILE,
                                        AMLConstants.STEREOTYPE_CONSTRAINS,
                                        null);

            if (diagram != null)
            {
                if (!mdp.isContainedInDiagram(diagram.getName(), pr))
                {
                    addElementToDiagram(rmClass, diagram);
                    mdp.registerRMClassUsageInDiagram(diagram.getName(), pr);
                }
            }
        }

        // Process Attributes of complex constraint
        List<CAttribute> attributes = complexConstraint.getAttributes();

        for (CAttribute attribute : attributes)
        {
            String rmAttributeName = attribute.getRmAttributeName();

            for (CObject co : attribute.getChildren())
            {
                if (co instanceof CComplexObject)
                {
                    Class constraintCls = addConstraint((CComplexObject) co,
                            language,
                            archPackage,
                            diagram,
                            ontology,
                            AMLConstants.CONSTRAINT_PROFILE,
                            AMLConstants.STEREOTYPE_COMPLEXOBJECTCONSTRAINT,
                            localIds);

                    String relationName = UMLUtils.createAMLAssociationEndName(constraintCls.getName());

                    String multiplicity = ADLHelper.getMultiplicityInterval(attribute.getExistence());
                    boolean isCollectionConstraint = UMLUtils.isMultipleMultiplicity(multiplicity);

                    String attributeConstraintStereotype = (isCollectionConstraint)?
                                                    AMLConstants.STEREOTYPE_ATTRIBUTECOLLECTIONCONSTRAINT:
                                                    AMLConstants.STEREOTYPE_SINGULARATTRIBUTECONSTRAINT;

                    Association relToConst = ModelUtils.createAssociation("",
                                                                          relationName,
                                                                          "1",
                                                                           multiplicity,
                                                                          currentClass,
                                                                          constraintCls,
                                                                          false,
                                                                          true,
                                                                          AggregationKindEnum.NONE,
                                                                          AMLConstants.CONSTRAINT_PROFILE,
                                                                          attributeConstraintStereotype,
                                                                            null);

                }
            }
       }

        // Process AttributeTuples of complex constraint
        //complexConstraint.getAttributeTuples();

    }
}
