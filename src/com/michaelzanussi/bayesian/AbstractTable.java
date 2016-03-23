package com.michaelzanussi.bayesian;

/**
 * The base class for token tables providing general support, including
 * functionality to instantiate tokenizer classes dynamically.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public abstract class AbstractTable {

	// The current location within the input string.
	private int pos;
	
	/**
	 * No-arg constructor.
	 */
	public AbstractTable() {
		
		pos = 0;
		
	}
	
	/**
	 * Calculates the naive Bayes approximation for a token. To prevent
	 * underflow, the log likelihood of the token will be used. Laplace
	 * smoothing will be utilized as well to prevent 0 values from being
	 * returned if a token is not found in the token table. 
	 * 
	 * @param table the token table.
	 * @param token the token to analyze.
	 * @return the Bayes approximation.
	 */
	protected double bayes(TokenTable table, String token) {
		
		// Get the count for this token, if it exists.
		Integer count = (Integer)table.getTable().get(token);
		
		if (count == null) {
			// Token not found in table.
			return Math.log((double)1 / (table.getTokenCount() + 1));
		} else {
			// Token found in table.
			return Math.log((double)(count.intValue() + 1) / (table.getTokenCount() + 1));
		}
		
	}
	
	/**
	 * Retrieve the first word from the input line. This is generally
	 * used when determining if the system is processing a new e-mail
	 * and if so, what to extract from the email header.
	 * 
	 * @param input the string to retrieve first word from.
	 * @return the first word from the input line.
	 */
	protected String getFirstWord(String input) {
		
		// Reset line position.
		pos = 0;

		// Walk through the input line until we hit a space. The space 
		// separates the field-name from the field-body. The 'pos' 
		// counter will be positioned on the first character of the
		// field body.
		StringBuffer word = new StringBuffer("");
		while (pos < input.length()) {
			char ch = input.charAt(pos++);
			if (ch == ' ') {
				break;
			}
			word.append(ch);
		}
		
		return word.toString();

	}

	/**
	 * Given the name of a tokenizer class, this method attempts to dynamically
	 * instantiate that class. This would allow third-party development of future
	 * tokenizers and the ability to plug them in (as we do from the command
	 * line) without having to modify any code within the SpamBGone suite.
	 * 
	 * @param tokenizerClass the tokenizer class to instantiate.
	 * @return the tokenizer.
	 */
	protected SkippingTokenizer getTokenizer(String tokenizerClass) {

		// Tokenizer class name cannot be null.
		if (tokenizerClass == null) {
			throw new NullPointerException();
		}
		
		SkippingTokenizer tokenizer = null;
		
		// Attempt to instantiate!
		try {
			tokenizer = (SkippingTokenizer)Class.forName(tokenizerClass).newInstance();
		} catch (Exception e) {
			// Oops. Exit gracefully.
			System.err.println("ERROR: Cannot instantiate " + e.getMessage());
			System.exit(1);
		}
		
		return tokenizer;
		
	}
	
	/**
	 * Returns the current position in the input string.
	 * 
	 * @return the current position in the input string.
	 */
	public int getPosition() {
		
		return pos;
		
	}
	
}
