package com.michaelzanussi.bayesian;

import java.io.*;

/**
 * Wrapper class around Java's BufferedReader class.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class TextFileReader implements BasicIO {

	// The input buffer.
	private BufferedReader buffer;
	
	/**
	 * No-arg constructor.
	 */
	public TextFileReader() {
		
		buffer = null;
		
	}
	
	/**
	 * Opens the specified file for reading. Supports direct file 
	 * support or an input stream from the standard input.
	 * 
	 * @param file the file to open or <code>null</code> for standard input.
	 * @return <code>true</code> if successful.
	 */
	public boolean open(File file) {
		
		try {
			if (file == null) {
				// Standard input. 
				buffer = new BufferedReader(new InputStreamReader(System.in));
			} else {	
				// Direct file.
				buffer = new BufferedReader(new FileReader(file));
			}
			return true;
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}
		
		return false;

	}

	/**
	 * Closes the file reader.
	 * 
	 * @return <code>true</code> if successful.
	 */
	public boolean close() {
		
		try {
			if (buffer != null) {
				buffer.close();
				return true;
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return false;
		
	}

	/**
	 * Returns the current line read from the buffer.
	 * 
	 * @return the current line read from the buffer.
	 */
	public String readLine() {
		
		String string = null;
		
		try {
			string = buffer.readLine();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}

		return string;
		
	}
	
}
