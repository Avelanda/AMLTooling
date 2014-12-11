package edu.mayo.aml.tooling.batch;

import edu.mayo.aml.tooling.auxiliary.ModelUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by dks02 on 12/11/14.
 */
public class AMLBatchEnvironment
{
    private String propertyFileName = "amlbatch.properties";
    private Properties properties = null;

    public void loadProperties()
    {
        InputStream input = null;

        try
        {

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            input = classloader.getResourceAsStream(propertyFileName);
            if(input==null)
            {
                System.out.println("Sorry, unable to find " + propertyFileName);
                return;
            }

            properties = new Properties();
            properties.load(input);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(input!=null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProjectFileName()
    {
        if ((properties == null)||(properties.size() == 0))
            loadProperties();

        return properties.getProperty("project.fileName");
    }
}
