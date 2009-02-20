/*==========================================================================*\
 |  $Id: WCDateTextBox.java,v 1.2 2009/02/20 02:27:21 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2008 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package net.sf.webcat.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import net.sf.webcat.ui._base.DojoFormElement;
import net.sf.webcat.ui.util.DojoOptions;
import net.sf.webcat.ui.util.DojoUtils;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

// ------------------------------------------------------------------------
/**
 * A text box that lets the user enter the text representation of a date, and
 * also provides a pop-up calendar when clicked that can be navigated.
 * <p>
 * Corresponds to Dojo type {@code dijit.form.DateTextBox}.
 * 
 * <h2>Bindings</h2>
 * <ul>
 * <li> {@code value}: An {@link NSTimestamp} object representing the date to be
 * displayed in the text box
 * <li> {@code dateformat}: A date format string, as described in
 * {@link NSTimestampFormatter}
 * </ul>
 * 
 * @author Tony Allevato
 * @version $Id: WCDateTextBox.java,v 1.2 2009/02/20 02:27:21 aallowat Exp $
 */
public class WCDateTextBox extends DojoFormElement
{
    //~ Constructor ...........................................................

    // ----------------------------------------------------------
    public WCDateTextBox(String name,
            NSDictionary<String, WOAssociation> someAssociations,
            WOElement template)
    {
        super("input", someAssociations, template);

        _dateformat = _associations.removeObjectForKey("dateformat");
    }


    //~ Methods ...............................................................
    
    // ----------------------------------------------------------
    public String inputTypeInContext(WOContext context)
    {
        return "text";
    }


    // ----------------------------------------------------------
    @Override
    public String dojoType()
    {
        return "dijit.form.DateTextBox";
    }


    // ----------------------------------------------------------
    @Override
    protected String stringValueForObject(Object value)
    {
        // Append the date set in the value binding, if it exists. Dojo
        // requires that this be string in ISO date format, so we convert it
        // before writing it out.

        return ISO_DATE_FORMAT.format(value);
    }


    // ----------------------------------------------------------
    @Override
    protected Object objectForStringValue(String stringValue)
    {
        // When Dojo populates the shadowed text field with the selected date,
        // it always formats it in ISO date format.

        Object object;

        try
        {
            object = new NSTimestamp(ISO_DATE_FORMAT.parse(stringValue));
        }
        catch (ParseException e)
        {
            object = null;
        }

        return object;
    }


    // ----------------------------------------------------------
    @Override
    public DojoOptions additionalConstraints(WOContext context)
    {
        // Append constraints based on the date format, if one was provided.

        DojoOptions manualConstraints = new DojoOptions();

        if(_dateformat != null)
        {
            String dateFormat =
                (String)_dateformat.valueInComponent(context.component());

            if(dateFormat != null)
            {
                manualConstraints.putValue("datePattern",
                        dateFormatToDatePattern(dateFormat));
            }
        }

        return manualConstraints;
    }


    // ----------------------------------------------------------
    /**
     * Converts an NSTimestampFormatter string into a JavaScript date format
     * string. This allows the dateformat binding to be used by Dojo for
     * validation.
     * 
     * Only the following format tokens from NSTimestampFormatter are
     * translatable to a JavaScript format, and are therefore the only ones that
     * can be reliably used in a DateTextBox dateformat: %%, %a, %A, %b, %B, %d,
     * %e, %H, %I, %m, %M, %p, %y, %Y
     * 
     * @param dateFormat
     *            the NSTimestampFormatter date format string
     * @return a JavaScript date format string that is as close as possible to
     *         the specified NSTimestampFormatter string
     */
    protected static String dateFormatToDatePattern(String dateFormat)
    {
        StringBuilder datePattern = new StringBuilder(32);

        int i = 0;
        while (i < dateFormat.length())
        {
            char ch = dateFormat.charAt(i);

            if (ch == '%')
            {
                i++;
                ch = dateFormat.charAt(i);

                switch (ch)
                {
                // Literal percent
                case '%':
                    datePattern.append('%');
                    break;

                // Abbreviated weekday name
                case 'a':
                    datePattern.append('E');
                    break;

                // Full weekday name
                case 'A':
                    datePattern.append("EE");
                    break;

                // Abbreviated month name
                case 'b':
                    datePattern.append("NNN");
                    break;

                // Full month name
                case 'B':
                    datePattern.append("MMM");
                    break;

                // Day of the month as a decimal number, leading 0
                case 'd':
                    datePattern.append("dd");
                    break;

                // Day of the month as a decimal number, no leading 0
                case 'e':
                    datePattern.append('d');
                    break;

                // 24-hour clock, 00-23
                case 'H':
                    datePattern.append("HH");
                    break;

                // 12-hour clock, 01-12
                case 'I':
                    datePattern.append('h');
                    break;

                // Month as decimal number, 01-12
                case 'm':
                    datePattern.append('M');
                    break;

                // Minute as decimal number, 00-59
                case 'M':
                    datePattern.append("mm");
                    break;

                // AM/PM designation
                case 'p':
                    datePattern.append('a');
                    break;

                // Year without century, 00-99
                case 'y':
                    datePattern.append("yy");
                    break;

                // Yeah with century
                case 'Y':
                    datePattern.append("yyyy");
                    break;
                }
            }
            else
            {
                datePattern.append(ch);
            }

            i++;
        }

        return datePattern.toString();
    }


    //~ Static/instance variables .............................................
    
    protected static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd");

    protected WOAssociation _dateformat;
}
