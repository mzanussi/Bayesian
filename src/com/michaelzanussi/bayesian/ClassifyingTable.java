package com.michaelzanussi.bayesian;

import java.text.DecimalFormat;
import java.io.File;
import java.util.*;

/**
 * The classifying table holds the token table for the unknown, sample
 * email being processed and provides the methods to analyze and
 * classify the email as either normal or spam. An optional dump()
 * method provides detailed statistics on the unknown email, provided
 * the user has specified a log file.
 *  
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class ClassifyingTable extends AbstractTable {

	// The token table for the unknown email.
	private MondoHashTable _testData;
	
	// These instance variables hold the priors for both email types
	// as well as the total email (spam+normal) that have been processed.
	private double _prNorm;
	private double _prSpam;
	private int _totEmail;
	
	// The final classification result for the unknown sample.
	private String _classification;
	
	// The two separate token tables for the email.
	private TokenTable _normal;
	private TokenTable _spam;
	
	// The tokenizer being used, and optional nGram.
	private String _tokenizer;
	private int _nGram;
	
	/**
	 * Construct a ClassifyingTable object using pre-existing token tables and
	 * specified tokenizer. 
	 * 
	 * @param normal the token table for normal email.
	 * @param spam the token table for spam email.
	 * @param tokenizerClass the tokenizer to use to tokenize the email.
	 * @param nGram if the tokenizer is NGram-type, contains the size
	 * of the NGram.
	 * @throws NullPointException if any parameters are <code>null</code>.
	 */
	public ClassifyingTable( TokenTable normal, TokenTable spam, String tokenizerClass, int nGram ) {
		
		if( normal == null || spam == null || tokenizerClass == null ) {
			throw new NullPointerException();
		}
		
		_normal = normal;
		_spam = spam;
		_tokenizer = tokenizerClass;
		_nGram = nGram;
		
		_testData = new MondoHashTable();
		
		_totEmail = 0;
		_prNorm = 0.0;
		_prSpam = 0.0;
		
	}

	/**
	 * Reads in the sample email and processes the contents. During processing,
	 * tokens are extracted based on a tokenizer and are stored in the token
	 * table, along with a count of how many times this token has been seen.
	 * The sample email is then analyzed using naive Bayes approximation
	 * 
	 * @param file the sample email to classify.
	 * @return the classification. 
	 */
	public String classify( File file ) {		

		// Calculate total email count and normal/spam priors, saving a 
		// copy of the priors for later use.
		_totEmail = _normal.getEmailCount() + _spam.getEmailCount();
		double priorN = (double)_normal.getEmailCount() / _totEmail;
		double priorS = (double)_spam.getEmailCount() / _totEmail;
		_prNorm = priorN; 
		_prSpam = priorS; 
		
		// Used in processing the email headers.
		boolean header = true;
		
		// Used for NGram-type tokenizers.
		boolean wraparound = false;
		String prevToken = "";

		// Open email file. May be direct file access or standard input.
		TextFileReader fr = new TextFileReader();
		fr.open( file );

		String input = null;
		
		// Begin processing each line from the mailbox...
		while( ( input = fr.readLine() ) != null ) {

			// If we're processing the header, we'll want to be sure to
			// throw out everything except From:, To: and Subject:. For the
			// fields we want, we need to ignore the field-name and
			// use only the field-body.
			if( header ) {
				
				// If we've hit a null line, the message body is probably
				// next, but we need to make sure this is or isn't an
				// empty message body; hence we'll check for the postmark.
				if( input.length() == 0 ) {
					header = false;
					continue;
				}
				// Get the first word from the input line and see what it is.
				// Keep what we want and throw away the rest.
				String fieldName = getFirstWord( input );
				if( fieldName.equals( "From:" ) || fieldName.equals( "To:" ) || fieldName.equals( "Subject:" ) ) {
					input = input.substring( getPosition() );
				}
				else {
					continue;
				}
				
			}
			
			// See special case below for nGram-type tokenizers.
			// Take the previous incomplete token and append to it
			// the next line of input.
			if( wraparound ) {
				input = prevToken + input;
				wraparound = false;
				prevToken = "";
			}
			
			// Attempt to load and instatiate dynamically the tokenizer.
			SkippingTokenizer tokenizer = getTokenizer( _tokenizer );
			
			// Set NGram. We can do this whether the tokenizer is NGram-type or not.
			tokenizer.setNGram( _nGram );

			// Now, break up the input line into tokens.
			tokenizer.setStringToTokenize( input );
			while( tokenizer.hasMoreTokens() ) {
				
				// Get the token.
				String s = tokenizer.nextToken();

				// We don't want to store empty strings (as possible with highly
				// specialized tokenizers) so ignore.
				if( s.length() == 0 ) {
					continue;
				}
				
				// Special case: If we have an nGram-type tokenizer, tokenizer
				// may return a token which is not of length nGram. If this
				// happens, most likely it's because we've reached the end of a line
				// and we'll need to take this partial token and prepend it to the
				// next line of input. Should there be no more lines of input, this 
				// token won't matter anyway.
				if( _nGram > 0 && s.length() != _nGram ) {
					wraparound = true;
					prevToken = s;
					break;
				}
				
				// Calculate the naive Bayes approximation. Since the approximation
				// returned is log-likelihood, we're performing a sum rather than
				// a product.
				double nBayes = bayes( _normal, s ); priorN += nBayes;
				double sBayes = bayes( _spam, s ); priorS += sBayes;
				
				// Attempt to get the token from the table. If it doesn't
				// exist, it's the first time we've seen this token, so
				// add it. Otherwise, update the token count for this
				// token.
				Object o = _testData.get( s );
				if( o == null ) {
					// It's the first time we've seen this token; add it.
					_testData.put( s, new _Entry( 1, nBayes, sBayes ) );
				}
				else {
					// Token exists, so update its count and approximations.
					_Entry e = (_Entry)o;
					_testData.put( s, new _Entry( 
							e.getCount() + 1, 
							e.getNBayes() + nBayes, 
							e.getSBayes() + sBayes ) );
				}
				
			}
		}
		
		// Close the input.
		fr.close();

		// Format the classification string, store it, and send it back.
		DecimalFormat df = new DecimalFormat("0.00");
		String strN = df.format( priorN );
		String strS = df.format( priorS );
		String strD = df.format( Math.abs( priorN - priorS ) );
		StringBuffer output = new StringBuffer();
		output.append( "X-Spam-Status: " );
		output.append( priorN > priorS ? "NORMAL, " : "SPAM, " );
		output.append( "N: " + strN + ", S: " + strS + ", Diff: " + strD );
		_classification = output.toString();
		
		return _classification;
		
	}

	/**
	 * Dumps unknown email statistics to the specified log file. 
	 * 
	 * @param file the log file to write to.
	 */
	public void dump( File file ) {
		
		TextFileWriter fw = new TextFileWriter();
		fw.open( file );
		
		fw.writeln( "Tokenizer: " + _tokenizer + "\n" );
		
		DecimalFormat df = new DecimalFormat("0.000000");
		DecimalFormat small = new DecimalFormat("0.0");
		String priorN = df.format( _prNorm );
		String priorS = df.format( _prSpam );
		fw.writeln( "Norm Table: Email=" + _normal.getEmailCount() + 
				", Tokens=" + _normal.getTokenCount() +
				", Unique=" + _normal.getTable().size() +
				", Prior=" + priorN );
		fw.writeln( "Spam Table: Email=" + _spam.getEmailCount() + 
				", Tokens=" + _spam.getTokenCount() +
				", Unique=" + _spam.getTable().size() +
				", Prior=" + priorS );
		
		fw.writeln( "\n" + _classification );
		
		fw.writeln( "\nBayes Norm\t\tBayes Spam\t\t  Diff\t\t\tCount\tToken");
		fw.writeln(   "----------\t\t----------\t\t  ----\t\t\t-----\t-----");
		
		Set s = _testData.entrySet();
		Iterator i = s.iterator();
		while( i.hasNext() ) {
			MondoHashTable.Entry me = (MondoHashTable.Entry)i.next();
			String key = (String)me.getKey();
			_Entry e = (_Entry)me.getValue();
			String strN = df.format( e.getNBayes() );
			String strS = df.format( e.getSBayes() );
			String strD = small.format( Math.abs( e.getNBayes() - e.getSBayes() ) );
			String like = ( e.getNBayes() > e.getSBayes() ? "n" : "s" );
			fw.writeln( strN + "\t\t" + strS + "\t\t" + like + " " + strD + "\t\t\t" + e.getCount() + "\t\t" + key);
		}
		
		fw.writeln( "\nEnd." );
		
		fw.close();
	}

	/**
	 * Private inner class for a classification entry.
	 * 
	 * @author <a href="mailto:zanussi@cs.unm.edu">Michael Zanussi</a>
	 * @version 1.0 (20 Feb 2004) 
	 */
	private final static class _Entry {
		
		// The token count.
		private int _count;
		
		// The naive Bayes approximation for this token.
		// within normal email.
		private double _nBayes;
		
		// The naive Bayes approximation for this token.
		// within spam email.
		private double _sBayes;
		
		/**
		 * No-arg constructor.
		 */
		public _Entry() {
			
			this( 0, 0.0, 0.0 );
			
		}
		
		/**
		 * Constructs an entry object built from token count and
		 * Bayes approximations for normal and spam.
		 * 
		 * @param count the token count.
		 * @param nBayes the naive Bayes approximation (normal)
		 * @param sBayes the naive Bayes approximation (spam)
		 */
		public _Entry( int count, double nBayes, double sBayes ) {
			
			_count = count;
			_nBayes = nBayes;
			_sBayes = sBayes;
			
		}
		
		/**
		 * Returns the token count.
		 * 
		 * @return the token count.
		 */
		public int getCount() {
			
			return _count;
			
		}
		
		/**
		 * Returns the naive Bayes approximation for this token (normal).
		 * 
		 * @return the naive Bayes approximation.
		 */
		public double getNBayes() {
			
			return _nBayes;
			
		}
		
		/**
		 * Returns the naive Bayes approximation for this token (spam).
		 * 
		 * @return the naive Bayes approximation.
		 */
		public double getSBayes() {
			
			return _sBayes;
			
		}
		
	}

}
