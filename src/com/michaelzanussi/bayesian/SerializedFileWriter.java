package com.michaelzanussi.bayesian;

import java.io.*;

/**
 * Wrapper class around Java's ObjectOutputStream class (serialization).
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class SerializedFileWriter implements BasicIO {

	// The output stream.
	private ObjectOutputStream output;
	
	/**
	 * No-arg constructor.
	 */
	public SerializedFileWriter() {
		
		output = null;
		
	}
	
	/**
	 * Opens the specified file for serialization.
	 * 
	 * @param file the file to open.
	 * @return <code>true</code> if successful.
	 */
	public boolean open(File file) {

		try {
			output = new ObjectOutputStream(new FileOutputStream(file));
			return true;
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}
		
		return false;
		
	}

	/**
	 * Closes the output stream.
	 * 
	 * @return <code>true</code> if successful.
	 */
	public boolean close() {
		
		try {
			if (output != null) {
				output.close();
				return true;
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return false;
		
	}
	
	/**
	 * Serializes and writes the specified object to the output stream.
	 * 
	 * @param object the object to serialize.
	 * @throws NullPointerException if the object to serialize is <code>null</code>.
	 */
	public void writeObject(Object object) {
		
		if (object == null) {
			throw new NullPointerException();
		}
		
		try {
			output.writeObject(object);
			output.flush();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
	}
	
}
