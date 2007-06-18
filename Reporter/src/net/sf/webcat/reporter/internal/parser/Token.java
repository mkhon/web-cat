package net.sf.webcat.reporter.internal.parser;

public interface Token
{
	public static final int MASK = 0x8000;

	public static final int END_OF_INPUT = MASK | 0;
	public static final int COMMA = MASK | 1;
	public static final int SEMI = MASK | 2;
	public static final int QSTRING = MASK | 3;
	public static final int ID = MASK | 4;
	public static final int FOR = MASK | 5;
	public static final int IN = MASK | 6;
	public static final int WHERE = MASK | 7;
	public static final int LET = MASK | 8;
	public static final int EQUAL = MASK | 9;
	public static final int ORDER = MASK | 10;
	public static final int BY = MASK | 11;
	public static final int ASC = MASK | 12;
	public static final int DESC = MASK | 13;
	public static final int STAR = MASK | 14;
	public static final int ONE = MASK | 15;
	public static final int COLON = MASK | 16;
	public static final int FROM = MASK | 17;
	public static final int WHOSE = MASK | 18;
	public static final int WHITESPACE = MASK | 19;
	public static final int OGNL_PART = MASK | 20;
	public static final int DOT = MASK | 21;
	public static final int INTEGER = MASK | 100;
	public static final int BOOLEAN = MASK | 101;
	public static final int FLOAT = MASK | 102;
	public static final int STRING = MASK | 103;
	public static final int TEXT = MASK | 104;
	public static final int DATETIME = MASK | 105;
	public static final int CHOOSE = MASK | 106;
}
