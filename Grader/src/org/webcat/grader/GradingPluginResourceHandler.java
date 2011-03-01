package org.webcat.grader;

import java.io.File;
import org.webcat.core.EntityResourceHandler;

//-------------------------------------------------------------------------
/**
 * The Web-CAT entity resource handler for accessing resources associated with
 * BatchResult entities through direct URLs.
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/03/01 18:01:07 $
 */
public class GradingPluginResourceHandler
    extends EntityResourceHandler<GradingPlugin>
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    public File pathForResource(GradingPlugin object, String relativePath)
    {
        return object.fileForPublicResourceAtPath(relativePath);
    }
}
