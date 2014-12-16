package edu.mayo.aml.tooling.adl2aml.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LWFileFilter implements FileFilter
{
	public String fileNameToAccept = null;
	
	public boolean accept(File f)
	{
        Pattern r = Pattern.compile(fileNameToAccept);

		if ((f != null)&&(fileNameToAccept != null))
        {
            if (f.isDirectory())
                return true;

            Matcher m = r.matcher(f.getName());
            return m.find();
        }

		return false;
	}

	public String getDescription()
	{
		return "This filter compares the file with a given file name to accept";
	}
}
