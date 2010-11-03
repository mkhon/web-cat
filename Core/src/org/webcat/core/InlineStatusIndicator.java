/*==========================================================================*\
 |  $Id: InlineStatusIndicator.java,v 1.1 2010/11/03 19:37:26 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2010 Virginia Tech
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

package org.webcat.core;

import org.webcat.ui.generators.JavascriptGenerator;
import org.webcat.ui.util.ComponentIDGenerator;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

//-------------------------------------------------------------------------
/**
 * A component that allows users to display small inline status update messages
 * along with a spinner that can be activated for longer running operations.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/11/03 19:37:26 $
 */
public class InlineStatusIndicator extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Initializes a new instance of InlineStatusIndicator.
     *
     * @param context the context
     */
    public InlineStatusIndicator(WOContext context)
    {
        super(context);
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public boolean isStateless()
    {
        return true;
    }


    // ----------------------------------------------------------
    public String id()
    {
        String id = (String) valueForBinding("id");

        if (id == null)
        {
            id = context().javaScriptElementID();
        }

        return id;
    }


    // ----------------------------------------------------------
    private static String spinnerId(String rootId)
    {
        return rootId + "_spinner";
    }


    // ----------------------------------------------------------
    public String spinnerId()
    {
        return spinnerId(id());
    }


    // ----------------------------------------------------------
    private static String messageBoxId(String rootId)
    {
        return rootId + "_messageBox";
    }


    // ----------------------------------------------------------
    public String messageBoxId()
    {
        return messageBoxId(id());
    }


    // ----------------------------------------------------------
    /**
     * Updates the indicator to start the spinner and display a message about
     * an ongoing operation.
     *
     * @param js the JavascriptGenerator into which to generate the client-side
     *     code
     * @param id the identifier assigned to the "id" binding of the
     *     InlineStatusIndicator
     * @param message the message to display in the indicator
     */
    public static void updateWithSpinner(JavascriptGenerator js,
            String id, String message)
    {
        js.removeClass(messageBoxId(id), WARNING)
            .removeClass(messageBoxId(id), ERROR)
            .removeClass(messageBoxId(id), SUCCESS);

        js.dijit(spinnerId(id)).call("start");
        js.assign("dojo.byId('" + messageBoxId(id) + "').innerHTML", message);
        js.style(messageBoxId(id), "opacity", "1");
    }


    // ----------------------------------------------------------
    /**
     * Updates the indicator to stop the spinner and display a message about
     * an ongoing operation, with an appropriate state class to color the
     * message. The message will also fade out after 5 seconds.
     *
     * @param js the JavascriptGenerator into which to generate the client-side
     *     code
     * @param id the identifier assigned to the "id" binding of the
     *     InlineStatusIndicator
     * @param stateClass one of the ERROR, WARNING, or SUCCESS constants
     *     defined in this class
     * @param message the message to display in the indicator
     */
    public static void updateWithState(JavascriptGenerator js, String id,
            String stateClass, String message)
    {
        js.addClass(messageBoxId(id), stateClass);

        js.dijit(spinnerId(id)).call("stop");
        js.assign("dojo.byId('" + messageBoxId(id) + "').innerHTML", message);
        js.fadeOut(JavascriptGenerator.newHash(
                "node", messageBoxId(id),
                "delay", 5000
                )).play();
    }


    //~ Static/instance variables .............................................

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String WARNING = "warning";
}
