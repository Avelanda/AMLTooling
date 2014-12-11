package edu.mayo.aml.tooling.auxiliary;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.impl.ElementsFactory;

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

    public static Relationship createRelation(String supplierEndName, String clientEndName, Element supplier, Element client, boolean isNavigable)
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

        ModelHelper.setNavigable(propType1, isNavigable);

        return rel;
    }

    public static Diagram createDiagram(String name, String type, Namespace container)
            throws ReadOnlyElementException
    {
        Diagram diag = getInstance().createDiagram(type, container);

        if (!Strings.isNullOrEmpty(name.trim()))
            diag.setName(name);

        return diag;
    }
}
