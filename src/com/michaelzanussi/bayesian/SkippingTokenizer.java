package com.michaelzanussi.bayesian;

/**
 * An object that splits a string into smaller strings (tokens) based on
 * delimiters. Modeled after Java's StringTokenizer class, but with slightly 
 * less functionality. However, it will optionally exclude from tokens any
 * undesirable characters (ergo the name, skipping).
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public interface SkippingTokenizer {

	/**
	 * Determines if there are more tokens remaining in the string.
	 * 
	 * @return <code>true</code> if there are more tokens remaining
	 * in the string.
	 */
	public boolean hasMoreTokens();
	
	/**
	 * Returns the next token in the string. Dependent on the
	 * delimiters used and whether certain types of characters
	 * (such as whitespace, punctuation, etc.) are discarded or
	 * not.
	 * 
	 * @return the next token in the string.
	 */
	public String nextToken();
	
	/**
	 * Sets the tokenizer with the string to tokenize. Necessary
	 * since dynamically loading classes with newInstance() 
	 * requires classes with no-arg constructors, so the string
	 * to tokenize has to be set with a helper function.
	 * 
	 * @param string the string to tokenize.
	 * @throws NullPointerException if string to tokenize is <code>null</code>.
	 */
	public void setStringToTokenize(String string);
	
	/**
	 * If <code>true</code>, tokenizer will <b>not</b> discard punctuation. 
	 * Punctuation is defined as any character other than whitespace,
	 * a letter, or a digit. By default, punctuation is retained.
	 * 
	 * @param keep <code>true</code> if punctuation is desired.
	 */
	public void keepPunctuation(boolean keep);

	/**
	 * If <code>true</code>, tokenizer will <b>not</b> discard whitespace. 
	 * Whitespace is defined as non-printable characters including (but not
	 * limited to) space, horizontal and vertical tabs, newlines, and carriage
	 * returns. By default, whitespace is discarded.
	 * 
	 * @param keep <code>true</code> if whitespace is desired.
	 */
	public void keepWhitespace(boolean keep);
	
	/**
	 * Specifies an exact number of contiguous characters a token will contain.
	 * 
	 * @param value the number of contiguous characters.
	 */
	public void setNGram(int value);
	
}
