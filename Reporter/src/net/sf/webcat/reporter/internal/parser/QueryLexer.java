package net.sf.webcat.reporter.internal.parser;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class QueryLexer extends AbstractLexer
{
	private String token;

	private static final int STATE_START = 0;
	private static final int STATE_IN_STRING = 1;
	private static final int STATE_IN_ID = 2;
	private static final int STATE_BACKSLASH = 3;

	private static NSDictionary keywordTable;
	
	static
	{
		NSMutableDictionary dict = new NSMutableDictionary();
		dict.setObjectForKey(Integer.valueOf(Token.FOR), "for");
		dict.setObjectForKey(Integer.valueOf(Token.IN), "in");
		dict.setObjectForKey(Integer.valueOf(Token.WHERE), "where");
		dict.setObjectForKey(Integer.valueOf(Token.LET), "let");
		dict.setObjectForKey(Integer.valueOf(Token.ORDER), "order");
		dict.setObjectForKey(Integer.valueOf(Token.BY), "by");
		dict.setObjectForKey(Integer.valueOf(Token.ASC), "asc");
		dict.setObjectForKey(Integer.valueOf(Token.DESC), "desc");
		dict.setObjectForKey(Integer.valueOf(Token.FROM), "from");
		dict.setObjectForKey(Integer.valueOf(Token.INTEGER), "integer");
		dict.setObjectForKey(Integer.valueOf(Token.BOOLEAN), "boolean");
		dict.setObjectForKey(Integer.valueOf(Token.FLOAT), "float");
		dict.setObjectForKey(Integer.valueOf(Token.STRING), "string");
		dict.setObjectForKey(Integer.valueOf(Token.TEXT), "text");
		dict.setObjectForKey(Integer.valueOf(Token.DATETIME), "datetime");
		dict.setObjectForKey(Integer.valueOf(Token.CHOOSE), "choose");
		
		keywordTable = dict;
	}

	public QueryLexer(String text)
	{
		super(text);
	}

	public QueryLexer(TokenSource source)
	{
		super(source);
	}

	@Override
	public int nextToken()
	{
		StringBuffer buffer = new StringBuffer();

		int state = STATE_START;

		int ch = peek();
		if(ch == -1)
		{
			state = Token.END_OF_INPUT;
			token = "end-of-input";
			return state;
		}

		while((state & Token.MASK) == 0)
		{
			if(ch == -1)
			{
				if(state == STATE_IN_STRING)
				{
					throw new IllegalArgumentException(
						"unterminated quoted string");
				}
				else if(state == STATE_IN_ID)
				{
					state = Token.ID;
				}
				else
				{
					state = Token.END_OF_INPUT;
					buffer = new StringBuffer("end-of-input");
				}
			}
			else if(state == STATE_START)
			{
				if(!Character.isWhitespace(ch))
				{
					if(ch == '1')
					{
						state = Token.ONE;
						buffer = new StringBuffer("1");
					}
					else if(ch == '*')
					{
						state = Token.STAR;
						buffer = new StringBuffer("*");
					}
					else if(ch == ':')
					{
						state = Token.COLON;
						buffer = new StringBuffer(":");
					}
					else if(ch == '=')
					{
						state = Token.EQUAL;
						buffer = new StringBuffer("=");
					}
					else if(ch == ',')
					{
						state = Token.COMMA;
						buffer = new StringBuffer(",");
					}
					else if(ch == ';')
					{
						state = Token.SEMI;
						buffer = new StringBuffer(";");
					}
					else if(ch == '.')
					{
						state = Token.DOT;
						buffer = new StringBuffer(".");
					}
					else if(ch == '"')
					{
						state = STATE_IN_STRING;
					}
					else if(Character.isJavaIdentifierStart(ch))
					{
						state = STATE_IN_ID;
						buffer.append((char)ch);
					}
					else
					{
						error("expected valid token");
					}
				}

				advance();
			}
			else if(state == STATE_IN_ID)
			{
				if(ch == -1 || !Character.isJavaIdentifierPart(ch))
				{
					state = Token.ID;
				}
				else
				{
					buffer.append((char)ch);
					advance();
				}
			}
			else if(state == STATE_IN_STRING)
			{
				if(ch == '\\')
				{
					state = STATE_BACKSLASH;
				}
				else if(ch == -1 || ch == '"')
				{
					state = Token.QSTRING;
				}
				else
				{
					buffer.append((char)ch);
				}
				
				advance();
			}
			else if(state == STATE_BACKSLASH)
			{
				state = STATE_IN_STRING;
				buffer.append((char)ch);
				advance();
			}

			ch = peek();
		}

		this.token = buffer.toString();
		
		return checkKeyword(state, this.token);
	}

	private int checkKeyword(int token, String word)
	{
		if(token == Token.ID)
		{
			String lc = word.toLowerCase();
			if(keywordTable.containsKey(lc))
				return ((Integer)keywordTable.objectForKey(lc)).intValue();
		}
			
		return token;
	}

	@Override
	public String tokenValue()
	{
		return token;
	}
}
