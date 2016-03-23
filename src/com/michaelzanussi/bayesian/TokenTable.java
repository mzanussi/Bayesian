package com.michaelzanussi.bayesian;

import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

/**
 * The token table class maintains a hash table of all the tokens for a 
 * specific type of email and their respective counts. It also keeps a
 * running count of how many emails have been used to build the token table.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class TokenTable extends AbstractTable implements Serializable {

	/**
	 * The types of email this table represents.
	 */
	public static final int NORMAL_EMAIL = 1;
	public static final int SPAM_EMAIL = 2;
	
	// The total email count.
	private int _email;

	// The total token count for all email.
	private int _tokens;

	// The hash table for this email type.
	private MondoHashTable _table;
	
	// The email type (see constants above).
	private int _emailType;
	
	/**
	 * No-arg constructor.
	 */
	public TokenTable() {
		
		this(0);
		
	}
	
	/**
	 * Standard constructor that constructs an object of a certain
	 * email type (normal or spam).
	 * 
	 * @param emailType the type of email this token table represents.
	 */
	public TokenTable( int emailType ) {
		
		_email = 0;
		_emailType = emailType;
		_table = new MondoHashTable();
		_tokens = 0;
		
	}
	
	/**
	 * Returns the total number of tokens processed for all email.
	 * 
	 * @return the total number of tokens.
	 */
	public int getTokenCount() {
		
		return _tokens;
		
	}
	
	/**
	 * Returns the total number of emails represented by the table.
	 * 
	 * @return the total number of emails.
	 */
	public int getEmailCount() {
		
		return _email;
		
	}
	
	/**
	 * Returns the token table.
	 * 
	 * @return the token table.
	 */
	public MondoHashTable getTable() {
		
		return _table;
		
	}
	
	/**
	 * Dumps token statistics to the standard output or to a log file. For a log
	 * file, the TextFileWriter object itself is passed to method dump(). Since
	 * a full statistical log is huge, a full-dump is only available when a log
	 * file has been specified. Otherwise, a standard statistical summary is
	 * displayed to the user.
	 * 
	 * @param fw the object containing the file being written to.
	 */
	public void dump( TextFileWriter fw ) {

		DecimalFormat df = new DecimalFormat("0.0000000000");
		
		if( fw == null ) {
			
			System.out.println( "Email processed   : " + _email );
			System.out.println( "Total token count : " + _tokens );
			System.out.println( "Unique token count: " + _table.size() );
			
		}
		else {
			
			fw.writeln( "Email processed   : " + _email );
			fw.writeln( "Total token count : " + _tokens );
			fw.writeln( "Unique token count: " + _table.size() );
			
			fw.writeln( "\nCount\t\tProb (cnt/tot)\t\tKey" );
			fw.writeln(   "-----\t\t--------------\t\t---------");
			Set set = _table.entrySet();
			Iterator it = set.iterator();
			while( it.hasNext() ) {
				MondoHashTable.Entry e = (MondoHashTable.Entry)it.next();
				String str = (String)e.getKey();
				Integer i = (Integer)e.getValue();
				String freq = df.format( (double)i.intValue() / _tokens );
				fw.writeln( i.intValue() + "\t\t\t" + freq + "\t\t" + str );
			}
			fw.writeln( "\n" );
			
		}
		
	}
	
	/**
	 * Reads in the mailbox and processes the contents. During processing,
	 * tokens are extracted based on a tokenizer and are stored in the token
	 * table, along with a count of how many times this token has been seen.
	 * Likewise, an email counter is kept for the number of emails processed.
	 * 
	 * @param file the mailbox containing the email to process.
	 * @param tokenizerClass the tokenizer to use to tokenize the email.
	 * @param nGram if the tokenizer is NGram-type, contains the size
	 * of the NGram.
	 * @return the number of emails processed. 
	 */
	public int process( File file, String tokenizerClass, int nGram ) {

		// The running tally of emails processed.
		int count = 0;
		
		// Used in processing the email headers.
		boolean header = true;
		boolean checkForPostmark = true;
		boolean postmark = false;
		
		// Used for NGram-type tokenizers.
		boolean wraparound = false;
		String prevToken = "";

		// Open email file. May be direct file access or standard input.
		TextFileReader fr = new TextFileReader();
		fr.open( file );

		
		// Begin processing each line from the mailbox...
		String input = null;
		while( ( input = fr.readLine() ) != null ) {

			// If we need to check for the postmark, we're looking for a
			// new email message. This segment of code is only executed
			// when a null line has been encountered. (Note: "empty" lines
			// containing only whitespace COULD be considered valid
			// tokenizable strings, so we shouldn't treat such a line as
			// null). If it is the postmark, increment email count and
			// inform the system we're inside the header.
			if( checkForPostmark ) {
				checkForPostmark = false;
				String firstWord = getFirstWord( input );
				// New email encountered.
				if( firstWord.equals( "From" ) ) {
					_email++; count++;
					header = true;
					postmark = true;
				}
			}
			
			// If we're processing the header, we'll want to be sure to
			// throw out everything except From:, To: and Subject:. For the
			// fields we want, we need to ignore the field-name and
			// use only the field-body.
			if( header ) {
				
				// We can ignore the postmark, if this is where we're at.
				if( postmark ) {
					postmark = false;
					continue;
				}
				
				// If we've hit a null line, the message body is probably
				// next, but we need to make sure this is or isn't an
				// empty message body; hence we'll check for the postmark.
				if( input.length() == 0 ) {
					header = false;
					checkForPostmark = true;
					continue;
				}
				
				// Get the first word from the input line and see what it is.
				// Keep what we want and throw away the rest.
				String fieldName = getFirstWord( input );
				if( fieldName.equals( "From:" ) || fieldName.equals( "To:" ) || fieldName.equals( "Subject:" ) ) {
					// We're not interested in the field-name, just the
					// field-body. Throw away the field-name.
					input = input.substring( getPosition() );
				}
				else {
					continue;
				}
				
			}
			
			// Check for a null line. If we're in the body and run across
			// one, the next line might be the postmark, so we need to 
			// check. Don't bother trying to process the null line, just
			// move onto the next line of input.
			if( !header && input.length() == 0 ) {
				checkForPostmark = true;
				continue;
			}
			
			// See special case below for nGram-type tokenizers.
			// Take the previous incomplete token and append to it
			// the next line of input (wrap-around).
			if( wraparound ) {
				input = prevToken + input;
				wraparound = false;
				prevToken = "";
			}
			
			// Attempt to load and instatiate dynamically the tokenizer.
			SkippingTokenizer tokenizer = getTokenizer( tokenizerClass );
			
			// Set NGram. We can do this whether the tokenizer is NGram-type or not.
			tokenizer.setNGram( nGram );

			// Now, break up the input line into tokens.
			tokenizer.setStringToTokenize( input );
			while( tokenizer.hasMoreTokens() ) {
				
				// Get the token.
				String s = tokenizer.nextToken();

				// We don't want to store empty tokens, which can be possible
				// to produce with highly specialized tokenizers. So, ignore.
				if( s.length() == 0 ) {
					continue;
				}
				
				// Special case: If we have an NGram-type tokenizer, tokenizer
				// may return a token which is not of length NGram. If this
				// happens, most likely it's because we've reached the end of a line
				// and we'll need to take this partial token and prepend it to the
				// next line of input (wrap-around). Should there be no more lines 
				// of input, this token won't matter anyway since it doesn't
				// satisfy the exact NGram length.
				if( nGram > 0 && s.length() != nGram ) {
					wraparound = true;
					prevToken = s;
					break;
				}
				
				// Attempt to get the token from the table. If it doesn't
				// exist, it's the first time we've seen this token, so
				// add it. Otherwise, update the token count for this
				// token.
				Object o = _table.get( s );
				if( o == null ) {
					_table.put( s, new Integer( 1 ) );
				}
				else {
					int i = ((Integer)o).intValue();
					_table.put( s, new Integer( i + 1 ) );
				}

				// Update the total number of tokens processed.
				_tokens++;
				
			}
		}
		
		// Close input.
		fr.close();
		
		// Return the number of emails processed.
		return count;
		
	}
	
}
