package com.michaelzanussi.bayesian;

import java.lang.IllegalArgumentException;

/**
 * This tokenizer splits the analyzable section of an email message
 * into tokens of <code>n</code> contiguous characters, where <code>n</code> 
 * is a parameter to the tokenizer. Whitespace and punctuation may or 
 * may not be excluded. By default they are both excluded.<p>
 * 
 * Whitespace is defined as non-printable characters including, but not
 * limited to, space, horizontal and vertical tabs, newlines, and carriage
 * returns. C.f., the JDK API call <code>Character.isWhiteSpace()</code>.<p>
 * 
 * Punctuation is defined as any character other than whitespace, letters, 
 * or digits.<p>
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class NGramTokenizer extends AbstractTokenizer implements SkippingTokenizer {
	
	/**
	 * No-arg constructor.
	 */
	public NGramTokenizer() {
		
		// By default, ignore punctuation and whitespace.
		keepPunctuation(false);
		keepWhitespace(false);

	}

	/**
	 * Returns the next token in the string. Dependent on the
	 * NGram length and whether certain types of characters
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

		// Setup count for NGram.
		int count = 0;

		// Cycle through the input until we've reached the end of the line
		// or we've reached the NGram length, making sure we skip necessary
		// characters along the way.
		while (curPos < str.length() && count < nGram) {
			char ch = str.charAt(curPos++);
			if (isSkipper(ch)) {
				continue;
			}
			token.append(ch);
			count++;
		}

		return token.toString();
		
	}
	
	/**
	 * Specifies an exact number of contiguous characters the token will 
	 * contain. This value must be 1 or greater.
	 * 
	 * @param value the number of contiguous characters.
	 * @throws IllegalArgumentException if <code>value</code> is less than 1.
	 */
	public void setNGram(int value) {

		if (value < 1) {
			throw new IllegalArgumentException("Invalid nGram value (" + value + "), or nGram not specified.");
		}
		nGram = value;
		
	}
	
	/**
	 * Determines if there is another token in the input string. For the
	 * NGram tokenizer, things are fine so long as we're not at the end
	 * of the string. 
	 * 
	 * @return <code>0</code> if there is another token, or <code>-1</code>
	 * if there are no more tokens.
	 * @throws NullPointerException if the string to test is <code>null</code>.
	 */
	protected int findNextToken() {

		// String cannot be null.
		if (str == null) {
			throw new NullPointerException();
		}
		
		// We're good to go if we're not at the end of the line.
		if (curPos < str.length()) {
			return 0;
		}
		
		return -1;
		
	}
	
	/**
	 * Checks if passed character is in skip list or not. The skip list
	 * includes either whitespace or punctuation, or both. This can be
	 * overridden by calls to keepWhitespace() and keepPunctuation(),
	 * respectively.
	 * 
	 * @param ch the character to check.
	 * @return <code>true</code> if character is in skip list.
	 */
	protected boolean isSkipper(char ch) {

		// Return right away if this is a digit or letter, since we
		// definitely don't want to skip them.
		if (Character.isDigit(ch) || Character.isLetter(ch)) {
			return false;
		}

		// Next, check if whitespace and if so, if we want to keep
		// it or not.
		if (Character.isWhitespace(ch)) {
			return (keepWhitespace ? false : true);
		}

		// Must be punctuation at this point, so determine if we
		// keep or not.
		return (keepPunctuation ? false : true);
		
	}
	
}
