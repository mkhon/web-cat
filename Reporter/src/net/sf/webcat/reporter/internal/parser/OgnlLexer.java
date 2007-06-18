package net.sf.webcat.reporter.internal.parser;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class OgnlLexer extends AbstractLexer
{
	private String token;

	private static final int STATE_START = 0;
	private static final int STATE_IN_WHITESPACE = 1;
	private static final int STATE_IN_QSTRING = 2;
	private static final int STATE_IN_ASTRING = 3;
	private static final int STATE_IN_WORD = 4;
	private static final int STATE_QSTRING_BACKSLASH = 5;
	private static final int STATE_ASTRING_BACKSLASH = 6;

	private static NSDictionary stopWordTable;
	
	static
	{
		NSMutableDictionary dict = new NSMutableDictionary();
		dict.setObjectForKey(Integer.valueOf(Token.SEMI), ";");
		dict.setObjectForKey(Integer.valueOf(Token.WHOSE), "whose");
		dict.setObjectForKey(Integer.valueOf(Token.WHERE), "where");
		dict.setObjectForKey(Integer.valueOf(Token.ORDER), "order");
		
		stopWordTable = dict;
	}

	public static boolean isStopWord(int token)
	{
		return
			token == Token.END_OF_INPUT ||
			token == Token.SEMI ||
			token == Token.WHOSE ||
			token == Token.WHERE ||
			token == Token.ORDER;
	}

	public OgnlLexer(String text)
	{
		super(text);
	}
	
	public OgnlLexer(TokenSource source)
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
				if(state == STATE_IN_QSTRING || state == STATE_IN_ASTRING)
				{
					throw new IllegalArgumentException(
						"unterminated quoted string");
				}
				else if(state == STATE_IN_WORD)
				{
					state = Token.OGNL_PART;
				}
				else
				{
					state = Token.END_OF_INPUT;
					buffer = new StringBuffer("end-of-input");
				}
			}
			else if(state == STATE_START)
			{
				if(Character.isWhitespace(ch))
				{
					state = STATE_IN_WHITESPACE;
				}
				else if(ch == ';')
				{
					state = Token.SEMI;
				}
				else if(ch == '"')
				{
					state = STATE_IN_QSTRING;
				}
				else if(ch == '\'')
				{
					state = STATE_IN_ASTRING;
				}
				else
				{
					state = STATE_IN_WORD;
				}

				buffer.append((char)ch);
				advance();
			}
			else if(state == STATE_IN_WORD)
			{
				if(ch == '"' || ch == '\'' || ch == ';' || ch == -1 ||
						Character.isWhitespace(ch))
				{
					state = Token.OGNL_PART;
				}
				else
				{
					buffer.append((char)ch);
					advance();
				}
			}
			else if(state == STATE_IN_WHITESPACE)
			{
				if(!Character.isWhitespace(ch))
				{
					state = Token.WHITESPACE;
				}
				else
				{
					buffer.append((char)ch);
					advance();
				}
			}
			else if(state == STATE_IN_QSTRING)
			{
				if(ch == '\\')
				{
					state = STATE_QSTRING_BACKSLASH;
				}
				else if(ch == '"')
				{
					state = Token.OGNL_PART;
				}

				buffer.append((char)ch);
				advance();
			}
			else if(state == STATE_IN_ASTRING)
			{
				if(ch == '\\')
				{
					state = STATE_ASTRING_BACKSLASH;
				}
				else if(ch == '\'')
				{
					state = Token.OGNL_PART;
				}

				buffer.append((char)ch);
				advance();
			}
			else if(state == STATE_QSTRING_BACKSLASH)
			{
				state = STATE_IN_QSTRING;
				buffer.append((char)ch);
				advance();
			}
			else if(state == STATE_ASTRING_BACKSLASH)
			{
				state = STATE_IN_ASTRING;
				buffer.append((char)ch);
				advance();
			}

			ch = peek();
		}

		this.token = buffer.toString();
		
		return checkStopWord(state, this.token);
	}

	@Override
	public String tokenValue()
	{
		return token;
	}

	private int checkStopWord(int token, String word)
	{
		if(token == Token.OGNL_PART)
		{
			String lc = word.toLowerCase();
			if(stopWordTable.containsKey(lc))
				return ((Integer)stopWordTable.objectForKey(lc)).intValue();
		}
			
		return token;
	}
}
