package edu.mayo.aml.tooling.auxiliary;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dks02 on 12/10/14.
 */
public class Utils
{
    public static String getCurrentTimeStampAsSuffix()
    {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmm");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
