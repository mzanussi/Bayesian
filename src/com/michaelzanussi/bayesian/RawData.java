package com.michaelzanussi.bayesian;

import java.io.Serializable;
import java.io.File;

/**
 * The raw data class holds two email token tables, one for normal email
 * and one for spam. The use of a single class makes for easy disk
 * storage and simpler statistical processing. The raw data class
 * guarantees the same tokenizer has been used for the normal and spam
 * email tables.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class RawData implements Serializable {

	// The two separate token tables for the email samples.
	private TokenTable normal;
	private TokenTable spam;
	
	// The tokenizer being used, and optional nGram.
	private String tokenizer;
	private int nGram;
	
	/**
	 * Because: It is strongly recommended that all serializable 
	 * classes explicitly declare serialVersionUID values. 
	 */
	private static final long serialVersionUID = 3899361308412564726L;

	/**
	 * No-arg constructor.
	 */
	public RawData() {
		
		tokenizer = null;
		nGram = 0;
		normal = new TokenTable();
		spam = new TokenTable();
		
	}

	/**
	 * Returns the total number of emails for a specific email type.
	 * 
	 * @param emailType the type of email to return count for.
	 * @return the total number of emails, or <code>-1</code> if there
	 * is an error.
	 */
	public int getEmailCount(int emailType) {

		switch (emailType) {
			case TokenTable.NORMAL_EMAIL:
				return normal.getEmailCount();
			case TokenTable.SPAM_EMAIL:
				return spam.getEmailCount();
			default:
				return -1;
		}
		
	}
	
	/**
	 * Returns the token table for a specific email type.
	 * 
	 * @param emailType the type of email to return table for.
	 * @return the token table or <code>null</code> if there is
	 * an error.
	 */
	public TokenTable getTable(int emailType) {

		switch (emailType) {
			case TokenTable.NORMAL_EMAIL:
				return normal;
			case TokenTable.SPAM_EMAIL:
				return spam;
			default:
				return null;
		}
		
	}
	
	/**
	 * Returns the tokenizer used to generate the token tables.
	 * 
	 * @return the tokenizer used to generate the token tables.
	 */
	public String getTokenizer() {
		
		return tokenizer;
		
	}

	/**
	 * Returns the nGram value of the tokenizer.
	 * 
	 * @return the nGram value of the tokenizer.
	 */
	public int getNGram() {
		
		return nGram;
		
	}
	
	/**
	 * Reads in the mailbox and processes the contents, depending on the
	 * current type of email. Actual processing is deferred to the
	 * email token table's process() method.
	 * 
	 * @param emailType the class of email being processed.
	 * @param file the mailbox containing the email to process.
	 * @param tokenizerClass the tokenizer to use to tokenize the email.
	 * @param nGram if the tokenizer is NGram-type, contains the size
	 * of the NGram.
	 * @return the number of emails processed. 
	 */
	public int process(int emailType, File file, String tokenizerClass, int nGram) {

		// The number of emails processed.
		int processed = 0;
		
		// If these are the first emails to be processed, we'll need to set
		// the tokenizer name and the nGram value with this object.
		if (tokenizer == null) {
			tokenizer = tokenizerClass;
		}
		if (nGram == 0) {
			this.nGram = nGram;
		}
		
		// Process the email.
		switch (emailType) {
			case TokenTable.NORMAL_EMAIL:
				processed = normal.process(file, tokenizerClass, nGram);
				break;
			case TokenTable.SPAM_EMAIL:
				processed = spam.process(file, tokenizerClass, nGram);
				break;
		}

		// Return the number of emails processed.
		return processed;
		
	}
	
	/**
	 * Dumps token statistics to the standard output or to a log file for a 
	 * specific email type. For a log file, the TextFileWriter object itself 
	 * is passed to method dump(). Since a full statistical log is huge, a 
	 * full-dump is only available when a log file has been specified. 
	 * Otherwise, a standard statistical summary is displayed to the user.
	 * This method calls the email's own dump method, which provides the
	 * full statistical details.
	 * 
	 * @param emailType the type of email to dump stats on.
	 * @param fw the object containing the file being written to.
	 */
	public void dump(int emailType, TextFileWriter fw) {
		
		switch (emailType) {
			case TokenTable.NORMAL_EMAIL:
				if (fw == null) {
					System.out.println("\nNORMAL email token dump (summary only)...");
					System.out.println("Tokenizer used    : " + tokenizer);
				} else {
					System.out.println("Dumping NORMAL stats, standby...");
					fw.writeln("NORMAL email token dump:\n");					
					fw.writeln("Tokenizer used    : " + tokenizer);
				}
				// Execute the table's dump method for details.
				normal.dump(fw);
				break;
			case TokenTable.SPAM_EMAIL:
				if (fw == null) {
					System.out.println("\nSPAM email token dump (summary only)...");
					System.out.println("Tokenizer used    : " + tokenizer);
				} else {
					System.out.println("Dumping SPAM stats, standby...");
					fw.writeln("SPAM email token dump:\n");					
					fw.writeln("Tokenizer used    : " + tokenizer);
				}
				// Execute the table's dump method for details.
				spam.dump( fw );
				break;
		}

	}
	
}
