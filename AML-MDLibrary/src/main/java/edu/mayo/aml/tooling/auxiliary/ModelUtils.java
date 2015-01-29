package edu.mayo.aml.tooling.auxiliary;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.ElementsFactory;

import java.util.*;

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

    public static Element findElementWithPath(Project project, String qualifiedName, java.lang.Class classifier)
    {
        return ModelHelper.findElementWithPath(project, qualifiedName, classifier);
    }

    public static Class findClassWithName(Package pkg, String className)
    {
        Preconditions.checkNotNull(pkg);
        Preconditions.checkNotNull(className);
        java.lang.Class[] ec = {Class.class};

        Collection<Class> allClasses = (Collection<Class> ) ModelHelper.getElementsOfType(pkg, ec, false);

        for (Class cls : allClasses)
        {
            String[] tokens = cls.getQualifiedName().split("::");
            if (className.equals(tokens[tokens.length - 1]))
                return cls;
        }

        return null;
    }

    public static Package createPackage(String packageName, Element parent)
    {
        return createPackage(packageName, parent, null, null, null);
    }

    public static Package createPackage(String packageName,
                                        Element parent,
                                        String profileName,
                                        String streotypeName,
                                        HashMap<String, Object> tagValues)
    {
        Preconditions.checkNotNull(packageName);

        Project project = Application.getInstance().getProject();

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        Package np = ef.createPackageInstance();
        np.setName(packageName);
        np.setOwner(parent);

        ModelUtils.findAndApplyStereotype(np, profileName, streotypeName, tagValues);
        return np;
    }

    public static PackageImport addPackageImport(Package mainPackage,
                                                    Package importedPackage,
                                                    String profileName,
                                                    String stereotypeName,
                                                    HashMap<String, Object> tagValues)
    {
        Preconditions.checkNotNull(mainPackage);
        Preconditions.checkNotNull(importedPackage);

        Project project = Application.getInstance().getProject();

        ElementsFactory ef = Application.getInstance().getProject().getElementsFactory();
        PackageImport pi = ef.createPackageImportInstance();
        pi.setImportedPackage(importedPackage);

        ModelUtils.findAndApplyStereotype(pi, profileName, stereotypeName, tagValues);

        mainPackage.getPackageImport().add(pi);

        return pi;
    }

    public static Collection<Package> findPackageForMatchingName(Project project, String nameRegex)
    {
        ArrayList<Package> matching = new ArrayList<Package>();
        for (Package pkg : getAllPackages(project))
            if (pkg.getName().matches(nameRegex))
                matching.add(pkg);

        return matching;
    }

    public static Collection<Package> findPackageForMatchingQN(Project project, String qnRegex)
    {
        ArrayList<Package> matching = new ArrayList<Package>();
        for (Package pkg : getAllPackages(project))
            if (pkg.getQualifiedName().matches(qnRegex))
                matching.add(pkg);

        return matching;
    }

    public static void removeAllPackages(Project project)
    {
        try
        {
            ModelElementsManager mm = ModelElementsManager.getInstance();
            Collection<Package> allPkgs = ModelUtils.getAllPackages(project);
            for (Package pkg : allPkgs)
                if (pkg.canBeDeleted())
                    mm.removeElement(pkg);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public static void removeAllProfiles(Project project)
    {
        try
        {
            ModelElementsManager mm = ModelElementsManager.getInstance();
            Collection<Profile> allProfiles = ModelUtils.getAllProfiles(project);
            for (Profile profile : allProfiles)
                if (profile.canBeDeleted())
                    mm.removeElement(profile);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    public static Class createClass(String name,
                                    Element parent)
            throws ReadOnlyElementException
    {
        return createClass(name, parent, null, null, null);
    }

    public static Class createClass(String name,
                                    Element parent,
                                    String profileName,
                                    String stereotypeName,
                                    HashMap<String, Object> tagValues)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);

        Project project = Application.getInstance().getProject();
        ElementsFactory ef = project.getElementsFactory();
        Class cls = ef.createClassInstance();
        cls.setName(name);

        if (parent != null)
            getInstance().addElement(cls, parent);

        ModelUtils.findAndApplyStereotype(cls, profileName, stereotypeName, tagValues);

        return cls;
    }

    public static void findAndApplyStereotype(Element element,
                                              String profileName,
                                              String stereotypeName,
                                              HashMap<String, Object> tagValues)
    {
        Project project = Application.getInstance().getProject();
        if ((profileName != null)&&(stereotypeName != null)&&(element != null))
        {
            Profile profile = StereotypesHelper.getProfile(project, profileName);
            Stereotype stereotype = StereotypesHelper.getStereotype(project, stereotypeName, profile);

            // Apply Stereotype
            if (StereotypesHelper.canApplyStereotype(element, stereotype))
            {
                StereotypesHelper.addStereotype(element, stereotype);

                if ((tagValues != null) && (!tagValues.isEmpty()))
                    for (String key : tagValues.keySet())
                        StereotypesHelper.setStereotypePropertyValue(element,
                                stereotype, key, tagValues.get(key));
            }
        }
    }

    public static Enumeration createEnumeration(String name,
                                                Element parent)
            throws ReadOnlyElementException
    {
        return createEnumeration(name, parent, null, null, null);
    }

    public static Enumeration createEnumeration(String name,
                                                Element parent,
                                                String profileName,
                                                String stereotypeName,
                                                HashMap<String, Object> tagValues)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);

        Project project = Application.getInstance().getProject();
        ElementsFactory ef = project.getElementsFactory();
        Enumeration enumInstance = ef.createEnumerationInstance();
        enumInstance.setName(name);

        if (parent != null)
            getInstance().addElement(enumInstance, parent);

        ModelUtils.findAndApplyStereotype(enumInstance, profileName, stereotypeName, tagValues);
        return enumInstance;
    }

    public static EnumerationLiteral createEnumerationLiteral(String name,
                                                Enumeration container)
            throws ReadOnlyElementException
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(container);

        Project project = Application.getInstance().getProject();
        ElementsFactory ef = project.getElementsFactory();
        EnumerationLiteral enumLit = ef.createEnumerationLiteralInstance();
        enumLit.setName(name);

        if (container != null)
            container.getOwnedLiteral().add(enumLit);

        return enumLit;
    }

    public static EnumerationLiteral findEnumerationLiteralInEnumeration(Enumeration container, String name)
    {
        if (container != null)
            for (EnumerationLiteral member : container.getOwnedLiteral())
                if (member.getName().equals(name))
                    return member;

        return null;
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
