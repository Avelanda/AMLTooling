package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;

/**
 * Created by dks02 on 1/27/15.
 */
public class AMLMDProjectCommons
{
    private MDProject mdp = null;

    public AMLMDProjectCommons(MDProject mdProject)
    {
        this.mdp = mdProject;
    }

    public Enumeration getLanguages()
    {
        return (Enumeration) ModelHelper.findElementWithPath(mdp.getProject(), "CommonResources::Languages::Languages", Enumeration.class);
    }

    public EnumerationLiteral getThisOrEnglish(String name)
    {
        EnumerationLiteral language = getLanugageByName(name);

        if (language == null)
            return getEnglishLanguage();

        return language;
    }

    public EnumerationLiteral getLanugageByName(String languageCodeOrName)
    {
        return ModelUtils.findEnumerationLiteralInEnumeration(getLanguages(), languageCodeOrName);

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

    private EnumerationLiteral getEnglishLanguage()
    {
        return getLanugageByName("en");
    }
}
