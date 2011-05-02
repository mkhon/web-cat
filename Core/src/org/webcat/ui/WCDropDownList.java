package org.webcat.ui;

import org.webcat.ui.generators.JavascriptGenerator;
import org.webcat.ui.util.ComponentIDGenerator;
import org.webcat.ui.util.JSHash;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSArray;
import er.extensions.appserver.ERXWOContext;

//-------------------------------------------------------------------------
/**
 * TODO real description
 *
 * @author  Tony Allevato
 * @author  Last changed by $Author: aallowat $
 * @version $Revision: 1.1 $, $Date: 2011/05/02 19:35:12 $
 */
public class WCDropDownList extends WOComponent
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    public WCDropDownList(WOContext context)
    {
        super(context);
    }


    //~ KVC attributes (must be public) .......................................

    public String id;
    public String menuId;
    public String title;
    public String noSelectionString;
    public String maximumSize;

    public ComponentIDGenerator idFor = new ComponentIDGenerator(this);


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if (id == null)
        {
            id = idFor.get();
        }

        if (menuId == null)
        {
            menuId = idFor.get("menu");
        }

        WCDropDownList oldList = setCurrentDropDownList(this);

        super.appendToResponse(response, context);

        setCurrentDropDownList(oldList);
    }


    // ----------------------------------------------------------
    @Override
    public void takeValuesFromRequest(WORequest request, WOContext context)
    {
        WCDropDownList oldList = setCurrentDropDownList(this);

        super.takeValuesFromRequest(request, context);

        setCurrentDropDownList(oldList);
    }


    // ----------------------------------------------------------
    @Override
    public WOActionResults invokeAction(WORequest request, WOContext context)
    {
        WCDropDownList oldList = setCurrentDropDownList(this);

        WOActionResults result = super.invokeAction(request, context);

        setCurrentDropDownList(oldList);

        return result;
    }


    // ----------------------------------------------------------
    public String triggerSetItemToSelection()
    {
        setItem(selection());
        isRenderingTitleArea = true;
        return null;
    }


    // ----------------------------------------------------------
    public String triggerClearItem()
    {
        setItem(null);
        isRenderingTitleArea = false;
        return null;
    }


    // ----------------------------------------------------------
    protected boolean isRenderingTitleArea()
    {
        return isRenderingTitleArea;
    }


    // ----------------------------------------------------------
    public NSArray<?> list()
    {
        return valueForNSArrayBindings("list", null);
    }


    // ----------------------------------------------------------
    public void setList(NSArray<?> list)
    {
        // Do nothing; keep KVC quiet.
    }


    // ----------------------------------------------------------
    public Object item()
    {
        return valueForBinding("item");
    }


    // ----------------------------------------------------------
    public void setItem(Object item)
    {
        setValueForBinding(item, "item");
    }


    // ----------------------------------------------------------
    public int index()
    {
        return valueForIntegerBinding("index", 0);
    }


    // ----------------------------------------------------------
    public void setIndex(int index)
    {
        if (canSetValueForBinding("index"))
        {
            setValueForBinding(index, "index");
        }
    }


    // ----------------------------------------------------------
    public Object selection()
    {
        return valueForBinding("selection");
    }


    // ----------------------------------------------------------
    public void setSelection(Object selection)
    {
        if (canSetValueForBinding("selection"))
        {
            setValueForBinding(selection, "selection");
        }
    }


    // ----------------------------------------------------------
    public boolean hasDisplayString()
    {
        return hasBinding("displayString");
    }


    // ----------------------------------------------------------
    public String displayString()
    {
        return valueForStringBinding("displayString", null);
    }


    // ----------------------------------------------------------
    public String displayStringForItem()
    {
        if (hasDisplayString())
        {
            return displayString();
        }
        else
        {
            return item().toString();
        }
    }


    // ----------------------------------------------------------
    public boolean hasContent()
    {
        if (_childTemplate() instanceof WODynamicGroup)
        {
            WODynamicGroup group = (WODynamicGroup) _childTemplate();
            return group.hasChildrenElements();
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    public void setDisplayString(String value)
    {
        // Do nothing; keep KVC quiet.
    }


    // ----------------------------------------------------------
    public String cssListStyle()
    {
        if (maximumSize != null)
        {
            StringBuilder buffer = new StringBuilder();

            String[] parts = maximumSize.split(",");

            if (parts[0].length() > 0)
            {
                buffer.append("max-width: ");
                buffer.append(parts[0]);
            }

            if (parts.length > 1 && parts[1].length() > 0)
            {
                buffer.append("; max-height: " + parts[1]);
            }

            return buffer.toString();
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    public String selectionId()
    {
        return idFor.get("selection");
    }


    // ----------------------------------------------------------
    public String displayStringForSelection()
    {
        if (title != null)
        {
            return title;
        }
        else
        {
            setItem(selection());
            return displayString();
        }
    }


    // ----------------------------------------------------------
    protected JavascriptGenerator selectCurrentItem()
    {
        setSelection(item());
        return null;
    }


    // ----------------------------------------------------------
    public String showMenuScript()
    {
        JavascriptGenerator js = new JavascriptGenerator();
        js.call("webcat.dropDownList.showDropDown",
                JSHash.code("event"),
                id, menuId);
        return js.toString(true);
    }


    // ----------------------------------------------------------
    public String hideMenuScript()
    {
        return hideMenu(false).toString(true);
    }


    // ----------------------------------------------------------
    protected JavascriptGenerator hideMenu(boolean force)
    {
        JavascriptGenerator js = new JavascriptGenerator();
        js.call("webcat.dropDownList.hideDropDown",
                JSHash.code("event"),
                id, menuId, force);
        return js;
    }


    // ----------------------------------------------------------
    public static WCDropDownList currentDropDownList()
    {
        return (WCDropDownList) ERXWOContext.contextDictionary().objectForKey(
                CURRENT_DROP_DOWN_LIST_KEY);
    }


    // ----------------------------------------------------------
    public static WCDropDownList setCurrentDropDownList(WCDropDownList list)
    {
        WCDropDownList oldList =
            (WCDropDownList) ERXWOContext.contextDictionary().objectForKey(
                    CURRENT_DROP_DOWN_LIST_KEY);

        if (list == null)
        {
            ERXWOContext.contextDictionary().removeObjectForKey(
                    CURRENT_DROP_DOWN_LIST_KEY);
        }
        else
        {
            ERXWOContext.contextDictionary().setObjectForKey(list,
                    CURRENT_DROP_DOWN_LIST_KEY);
        }

        return oldList;
    }


    //~ Static/instance variables .............................................

    private static final String CURRENT_DROP_DOWN_LIST_KEY =
        "org.webcat.ui.WCDropDownList.currentDropDownList";

    private boolean isRenderingTitleArea;
}
