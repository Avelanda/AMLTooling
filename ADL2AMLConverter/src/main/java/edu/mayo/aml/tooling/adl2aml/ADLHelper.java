package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.ArchetypeOntology;
import org.openehr.jaxb.am.ArchetypeTerm;
import org.openehr.jaxb.am.CodeDefinitionSet;
import org.openehr.jaxb.rm.StringDictionaryItem;

import java.util.List;
import java.util.Vector;

/**
 * Created by dks02 on 1/27/15.
 */
public class ADLHelper
{
    public static String getTermDefinitionText(ArchetypeOntology archetypeOntology, String id, String language)
    {
        Preconditions.checkNotNull(archetypeOntology);
        Preconditions.checkNotNull(id);

        for (CodeDefinitionSet cds : archetypeOntology.getTermDefinitions())
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

    public static Vector<String> getTermDefinitionIds(ArchetypeOntology ontology, String language)
    {
        Preconditions.checkNotNull(ontology);

        Vector<String> ids = new Vector<String>();
        for (CodeDefinitionSet cds : ontology.getTermDefinitions())
        {
            // if language is supplied, it has to match with code definition set's language
            if ((language != null)&&(!language.equalsIgnoreCase(cds.getLanguage())))
                continue;

            for (ArchetypeTerm term : cds.getItems())
                ids.add(term.getCode());
        }

        return ids;
    }
}
