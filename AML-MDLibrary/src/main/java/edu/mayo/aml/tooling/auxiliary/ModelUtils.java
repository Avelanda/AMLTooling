package edu.mayo.aml.tooling.auxiliary;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.ui.actions.PresentationElementConfigurator;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ElementsFactory;

import java.util.Arrays;
import java.util.Collection;

import static com.nomagic.magicdraw.openapi.uml.ModelElementsManager.getInstance;

/**
 * Created by dks02 on 12/11/14.
 */
public class ModelUtils
{
    public static Collection<Package> getAllPackages(Project project)
    {
        java.lang.Class[] pc = {Package.class};
        return (Collection<Package>) ModelHelper.getElementsOfType(project.getModel(), pc, false);
    }

    public static Collection<Profile> getAllProfiles(Project project)
    {
        java.lang.Class[] pc = {Profile.class};
        return (Collection<Profile>) ModelHelper.getElementsOfType(project.getModel(), pc, false);
    }


    public static Package createPackage(String packageName, Element parent)
    {
        Preconditions.checkNotNull(packageName);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Package np = ef.createPackageInstance();
        np.setName(packageName);
        np.setOwner(parent);

        return np;
    }

    public static Class createClass(String name, Element parent)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Class cls = ef.createClassInstance();
        cls.setName(name);

        if (parent != null)
            getInstance().addElement(cls, parent);

        return cls;
    }

    public static Property createProperty(String name, Classifier type, VisibilityKind visibility, Element container)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Property property = ef.createPropertyInstance();
        property.setName(name);

        if (type != null)
            property.setType(type);

        if (visibility != null)
            property.setVisibility(visibility);

        if (container != null)
            getInstance().addElement(property, container);

        return property;
    }

    public static Association createAssociation(String supplierEndName, String clientEndName, Element supplier, Element client, boolean isNavigable, AggregationKind aggregation)
    {
        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Association rel = ef.createAssociationInstance();

        rel.setName(supplierEndName);

        if (supplier != null)
        {
            rel.setOwner(supplier);
            ModelHelper.setSupplierElement(rel, supplier);
        }

        if (client != null)
            ModelHelper.setClientElement(rel, client);

        Property propType1 = ModelHelper.getFirstMemberEnd(rel);

        if (!Strings.isNullOrEmpty(supplierEndName.trim()))
            propType1.setName(supplierEndName);

        Property propType2 = ModelHelper.getSecondMemberEnd(rel);

        if (!Strings.isNullOrEmpty(clientEndName.trim()))
            propType2.setName(clientEndName);

        ModelHelper.setMultiplicity("1", propType1);
        ModelHelper.setMultiplicity("0..*", propType2);

        ModelHelper.setNavigable(propType1, isNavigable);
        ModelHelper.setNavigable(propType2, isNavigable);

        if (aggregation != null)
            propType1.setAggregation(aggregation);

        return rel;
    }

    public static Parameter createParameter(String name, Classifier type, ParameterDirectionKind direction)
    {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(direction);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Parameter param = ef.createParameterInstance();
        param.setType(type);
        param.setDirection(direction);
        param.setName(name);

        return param;
    }

    public static Operation createOperation(String name, Collection<Parameter> params, Element owner)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(owner);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Operation operation = ef.createOperationInstance();

        operation.setName(name);
        operation.setOwner(owner);

        if (!params.isEmpty())
            for (Parameter param : params)
            {
                param.setOwner(operation);
                ModelElementsManager.getInstance().addElement(param, operation);
            }

        return null;
    }

    public static Relationship createDependency(String supplierEndName, String clientEndName, Element supplier, Element client, Element owner)
    {
        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Dependency dependency = ef.createDependencyInstance();

        dependency.setName("dependency");
        dependency.setOwner(owner);

        if (supplier != null)
        {
            ModelHelper.setSupplierElement(dependency, supplier);
        }

        if (client != null)
            ModelHelper.setClientElement(dependency, client);

        return dependency;
    }

    public static Diagram createDiagram(String name, String type, Namespace container)
            throws ReadOnlyElementException
    {
        Diagram diag = getInstance().createDiagram(type, container);

        if (!Strings.isNullOrEmpty(name.trim()))
            diag.setName(name);

        return diag;
    }

    public static Generalization createGeneralization(Class baseClass, Class specialiazed)
            throws ReadOnlyElementException
    {
        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Generalization gen = ef.createGeneralizationInstance();
        gen.setGeneral(baseClass);
        gen.setSpecific(specialiazed);

        ModelElementsManager.getInstance().addElement(gen, specialiazed);

        return gen;
    }

    public static Profile createProfile(String name, Element parent)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(parent);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();

        Profile profile = ef.createProfileInstance();
        profile.setName(name);
        ModelElementsManager.getInstance().addElement(profile, parent);

        return profile;
    }

    public static Stereotype createStereotype(String name, String metaClassName, Project project, Profile profile)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(metaClassName);
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(project);

        Class metaClass = StereotypesHelper.getMetaClassByName(project, metaClassName);
        Stereotype stereotype = StereotypesHelper.createStereotype(profile, name, Arrays.asList(metaClass));

        return stereotype;
    }

    public static void addStereotypeTag(Stereotype stereotype, String tagName, Classifier type)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(stereotype);
        Preconditions.checkNotNull(tagName);
        Preconditions.checkNotNull(type);

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();

        Property tag = ef.createPropertyInstance();
        tag.setName(tagName);
        tag.setType(type);

        ModelElementsManager.getInstance().addElement(tag, stereotype);
    }
}
