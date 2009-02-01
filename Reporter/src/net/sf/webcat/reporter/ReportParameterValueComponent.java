package net.sf.webcat.reporter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.webcat.core.WCComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSTimestamp;

/**
 * 
 * @author Tony Allevato
 * @version $Id: ReportParameterValueComponent.java,v 1.1 2009/02/01 21:57:46 aallowat Exp $
 */
public class ReportParameterValueComponent extends WCComponent
{
    //~ Constructors ..........................................................
    
    // ----------------------------------------------------------
    public ReportParameterValueComponent(WOContext context)
    {
        super(context);
    }
    

    //~ KVC attributes (must be public) .......................................
    
    public NSDictionary<String, Object> parameter;
    public Object value;

    public NSNumberFormatter integerFormatter = new NSNumberFormatter("0");
    public NSNumberFormatter floatFormatter = new NSNumberFormatter("0.0");

    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public Object value()
    {
        if (value == null)
        {
            return coercedDefaultValue();
        }
        else
        {
            return value;
        }
    }
    
    
    // ----------------------------------------------------------
    public boolean isParameterBoolean()
    {
        return IReportParameterConstants.TYPE_BOOLEAN.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterDecimal()
    {
        return IReportParameterConstants.TYPE_DECIMAL.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterDate()
    {
        return IReportParameterConstants.TYPE_DATE.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterDatetime()
    {
        return IReportParameterConstants.TYPE_DATETIME.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterFloat()
    {
        return IReportParameterConstants.TYPE_FLOAT.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterInteger()
    {
        return IReportParameterConstants.TYPE_INTEGER.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }

    
    // ----------------------------------------------------------
    public boolean isParameterString()
    {
        return IReportParameterConstants.TYPE_STRING.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }


    // ----------------------------------------------------------
    public boolean isParameterTime()
    {
        return IReportParameterConstants.TYPE_TIME.equals(parameter.
                objectForKey(IReportParameterConstants.DATA_TYPE_KEY));
    }
    
    
    // ----------------------------------------------------------
    private Object coercedDefaultValue()
    {
        String defaultValue = (String) parameter.objectForKey(
                IReportParameterConstants.DEFAULT_VALUE_KEY);

        if (isParameterBoolean())
        {
            return Boolean.valueOf(defaultValue);
        }
        else if (isParameterDate())
        {
            try
            {
                return new NSTimestamp(DATE_FORMAT.parse(defaultValue));
            }
            catch (ParseException e)
            {
                return null;
            }
        }
        else if (isParameterDatetime())
        {
            try
            {
                return new NSTimestamp(DATETIME_FORMAT.parse(defaultValue));
            }
            catch (ParseException e)
            {
                return null;
            }
        }
        else if (isParameterDecimal())
        {
            return new BigDecimal(defaultValue);
        }
        else if (isParameterFloat())
        {
            return Double.valueOf(defaultValue);
        }
        else if (isParameterInteger())
        {
            return Float.valueOf(defaultValue);
        }
        else if (isParameterString())
        {
            return defaultValue;
        }
        else if (isParameterTime())
        {
            try
            {
                return new NSTimestamp(TIME_FORMAT.parse(defaultValue));
            }
            catch (ParseException e)
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
    

    //~ Static/instance variables .............................................
    
    private static final DateFormat DATETIME_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final DateFormat DATE_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd");
    
    private static final DateFormat TIME_FORMAT =
        new SimpleDateFormat("HH:mm:ss");
}
