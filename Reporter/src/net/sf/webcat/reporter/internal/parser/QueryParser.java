package net.sf.webcat.reporter.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.webcat.reporter.ReportParameter;
import net.sf.webcat.reporter.internal.datamodel.AliasStatement;
import net.sf.webcat.reporter.internal.datamodel.IterationStatement;
import net.sf.webcat.reporter.internal.datamodel.Query;

import ognl.OgnlException;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;

public class QueryParser
{
	private AbstractLexer lexer;
	
	private QueryLexer queryLexer;

	private OgnlLexer ognlLexer;

	private Query query;
	
	public QueryParser(String text, Query query)
	{
		TokenSource source = new TokenSource(text);
		queryLexer = new QueryLexer(source);
		ognlLexer = new OgnlLexer(source);
		
		lexer = queryLexer;
		this.query = query;
	}
	
	public void processQueryString()
	{
		parseQuery();
	}
	
	private void parseQuery()
	{
		int token;
		
		while((token = lexer.nextToken()) != Token.END_OF_INPUT)
		{
			if(token == Token.FOR)
			{
				parseForStatement();
			}
			else if(token == Token.LET)
			{
				parseLetStatement();
			}
			else if(token == Token.SEMI)
			{
				// do nothing
			}
			else
			{
				lexer.error("expected 'FOR' or 'LET'");
			}
		}
	}
	
	private void parseForStatement()
	{
		NSMutableDictionary options = new NSMutableDictionary();

		if(lexer.nextToken() != Token.ID)
			lexer.error("expected name of binding after 'FOR'");

		options.setObjectForKey(lexer.tokenValue(), "binding");

		if(lexer.nextToken() != Token.IN)
			lexer.error("expected 'IN'");
		
		lexer = ognlLexer;

		StringBuffer inClauseBuffer = new StringBuffer();

		int token = lexer.nextToken();
		while(!OgnlLexer.isStopWord(token))
		{
			inClauseBuffer.append(lexer.tokenValue());
			token = lexer.nextToken();
		}
		
		String inClause = inClauseBuffer.toString().trim();

		// Determine if the "expression" is actually the name of an EO model
		// entity. If so, we handle it as a special case (since the OGNL
		// grammar/semantics prevent such a name from being a valid expression
		// anyway.
		EOEntity entity = EOModelGroup.defaultGroup().entityNamed(inClause);

		if(entity != null)
		{
			options.setObjectForKey(inClause, ReportParameter.OPTION_ENTITY);
		}
		else
		{
			try
			{
				Object ast = ognl.Ognl.parseExpression(inClause);
				options.setObjectForKey(ast, ReportParameter.OPTION_SOURCE);
			}
			catch (OgnlException oe)
			{
				throw new IllegalArgumentException(
						"IN clause was not a valid OGNL expression", oe);
			}
		}
		
		if(token == Token.WHOSE)
		{
			StringBuffer whoseClause = new StringBuffer();

			token = lexer.nextToken();
			while(!OgnlLexer.isStopWord(token))
			{
				whoseClause.append(lexer.tokenValue());
				token = lexer.nextToken();
			}
			
			EOQualifier qualifier =
				qualifierForOgnlExpression(whoseClause.toString());
			options.setObjectForKey(qualifier,
					ReportParameter.OPTION_QUALIFIER);
		}

		if(token == Token.WHERE)
		{
			StringBuffer whereClause = new StringBuffer();

			token = lexer.nextToken();
			while(!OgnlLexer.isStopWord(token))
			{
				whereClause.append(lexer.tokenValue());
				token = lexer.nextToken();
			}

			try
			{
				Object ast = ognl.Ognl.parseExpression(whereClause.toString());
				options.setObjectForKey(ast, ReportParameter.OPTION_FILTER);
			}
			catch (OgnlException e)
			{
				throw new IllegalArgumentException(
						"WHERE clause was not a valid OGNL expression", e);
			}
		}
		
		lexer = queryLexer;

		if(token == Token.ORDER)
		{
			if(lexer.nextToken() != Token.BY)
				lexer.error("expected 'BY' after 'ORDER");

			NSMutableArray sortOrderings = new NSMutableArray();
			token = parseOrderBy(sortOrderings);
			options.setObjectForKey(sortOrderings,
					ReportParameter.OPTION_SORT_ORDERINGS);
		}

		if(token != Token.SEMI &&
			token != Token.END_OF_INPUT)
			lexer.error("expected semicolon or end-of-input");
		
		// Add the statement to the query here
		query.addStatement(new IterationStatement(options));
	}

	private static EOQualifier qualifierForOgnlExpression(String expression)
	{
		try
		{
			return ognl.OgnlQualifierUtils.
				qualifierFromOgnlExpression(expression);
		}
		catch (OgnlException e)
		{
			throw new IllegalArgumentException("Entity qualifier expression " +
					"was malformed: " + e.getMessage(), e);
		}
	}

	private int parseOrderBy(NSMutableArray orderings)
	{
		int token;
	
		do
		{
			token = lexer.nextToken();

			String key = "";
			NSSelector selector = null;
			
			if(token != Token.ID)
				lexer.error("expected key for sorting criteria");

			key = lexer.tokenValue();
			
			token = lexer.nextToken();
			while(token == Token.DOT)
			{
				token = lexer.nextToken();
				if(token != Token.ID)
					lexer.error("expected identifier after '.'");

				key += "." + lexer.tokenValue();

				token = lexer.nextToken();
			}
			
			if(token != Token.ASC && token != Token.DESC)
				lexer.error("expected 'ASC' or 'DESC' after sort key");
			else if(token == Token.ASC)
				selector = EOSortOrdering.CompareAscending;
			else
				selector = EOSortOrdering.CompareDescending;
			
			orderings.addObject(
					EOSortOrdering.sortOrderingWithKey(key, selector));
			
			token = lexer.nextToken();
		}
		while(token == Token.COMMA);

		return token;
	}

	private void parseLetStatement()
	{
		NSMutableDictionary options = new NSMutableDictionary();

		if(lexer.nextToken() != Token.ID)
			lexer.error("expected name of new binding after 'LET'");

		options.setObjectForKey(lexer.tokenValue(), "binding");

		if(lexer.nextToken() != Token.EQUAL)
			lexer.error("expected '='");

		lexer = ognlLexer;

		StringBuffer inClauseBuffer = new StringBuffer();

		int token = lexer.nextToken();
		while(!OgnlLexer.isStopWord(token))
		{
			inClauseBuffer.append(lexer.tokenValue());
			token = lexer.nextToken();
		}
		
		String inClause = inClauseBuffer.toString().trim();

		// Determine if the "expression" is actually the name of an EO model
		// entity. If so, we handle it as a special case (since the OGNL
		// grammar/semantics prevent such a name from being a valid expression
		// anyway.
		EOEntity entity = EOModelGroup.defaultGroup().entityNamed(inClause);

		if(entity != null)
		{
			options.setObjectForKey(inClause, ReportParameter.OPTION_ENTITY);
		}
		else
		{
			try
			{
				Object ast = ognl.Ognl.parseExpression(inClause);
				options.setObjectForKey(ast, ReportParameter.OPTION_SOURCE);
			}
			catch (OgnlException oe)
			{
				throw new IllegalArgumentException(
						"IN clause was not a valid OGNL expression", oe);
			}
		}
		
		if(token == Token.WHOSE)
		{
			StringBuffer whoseClause = new StringBuffer();

			token = lexer.nextToken();
			while(!OgnlLexer.isStopWord(token))
			{
				whoseClause.append(lexer.tokenValue());
				token = lexer.nextToken();
			}
			
			EOQualifier qualifier =
				qualifierForOgnlExpression(whoseClause.toString());
			options.setObjectForKey(qualifier,
					ReportParameter.OPTION_QUALIFIER);
		}
		
		lexer = queryLexer;

		if(token != Token.SEMI &&
			token != Token.END_OF_INPUT)
			lexer.error("expected semicolon or end-of-input");
		
		// Add the statement to the query here
		query.addStatement(new AliasStatement(options));
	}
}
