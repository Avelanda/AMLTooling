package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.TermBindingItem;

import java.net.URI;
import java.util.Vector;

/**
 * Created by dks02 on 1/20/15.
 */
public class AMLWriterHelper
{
    private static String removeMinorVersion(String str)
    {
        if (!AU.isNull(str))
            return (str.split("\\.[0-9]*\\.[0-9]*"))[0];

        return str;
    }

    public static String getSearchId(Archetype archetype)
    {
        Preconditions.checkNotNull(archetype);
        return getSearchId(archetype.getArchetypeId().getValue());
    }

    public static String getSearchId(String str)
    {
        if (str != null)
            return AMLWriterHelper.removeMinorVersion(str);

        return str;
    }
}
