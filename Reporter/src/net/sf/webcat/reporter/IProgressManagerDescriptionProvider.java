/*==========================================================================*\
 |  $Id: IProgressManagerDescriptionProvider.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
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

package net.sf.webcat.reporter;

//-------------------------------------------------------------------------
/**
 * Defines the interface for objects that can provide job descriptions
 * to the {@link ProgressManager}.
 *
 * @author  Anthony Allevato
 * @version $Id: IProgressManagerDescriptionProvider.java,v 1.4 2008/04/02 01:36:38 stedwar2 Exp $
 */
public interface IProgressManagerDescriptionProvider
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Get the description for a given job.
     * @param jobToken The job to describe
     * @return The description.
     */
	String description(Object jobToken);
}
