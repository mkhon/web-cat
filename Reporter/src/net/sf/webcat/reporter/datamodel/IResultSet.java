package net.sf.webcat.reporter.datamodel;

import java.util.Date;

/**
 * <p>
 * Represents the results of a query on the Web-CAT data store. A row in the
 * result set corresponds to values of the set of variables in a query. Data
 * can be read from the result set by evaluating key paths starting at one of
 * these variables. 
 * </p><p>
 * When a new result set is obtained (i.e., through 
 * <code>IDataSource.executeQuery</code>), it is automatically positioned on
 * the first row (unless it is empty, in which case it is not positioned on any
 * row). To read values from the result set, a simple <code>while</code> loop
 * will do:
 * </p><p>
 * <pre>
 *     var resultSet = WebCAT.executeQuery(...);
 *     while(resultSet.hasRow())
 *     {
 *         // read values with <i>xxx</i>Value methods
 *         
 *         resultSet.moveToNextRow();
 *     }
 * </pre>
 * </p>
 * 
 * @author Tony Allevato
 */
public interface IResultSet
{
	/**
	 * Moves the current position of the result set to the first row.
	 * 
	 * @return true if the current position was able to be moved to the first
	 *     row; otherwise, false (that is, if the result set contained no rows).
	 */
	boolean moveToFirstRow();

	
	/**
	 * Moves the current position to the next row in the result set, if the
	 * next row exists.
	 * 
	 * @return true if the current position was able to be moved; otherwise,
	 *     false if the current position is already at the last row.
	 */
	boolean moveToNextRow();

	
	/**
	 * <p>
	 * Moves the current position to a specific row in the result set.
	 * </p><p>
	 * This is a linear-time operation, since moving to the <i>i</i>th row
	 * requires the current position to be reset to the first row and then
	 * moved row-by-row to the <i>i</i>th position.
	 * </p> 
	 *
	 * @param row the 0-based index of the row to move to.
	 * 
	 * @return true if the current position was able to be moved; otherwise,
	 *     false if the specified position is after the last row in the result
	 *     set.
	 */
	boolean moveToRow(int row);

	
	/**
	 * Returns the index of the current row the cursor is on in the result set.
	 * 
	 * @return the 0-based index of the current row.
	 */
	int currentRow();
	
	
	/**
	 * Returns a value indicating whether the current position in the result
	 * set is a valid row.
	 * 
	 * @return true if the current position is on a valid row; otherwise, false
	 *     if the current position has moved past the last row.
	 */
	boolean hasRow();


	/**
	 * Returns a value indicating whether the last
	 * <code><i>xxx</i>Value</code> call was null or invalid.
	 *  
	 * @return true if the last <code><i>xxx</i>Value</code> call was null;
	 *     otherwise, false.
	 */
	boolean wasNull();


	/**
	 * <p>
	 * Retrieves the value of the specified expression in the result set as a
	 * generic object, without performing any data conversion.
	 * </p><p>
	 * If you need to evaluate an expression whose type is a primitive value
	 * (such as an integer or floating-point value), you should use one of the
	 * specialized <code><i>xxx</i>Value</code> methods provided so
	 * that some basic String-to-value conversion can be performed if needed
	 * (this is the case when an expression has its destination in a plain-text
	 * Java properties file, where all values are Strings).
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the value of the expression
	 */
	Object value(String expression);

	
	/**
	 * <p>
	 * Retrieves the string value of the specified expression in the result set.
	 * </p><p>
	 * This method calls <code>value(expression)</code> and then returns
	 * the result of calling the <code>toString()</code> method on that object.
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the string value of the expression
	 */
	String stringValue(String expression);

	
	/**
	 * <p>
	 * Retrieves the boolean value of the specified expression in the result
	 * set.
	 * </p><p>
	 * This method tries the following operations to get a boolean value for
	 * the expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Boolean</code>, cast it and return
	 * the result of <code>Boolean.booleanValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Boolean.parseBoolean</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the boolean value of the expression
	 */
	boolean booleanValue(String expression);
	
	
	/**
	 * <p>
	 * Retrieves the character value of the specified expression in the result
	 * set.
	 * </p><p>
	 * This method tries the following operations to get a character value for
	 * the expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Character</code>, cast it and return
	 * the result of <code>Character.charValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object. If the
	 * resulting String has length 1, return the sole character in the String.
	 * If the String has more than one character, the return value is undefined
	 * and <code>wasNull</code> is set to <code>true</code>.</li>
	 * </ol>
	 * </p>  
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the character value of the expression
	 */
	char charValue(String expression);
	
	
	/**
	 * <p>
	 * Retrieves the byte value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a byte value for the
	 * expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Byte</code>, cast it and return the
	 * result of <code>Byte.byteValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Byte.parseByte</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the byte value of the expression
	 */
	byte byteValue(String expression);
	
	
	/**
	 * <p>
	 * Retrieves the short value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a short value for the
	 * expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Short</code>, cast it and return the
	 * result of <code>Short.shortValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Short.parseShort</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the short value of the expression
	 */
	short shortValue(String expression);

	
	/**
	 * <p>
	 * Retrieves the integer value of the specified expression in the result
	 * set.
	 * </p><p>
	 * This method tries the following operations to get an integer value for
	 * the expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Integer</code>, cast it and return
	 * the result of <code>Integer.intValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Integer.parseInt</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the integer value of the expression
	 */
	int intValue(String expression);
	
	
	/**
	 * <p>
	 * Retrieves the long value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a long value for the
	 * expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Long</code>, cast it and return the
	 * result of <code>Long.longValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Long.parseLong</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the long value of the expression
	 */
	long longValue(String expression);

	
	/**
	 * <p>
	 * Retrieves the float value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a float value for the
	 * expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Float</code>, cast it and return the
	 * result of <code>Float.floatValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Float.parseFloat</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the float value of the expression
	 */
	float floatValue(String expression);

	
	/**
	 * <p>
	 * Retrieves the double value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a double value for
	 * the expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Double</code>, cast it and return
	 * the result of <code>Double.doubleValue()</code>.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with <code>Double.parseDouble</code>.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the double value of the expression
	 */
	double doubleValue(String expression);
		
	
	/**
	 * <p>
	 * Retrieves the date value of the specified expression in the result set.
	 * </p><p>
	 * This method tries the following operations to get a Date value for the
	 * expression:
	 * <ol>
	 * <li>Call <code>value(expression)</code>.</li>
	 * <li>If the result is of type <code>Date</code>, return it.</li>
	 * <li>Otherwise, call <code>toString()</code> on the raw object and then
	 * attempt to parse that with
	 * <code>NSTimestampFormatter.parseObjectInUTC</code>, using the default
	 * date format pattern.</li>
	 * <li>If parsing succeeds, return the result. If it fails, the return
	 * value is undefined and <code>wasNull</code> is set to <code>true</code>.
	 * </li>
	 * </ol>
	 * </p>
	 * 
	 * @param expression the OGNL expression to be evaluated
	 * @return the java.util.Date value of the expression
	 */
	Date dateValue(String expression);
}
