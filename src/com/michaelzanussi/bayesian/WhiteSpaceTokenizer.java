package com.michaelzanussi.bayesian;

/**
 * This tokenizer splits the analyzable section of an email message
 * at whitespace characters. It optionally discards punctuation.<p>
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
public class WhiteSpaceTokenizer extends AbstractTokenizer implements SkippingTokenizer {
	
	/**
	 * No-arg constructor.
	 */
	public WhiteSpaceTokenizer() {
		
		// Ignore punctuation.
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
