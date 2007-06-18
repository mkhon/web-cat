package net.sf.webcat.reporter.internal.parser;

import org.apache.log4j.Logger;

public abstract class AbstractLexer
{
	private TokenSource source;

	public AbstractLexer(String source)
	{
		this(new TokenSource(source));
	}

	public AbstractLexer(TokenSource source)
	{
		this.source = source;
	}

	public void error(String hint)
	{
		StringBuffer msg = new StringBuffer();
		msg.append("Parse error near line ");
		msg.append(source.currentLine());
		msg.append(", column ");
		msg.append(source.currentColumn());
		msg.append(" [... ");
		
		int start = Math.max(0, source.currentPosition() - 10);
		msg.append(source.text().substring(start,
			Math.min(start + Math.min(10, start) + 1, source.text().length())));
		msg.append("_");
		msg.append(source.text().substring(source.currentPosition(),
			Math.min(source.currentPosition() + 10, source.text().length())));
		msg.append(" ...]: ");
		msg.append(hint);
		msg.append(", but found \"");
		msg.append(tokenValue());
		msg.append("\"");
		
		throw new IllegalArgumentException(msg.toString());
	}

	public abstract int nextToken();

	public abstract String tokenValue();

	protected int peek()
	{
		return source.peek();
	}
	
	protected void advance()
	{
		source.advance();
	}
}
