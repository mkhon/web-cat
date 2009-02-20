package net.sf.webcat.core;

import com.webobjects.foundation.NSArray;

//--------------------------------------------------------------------------
/**
 * 
 * @author Tony Allevato
 * @version $Id: INavigatorObject.java,v 1.1 2009/02/20 02:27:21 aallowat Exp $
 */
public interface INavigatorObject
{
    // ----------------------------------------------------------
    /**
     * Gets the set of (possibly one) entities that are represented by this
     * navigator object.
     * 
     * @return the array of represented objects
     */
    NSArray<?> representedObjects();
}
