package com.michaelzanussi.bayesian;

/**
 * This class provides a skeletal implementation of the <tt>SkippingTokenizer</tt>
 * interface, to minimize the effort required to implement this interface.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public abstract class AbstractTokenizer implements SkippingTokenizer {

	/**
	 * The current position in the input string.
	 */
	protected int curPos;
	
	/**
	 * The NGram size.
	 */
	protected int nGram;
	
	/**
	 * Should punctuation be kept?
	 */
	protected boolean keepPunctuation;

	/**
	 * Should whitepace be kept?
	 */
	protected boolean keepWhitespace;
	
	/**
	 * The input string to tokenize. Cannot be <code>null</code>.
	 */
	protected String str;
	
	/**
	 * The delimiters. Contains a string of characters to be used as
	 * token delimiters. Cannot be <code>null</code> nor can the
	 * string be empty.
	 */
	protected String delimiters;
	
	/**
	 * The skip list. Contains a string of characters to be excluded
	 * from the token. Cannot be <code>null</code>, but empty strings
	 * (thus specifying no characters to be skipped) are allowable.
	 */
	protected String skippers;
	
	/**
	 * The default delimiters.
	 */
	public static final String WHITESPACE = " \r\n\t\f\b";

	/**
	 * The default constructor. 
	 */
	protected AbstractTokenizer() {
		
		curPos = 0;
		str = null;
		delimiters = WHITESPACE;
		skippers = "";
		keepPunctuation = true;
		keepWhitespace = false;
		nGram = 0;
		
	}
	
	/**
	 * Determines if there are more tokens remaining in the string.
	 * 
	 * @return <code>true</code> if there are more tokens remaining
	 * in the string.
	 */
	public boolean hasMoreTokens() {
		
		return (findNextToken() < 0 ? false : true);

	}
	
	/**
	 * Returns the next token in the string. Dependent on the
	 * delimiters used and whether certain types of characters
	 * (such as whitespace, punctuation, etc.) are discarded or
	 * not.
	 * 
	 * @return the next token in the string.
	 * @throws NullPointerException if there aren't any more tokens.
	 */
	public String nextToken() {

		// StringBuffer is used instead of normal String since appending
		// is quicker than concatenating.
		StringBuffer token = new StringBuffer("");

		// Update the current location to the next token.
		curPos = findNextToken();
		
		// Check to see if there weren't any more tokens. Hopefully, this
		// shouldn't occur if hasMoreTokens() had been called prior to
		// calling nextToken().
		if (curPos == -1) {
			throw new NullPointerException();
		}
		
		// Cycle through the input until we've reached the end of the line
		// or we've reached a delimiter, making sure we skip necessary
		// characters along the way.
		while (curPos < str.length()) {
			
			char ch = str.charAt(curPos++);
			// Delimiter found, we're done.
			if (isDelimiter(ch)) {
				break;
			}
			// Skip this character.
			if (isSkipper(ch)) {
				continue;
			}
			token.append(ch);
			
		}
		
		return token.toString();
	}
	
	/**
	 * Sets the tokenizer with the string to tokenize. Necessary
	 * since dynamically loading classes with newInstance() 
	 * requires classes with no-arg constructors, so the string
	 * to tokenize has to be set with a helper function.
	 * 
	 * @param string the string to tokenize.
	 * @throws NullPointerException if string to tokenize is <code>null</code>.
	 */
	public void setStringToTokenize(String string) {
		
		// String cannot be null.
		if (string == null) {
			throw new NullPointerException();
		}
		
		str = string;
		
	}

	/**
	 * If <code>true</code>, tokenizer will <b>not</b> discard punctuation. 
	 * Punctuation is defined as any character other than whitespace,
	 * a letter, or a digit. By default, punctuation is retained.
	 * 
	 * @param keep <code>true</code> if punctuation is desired.
	 */
	public void keepPunctuation(boolean keep) {
		
		keepPunctuation = keep;
		
	}
	
	/**
	 * If <code>true</code>, tokenizer will <b>not</b> discard whitespace. 
	 * Whitespace is defined as non-printable characters including (but not
	 * limited to) space, horizontal and vertical tabs, newlines, and carriage
	 * returns. By default, whitespace is discarded.
	 * 
	 * @param keep <code>true</code> if whitespace is desired.
	 */
	public void keepWhitespace(boolean keep) {
		
		keepWhitespace = keep;
		
	}
	
	/**
	 * Specifies an exact number of contiguous characters a token will contain.
	 * 
	 * @param value the number of contiguous characters.
	 */
	public void setNGram(int value) {
		
		nGram = value;
		
	}
	
	/**
	 * Returns the position of the next token in the string. If
	 * there are no more tokens, returns <code>-1</code>.
	 * 
	 * @return the position of the next token, or <code>-1</code>
	 * if there are no more tokens.
	 * @throws NullPointerException if the string to tokenize is <code>null</code>.
	 */
	protected int findNextToken() {

		// String cannot be null.
		if (str == null) {
			throw new NullPointerException();
		}
		
		// Use local copy of current position.
		int loc = curPos;
		boolean tokenFound = false;
		
		// Cycle through string until another token is found or
		// we reach the end of the string.
		while (loc < str.length()) {
			char ch = str.charAt(loc++);
			if (!isDelimiter(ch) && !isSkipper(ch)) {
				tokenFound = true;
				break;					
			}
		}
		
		return (tokenFound ? --loc : -1);
		
	}
	
	/**
	 * Checks if the passed character is a delimiter or not, that is,
	 * if it's part of the delimiter list, <code>delimiter</code>.
	 * 
	 * @param ch the character to check.
	 * @return <code>true</code> if character is a delimiter.
	 * @throws NullPointerException if delimiter list is <code>null</code>.
	 */
	protected boolean isDelimiter(char ch) {

		// Delimiter list cannot be null.
		if (delimiters == null) {
			throw new NullPointerException();
		}
		
		return (delimiters.indexOf(ch) < 0 ? false : true);
		
	}

	/**
	 * Checks if passed character is in skip list or not, that is, 
	 * if it's part of the skip list, <code>skippers</code>.
	 * 
	 * @param ch the character to check.
	 * @return <code>true</code> if character is in skip list.
	 * @throws NullPointerException if skip list is <code>null</code>.
	 */
	protected boolean isSkipper(char ch) {

		// Skip list cannot be null.
		if (skippers == null) {
			throw new NullPointerException();
		}
		
		return (skippers.indexOf(ch) < 0 ? false : true);
		
	}
	
}
