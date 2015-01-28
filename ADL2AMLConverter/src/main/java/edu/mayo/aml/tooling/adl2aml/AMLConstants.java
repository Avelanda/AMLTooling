package edu.mayo.aml.tooling.adl2aml;

/**
 * Created by dks02 on 1/22/15.
 */
public class AMLConstants
{
    public static String projectLocation = "AMLBaseProject.mdzip";
    public static String defaultRootPackageName = "AML";

    public static String defaultTermsPackageName = "Terms";
    public static String sctTermsPackageName = "SNOMED-CT";
    public static String loincTermsPackageName = "LOINC";
    public static String otherTermsPackageName = "OtherTerms";

    public static String localScopedIdentifiersEnumerationName = "Identifiers";


    // Imported Profiles
    public static String[] imports =
                            {       "CommonResources.mdzip",
                                    "TerminologyProfile.mdzip",
                                    "ReferenceModelProfile.mdzip",
                                    "ConstraintProfile.mdzip",
                                    "ArchetypeProfile.mdzip",
                                    "ArchetypeMetadataProfile.mdzip",
                                    "CIMI RM v3.0.1.mdzip"
                            };

    // Profile Names
    public static String TERMINOLOGY_PROFILE = "TerminologyProfile";
    public static String REF_MODEL_PROFILE = "ReferenceModelProfile";
    public static String CONSTRAINT_PROFILE = "ConstraintProfile";

    // Stereotypes
    public static String STEREOTYPE_ARCHETYPE = "Archetype";
    public static String STEREOTYPE_ARCHETYPE_VERSION = "ArchetypeVersion";
    public static String STEREOTYPE_CONCEPT_REFERENCE = "ConceptReference";
    public static String STEREOTYPE_CONSTRAINS = "Constrains";
    public static String STEREOTYPE_SCOPEDIDENTIFIER = "ScopedIdentifier";


    // tags
    public static String TAG_URI = "uri";
    public static String TAG_ID = "id";
    public static String TAG_SIGN = "sign";
    public static String TAG_LANGUAGE = "language";
    public static String TAG_DESCRIPTION = "description";
    public static String TAG_SCOPED_IDENTIFIER = "ScopedIdentifier";
    public static String TAG_ID_URI_PATTERN = "identifierURIPattern";

    // Attributes
    public static String ATTRIBUTE_TEXT = "text";

    public static String DEFAULT_DESCRIPTION = "";

}
