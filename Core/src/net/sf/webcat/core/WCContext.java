/*==========================================================================*\
 |  $Id: WCContext.java,v 1.1 2010/03/15 16:48:38 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2006-2009 Virginia Tech
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

package net.sf.webcat.core;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import er.extensions.appserver.ERXWOContext54;

public class WCContext extends ERXWOContext54
{
    // ----------------------------------------------------------
    public WCContext(WORequest request)
    {
        super(request);
    }


    // ----------------------------------------------------------
    /**
     * Note that we call <tt>super.wasFormSubmitted</tt> instead of
     * <tt>super._wasFormSubmitted</tt>, because Wonder overrides only the
     * latter to provide its own partial submit handling. This may not actually
     * be necessary, since we use different request keys than Wonder does to
     * indicate that a request is partial, so Wonder's implementation would
     * never interfere with ours anyway.
     */
    @Override
    public boolean _wasFormSubmitted()
    {
        boolean wasFormSubmitted = super.wasFormSubmitted();
        return wasFormSubmittedIfPartialSubmit(wasFormSubmitted);
    }


    // ----------------------------------------------------------
    @Override
    public boolean wasFormSubmitted()
    {
        boolean wasFormSubmitted = super.wasFormSubmitted();
        return wasFormSubmittedIfPartialSubmit(wasFormSubmitted);
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * This is arguably a nicer implementation of similar logic that was built
     * in to the Wonder Ajax framework for supporting partial submits. This
     * <b>replaces</b> that functionality; in other words, the Web-CAT partial
     * submit functionality and Wonder's are <b>incompatible</b>.
     * </p><p>
     * An element that executes a remote action (say, a WCButton with "remote"
     * set to true) can choose to only submit a subset of its form, by
     * specifying a DOM node identifier in the "remote.submit" binding. Only
     * those form elements that are children of that DOM node will be
     * serialized in the request. (If this binding is omitted, the entire form
     * will be serialized.) This function ensures that the elements that are
     * not submitted will not have their bindings erroneously reset to null.
     * Every element calls <tt>WOContext.wasFormSubmitted</tt> in its
     * implementation of <tt>takeValuesForRequest</tt> to determine whether it
     * should update its value binding; this override will force the result to
     * be false if an element was not submitted in the partial request.
     * </p>
     *
     * @param wasFormSubmitted the original state of wasFormSubmitted as
     *     determined by the superclass implementation
     * @return forced to be false is the current element was not submitted as
     *     part of the partial request
     */
    public boolean wasFormSubmittedIfPartialSubmit(boolean wasFormSubmitted)
    {
        // Trick: wasFormSubmitted will be false if a partial submit comes from
        // a WCLink, WCConnectAction, or WCActionFunction. We would like to
        // synchronize bindings in those cases as well, so if the request is
        // a partial submit, we force it to be true and then check to see if
        // a value exists for a particular element before we (possibly) reset
        // it back to false.

        WORequest request = request();
        Object wasPartial = request.formValueForKey(
                "webcat.wasPartialSubmit");

        if (wasPartial != null)
        {
            wasFormSubmitted = true;
        }

        if (wasFormSubmitted && wasPartial != null)
        {
            // Check to see if a value exists in the request for whichever
            // elementID we are currently processing. If there isn't one, we
            // reset wasFormSubmitted to false so that the binding does not
            // get updated by the calling element.

            String name = elementID();
            String possibleElementID = (String) userInfoForKey(name);

            if (possibleElementID != null)
            {
                name = possibleElementID;
                setUserInfoForKey(null, name);
            }

            if (request.formValueForKey(name) == null)
            {
                wasFormSubmitted = false;
            }
        }

        return wasFormSubmitted;
    }


    // ----------------------------------------------------------
    public static void installIntoApplication(WOApplication application)
    {
        String ctxClassName = application.contextClassName();

        if ("ERXWOContext".equals(ctxClassName) ||
                "ERXWOContext54".equals(ctxClassName))
        {
            application.setContextClassName(WCContext.class.getSimpleName());
        }
    }
}