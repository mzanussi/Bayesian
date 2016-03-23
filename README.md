# Bayesian Spam Filter


BSFTrain

The Bayesian spam filter training tool is responsible for analyzing known examples of normal (non-spam) and spam emails, compiling statistical models for each, and saving a durable copy of the models to disk. BSFTrain will then use these models to classify unknown email samples as either normal or spam.

BSFTrain accepts email inputs in the format described by RFC822 as training input. It also accepts Unix mailbox (mbox) format files as training input, recoginizing the individual messages that occur within the file as separate email entities.

BSFTrain accepts its training files either from standard input or from direct access to them via the -f command line option. The resultant statistical models are stored on disk as a single file as specified by the -m option. BSFTrain automatically adds the extension of .stat to the file.

BSFTrain also produces a human-readable dump of the current statistical models, via the -d option.

The full suite of commands and options follow:

		-s			Treat input data as spam.
		-n			Treat input data as normal (non-spam).
		-t			Runs BSFTrain in TRAINING mode. Compiles input data into
						tokens and statistics and updates the existing models.
		-d			Runs BSFTrain in DUMP mode, providing detailed statistics
						if a log file is specified or summary statistics only if
						no log file has been specified.
		-f 			file The mailbox to read in email from. If not specified,
						email is read in from the standard output (optional).
		-g val	The NGram value, if the tokenizer is NGram-type.
		-k name	The name of the tokenizer to be used to compile the
						token tables.
		-l file	The name of the log file to output dump results to. If
						not specified and running in dump mode, summary stats
						will be displayed to the standard output (optional).
		-m file	The name of the statistics model the tokens tables are
						output to. BSFTrain will add the .stat extension.


BSFTest

The Bayesian spam filter testing tool is responsible for analyzing unknown spam emails, calculating the naive Bayes approximation, and classifying the unknown email as either normal or spam.

BSFTest accepts a single unknown email in the format described by RFC822 as input. BSFTest accepts its training files via standard input, or optionally as direct input when using the -f command. If a log file is specified with the -l option, a detailed log is created showing data used to determine normal/spam classification.

Like BSFTrain, BSFTest requires the model file and tokenizer to be specified. However, in the case of NGram-type tokenizers, the NGram value does not need to be specified.

		-f file	The unknown email to process from (optional).
		-k name	The name of the tokenizer to be used to compile the
						token tables.
		-m file	The name of the statistics model the tokens tables are
						output to. BSFTest will add the .stat extension.
		-l file	The name of the log file to output dump results to (optional).
