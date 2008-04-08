/*==========================================================================*\
 |  $Id: IPreferencesConstants.java,v 1.1 2008/04/08 18:31:09 aallowat Exp $
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

package net.sf.webcat.oda.designer.preferences;

/**
 * This interface provides definitions for the names of keys that are used to
 * store preferences information for the Web-CAT Data Source plug-in.
 *
 * @author Tony Allevato
 */
public interface IPreferencesConstants
{
    /**
     * The URL of the Web-CAT server that will be used to obtain data in a
     * report preview. This URL should start with the "http://" prefix and end
     * with the "WebCAT.woa" portion of the server address.
     */
    static final String SERVER_URL_KEY = "serverURL";

    /**
     * The user name that should be used to connect to the Web-CAT server.
     */
    static final String USERNAME_KEY = "username";

    /**
     * The password that should be used to connect to the Web-CAT server.
     */
    static final String PASSWORD_KEY = "password";

    /**
     * The maximum number of records to retrieve from the Web-CAT server during
     * a preview operation.
     */
    static final String MAX_RECORDS_KEY = "maxRecords";

    /**
     * The maximum amount of time, in seconds, to use to obtain preview data for
     * a single data set. If this time elapses, only those rows retrieved up to
     * that point will be returned.
     */
    static final String CONNECTION_TIMEOUT_KEY = "connectionTimeout";
}
