/*==========================================================================*\
 |  $Id: WOActionDispatcher.java,v 1.1 2008/04/08 18:31:13 aallowat Exp $
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

package net.sf.webcat.oda.designer.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import net.sf.webcat.oda.designer.DesignerActivator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class WOActionDispatcher
{
    public WOActionDispatcher(String serverUrl)
    {
        this.serverUrl = serverUrl;
    }


    public WOActionResponse send(String action, String sessionId,
            Map<String, String> parameters)
    {
        HttpURLConnection connection = null;
        IStatus status = Status.OK_STATUS;
        InputStream stream = null;

        try
        {
            StringBuilder params = new StringBuilder();
            boolean firstParameter = true;

            if (sessionId != null)
            {
                params.append("wosid=");
                params.append(URLEncoder.encode(sessionId, "UTF-8"));

                firstParameter = false;
            }

            if (parameters != null)
            {
                for (String parameterName : parameters.keySet())
                {
                    if (!firstParameter)
                        params.append('&');

                    params.append(parameterName);
                    params.append('=');
                    params.append(URLEncoder.encode(parameters
                            .get(parameterName), "UTF-8"));

                    firstParameter = false;
                }
            }

            byte[] paramBytes = params.toString().getBytes();

            URL actionUrl = new URL(serverUrl + "/wa/" + action);

            connection = (HttpURLConnection) actionUrl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer
                    .toString(paramBytes.length));
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(paramBytes);
            outputStream.flush();

            stream = connection.getInputStream();
        }
        catch (Exception e)
        {
            status = new Status(IStatus.ERROR, DesignerActivator.PLUGIN_ID, e
                    .getMessage(), e);
        }

        return new WOActionResponse(status, connection, stream);
    }


    private String serverUrl;
}
