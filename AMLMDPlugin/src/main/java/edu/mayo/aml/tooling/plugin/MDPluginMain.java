package edu.mayo.aml.tooling.plugin;

import com.nomagic.magicdraw.plugins.Plugin;

/**
 * Created by dks02 on 12/4/14.
 */
public class MDPluginMain extends Plugin
{

    public static void main( String[] args )
    {
        System.out.println( "Hello World! I am AML Plugin running from Main Method." );
    }

    @Override
    public void init()
    {
        javax.swing.JOptionPane.showMessageDialog(null, "AML MD Plugin init.");
        System.out.print("my plugin got executed again");
    }

    @Override
    public boolean close()
    {
        javax.swing.JOptionPane.showMessageDialog(null, "AML MD Plugin close.");
        return true;
    }

    @Override
    public boolean isSupported()
    {
        return true;
    }
}
