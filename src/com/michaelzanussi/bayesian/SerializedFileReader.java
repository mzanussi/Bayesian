package com.michaelzanussi.bayesian;

import java.io.*;

/**
 * Wrapper class around Java's ObjectInputStream class (serialization).
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public class SerializedFileReader implements BasicIO {

	// The input stream.
	private ObjectInputStream input;
	
	/**
	 * No-arg constructor.
	 */
	public SerializedFileReader() {
		
		input = null;
		
	}
	
	/**
	 * Opens the specified file for de-serialization.
	 * 
	 * @param file the file to open.
	 * @return <code>true</code> if successful.
	 */
	public boolean open(File file) {

		try {
			input = new ObjectInputStream(new FileInputStream(file));
			return true;
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
			System.exit(1);
		}
		
		return false;
		
	}

	/**
	 * Closes the input stream.
	 * 
	 * @return <code>true</code> if successful.
	 */
	public boolean close() {
		
		try {
			if (input != null) {
				input.close();
				return true;
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return false;
		
	}
	
	/**
	 * Returns the serialized object from the input stream.
	 * 
	 * @return the serialized object from the input stream.
	 */
	public Object readObject() {
		
		Object object = null;
		
		try {
			object = input.readObject();
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("ERROR: " + e.getMessage());
		}
		
		return object;
		
	}
	
}
