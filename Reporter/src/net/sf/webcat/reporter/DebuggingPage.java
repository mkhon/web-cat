package net.sf.webcat.reporter;

import ognl.Node;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.enhance.ExpressionAccessor;
import net.sf.webcat.grader.Submission;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSComparator.ComparisonException;
import er.extensions.eof.ERXQ;
import er.ajax.AjaxTreeModel;

public class DebuggingPage extends ReporterComponent
{
    public DebuggingPage(WOContext context)
    {
        super(context);
    }
    
    
    public String entity = "Submission";
    public String qualifier;
    public String expression;
    public NSMutableArray<String> entities;
    public NSMutableArray results;
    public NSDictionary result;
    public int index;
    
    public Node astItem;
    public ASTDelegate astDelegate = new ASTDelegate();
    public Node astRoot = null;
    
    
    @Override
    public void appendToResponse(WOResponse response, WOContext context)
    {
        entities = new NSMutableArray<String>();

        for (EOModel model : EOModelGroup.defaultGroup().models())
        {
            for (EOEntity entity : model.entities())
            {
                entities.addObject(entity.name());
            }
        }

        try
        {
            entities.sortUsingComparator(
                    NSComparator.AscendingCaseInsensitiveStringComparator);
        }
        catch (ComparisonException e)
        {
            e.printStackTrace();
        }

        super.appendToResponse(response, context);
    }
    
    
    public WOActionResults executeExpression()
    {
        // Fetch some objects
        if (expression != null && expression.length() > 0)
        {
            EOQualifier q = null;
            
            if (qualifier != null && qualifier.length() > 0)
            {
                q = EOQualifier.qualifierWithQualifierFormat(qualifier, null);
            }

            EOFetchSpecification fetch = new EOFetchSpecification(entity, q, null);
            fetch.setFetchLimit(100);
            NSArray<Submission> submissions = localContext().objectsWithFetchSpecification(fetch);

            OgnlContext context = ReportUtilityEnvironment.newOgnlContext();
            ExpressionAccessor accessor = null;

            try
            {
                Node node = Ognl.compileExpression(context, null, expression);
                astRoot = node;
                accessor = node.getAccessor();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }

            results = new NSMutableArray();

            for (EOGenericRecord sub : submissions)
            {
                NSMutableDictionary result = new NSMutableDictionary();
                result.setObjectForKey(sub.valueForKey("id").toString(), "id");

                try
                {
                    context.setRoot(sub);
                    context.setCurrentObject(sub);
                    
                    String value = Ognl.getValue(accessor, context, sub).toString(); 
                    result.setObjectForKey(value, "value");
                }
                catch (Exception e)
                {
                    result.setObjectForKey("<span style='color: red'>" + e.toString() + "</span>",
                            "value");
                }
                
                results.addObject(result);
            }
        }

        return null;  
    }
    

    public static class ASTDelegate implements AjaxTreeModel.Delegate
    {
        public NSArray childrenTreeNodes(Object node)
        {
            if (node == null)
            {
                return null;
            }

            NSMutableArray<Node> nodes = new NSMutableArray<Node>();
            
            for (int i = 0; i < ((Node) node).jjtGetNumChildren(); i++)
            {
                nodes.addObject(((Node) node).jjtGetChild(i));
            }
            
            return nodes;
        }

        public boolean isLeaf(Object node)
        {
            if (node == null)
            {
                return true;
            }
            
            return ((Node) node).jjtGetNumChildren() == 0;
        }

        public Object parentTreeNode(Object node)
        {
            if (node == null)
            {
                return null;
            }

            return ((Node) node).jjtGetParent();
        }
    }
}