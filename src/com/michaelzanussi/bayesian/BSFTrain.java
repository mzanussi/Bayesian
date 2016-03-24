package com.michaelzanussi.bayesian;

import java.io.File;
import gnu.getopt.Getopt;

/**
 * The Bayesian spam filter training tool is responsible for analyzing known
 * examples of normal (non-spam) and spam emails, compiling statistical models 
 * for each, and saving a durable copy of the models to disk. BSFTrain will 
 * then use these models to classify unknown email samples as either normal or spam.<p>
 * 
 * BSFTrain accepts email inputs in the format described by RFC822 as training
 * input. It also accepts Unix mailbox (mbox) format files as training input,
 * recoginizing the individual messages that occur within the file as separate
 * email entities. <p>
 * 
 * BSFTrain accepts its training files either from standard input or from
 * direct access to them via the -f command line option. The resultant statistical
 * models are stored on disk as a single file as specified by the -m option.
 * BSFTrain automatically adds the extension of .stat to the file. <p>
 * 
 * BSFTrain also produces a human-readable dump of the current statistical models,
 * via the -d option.<p>
 * 
 * The full suite of commands and options follow:<p>
 * 
 * 		-s		Treat input data as spam.<br>
 * 		-n		Treat input data as normal (non-spam).<br>
 *  	-t		Runs BSFTrain in TRAINING mode. Compiles input data into<br>
 * 				tokens and statistics and updates the existing models.<br>
 * 		-d		Runs BSFTrain in DUMP mode, providing detailed statistics<br>
 *              if a log file is specified or summary statistics only if<br>
 *				no log file has been specified.<br>
 * 		-f file The mailbox to read in email from. If not specified,<br> 
 * 				email is read in from the standard output (optional).<br>
 * 		-g val	The NGram value, if the tokenizer is NGram-type.<br>
 * 		-k name	The name of the tokenizer to be used to compile the<br>
 * 				token tables.<br>
 * 		-l file The name of the log file to output dump results to. If<br>
 * 				not specified and running in dump mode, summary stats<br>
 * 				will be displayed to the standard output (optional).<br>
 * 		-m file The name of the statistics model the tokens tables are<br>
 * 				output to. BSFTrain will add the .stat extension.<br>
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class BSFTrain {
	
	/**
	 * BSFTrain running modes.
	 */
	public static final int TRAINING_MODE = 1;
	public static final int DUMP_MODE = 2;
	
	// The running mode, either training or dump.
	private int runMode;

	// The email type being trained, either 
	// NORMAL_EMAIL or SPAM_EMAIL (see the class
	// TokenTable).
	private int emailType;
	
	// The statistical model file name.
	private File model;
	
	// The log file.
	private File log;

	// The input file (either direct or standard input).
	private File file;
	
	// The name of the tokenizer.
	private String tokenizer;

	// The command line arguments.
	private String[] args;
	
	// Holds the normal and spam token tables.
	private RawData email;
	
	// The NGram (for NGram-type tokenizers).
	private int nGram;
	
	/**
	 * No-arg constructor.
	 */
	BSFTrain() {
		
		this(null);
		
	}
	
	/**
	 * The standard constructor. Initializes local data.
	 *
	 * @param args the command line arguments.
	 */
	BSFTrain(String[] args) {
		
		this.args = args;
		email = null;
		file = null;
		log = null;
		nGram = 0;
		runMode = 0;
		emailType = 0;
		model = null;
		tokenizer = null;
		
	}

	/**
	 * Parses the command line options via Getopt, then verifies and cleans up
	 * the options before running the processor itself.
	 */
	public void run() {

		Getopt option = new Getopt("BSFTrain", args, "df:g:k:l:m:nst");
		int ch;
		while ((ch = option.getopt()) != -1) {
			switch (ch) {
				case 'd':
					// Run BSFTrain in dump mode. Check to see if we're already in
					// training mode (would indicate both modes were specified). We
					// shouldn't assume what the user might want, so exit gracefully.
					if (runMode == TRAINING_MODE) {
						System.err.println("ERROR: Training and Dump mode cannot run simultaneously.");
						usage();
						System.exit(1);
					}
					runMode = DUMP_MODE;
					break;
				case 'f':
					// Set the file to process email from (optional).
					file = new File(option.getOptarg());
					break;
				case 'g':
					// Verify and set the nGram for this tokenizer.
					nGram = Integer.parseInt(option.getOptarg());
					if (nGram < 1) {
						System.err.println("ERROR: nGram must be greater than 0.");
						usage();
						System.exit(1);
					}
					break;
				case 'n':
					// Process normal email. First, check to see if we're 
					// already processing spam.
					if (emailType == TokenTable.SPAM_EMAIL) {
						System.err.println("ERROR: Normal and Spam training mode cannot run simultaneously.");
						usage();
						System.exit(1);
					}
					emailType = TokenTable.NORMAL_EMAIL;
					break;
				case 's':
					// Process spam email. First, check to see if we're 
					// already processing normal email.
					if (emailType == TokenTable.NORMAL_EMAIL) {
						System.err.println("ERROR: Normal and Spam training mode cannot run simultaneously.");
						usage();
						System.exit(1);
					}
					emailType = TokenTable.SPAM_EMAIL;
					break;
				case 't':
					// Run BSFTrain in training mode. Check to see if we're already in
					// dump mode (would indicate both modes were specified). We
					// shouldn't assume what the user might want, so exit gracefully.
					if (runMode == DUMP_MODE) {
						System.err.println("ERROR: Training and Dump mode cannot run simultaneously.");
						usage();
						System.exit(1);
					}
					runMode = TRAINING_MODE;
					break;
				case 'k':
					// Set the tokenizer.
					tokenizer = option.getOptarg();
					break;
				case 'l':
					// Set the log file to use for dump mode.
					log = new File(option.getOptarg());
					break;
				case 'm':
					// Set the model statistics file. Assume extension has
					// not been provided by user.
					String fileName = option.getOptarg() + ".stat";
					model = new File(fileName);
					break;
				default:
					// Unknown or illegal option, skip. If using getOpt,
					// getOpt will return its own error messages.

			}
			
		}
		
		// With command line options dealt with, check if a tokenizer and
		// a statistical model file were passed to us.
		if (tokenizer == null) {
			System.err.println("ERROR: A tokenizer was not specified.\n");
			usage();
			System.exit(1);
		} else if (model == null) {
			System.err.println("ERROR: A statistical model file was not specified.\n");
			usage();
			System.exit(1);
		}

		// Do something useful, depending on which mode we're running in...
		switch (runMode) {
			case TRAINING_MODE:
				// Check to see if the model file exists. If it does, load it.
				// If not, create a new email table for specified type.
				if (model.exists()) {
					
					// Open the existing model file.
					System.out.println("Loading model...");
					SerializedFileReader sfr = new SerializedFileReader();
					sfr.open(model);
					email = (RawData)sfr.readObject();
					if (email == null) {
						throw new NullPointerException();
					}
					sfr.close();
					
					// Verify the passed tokenizer is the same as the one stored. 
					// If not, dump a warning message but continue processing by 
					// reverting to the original tokenizer used.
					if (!tokenizer.equals(email.getTokenizer())) {
						System.err.println("Warning: The specified tokenizer is incompatible with the ");
						System.err.println("         stored data tokenizer. Reverting to stored version.");
						System.err.println("         Specified is [" + tokenizer + "]");
						System.err.println("         Stored is [" + email.getTokenizer() + "]\n");
						tokenizer = email.getTokenizer();
						nGram = email.getNGram();
					} else {
						// The tokenizer matches, now check for nGram conflicts.
						if (email.getNGram() != nGram) {
							System.err.println("Warning: The specified NGram is incompatible with the ");
							System.err.println("         stored NGram. Reverting to stored version.");
							System.err.println("         Specified is [" + nGram + "]");
							System.err.println("         Stored is [" + email.getNGram() + "]\n");
							nGram = email.getNGram();
						}
					}
				} else {
					// Model doesn't exist.
					email = new RawData();
				}
				
				// Provide some helpful messages.
				System.out.print("Processing ");
				System.out.print(emailType == TokenTable.NORMAL_EMAIL ? "normal " : "spam ");
				if (file == null) {
					System.out.print("email (standard input) using ");
				} else {
					System.out.print("email (" + file + ") using ");
				}
				System.out.println(tokenizer + "...");
				
				// Process the email file!
				int nEmail = email.process(emailType, file, tokenizer, nGram);
				
				// Display some results.
				System.out.println("Processed: " + nEmail + " email(s).");
				System.out.println("Total now: " + email.getEmailCount(emailType) + " email(s).");
				System.out.println("Token cnt: " + email.getTable(emailType).getTokenCount());
				
				// Write the object to disk, but only if email was processed.
				if (nEmail > 0) {
					System.out.println("Saving model...");
					SerializedFileWriter sfw = new SerializedFileWriter();
					sfw.open(model);
					sfw.writeObject(email);
					sfw.close();
				}
				break;
			case DUMP_MODE:
				if (model.exists()) {
					// Open the model file.
					System.out.println("Loading model...");
					SerializedFileReader sfr = new SerializedFileReader();
					sfr.open(model);
					email = (RawData)sfr.readObject();
					if (email == null) {
						throw new NullPointerException();
					}
					sfr.close();
					// Open the file writer.
					TextFileWriter fw = null;
					if (log != null) {
						// User wants to write to a log.
						fw = new TextFileWriter();
						fw.open(log);
					}
					// Perform the dump.
					email.dump(TokenTable.NORMAL_EMAIL, fw);
					email.dump(TokenTable.SPAM_EMAIL, fw);
					// Close the file writer (if open).
					if (fw != null) {
						fw.close();
					} else {
						System.out.println();
					}
				} else {
					System.err.println("ERROR: Cannot perform dump because model file does not exist.");
					System.exit(1);
				}
				break;
			default:
				System.err.println("ERROR: Neither training nor dump mode was specified.\n");
				usage();
				System.exit(1);
		}
		
	}

	/**
	 * Displays usage notes.
	 */
	public void usage() {
		System.out.println("Usage: java -classpath .:./java-getopt-1.0.9.jar BSFTrain [options] -m modelFile -k tokenizer");
		System.out.println("   Where commands and options are: " );
		System.out.println("          -s       Treat input as spam." );
		System.out.println("          -n       Treat input as normal." );
		System.out.println("          -t       Run in training mode." );
		System.out.println("          -d       Run in dump mode." );
		System.out.println("          -g       NGram value if tokenizer is type NGram." );
		System.out.println("          -m file  Load/save statistical model." );
		System.out.println("          -k name  Load tokenizer 'name'." );
		System.out.println("          -f file  Mail file to process.");
		System.out.println("          -l file  Log file to output dump to.");
	}
	
	public static void main(String[] args) {

		BSFTrain train = new BSFTrain(args);
		
		if (args.length < 3) {
			train.usage();
			System.exit(1);
		}
		
		System.out.println("Standby...");
		train.run();
		System.out.println("Finished.");
	}
	
}
