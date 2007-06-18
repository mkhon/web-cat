package net.sf.webcat.reporter.internal.parser;

public class TokenSource
{
	private String text;
	
	private int currentPosition;
	
	private int currentLine, currentColumn;

	public TokenSource(String text)
	{
		this(text, 0, 1, 1);
	}
	
	public TokenSource(String text, int currentPosition, int currentLine,
			int currentColumn)
	{
		this.text = text;
		this.currentPosition = currentPosition;
		this.currentLine = currentLine;
		this.currentColumn = currentColumn;
	}

	public String text()
	{
		return text;
	}
	
	public int currentPosition()
	{
		return currentPosition;
	}
	
	public int currentLine()
	{
		return currentLine;
	}
	
	public int currentColumn()
	{
		return currentColumn;
	}
	
	public int peek()
	{
		if(currentPosition < text.length())
			return text.charAt(currentPosition);
		else
			return -1;
	}
	
	public void advance()
	{
		if(currentPosition < text.length())
		{
			currentPosition++;
			
			currentColumn++;
			if(currentPosition < text.length() &&
				text.charAt(currentPosition) == '\n')
			{
				currentLine++;
				currentColumn = 1;
			}
		}
	}
}
