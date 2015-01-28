package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Preconditions;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.DiagramTypeConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import edu.mayo.aml.tooling.adl2aml.utils.AU;
import edu.mayo.aml.tooling.adl2aml.utils.UMLUtils;
import edu.mayo.aml.tooling.auxiliary.ModelUtils;
import org.openehr.jaxb.am.*;
import org.openehr.jaxb.rm.CodePhrase;
import org.openehr.jaxb.rm.StringDictionaryItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dks02 on 1/27/15.
 */
public class AMLMDProjectTerms
{
    private MDProject mdp = null;

    private Package termsPackage = null;

    private Package snomedCTTermsPackage = null;
    private Package loincTermsPackage = null;
    private Package otherTermsPackage = null;

    private Diagram snomedctTermsDiag = null;
    private Diagram loincTermsDiag = null;
    private Diagram otherTermsDiag = null;

    private Enumeration snomedctIds = null;
    private Enumeration loincIds = null;
    private Enumeration otherIds = null;

    private HashMap<String, Class> terms = new HashMap<String, Class>();

    public AMLMDProjectTerms(MDProject mdProject)
    {
        this.mdp = mdProject;
    }

    public String getDefaultTermsPackageName()
    {
        return AMLConstants.defaultTermsPackageName;
    }

    public Package getTermsPackage()
    {
        if (termsPackage == null)
            initialize(null);

        return termsPackage;
    }

    public void initialize(Package container)
    {
        mdp.startSession("Initializing Terminology Packages...");

        if (container == null)
            container = mdp.getProject().getModel();

        termsPackage = ModelUtils.createPackage(getDefaultTermsPackageName(), container);
        snomedCTTermsPackage = ModelUtils.createPackage(AMLConstants.sctTermsPackageName, termsPackage);
        loincTermsPackage = ModelUtils.createPackage(AMLConstants.loincTermsPackageName, termsPackage);
        otherTermsPackage = ModelUtils.createPackage(AMLConstants.otherTermsPackageName, termsPackage);

        HashMap<String, Object> tagValues = new HashMap<String, Object>();

        try
        {
            snomedctTermsDiag = ModelUtils.createDiagram(AMLConstants.sctTermsPackageName,
                    DiagramTypeConstants.UML_CLASS_DIAGRAM,
                    snomedCTTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "snomed-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://snomed.org");

            snomedctIds = ModelUtils.createEnumeration(AMLConstants.sctTermsPackageName +"-ID",
                    snomedCTTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement sctdpe = mdp.getProject().getDiagram(snomedctTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(snomedctIds, sctdpe);

            loincTermsDiag = ModelUtils.createDiagram(AMLConstants.loincTermsPackageName,
                    DiagramTypeConstants.UML_CLASS_DIAGRAM,
                    loincTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "loinc-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://loinc.org");

            loincIds = ModelUtils.createEnumeration(AMLConstants.loincTermsPackageName +"-ID",
                    loincTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement loincdpe = mdp.getProject().getDiagram(loincTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(loincIds, loincdpe);

            otherTermsDiag = ModelUtils.createDiagram(AMLConstants.otherTermsPackageName,
                    DiagramTypeConstants.UML_CLASS_DIAGRAM,
                    otherTermsPackage);

            tagValues.put(AMLConstants.TAG_ID_URI_PATTERN, "other-uri-pattern");
            tagValues.put(AMLConstants.TAG_URI, "http://other.org");

            otherIds = ModelUtils.createEnumeration(AMLConstants.otherTermsPackageName +"-ID",
                    otherTermsPackage,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.TAG_SCOPED_IDENTIFIER,
                    tagValues);

            DiagramPresentationElement othdpe = mdp.getProject().getDiagram(otherTermsDiag);
            PresentationElementsManager.getInstance().createShapeElement(otherIds, othdpe);
        }
        catch (ReadOnlyElementException e)
        {
            e.printStackTrace();
        }
        mdp.closeSession();
    }

    public void addTerms(ArchetypeOntology ontology)
    {
        Preconditions.checkNotNull(ontology);

        if (getTermsPackage() == null)
            return;

        if (ontology.getTermBindings().isEmpty())
            return;

        for (TermBindingSet set : ontology.getTermBindings())
            for (TermBindingItem item : set.getItems())
                createConceptReference(item);
    }

    private Class createConceptReference(TermBindingItem item)
    {
        Class cls = null;
        try
        {

            String ontId = item.getValue().getTerminologyId().getValue();
            String code = item.getValue().getCodeString();

            // if code has any non-alphanumeric
            if (code.matches("^.*[^a-zA-Z0-9 ].*$"))
                return null;

            Class cr = getConceptReference(item.getValue());

            if (cr != null)
                return cr;

            mdp.startSession("Creating Concept Reference");

            Package termPkg = getPackageForTermID(ontId);

            HashMap<String, Object> tagValues = new HashMap<String, Object>();
            tagValues.put(AMLConstants.TAG_ID, code);
            tagValues.put(AMLConstants.TAG_URI, ontId+code);

            cls = ModelUtils.createClass(code, termPkg,
                    AMLConstants.TERMINOLOGY_PROFILE,
                    AMLConstants.STEREOTYPE_CONCEPT_REFERENCE,
                    tagValues);

            storeConceptReference(item.getValue(), cls);

            Diagram termDiag = getDiagramForTermID(ontId);
            DiagramPresentationElement cdpe = mdp.getProject().getDiagram(termDiag);
            PresentationElementsManager.getInstance().createShapeElement(cls, cdpe);

            ModelUtils.createEnumerationLiteral(code, getEnumerationContainerForTermID(ontId));
        }
        catch (ReadOnlyElementException e1)
        {
            e1.printStackTrace();
        }

        mdp.closeSession();

        return cls;
    }

    private void storeConceptReference(CodePhrase codePhrase, Class conceptReference)
    {
        Preconditions.checkNotNull(conceptReference);
        this.terms.put(getKey(codePhrase), conceptReference);
    }

    public Class getConceptReference(CodePhrase codePhrase)
    {
        return this.terms.get(getKey(codePhrase));
    }

    private Package getPackageForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedCTTermsPackage;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincTermsPackage;

        return otherTermsPackage;
    }

    private Enumeration getEnumerationContainerForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedctIds;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincIds;

        return otherIds;
    }

    private Diagram getDiagramForTermID(String id)
    {
        Preconditions.checkNotNull(id);
        if (id.toLowerCase().indexOf("snomed") != -1)
            return snomedctTermsDiag;

        if (id.toLowerCase().indexOf("loinc") != -1)
            return loincTermsDiag;

        return otherTermsDiag;
    }

    private String getKey(CodePhrase codePhrase)
    {
        Preconditions.checkNotNull(codePhrase);
        return codePhrase.getTerminologyId().getValue()
                + codePhrase.getCodeString();
    }
}
