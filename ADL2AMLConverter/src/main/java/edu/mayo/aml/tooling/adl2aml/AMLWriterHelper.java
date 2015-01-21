package edu.mayo.aml.tooling.adl2aml;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import org.openehr.jaxb.am.Archetype;

import java.util.Vector;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLWriterHelper
{
    private Vector<String> targetArchetypeIds = new Vector<String>();

    public AMLWriterHelper()
    {
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.cluster.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.compound_clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.indivisible_clinical_statement.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_document.v2.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.clinical_report_header.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.action.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.author_action.v1.0.0");
        targetArchetypeIds.add("CIMI-CORE-ITEM_GROUP.issue_action.v1.0.0");
    }
    public boolean toProecess(Archetype archetype)
    {
        if (archetype == null)
            return false;

        if (targetArchetypeIds.contains(archetype.getArchetypeId().getValue()))
            return true;

        return false;
    }
}
