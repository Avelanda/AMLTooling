package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeTerm;
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.openehr.jaxb.rm.StringDictionaryItem;

import java.util.List;

/**
 * Created by dks02 on 1/27/15.
 */
public class ADLHelper
{
    public static String getTermDefinitionText(Archetype archetype, String id, String language)
    {
        Preconditions.checkNotNull(archetype);
        Preconditions.checkNotNull(id);

        for (CodeDefinitionSet cds : archetype.getOntology().getTermDefinitions())
        {
            // if language is supplied, it has to match with code definition set's language
            if ((language != null)&&(!language.equalsIgnoreCase(cds.getLanguage())))
                continue;

            for (ArchetypeTerm term : cds.getItems()) {
                if (term.getCode().equals(id))
                    for (StringDictionaryItem dict : term.getItems())
                        if (dict.getId().equalsIgnoreCase(AMLConstants.ATTRIBUTE_TEXT))
                            return dict.getValue();
            }
        }

        // If nothing is found, the id is returned.
        return id;
    }
}
