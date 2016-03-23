package com.michaelzanussi.bayesian;

import java.util.Iterator;

/**
 * Abstract iterator for MondoHashTable's inner Entry classes.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (6 Feb 2004) 
 */
public abstract class AbstractIterator implements Iterator<Object> {

	// Index into the set.			
	private int idx;
			
	// The table of entries.
	private MondoHashTable.Entry[] iaTable;

	/**
	 * No-arg constructor.
	 */
	public AbstractIterator() {
		
		idx = 0;
		
	}
			
	/**
	 * Standard constructor.
	 */
	public AbstractIterator(MondoHashTable.Entry[] table) {
		
		iaTable = table;
		idx = 0;
		
	}
			
	/**
	 * Returns <code>true</code> if there are more elements.
	 * Also updates current index.
	 *
	 * @return <code>true</code> if there are more elements.
	 */
	public boolean hasNext() {
		
		// Advance pointer until an element has been found or we
		// run off the end of the table.
		for (; idx < iaTable.length; idx++) {
			if (iaTable[idx] != null && iaTable[idx] != MondoHashTable.AVAILABLE) {
				return true;
			}
		}
		
		return false;
		
	}
			
	/**
	 * Returns current element in the table.
	 *
	 * @return the current element in the table.
	 */
	public Object next() {
		
		Object elt = iaTable[idx++];
		return elt;
		
	}
			
	/**
	 * (not supported)
	 *  
	 * @throws UnsupportedOperationException if method not supported.
	 */
	public void remove() {
		
		throw new UnsupportedOperationException("remove() method not supported.");
		
	}
	
}
