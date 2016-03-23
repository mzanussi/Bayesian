package com.michaelzanussi.bayesian;

import java.io.File;
import gnu.getopt.Getopt;

/**
 * The Bayesian spam filter testing tool is responsible for analyzing unknown
 * spam emails, calculating the naive Bayes approximation, and classifying
 * the unknown email as either normal or spam.<p>
 * 
 * BSFTest accepts a single unknown email in the format described by RFC822 
 * as input. BSFTest accepts its training files via standard input, or
 * optionally as direct input when using the -f command. If a log file is
 * specified with the -l option, a detailed log is created showing data
 * used to determine normal/spam classification. <p>
 * 
 * Like BSFTrain, BSFTest requires the model file and tokenizer to be specified.
 * However, in the case of NGram-type tokenizers, the NGram value does not need
 * to be specified. <p>
 * 
 * 		-f file The unknown email to process from (optional). <br>
 * 		-k name	The name of the tokenizer to be used to compile the<br>
 * 				token tables.<br>
 * 		-m file The name of the statistics model the tokens tables are<br>
 * 				output to. BSFTest will add the .stat extension.<br>
 * 		-l file The name of the log file to output dump results to (optional).<br>
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class BSFTest {

	// The email type being trained, either 
	// NORMAL_EMAIL or SPAM_EMAIL (see the class
	// TokenTable).
	private int _emailType;
	
	// The statistical model file name.
	private File _model;
	
	// The input file (either direct or standard input).
	private File _file;
	
	// The log file.
	private File _log;
	
	// The name of the tokenizer.
	private String _tokenizer;

	// The command line arguments.
	private String[] _args;
	
	// Holds the unknown email statistics.
	private RawData _rawData;
	
	/**
	 * No-arg constructor.
	 */
	BSFTest() {
		
		this( null );
		
	}
	
	/**
	 * The standard constructor. Initializes local data.
	 *
	 * @param args the command line arguments.
	 */
	BSFTest( String [] args ) {
		
		_args = args;
		_rawData = null;
		_file = null;
		_emailType = 00;
		_log = null;
		_model = null;
		_tokenizer = null;
		
	}

	/**
	 * Process command line options and run the trainer.
	 */
	public void run() {

		Getopt option = new Getopt( "BSFTest", _args, "f:k:l:m:" );
		int ch;
		while( ( ch = option.getopt() ) != -1 ) {
			switch( ch ) {
				case 'f':
					// Set the file to process email from (optional).
					_file = new File( option.getOptarg() );
					break;
				case 'k':
					// Set the tokenizer. Note: This option is basically useless
					// since the tokenizer class is stored with the data we're
					// testing this email against. This is why there is no option
					// for specifying the nGram value. However, since the specs
					// require this option, it's included and verified.
					_tokenizer = option.getOptarg();
					break;
				case 'l':
					// Set the log file to use for the classification log (optional).
					_log = new File( option.getOptarg() );
					break;
				case 'm':
					// Set the model statistics file. Assume extension has
					// not been provided by user.
					String fileName = option.getOptarg() + ".stat";
					_model = new File( fileName );
					break;
				default:
					// Unknown or illegal option, skip. If using getOpt,
					// getOpt will return its own error messages.
	
			}
		}
		
		// With command line options dealt with, check if a tokenizer and
		// a statistical model file were passed to us.
		if( _tokenizer == null ) {
			System.err.println( "ERROR: A tokenizer was not specified.\n" );
			usage();
			System.exit(1);
		}
		else if( _model == null ) {
			System.err.println( "ERROR: A statistical model file was not specified.\n" );
			usage();
			System.exit(1);
		}
		
		// Check if the model exists. If it does, open the model and store
		// it off, then verify the tokenzier passed is correct. If the model
		// doesn't exist, exit gracefully.
		if( _model.exists() ) {
			
			// Open the model file.
			SerializedFileReader sfr = new SerializedFileReader();
			sfr.open( _model );
			_rawData = (RawData) sfr.readObject();
			sfr.close();
			
			// Verify the passed tokenizer is the same as the one stored. 
			// If not, dump a warning message but continue processing by 
			// reverting to the original tokenizer used. In actuality, we're 
			// not reverting since we're just ignoring the command line
			// option anyhow (see note above for option -m).
			if( !_tokenizer.equals( _rawData.getTokenizer() ) ) {
				System.err.println( "Warning: The specified tokenizer is incompatible with the " );
				System.err.println( "         stored data tokenizer. Reverting to stored version." );
				System.err.println( "         Specified is [" + _tokenizer + "]" );
				System.err.println( "         Stored is [" + _rawData.getTokenizer() + "]\n" );
			}
			
		}
		else {
			// Oops, specified model doesn't exist.
			System.err.println( "ERROR: The statistical model (" + _model + ") does not exist." );
			System.err.println( "       Cannot proceed until analyzer has been trained." );
			System.exit(1);
		}

		// Create a new classifier.
		ClassifyingTable cl = new ClassifyingTable( 
				_rawData.getTable( TokenTable.NORMAL_EMAIL ), 
				_rawData.getTable( TokenTable.SPAM_EMAIL ), 
				_rawData.getTokenizer(), 
				_rawData.getNGram() );
		
		// Classify the email. 
		System.out.println( cl.classify( _file ) );
		
		// Dump log, if necessary.
		if( _log != null ) {
			cl.dump( _log );
		}
	
	}

	/**
	 * Displays usage notes.
	 */
	public void usage() {
		System.out.println( "Usage: java -classpath .:./java-getopt-1.0.9.jar BSFTest [options]  -m modelFile -k tokenizer" );
		System.out.println( "   Where options are: " );
		System.out.println( "          -f file  Single email to process (optional).");
		System.out.println( "          -k name  Tokenizer." );
		System.out.println( "          -m file  Load statistical model." );
		System.out.println( "          -l file  Log file to output dump to (optional).");
	}
	
	public static void main(String[] args) {

		BSFTest test = new BSFTest( args );
		
		if(args.length < 3) {
			test.usage();
			System.exit(1);
		}
		
		test.run();
		
	}
	
}
