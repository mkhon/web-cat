package net.sf.webcat.reporter;

import java.util.Stack;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.DesignVisitor;
import org.eclipse.birt.report.model.api.ParameterGroupHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

public class ReportParameterVisitor extends DesignVisitor
{
    //~ Constructors ..........................................................
    
    // ----------------------------------------------------------
    public ReportParameterVisitor()
    {
        parameterGroups =
            new NSMutableArray<NSMutableDictionary<String, Object>>();
        
        uncategorizedGroup = new NSMutableDictionary<String, Object>();
        uncategorizedGroup.setObjectForKey("Uncategorized",
                IReportParameterConstants.NAME_KEY);
        uncategorizedGroup.setObjectForKey(
                new NSMutableArray<NSMutableDictionary<String, Object>>(),
                IReportParameterConstants.PARAMETERS_KEY);

        currentGroup = null;
    }
    
    
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    protected void visitReportDesign(ReportDesignHandle handle)
    {
        super.visitReportDesign(handle);

        applyToSlot(handle.getParameters());
    }

    
    // ----------------------------------------------------------
    protected void visitParameterGroup(ParameterGroupHandle handle)
    {
        super.visitParameterGroup(handle);

        processParameterGroup(handle);
    }
    

    // ----------------------------------------------------------
    protected void visitScalarParameter(ScalarParameterHandle handle)
    {
        super.visitScalarParameter(handle);
        
        processScalarParameter(handle);
    }

    
    // ----------------------------------------------------------
    public NSArray<NSMutableDictionary<String, Object>> parameterGroups()
    {
        NSArray<?> uncategorizedParams =
            (NSArray<?>) uncategorizedGroup.objectForKey(
                    IReportParameterConstants.PARAMETERS_KEY);

        if (uncategorizedParams.count() > 0)
        {
            NSMutableArray<NSMutableDictionary<String, Object>> groups =
                parameterGroups.mutableClone();
            
            groups.addObject(uncategorizedGroup);
            
            return groups;
        }
        else
        {
            return parameterGroups;
        }
    }


    // ----------------------------------------------------------
    /**
     * Iterates over the elements in a slot and applies the visitor to each of
     * them in order.
     *
     * @param slot the slot to iterate over
     */
    private void applyToSlot(SlotHandle slot)
    {
        for(int i = 0; i < slot.getCount(); i++)
        {
            apply(slot.get(i));
        }
    }


    // ----------------------------------------------------------
    private void processParameterGroup(ParameterGroupHandle handle)
    {
        currentGroup = new NSMutableDictionary<String, Object>();

        parameterGroups.addObject(currentGroup);

        NSMutableArray<NSMutableDictionary<String, Object>> params =
            new NSMutableArray<NSMutableDictionary<String, Object>>();

        String name = handle.getName();
        String displayName = handle.getDisplayName();

        currentGroup.setObjectForKey(name,
                IReportParameterConstants.NAME_KEY);

        if (displayName != null)
        {
            currentGroup.setObjectForKey(displayName,
                    IReportParameterConstants.DISPLAY_NAME_KEY);
        }
        
        currentGroup.setObjectForKey(params,
                IReportParameterConstants.PARAMETERS_KEY);
        
        applyToSlot(handle.getParameters());

        currentGroup = null;
    }


    // ----------------------------------------------------------
    private void processScalarParameter(ScalarParameterHandle handle)
    {
        NSMutableDictionary<String, Object> group;
        
        if (currentGroup != null)
            group = currentGroup;
        else
            group = uncategorizedGroup;

        NSMutableArray<NSMutableDictionary<String, Object>> params =
            (NSMutableArray<NSMutableDictionary<String, Object>>)
            group.objectForKey(IReportParameterConstants.PARAMETERS_KEY);

        String name = handle.getName();
        String promptText = handle.getDisplayPromptText();
        String helpText = handle.getHelpText();
        String type = handle.getDataType();
        String defaultValue = handle.getDefaultValue();
        boolean required = handle.isRequired();
        boolean hidden = handle.isHidden();
        boolean concealed = handle.isConcealValue();

        NSMutableDictionary<String, Object> param =
            new NSMutableDictionary<String, Object>();

        param.setObjectForKey(name, IReportParameterConstants.NAME_KEY);
        
        if (promptText != null)
        {
            param.setObjectForKey(promptText,
                    IReportParameterConstants.PROMPT_TEXT_KEY);
        }
        
        if (helpText != null)
        {
            param.setObjectForKey(helpText,
                    IReportParameterConstants.HELP_TEXT_KEY);
        }
        
        param.setObjectForKey(type, IReportParameterConstants.DATA_TYPE_KEY);
        
        if (defaultValue != null)
        {
            param.setObjectForKey(defaultValue,
                    IReportParameterConstants.DEFAULT_VALUE_KEY);
        }
        
        param.setObjectForKey(required, IReportParameterConstants.REQUIRED_KEY);
        param.setObjectForKey(hidden, IReportParameterConstants.HIDDEN_KEY);
        param.setObjectForKey(concealed,
                IReportParameterConstants.CONCEALED_KEY);

        params.addObject(param);
    }
    
    
    //~ Static/instance variables .............................................

    private NSMutableArray<NSMutableDictionary<String, Object>> parameterGroups;
    private NSMutableDictionary<String, Object> currentGroup;
    private NSMutableDictionary<String, Object> uncategorizedGroup;    
}
