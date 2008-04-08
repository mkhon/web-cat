/*==========================================================================*\
 |  $Id: OgnlEditorInput.java,v 1.1 2008/04/08 18:31:00 aallowat Exp $
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

package net.sf.webcat.oda.designer.ognl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class OgnlEditorInput implements IEditorInput
{
    private class OgnlStorage implements IStorage
    {

        /**
         *
         */
        public OgnlStorage()
        {
            super();
        }


        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.resources.IStorage#getContents()
         */
        public InputStream getContents() throws CoreException
        {
            if (name == null)
            {
                name = ""; //$NON-NLS-1$
            }

            return new ByteArrayInputStream(name.getBytes());
        }


        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.resources.IStorage#getFullPath()
         */
        public IPath getFullPath()
        {
            return null;
        }


        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.resources.IStorage#getName()
         */
        public String getName()
        {
            return name;
        }


        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.resources.IStorage#isReadOnly()
         */
        public boolean isReadOnly()
        {
            return false;
        }


        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
         */
        @SuppressWarnings("unchecked")
        public Object getAdapter(Class adapter)
        {
            return null;
        }
    }


    private String name = null;


    /**
     * @param _name
     *            the name of the editor input
     */
    public OgnlEditorInput(String _name)
    {
        super();
        this.name = _name;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists()
    {
        // TODO Auto-generated method stub
        return false;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return name;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        return null;
    }


    /**
     * @see org.eclipse.ui.IStorageEditorInput#getStorage()
     * @return IStorage
     * @throw CoreException
     */
    public IStorage getStorage()
    {
        return new OgnlStorage();
    }
}
