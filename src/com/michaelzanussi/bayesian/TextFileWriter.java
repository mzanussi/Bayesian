package com.michaelzanussi.bayesian;

import java.io.*;

/**
 * Wrapper class around Java's BufferedWriter class.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class TextFileWriter implements BasicIO {

	// The output buffer.
	private BufferedWriter buffer;
		 
	/**
	 * No-arg constructor.
	 */
	public TextFileWriter() { 
		
		buffer = null;
		
	}
	
	/**
	 * Opens the specified file for writing.
	 * 
	 * @param file the file to open.
	 * @return <code>true</code> if successful.
	 */
	public boolean open(File file) {

		try {
			buffer = new BufferedWriter(new FileWriter(file));
			return true;
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}
		
		return false;
		
	}

	/**
	 * Closes the file writer.
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
	 * Writes a string to the buffer, without a newline.
	 * 
	 * @param string the string to write to the buffer.
	 */
	public void write(String string) {
		
		try {
			buffer.write(string);
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
	}

	/**
	 * Writes a string to the buffer, followed by a newline.
	 * 
	 * @param string the string to write to the buffer.
	 */
	public void writeln(String string) {
		
		try {
			buffer.write(string);
			buffer.newLine();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
	}

	/**
	 * Writes a newline to the buffer.
	 */
	public void writeln() {
		
		try {
			buffer.newLine();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
	}

}
