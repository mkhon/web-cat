/*==========================================================================*\
 |  $Id: ImageUtils.java,v 1.1 2008/04/08 18:31:13 aallowat Exp $
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

import java.io.IOException;
import java.net.URL;
import net.sf.webcat.oda.designer.DesignerActivator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public class ImageUtils
{
    public static Image getImage(String path)
    {
        Image image = null;

        try
        {
            URL url = FileLocator.find(DesignerActivator.getDefault().getBundle(),
                    new Path(path), null);
            url = FileLocator.resolve(url);
            image = ImageDescriptor.createFromURL(url).createImage();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return image;
    }
}