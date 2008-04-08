/*==========================================================================*\
 |  $Id: ContentAssistIOUtils.java,v 1.1 2008/04/08 18:31:09 aallowat Exp $
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

package net.sf.webcat.oda.designer.contentassist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentAssistIOUtils
{
    public static void readSubsystemVersions(
            Map<String, String> subsystemVersions, BufferedReader reader)
            throws IOException
    {
        subsystemVersions.clear();

        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] parts = line.split(":"); //$NON-NLS-1$

            if (parts.length > 0)
            {
                if (parts[0].equals("version")) //$NON-NLS-1$
                {
                    String[] versionParts = parts[1].split(","); //$NON-NLS-1$

                    if (versionParts.length == 2)
                    {
                        subsystemVersions.put(versionParts[0], versionParts[1]);
                    }
                }
            }
        }
    }


    public static void readEntityDescriptions(
            Map<String, String> subsystemVersions,
            Map<String, List<ContentAssistAttributeInfo>> entityDescriptions,
            BufferedReader reader) throws IOException
    {
        subsystemVersions.clear();
        entityDescriptions.clear();

        ArrayList<ContentAssistAttributeInfo> attributes = null;

        String line;
        while ((line = reader.readLine()) != null)
        {
            String[] parts = line.split(":"); //$NON-NLS-1$

            if (parts.length > 0)
            {
                if (parts[0].equals("version")) //$NON-NLS-1$
                {
                    String[] versionParts = parts[1].split(","); //$NON-NLS-1$

                    if (versionParts.length == 2)
                    {
                        subsystemVersions.put(versionParts[0], versionParts[1]);
                    }
                }
                else if (parts[0].equals("entity")) //$NON-NLS-1$
                {
                    attributes = new ArrayList<ContentAssistAttributeInfo>();
                    entityDescriptions.put(parts[1], attributes);
                }
                else if (parts[0].equals("attribute")) //$NON-NLS-1$
                {
                    String[] attrParts = parts[1].split(","); //$NON-NLS-1$

                    if (attrParts.length == 2 && attributes != null)
                    {
                        attributes.add(new ContentAssistAttributeInfo(
                                attrParts[0], attrParts[1]));
                    }
                }
            }
        }
    }


    public static void writeEntityDescriptions(
            Map<String, String> subsystemVersions,
            Map<String, List<ContentAssistAttributeInfo>> entityDescriptions,
            BufferedWriter writer) throws IOException
    {
        for (String subsystem : subsystemVersions.keySet())
        {
            writer.write("version:"); //$NON-NLS-1$
            writer.write(subsystem);
            writer.write(","); //$NON-NLS-1$
            writer.write(subsystemVersions.get(subsystem));
            writer.newLine();
        }

        for (String entity : entityDescriptions.keySet())
        {
            writer.write("entity:"); //$NON-NLS-1$
            writer.write(entity);
            writer.newLine();

            for (ContentAssistAttributeInfo attrInfo : entityDescriptions
                    .get(entity))
            {
                writer.write("attribute:"); //$NON-NLS-1$
                writer.write(attrInfo.name());
                writer.write(","); //$NON-NLS-1$
                writer.write(attrInfo.type());
                writer.newLine();
            }
        }
    }


    public static void readObjectDescriptions(
            Map<String, List<ContentAssistObjectDescription>> objectDescriptions,
            BufferedReader reader) throws IOException
    {
        objectDescriptions.clear();

        ArrayList<ContentAssistObjectDescription> objects = null;
        String currentType = null;

        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith("entity:")) //$NON-NLS-1$
            {
                String rest = line.substring("entity:".length()); //$NON-NLS-1$

                currentType = rest;
                objects = new ArrayList<ContentAssistObjectDescription>();
                objectDescriptions.put(rest, objects);
            }
            else if (line.startsWith("object:")) //$NON-NLS-1$
            {
                String rest = line.substring("object:".length()); //$NON-NLS-1$

                int commaIndex = rest.indexOf(',');

                String idString = rest.substring(0, commaIndex);
                String desc = rest.substring(commaIndex + 1);

                int id = Integer.parseInt(idString);

                if (objects != null)
                {
                    objects.add(new ContentAssistObjectDescription(currentType,
                            id, desc));
                }
            }
        }
    }


    public static void writeObjectDescriptions(
            Map<String, List<ContentAssistObjectDescription>> objectDescriptions,
            BufferedWriter writer) throws IOException
    {
        for (String entity : objectDescriptions.keySet())
        {
            writer.write("entity:"); //$NON-NLS-1$
            writer.write(entity);
            writer.newLine();

            for (ContentAssistObjectDescription objDesc : objectDescriptions
                    .get(entity))
            {
                writer.write("object:"); //$NON-NLS-1$
                writer.write(Integer.toString(objDesc.id()));
                writer.write(","); //$NON-NLS-1$
                writer.write(objDesc.description());
                writer.newLine();
            }
        }
    }
}
