package com.michaelzanussi.bayesian;

/**
 * Thrown by methods in the <code>MondoHashTable</code> class to 
 * indicate that the hash table is full.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (6 Feb 2004) 
 */
public class HashTableFullException extends RuntimeException {

	/**
	 * Because: It is strongly recommended that all serializable 
	 * classes explicitly declare serialVersionUID values. 
	 */
	private static final long serialVersionUID = -7473829459851280825L;

	/**
	 * Default constructor.
	 */
	public HashTableFullException(String error) {
		
		super(error);
		
	}
	
}
