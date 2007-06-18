import ognl.OgnlException;

import com.webobjects.eocontrol.EOQualifier;

import junit.framework.TestCase;


public class OgnlTest extends TestCase
{
	public void testOgnlAST() throws OgnlException
	{
		EOQualifier q;
		q = ognl.OgnlQualifierUtils.qualifierFromOgnlExpression(
				"a == 'text'");
		q = ognl.OgnlQualifierUtils.qualifierFromOgnlExpression(
				"!(a.b.d < #b.d)");
		q = ognl.OgnlQualifierUtils.qualifierFromOgnlExpression(
				"d.isLike('pattern')");
	}
}
