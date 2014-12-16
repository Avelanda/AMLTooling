package edu.mayo.aml.tooling.adl2aml.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ClassPathManager
{
	/**
	  * @author <A HREF="mailto:Sharma.Deepak2@mayo.edu">Deepak Sharma</A>
	 */


		private static LWFileFilter filter_ = null;
		public static Vector<String> filesAdded = new Vector<String>();
		
		 public static String FindItAndAddToClassPath(File startDirectory, 
				 									  String fileNameToMatch, 
				 									  boolean addEvenIfAlreadyThere) 
		 {
			 AU.debug("Calling with " + startDirectory.getAbsolutePath() + " and " + fileNameToMatch);
			 if (fileNameToMatch == null)
				 return null;
			 
			 if ((!addEvenIfAlreadyThere)&&(filesAdded.contains(fileNameToMatch)))
				 return "" + fileNameToMatch + " is in classpath.";
			 
			 String msg = null;
			 
			 if (filter_ == null)
			 {
				 filter_ = new LWFileFilter();
				 filter_.fileNameToAccept = fileNameToMatch;
			 }
			 
			 File[] roots = File.listRoots();;
			 
			 if ((startDirectory != null)&&(startDirectory.exists()))
				 roots = new File[]{ startDirectory };
			     
			    List<File> list = new ArrayList<File>();
			    for (int i = 0; i < roots.length; i++) 
			    {
					  try
					{
						scan(roots[i],list, filter_);
						for (Iterator<File> itr = list.iterator();itr.hasNext();)
						{
							File fl = itr.next();
							
							addURL(fl.toURI());
								
							if (msg == null)
								msg = "Added " + fl.toString() + " to classpath.\n" ;
							else
								msg += "Added " + fl.toString() + " to classpath.\n" ;
							
							filesAdded.add(fileNameToMatch);
						}
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    
			    return msg;
		 	}

		 public static void scan(File path, List<File> list, FileFilter filter) throws IOException 
		 {
			// Get filtered files in the current path
			File[] files = path.listFiles(filter);
			
			// Process each filtered entry
			for (int i = 0; i < files.length; i++) 
			{
				// recurse if the entry is a directory
				if (files[i].isDirectory()) 
				{
					scan(files[i],list,filter);
				}
				else 
				{
					// add the filtered file to the list
					list.add(files[i]);
				}
			} // for
		} // scan

		public static void addURL(URI u) throws IOException
		{

			URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<URLClassLoader> sysclass = URLClassLoader.class;

			try
			{
				Method method = sysclass.getDeclaredMethod("addURL", new Class[] {URL.class});
				method.setAccessible(true);
				method.invoke(sysloader, new Object[] {u.toURL()});
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				throw new IOException("Error, could not add URL to system classloader");
			}
		}

}
