package org.webcat.grader;

import java.io.File;
import org.webcat.core.EntityResourceHandler;
import com.webobjects.eocontrol.EOFetchSpecification;
import er.extensions.eof.ERXQ;

//-------------------------------------------------------------------------
/**
 * The Web-CAT entity resource handler for accessing resources associated with
 * BatchResult entities through direct URLs.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.2 $, $Date: 2011/03/07 16:11:14 $
 */
public class GradingPluginResourceHandler
    extends EntityResourceHandler<GradingPlugin>
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public File pathForResource(GradingPlugin object, String relativePath)
    {
        return object.fileForPublicResourceAtPath(relativePath);
    }


    // ----------------------------------------------------------
    @Override
    public EOFetchSpecification fetchSpecificationForFriendlyName(String name)
    {
        return new EOFetchSpecification(
                GradingPlugin.ENTITY_NAME,
                GradingPlugin.name.is(name),
                GradingPlugin.lastModified.ascs());
    }
}
