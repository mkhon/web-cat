/*==========================================================================*\
 |  $Id: Messages.java,v 1.1 2008/04/08 18:31:13 aallowat Exp $
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

package net.sf.webcat.oda.designer.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * Manages the internationalizable message strings in the Web-CAT Data Source
 * designer plug-in.
 *
 * @author Tony Allevato
 */
public class Messages extends NLS
{
    public static String CONTENTASSIST_CONNECTION_ERROR;
    public static String CONTENTASSIST_JOB_NAME;
    public static String CONTENTASSIST_PROGRESS_DESCRIPTION;
    public static String DATASET_DEFAULT_TITLE;
    public static String DATASET_DEFINE_QUERY_BUTTON;
    public static String DATASET_DEFINE_QUERY_LABEL;
    // -----------------------------------------------------------------------
    /*
     * Web-CAT Data Set properties page messages
     */
    public static String DATASET_ENTITY_LABEL;
    public static String DATASET_ENTITY_COMBO_LABEL;
    public static String DATASET_COLUMN_MAPPING_LABEL;
    public static String DATASET_NEW_COLUMN_PROMPT;
    public static String DATASET_COLUMN_NAME_HEADER;
    public static String DATASET_EXPRESSION_HEADER;
    public static String DATASET_TYPE_HEADER;
    public static String DATASET_COLUMN_MOVE_UP;
    public static String DATASET_COLUMN_MOVE_DOWN;
    public static String DATASET_COLUMN_REMOVE;
    public static String DATASET_UPDATE_ASSIST_INFO;
    public static String DATASET_COLUMN_NAME_IN_USE;
    public static String DATASET_EXPRESSION_INVALID;

    // -----------------------------------------------------------------------
    /*
     * Web-CAT Data Source preferences page messages
     */
    public static String PREFS_MAIN_LABEL;
    public static String PREFS_SERVER_URL_LABEL;
    public static String PREFS_USERNAME_LABEL;
    public static String PREFS_PASSWORD_LABEL;
    public static String PREFS_LIMITS_LABEL;
    public static String PREFS_MAX_RECORDS_LABEL;
    public static String PREFS_TIMEOUT_LABEL;

    // -----------------------------------------------------------------------
    /*
     * OGNL Expression Builder dialog messages
     */
    public static String EXPR_BUILDER_TITLE;
    public static String EXPR_BUILDER_PROMPT;
    public static String RESETPREVIEWCACHE_CONFIRMATION;
    public static String RESETPREVIEWCACHE_MSGBOX_NAME;

    public static String ACTIVATOR_SERVER_URL_NOT_SET;

    // -----------------------------------------------------------------------
    private static final String BUNDLE_NAME = "net.sf.webcat.oda.designer.i18n.messages"; //$NON-NLS-1$

    // -----------------------------------------------------------------------
    /**
     * Initializes the message table by loading the strings into the static
     * fields declared above.
     */
    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
}
