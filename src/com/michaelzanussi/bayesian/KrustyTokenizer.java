package com.michaelzanussi.bayesian;

/**
 * This tokenizer splits the analyzable section of an email message
 * at whitespace characters and at HTML-type delimiters that typically
 * appear in HTML tags, while discarding all remaining punctuation. <p>
 * 
 * Whitespace is defined as non-printable characters including, but not
 * limited to, space, horizontal and vertical tabs, newlines, and carriage
 * returns. C.f., the JDK API call <code>Character.isWhiteSpace()</code>.<p>
 * 
 * Punctuation is defined as any character other than whitespace, letters, 
 * or digits.<p>
 * 
 * HTML-type delimiters is defined as puntuation that occurs within an
 * HTML tag.<p>
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class KrustyTokenizer extends AbstractTokenizer implements SkippingTokenizer {
	
	/**
	 * The HTML delimiter list, or punctuation that occurs within
	 * and HTML tag we wish to treat as a delimiter.
	 */
	public static String HTML = "<>=.\":/_?@";
	
	/**
	 * No-arg constructor.
	 */
	public KrustyTokenizer() {
		
		// Discard punctuation.
		keepPunctuation(false);
		
	}

	/**
	 * Checks if the passed character is a delimiter or not, in this case,
	 * if the character is whitespace or not.
	 * 
	 * @param ch the character to check.
	 * @return <code>true</code> if character is a delimiter.
	 */
	protected boolean isDelimiter(char ch) {

		// Delimiter here is whitespace.
		return (Character.isWhitespace(ch) ? true : false);
		
	}

	/**
	 * Returns the next token in the string. Dependent on the
	 * delimiters used and whether certain types of characters
	 * (such as whitespace, punctuation, etc.) are discarded or
	 * not.
	 * 
	 * Prior to checking for a delimiter or skip list character,
	 * any "special" punctuation is converted to spaces.
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

			// Delimiter or HTML delimiter found, we're done.
			if (isDelimiter(ch) || HTML.indexOf(ch) >= 0) {
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
			if (!isDelimiter(ch) && !(HTML.indexOf(ch) >= 0) && !isSkipper(ch)) {
				tokenFound = true;
				break;
			}
		}
		
		return (tokenFound ? --loc : -1);
		
	}
	
	/**
	 * Checks if passed character is in skip list or not. The skip list
	 * contains punctuation. This can be overridden by a call to 
	 * keepPunctuation().
	 * 
	 * @param ch the character to check.
	 * @return <code>true</code> if character is in skip list.
	 */
	protected boolean isSkipper(char ch) {

		// Don't skip punctuation is specified.
		if (keepPunctuation) {
			return false;
		}
		
		// Under no circumstances are we to discard whitespace,
		// digits or characters.
		if (Character.isWhitespace(ch) || Character.isDigit(ch) || Character.isLetter(ch)) {
			return false;
		}
		
		return true;
		
	}
	
}
