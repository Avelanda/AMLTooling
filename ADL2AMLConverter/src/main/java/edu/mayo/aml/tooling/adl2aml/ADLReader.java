package edu.mayo.aml.tooling.adl2aml;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import edu.mayo.aml.tooling.adl2aml.utils.ClassPathManager;
import edu.mayo.aml.tooling.adl2aml.utils.LWFileFilter;
import org.apache.commons.io.IOUtils;
import org.openehr.adl.parser.AdlDeserializer;
import org.openehr.adl.parser.BomSupportingReader;
import org.openehr.adl.rm.OpenEhrRmModel;
import org.openehr.adl.rm.RmModel;
import org.openehr.jaxb.am.Archetype;
import org.openehr.jaxb.am.DifferentialArchetype;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dks02 on 12/12/14.
 */
public class ADLReader
{
    private String adlFolder = ".";
    private RmModel rmModel = new OpenEhrRmModel();
    private String defaultRegex = ".\\.adls";

    public String getAdlFolderOfFilePath()
    {
        return adlFolder;
    }

    public void setAdlFolderOfFilePath(String adlFolder)
    {
        this.adlFolder = adlFolder;
    }

    public RmModel getReferenceModel()
    {
        return rmModel;
    }

    public void setReferenceModel(RmModel refModel)
    {
        if (refModel != null)
            rmModel = refModel;
    }

    public List<File> getAllFiles(String adlFolderOrFileName, String fileNameRegex)
    {
        // Any file that has extension .adls is default if filename Regular experssion
        // is not supplied
        List<File> inputFiles = new ArrayList<File>();
        LWFileFilter lwff = new LWFileFilter();
        lwff.fileNameToAccept = (fileNameRegex == null) ? defaultRegex : fileNameRegex;

        try
        {
            ClassPathManager.scan(new File(adlFolderOrFileName), inputFiles, lwff);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

        return inputFiles;
    }

    public List<File> getAllFiles(String fileNameRegex)
    {
        // Any file that has extension .adls is default if filename Regular experssion
        // is not supplied
        List<File> inputFiles = new ArrayList<File>();
        LWFileFilter lwff = new LWFileFilter();
        lwff.fileNameToAccept = (fileNameRegex == null) ? defaultRegex : fileNameRegex;

        try
        {
            ClassPathManager.scan(new File(this.getAdlFolderOfFilePath()), inputFiles, lwff);
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

        return inputFiles;
    }

    public Archetype getADLArchetype(File adlFile) throws FileNotFoundException, IOException
    {
        AdlDeserializer adlDeserializer = new AdlDeserializer(rmModel);
        InputStream in = new FileInputStream(adlFile);
        Reader reader = new BomSupportingReader(in, Charsets.UTF_8);
        return adlDeserializer.parse(IOUtils.toString(reader));
    }

    public static void main(String[] args)
    {
        int total = 0;
        int failed = 0;
        List<String> failedFiles = new ArrayList<String>();

        ADLReader adlReader = new ADLReader();
        adlReader.setAdlFolderOfFilePath("./ADL2AMLConverter/adl15");

        List<File> inputFiles = adlReader.getAllFiles(null);

        System.out.println("Loaded =" + inputFiles.size() + " Archetypes!!");
        total = inputFiles.size();
        for (File adlFile : inputFiles)
        {
            String canonicalPath = null;
            Archetype arch = null;
            try
            {
                canonicalPath = adlFile.getCanonicalPath();
                arch = adlReader.getADLArchetype(adlFile);
            }
            catch (Exception e)
            {
                System.out.println("Failed:[" + canonicalPath + "] " + e.getMessage());
                failedFiles.add(canonicalPath);
                failed++;
                continue;
            }

            if (arch == null)
                continue;

            System.out.println("##############################################################");
            System.out.println("File: " + canonicalPath);
            System.out.println("Archetype: " + arch.getArchetypeId().getValue());

            if ((arch.getDescription() != null)&&(arch.getDescription().getDetails().size() > 0))
                System.out.println("Description: " + arch.getDescription().getDetails().get(0).getPurpose());
        }

        System.out.println("Total=" + total + "  Success=" + (total - failed) + " Failed=" + failed);
        System.out.println("DONE!!");
    }
}
