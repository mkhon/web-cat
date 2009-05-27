package net.sf.webcat.reporter;

import org.eclipse.birt.core.exception.BirtException;
import net.sf.webcat.core.MutableArray;
import net.sf.webcat.core.MutableDictionary;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public class ReportErrorsBlock extends WOComponent
{
    public ReportErrorsBlock(WOContext context)
    {
        super(context);
    }

    
    public MutableArray errors;
    public MutableDictionary error;
    public MutableDictionary chainEntry;


    // ----------------------------------------------------------
    public String errorSeverity()
    {
        int severity = (Integer)error.objectForKey("severity");

        switch (severity)
        {
            case BirtException.OK:
                return "OK";

            case BirtException.INFO:
                return "INFO";

            case BirtException.WARNING:
                return "WARNING";

            case BirtException.ERROR:
                return "ERROR";

            case BirtException.CANCEL:
                return "CANCEL";
        }

        return "ERROR";
    }


    // ----------------------------------------------------------
    public String errorCssClass()
    {
        int severity = (Integer)error.objectForKey("severity");

        switch (severity)
        {
            case BirtException.OK:
            case BirtException.INFO:
            case BirtException.CANCEL:
                return "infoBox";

            case BirtException.WARNING:
                return "warningBox";

            case BirtException.ERROR:
                return "errorBox";
        }
        return "errorBox";
    }


    // ----------------------------------------------------------
    public boolean isChainEntryFirst()
    {
        return (((MutableArray)error.objectForKey("chain")).objectAtIndex(0)
                == chainEntry);
    }
}
