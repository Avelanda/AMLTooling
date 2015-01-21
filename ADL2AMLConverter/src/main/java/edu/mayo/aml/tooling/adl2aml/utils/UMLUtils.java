package edu.mayo.aml.tooling.adl2aml.utils;

import com.google.common.base.Preconditions;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import java.util.Collection;

/**
 * Created by dks02 on 1/21/15.
 */
public class UMLUtils
{
    public static String printUMLNamedElement(NamedElement element)
    {
        if (element == null)
            return null;

        return "NamedElement->[Name=" + element.getName() + "][QN=" + element.getQualifiedName() + "]"
                + "[" + element.getClassType() + "]";
    }

    public static String printUMLElement(Element element)
    {
        if (element == null)
            return null;

        return "Element->[Name=" + element.getHumanName() + "][ID=" + element.getID() + "]"
                + "[" + element.getClassType() + "]";
    }

    public static String printUMLElements(Collection elements)
    {
        if (elements  == null)
            return null;

        String ret = "\n";

        if (elements.isEmpty())
            return ret;

        for (Object ne : elements)
            if (ne instanceof NamedElement)
                ret += "\t" + printUMLNamedElement((NamedElement) ne) + "\n";
            else if (ne instanceof Element)
                ret += "\t" + printUMLElement((Element) ne) + "\n";
            else
                ret += "\tObject of Class->[" + ne.getClass().getName() + "]\n";

        return ret;
    }
}
