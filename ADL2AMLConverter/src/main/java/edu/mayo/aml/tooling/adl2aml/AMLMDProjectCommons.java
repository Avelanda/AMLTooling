package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;

/**
 * Created by dks02 on 1/27/15.
 */
public class AMLMDProjectCommons
{

    public static Enumeration getLanguages(Project project)
    {
        return (Enumeration) ModelHelper.findElementWithPath(project, "CommonResources::Languages::Languages", Enumeration.class);
    }

    public static EnumerationLiteral getThisOrEnglish(Project project, String name)
    {
        if (AU.isNull(name))
            return getEnglishLanguage(project);

        EnumerationLiteral language = getLanugageByName(project, name);

        if (language == null)
            return getEnglishLanguage(project);

        return language;
    }

    public static EnumerationLiteral getLanugageByName(Project project, String languageCodeOrName)
    {
        return ModelUtils.findEnumerationLiteralInEnumeration(getLanguages(project), languageCodeOrName);

//        Element languages = getLanguages();
//
//        if (languages == null)
//            return null;
//
//        if (languages instanceof Enumeration)
//        {
//            for (EnumerationLiteral el : ((Enumeration) languages).getOwnedLiteral())
//                if (el.getName().equalsIgnoreCase(languageCodeOrName))
//                    return el;
//        }
//
//        return null;
    }

    public static EnumerationLiteral getEnglishLanguage(Project project)
    {
        return getLanugageByName(project, "en");
    }
}
