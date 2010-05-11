/*==========================================================================*\
 |  $Id: EditBatchPluginFilesPage.java,v 1.1 2010/05/11 14:51:46 aallowat Exp $
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

package org.webcat.batchprocessor;

import com.webobjects.foundation.*;
import com.webobjects.appserver.*;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;
import org.webcat.archives.ArchiveManager;
import org.webcat.core.*;
import org.webcat.ui.generators.JavascriptGenerator;

// -------------------------------------------------------------------------
/**
 * This class presents the list of scripts (grading steps) that
 * are available for selection.
 *
 * @author Stephen Edwards
 * @author  latest changes by: $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2010/05/11 14:51:46 $
 */
public class EditBatchPluginFilesPage
    extends WCComponent
    implements EditFilePage.FileEditListener
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * This is the default constructor
     *
     * @param context The page's context
     */
    public EditBatchPluginFilesPage( WOContext context )
    {
        super( context );

        refresher = new JavascriptGenerator()
            .refresh("fileBrowser", "folderList1", "folderList2");
    }


    //~ KVC Attributes (must be public) .......................................

    public BatchPlugin batchPlugin;
    public File       base;
    public boolean    isEditable;
    public boolean    allowSelectDir;
    public NSArray    allowSelectExtensions;
    public String     folderName;
    public String     aFolder;
    public String     selectedParentFolderForSubFolder;
    public String     selectedParentFolderForUpload;
    public NSMutableArray folderList;
    public NSData     uploadedFile2;
    public String     uploadedFileName2;
    public NSData     uploadedFile3;
    public String     uploadedFileName3;
    public boolean    unzip = false;
    public FileBrowser.FileSelectionListener fileSelectionListener = null;
    public String     currentSelection;


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public void appendToResponse(WOResponse response, WOContext context)
    {
        log.debug( "listener = " + fileSelectionListener );
        folderName = null;
        if ( base == null )
        {
            if ( batchPlugin.hasSubdir() )
            {
                base = new File( batchPlugin.dirName() );
            }
            else
            {
                base = new File( batchPlugin.mainFilePath() );
            }
        }

        folderList = null;
        refreshFolderList();

        super.appendToResponse( response, context );
    }


    // ----------------------------------------------------------
    private void refreshFolderList()
    {
        if ( folderList == null )
        {
            folderList = new NSMutableArray();
            String parent = base.getParent();
            int stripLength = 0;
            if ( parent != null && parent.length() > 0 )
            {
                stripLength = parent.length() + 1;
            }
            addFolders( folderList, base, stripLength );
        }
    }


    // ----------------------------------------------------------
    private void addFolders( NSMutableArray list, File file, int stripLength )
    {
        if ( !file.isDirectory() ) return;
        String name = file.getName();
        if ( name.equals( "." ) || name.equals( ".." ) ) return;
        name = file.getPath().substring( stripLength )
            .replaceAll( "\\\\", "/" );
        list.addObject( name );
        File[] files = file.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            addFolders( list, files[i], stripLength );
        }
    }


    // ----------------------------------------------------------
    public String sideStepTitle()
    {
        if ( title == null )
        {
            title = isEditable ? "Edit Your " : "Browse Your ";
            if ( batchPlugin == null )
            {
                title += "Configuration ";
            }
            else
            {
                title += "Script ";
            }
            if ( base.isDirectory() )
            {
                title += "Files";
            }
            else
            {
                title += "File";
            }
        }
        return title;
    }


    // ----------------------------------------------------------
    public String browserTitle()
    {
        return sideStepTitle();
    }


    // ----------------------------------------------------------
    public boolean allowSelection()
    {
        return fileSelectionListener != null;
    }


    // ----------------------------------------------------------
    public JavascriptGenerator createFolder()
    {
        if (!applyLocalChanges())
        {
            return new JavascriptGenerator();
        }

        if ( folderName == null || folderName.length() == 0 )
        {
            error( "Please enter a folder name." );
        }
        else
        {
            File target =
                new File( base.getParent(),
                          selectedParentFolderForSubFolder + "/" + folderName );
            try
            {
                target.mkdirs();
            }
            catch ( Exception e )
            {
                error( e.getMessage() );
            }
        }

        folderList = null;
        refreshFolderList();

        return refresher;
    }


    // ----------------------------------------------------------
    public JavascriptGenerator fileWasUploaded()
    {
        if (!applyLocalChanges())
        {
            return new JavascriptGenerator();
        }

        if ( unzip && FileUtilities.isArchiveFile( uploadedFileName2 ) )
        {
            File target =
                new File( base.getParent(), selectedParentFolderForUpload );
            // ZipInputStream zipStream =
            //    new ZipInputStream( uploadedFile2.stream() );
            try
            {
                ArchiveManager.getInstance().unpack(
                    target, uploadedFileName2, uploadedFile2.stream() );
            }
            catch ( java.io.IOException e )
            {
                error( e.getMessage() );
            }

            folderList = null;
            refreshFolderList();
        }
        else
        {
            uploadedFileName2 = new File( uploadedFileName2 ).getName();
            File target =
                new File( base.getParent(), selectedParentFolderForUpload
                                            + "/" + uploadedFileName2 );
            try
            {
                FileOutputStream out = new FileOutputStream( target );
                uploadedFile2.writeToStream( out );
                out.close();
            }
            catch ( java.io.IOException e )
            {
                error( e.getMessage() );
            }
        }

        if ( batchPlugin != null )
        {
            batchPlugin.initializeConfigAttributes();
            applyLocalChanges();
        }

        return refresher;
    }


    // ----------------------------------------------------------
    public JavascriptGenerator folderReplacementWasUploaded()
    {
        if (!applyLocalChanges())
        {
            return new JavascriptGenerator();
        }

        if ( FileUtilities.isArchiveFile( uploadedFileName3 ) )
        {
            org.webcat.core.FileUtilities.deleteDirectory( base );
            base.mkdirs();
            // ZipInputStream zipStream =
            //    new ZipInputStream( uploadedFile3.stream() );
            try
            {
                ArchiveManager.getInstance().unpack(
                    base, uploadedFileName3, uploadedFile3.stream() );
            }
            catch ( java.io.IOException e )
            {
                error( e.getMessage() );
            }
            if ( batchPlugin != null )
            {
                batchPlugin.initializeConfigAttributes();
                applyLocalChanges();
            }

            folderList = null;
            refreshFolderList();
        }
        else
        {
            error( "To replace this entire folder, you must upload a "
                          + "zip or a jar file." );
        }

        return refresher;
    }


    // ----------------------------------------------------------
    public boolean nextEnabled()
    {
        return !hideNextBack
            && ( nextPage != null || currentTab().hasNextSibling() );
    }


    // ----------------------------------------------------------
    public boolean backEnabled()
    {
        return !hideNextBack && super.backEnabled();
    }


    // ----------------------------------------------------------
    public void hideNextAndBack( boolean value )
    {
        hideNextBack = value;
    }


    // ----------------------------------------------------------
    public void saveFile( String fileName )
    {
        if ( batchPlugin != null )
        {
            batchPlugin.initializeConfigAttributes();
            batchPlugin.setLastModified( new NSTimestamp() );
            applyLocalChanges();
        }
    }


    // ----------------------------------------------------------
    public EditFilePage.FileEditListener thisPage()
    {
        return this;
    }


    //~ Instance/static variables .............................................

    // Since the same panes are refreshed by most of the upload actions, we
    // create a common JavascriptGenerator here to be returned by those
    // methods.
    private JavascriptGenerator refresher;

    private String title;
    private boolean hideNextBack = false;
    static Logger log = Logger.getLogger( EditBatchPluginFilesPage.class );
}
