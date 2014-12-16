package edu.mayo.aml.tooling.adl2aml.utils;

import java.io.*;

public class AU
{
	public static final String addNLs(String str)
	{
		if (!AU.isNull(str))
			str += "\n";
		return str;
	}

	public static boolean isNull(String str)
	{
		return ((str == null)||("null".equalsIgnoreCase(str))||("".equals(str.trim())));
	}
	
	public static String replaceWithEmptyStringIfNull(String str)
	{
		if (str == null)
			return "";
		return str;
	}
	
	public static boolean addFileAtRuntimeToClassPath(File startSearchLocation, 
													  String fileName)
	{
		boolean added = true;
		
		if (startSearchLocation == null)
			return false;
		
		String result = null;
		try
		{
			result = ClassPathManager.FindItAndAddToClassPath(startSearchLocation, fileName, false);
			
			if (result == null)
			{
				result = "Could not locate file '" + fileName + "' under specified directory.";
				String errorMsg = result + ". Please configure again!";
				System.out.println(errorMsg);
				added = false;
			}

			if ((result != null)&&(result.startsWith("Could not")))
                AU.warn(result);
		}
		catch (Exception exp) 
		{
            AU.error("Error while trying to add file to classpath");
			exp.printStackTrace();
		}
		
		return added;
	}

	public static String getOsName() 
	{
		  String os = "";
		  if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) 
		  {
		    os = "windows";
		  } 
		  else if (System.getProperty("os.name").toLowerCase().indexOf("linux") > -1) 
		  {
			  os = "linux";
		  } 
		  else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) 
		  {
		    os = "mac";
		  }
		 
		  return os;
	}
	
	public static void setContents(File aFile, String aContents, boolean append)
									throws FileNotFoundException, IOException 
	{
		if (aFile == null) 
		{
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!aFile.exists()) 
		{
			throw new FileNotFoundException ("File does not exist: " + aFile);
		}
		if (!aFile.isFile()) 
		{
			throw new IllegalArgumentException("Should not be a directory: " + aFile);
		}
		
		if (!aFile.canWrite()) 
		{
			throw new IllegalArgumentException("File cannot be written: " + aFile);
		}
		
		//use buffering
		Writer output = new BufferedWriter(new FileWriter(aFile));
		try 
		{
			//FileWriter always assumes default encoding is OK!
			if (append)
				output.append(aContents);
			else
				output.write( aContents );
		
			output.flush();
		}
		finally 
		{
			output.close();
		}
	}
	
	public static String escapeSpecialChars1(String str)
	{
		if (isNull(str))
			return null;
		
		return str.replaceAll("\\.", "_p_").
					replaceAll(":", "_s_").
					replaceAll(",", "_c_");
	}
	
	public static void trace(StackTraceElement e[]) 
	{
	   boolean doNext = false;
	   for (StackTraceElement s : e) 
	   {
	       if (doNext) 
	       {
	    	  String c = s.getClassName();
              AU.info("Executing [" + ((c == null) ? "" : (c.substring(c.lastIndexOf(".") + 1)))
                       + "." + s.getMethodName() + "()]");
	          return;
	       }
	       doNext = s.getMethodName().equals("getStackTrace");
	   }
	}

    public static void debug(String message)
    {
        System.out.println("DEBUG:" + message);
    }

    public static void info(String message)
    {
        System.out.println("INFO:" + message);
    }

    public static void warn(String message)
    {
        System.out.println("WARN:" + message);
    }

    public static void error(String message)
    {
        System.out.println("ERROR:" + message);
    }
}
