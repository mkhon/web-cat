/*==========================================================================*\
 |  $Id: EclipseSubmitterDefinitions.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
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

package org.webcat.grader.actions;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import org.apache.log4j.Logger;
import org.webcat.core.*;
import org.webcat.grader.*;

//-------------------------------------------------------------------------
/**
 * This page generates an assignment definition set published for
 * the Eclipse submitter plugin.
 *
 * @author Stephen Edwards
 * @version $Id: EclipseSubmitterDefinitions.java,v 1.1 2010/05/11 14:51:40 aallowat Exp $
 */
public class EclipseSubmitterDefinitions
    extends BlueJSubmitterDefinitions
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new EclipseSubmitterDefinitions page.
     *
     * @param context The context for this page
     */
    public EclipseSubmitterDefinitions( WOContext context )
    {
        super( context );
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public int submitterEngine()
    {
        return 2;
    }


    // ----------------------------------------------------------
    public boolean useSecureSubmissionURLs()
    {
        return true;
    }


    // ----------------------------------------------------------
    public String mimeType()
    {
        return "text/xml";
    }


    // ----------------------------------------------------------
    public String escapeBareName( String name )
    {
        return escapeElementEntities( name );
    }


    // ----------------------------------------------------------
    public String escapeURLParameter( String name )
    {
        return escapeElementEntities( name );
    }


    // ----------------------------------------------------------
    public String thisPattern()
    {
        return escapeElementEntities( super.thisPattern() );
    }


    // ----------------------------------------------------------
    protected String escapeElementEntities( String text )
    {
        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for ( i = 0; i < size; i++ )
        {
            String entity = null;
            char c = text.charAt(i);

            switch ( c )
            {
                case '<':
                    entity = "&lt;";
                    break;

                case '>':
                    entity = "&gt;";
                    break;

                case '&':
                    entity = "&amp;";
                    break;

                case '\"':
                    entity = "&quot;";
                    break;

                case '\t':
                case '\n':
                case '\r':
                    entity = String.valueOf(c);
                    break;

                default:
                    if ( c < 32 )
                    {
                        entity = "&#" + (int) c + ";";
                    }
                    break;
            }

            if ( entity != null )
            {
                if ( block == null )
                {
                    block = text.toCharArray();
                }

                buffer.append( block, last, i - last );
                buffer.append( entity );
                last = i + 1;
            }
        }

        if ( last == 0 )
        {
            return text;
        }

        if ( last < size )
        {
            if ( block == null )
            {
                block = text.toCharArray();
            }

            buffer.append( block, last, i - last );
        }

        String answer = buffer.toString();
        buffer.setLength( 0 );

        return answer;
    }


    //~ Instance/static variables .............................................

    private StringBuffer buffer = new StringBuffer();
}
