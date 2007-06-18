package net.sf.webcat.reporter.internal.parser;

import java.text.DateFormat;
import java.text.ParseException;

import ognl.OgnlException;

import net.sf.webcat.reporter.ReportParameter;

import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

public class ParameterParser
{
	private AbstractLexer lexer;
	
	private QueryLexer queryLexer;

	private OgnlLexer ognlLexer;
	
	private String type;
	
	private NSMutableDictionary options;

	public ParameterParser(String text)
	{
		TokenSource source = new TokenSource(text);
		queryLexer = new QueryLexer(source);
		ognlLexer = new OgnlLexer(source);
		
		lexer = queryLexer;
		options = new NSMutableDictionary();
	}
	
	public String getType()
	{
		return type;
	}

	public NSDictionary getOptions()
	{
		return options;
	}

	public void parse()
	{
		int token = lexer.nextToken();

		switch(token)
		{
			case Token.INTEGER:
			case Token.BOOLEAN:
			case Token.STRING:
			case Token.TEXT:
			case Token.FLOAT:
			case Token.DATETIME:
				parseScalar(token);
				break;
			
			case Token.CHOOSE:
				parseChoose();
				break;

			default:
				lexer.error("expected one of the following: 'INTEGER', " + 
						"'BOOLEAN', 'STRING', 'TEXT', 'FLOAT', " +
						"'DATETIME', 'CHOOSE'");
				break;
		}
	}
	
	private void parseScalar(int type)
	{
		this.type = lexer.tokenValue().toUpperCase();
		
		lexer = ognlLexer;

		StringBuffer exprBuffer = new StringBuffer();

		int token = lexer.nextToken();
		while(!OgnlLexer.isStopWord(token))
		{
			exprBuffer.append(lexer.tokenValue());
			token = lexer.nextToken();
		}

		if(exprBuffer.length() > 0)
		{
			try
			{
				// We don't actually do anything with the expression tree
				// here; we just want to check that it parses at report upload
				// time instead of dealing with it at report generation time.
				ognl.Ognl.parseExpression(exprBuffer.toString());
			}
			catch (OgnlException oe)
			{
				throw new IllegalArgumentException(
						"Default value was not a valid OGNL expression", oe);
			}
	
			options.setObjectForKey(exprBuffer.toString(),
					ReportParameter.OPTION_SOURCE);
		}
		
		if(token != Token.END_OF_INPUT)
			lexer.error("expected end-of-input");
	}

	private void parseChoose()
	{
		this.type = lexer.tokenValue().toUpperCase();

		int token = lexer.nextToken();
		if(token != Token.ONE && token != Token.STAR)
			lexer.error("expected '1' or '*' after 'CHOOSE'");

		if(token == Token.ONE)
		{
			options.setObjectForKey(Boolean.FALSE,
					ReportParameter.OPTION_MULTIPLE_SELECTION);
		}
		else // if(token == Token.STAR)
		{
			options.setObjectForKey(Boolean.TRUE,
					ReportParameter.OPTION_MULTIPLE_SELECTION);
		}
		
		if(lexer.nextToken() != Token.FROM)
			lexer.error("expected 'FROM'");
		
		lexer = ognlLexer;

		StringBuffer inClauseBuffer = new StringBuffer();

		token = lexer.nextToken();
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
				ognl.Ognl.parseExpression(inClause);
				options.setObjectForKey(inClause, ReportParameter.OPTION_SOURCE);
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
			
			qualifierForOgnlExpression(whoseClause.toString());
			
			options.setObjectForKey(whoseClause.toString(),
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
				ognl.Ognl.parseExpression(whereClause.toString());
				options.setObjectForKey(whereClause.toString(),
						ReportParameter.OPTION_FILTER);
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

			String key;
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
}
