package edu.mayo.aml.tooling.adl2aml;

import edu.mayo.aml.tooling.adl2aml.utils.AU;
import org.openehr.jaxb.am.Archetype;

/**
 * Created by dks02 on 12/16/14.
 */
public class AMLWriter
{
    public void convertToAML(Archetype archetype)
    {
        if (archetype == null)
            AU.warn("Archetype is null!!");

        AU.info("Converting Archetype:" + archetype.getArchetypeId().getValue());
    }
}
