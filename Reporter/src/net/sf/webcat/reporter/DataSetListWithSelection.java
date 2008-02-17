package net.sf.webcat.reporter;

import net.sf.webcat.core.WCComponent;

import com.webobjects.appserver.*;
import com.webobjects.foundation.NSArray;

/**
 * This component is used to display a list of data sets that are defined in a
 * report during the query construction phase of report generation. It also
 * allows one of the data sets to act as the current "selection" -- this data
 * set will be highlighted and have its description elaborated in the list for
 * emphasis.
 * 
 * @binding dataSets the data sets to be displayed in the list
 * @binding selection a data set equal to one of the elements in dataSets, this
 *     entry will be highlighted when the list is displayed
 * 
 * @author aallowat
 */
public class DataSetListWithSelection extends WCComponent
{
    public DataSetListWithSelection(WOContext context)
    {
        super(context);
    }
    
    // --- Component bindings -----------------------------
    
    /**
     * The data sets to iterate over in the list.
     */
    public NSArray<ReportDataSet> dataSets;
    
    /**
     * The current data set that should be highlighted and have its description
     * displayed in the list.
     */
    public ReportDataSet selection;

    // --- Internal state ---------------------------------
    
    /**
     * The current data set in the iteration.
     */
    public ReportDataSet dataSet;
}