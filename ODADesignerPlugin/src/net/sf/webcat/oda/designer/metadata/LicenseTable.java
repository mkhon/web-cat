/*==========================================================================*\
 |  $Id: LicenseTable.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

package net.sf.webcat.oda.designer.metadata;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import com.ibm.icu.util.ULocale;

public class LicenseTable
{
    // ------------------------------------------------------------------------
    private LicenseTable()
    {
        licenseMap = new Hashtable<String, String>();

        for (String[] licensePair : LICENSES)
        {
            licenseMap.put(licensePair[0], licensePair[1]);
        }
    }


    // ------------------------------------------------------------------------
    public static LicenseTable getInstance()
    {
        if (instance == null)
        {
            instance = new LicenseTable();
        }

        return instance;
    }


    // ------------------------------------------------------------------------
    public String[] getLicenses()
    {
        String[] licenses = new String[licenseMap.size()];
        licenseMap.keySet().toArray(licenses);
        Arrays.sort(licenses, String.CASE_INSENSITIVE_ORDER);

        return licenses;
    }


    // ------------------------------------------------------------------------
    public String getURLForLicense(String license)
    {
        return licenseMap.get(license);
    }


    private static LicenseTable instance;

    private Map<String, String> licenseMap;

    private final static String[][] LICENSES = {
            { "Apache License, version 2.0",
                    "http://www.apache.org/licenses/LICENSE-2.0", },
            { "GNU General Public License, version 1",
                    "http://www.gnu.org/licenses/old-licenses/gpl-1.0.txt", },
            { "GNU General Public License, version 2",
                    "http://www.gnu.org/licenses/old-licenses/gpl-2.0.html", },
            { "GNU General Public License, version 3",
                    "http://www.gnu.org/licenses/gpl-3.0.html", },
            { "MIT License",
                    "http://www.opensource.org/licenses/mit-license.php", },
            {
                    "Common Development and Distribution License (CDDL), version 1.0",
                    "http://www.sun.com/cddl/cddl.html", },
            { "Common Public License, version 1.0",
                    "http://www.ibm.com/developerworks/library/os-cpl.html", },
            { "Eclipse Public License, version 1.0",
                    "http://www.eclipse.org/legal/epl-v10.html", },
            { "Educational Community License",
                    "http://www.opensource.org/licenses/ecl1.php", },
            { "GNU Affero General Public License, version 3",
                    "http://www.gnu.org/licenses/agpl-3.0.html", }, };
}
