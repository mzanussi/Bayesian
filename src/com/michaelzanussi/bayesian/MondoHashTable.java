package com.michaelzanussi.bayesian;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.AbstractSet;
import java.util.Iterator;
import java.io.Serializable;

/**
 * An updated hash table implementation utilizing an open
 * addressing scheme (quadratic probing). Implements 
 * java.util.Map interface completely.<p>
 * 
 * Updates since v1.1 - serialized MondoHashTable.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.1 (20 Feb 2004) 
 */
public class MondoHashTable implements Map, Serializable {

	// When locating an index, if key is already in hash table, isKey
	// will be set to true. Used to distinguish between empty or
	// AVAILABLE slots.
	transient private boolean _isKey = false;
	
	// The current number of mappings in the hash table.	
	private int _mappings;

	// The hash table.
	private Entry [] _table;

	// The default hash table size. This integer value should be a prime. 
	private static final int DEFAULT_TABLE_SIZE = 101;
	
	// Load factor. Rehashing occurs when load factor exceeds default
	// (should be between .3 and .5).
	private static final float DEFAULT_LOAD_FACTOR = 0.5f;
	private float _loadFactor;

	/**
	 * Sentinel (for items removed from hash table).
	 */
	public static final Entry AVAILABLE = new Entry( null, null );

	/**
	 * No-arg constructor. Instantiates a new hash table of default
	 * size DEFAULT_TABLE_SIZE.
	 */
	public MondoHashTable() {
		
		this( DEFAULT_TABLE_SIZE, DEFAULT_LOAD_FACTOR );
		
	}
	
	/**
	 * Single-arg constructor.
	 */
	public MondoHashTable( int hashSize ) {
		
		this( hashSize, DEFAULT_LOAD_FACTOR );
		
	}
	
	/**
	 * Instantiates a new hash table with size <code>hashSize</code>. If
	 * <code>hashSize</code> is not prime, the next largest prime is
	 * calculated and the table is created with that new size. The load
	 * factor <code>loadFactor</code>determines at which point the table 
	 * needs to be resized. Valid values are between 0.0 and 1.0.
	 * 
	 * @param hashSize the (approximate) size of the hash table to create.
	 * @param loadFactor the load factor for the hash table.
	 */
	public MondoHashTable( int hashSize, float loadFactor ) {
		
		_mappings = 0;
		_loadFactor = loadFactor;
		hashSize = _getPrime( hashSize );
		_table = new Entry[hashSize];
		
	}

	/**
	 * Returns the capacity of the hash table.
	 * 
	 * @return the capacity of the hash table.
	 */
	public int capacity() {
		
		return _table.length;
		
	}
	
	/**
	 * Clears all key-value mappings from the hash table.
	 */
	public void clear() {
		
		// Set everything to null.
		for( int i = 0; i < _table.length; i++ ) {
			_table[i] = null;
		}
		
		_mappings = 0;
		
	}
	
	/**
	 * Returns <code>true</code> if the hash table contains the specified key.
	 *
	 * @param key the key to be located.
	 * @return <code>true</code> if the hash table contains the specified key.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public boolean containsKey( Object key ) {

		// Null keys not allowed.
		if( key == null ) {
			throw new NullPointerException( "Key cannot be null." );
		}
		
		// Locate the index into the hash table where
		// this key is mapped to.
		int index = _findKey( key );

		// Key doesn't exist if index is -1.
		return ( index == -1 ? false : true );

	}

	/**
	 * Returns <code>true</code> if the hash table contains the specified value.
	 *
	 * @param value the value to be located.
	 * @return <code>true</code> if the hash table contains the specified value.
	 * @throws NullPointerException if the value is <code>null</code>.
	 */
	public boolean containsValue( Object value ) {

		// Null values not allowed.
		if( value == null ) {
			throw new NullPointerException( "Value cannot be null." );
		}

		// Cycle through the table, searching...
		for( int i = 0; i < _table.length; i++ ) {
			if( _table[i] != null && _table[i] != AVAILABLE ) {
				if( value.equals( _table[i].getValue() ) ) {
					return true;
				}
			}
		}

		// Value wasn't found.
		return false;
		
	}

	/**
	 * Inner class for a map entry (key-value pair).
	 * 
	 * Updates since v1.1 - serialized Entry.
	 * 
	 * @author <a href="mailto:zanussi@cs.unm.edu">Michael Zanussi</a>
	 * @version 1.1 (20 Feb 2004) 
	 */
	final static class Entry implements Map.Entry, Serializable {
		
		// The key-value pair.
		private Object key;
		private Object value;
		
		/**
		 * Instantiate a new key-value pair entry.
		 * 
		 * @param k the key
		 * @param v the value
		 */
		public Entry( Object k, Object v ) {
			
			key = k;
			value = v;
			
		}
		
		/**
		 * Returns the key for this mapping entry.
		 * 
		 * @return the key for this mapping entry.
		 */
		public Object getKey() {
			
			return key;
			
		}
		
		/**
		 * Returns the value for this mapping entry.
		 * 
		 * @return the value for this mapping entry.
		 */
		public Object getValue() {
			
			return value;
			
		}
		
		/**
		 * Sets the value for this mapping entry.
		 * 
		 * @param value the new value for this mapping entry.
		 * @return the old value for this mapping entry.
		 */
		public Object setValue( Object value ) {
			
			Object o = this.value;
			this.value = value;
			return o;
			
		}

		/**
		 * Tests passed object with this entry for equality.
		 * 
		 * @param o the object to be compared.
		 * @return <code>true</code> if objects are equal.
		 */
		public boolean equals( Object o ) {
			
			// Proceed only if object o is an instance of Map.entry.
			if( o instanceof Map.Entry ) {
				Map.Entry entry = (Map.Entry) o;
				Object thisKey = key, thisValue = value;
				Object thatKey = entry.getKey(), thatValue = entry.getValue();
				// Perform the comparision.
				return 	( thisKey == null ? thatKey == null : thisKey.equals( thatKey ) ) &&
						( thisValue == null ? thatValue == null : thisValue.equals( thatValue ) ); 
			}
			
			return false;
			
		}
		
		/**
		 * Returns the hash code for this mapping entry.
		 * 
		 * @return the hash code for this mapping entry.
		 */
		public int hashCode() {
			
			int hashCode =	( key == null ? 0 : key.hashCode() ) ^
							( value == null ? 0 : value.hashCode() );
			
			return hashCode;
			
		}
	}

	/**
	 * Returns a set view of the key-value pairs contained in the hash table.
	 *
	 * @return the set view of the key-value pairs contained in the hash table.
	 */
	public Set entrySet() {
		
		return new _entrySetView();
		
	}
	
	/**
	 * Tests whether the specified map is equal to the current map.
	 *
	 * @param o the map to compare.
	 * @return <code>true</code> if the maps are equal.
	 */
	public boolean equals( Object o ) {

		// Make sure the passed object is a map.
		if( ( o instanceof Map ) ) {
			Map map = (Map)o;
			// Compare sizes first.
			if( this.size() == map.size() ) {
				// Test for equality.
				return map.entrySet().equals( entrySet() );
			}
		}
		
		return false;
		
	}
	
	/**
	 * Returns the value associated with the specified key in this map.
	 *
	 * @param key the key to be located.
	 * @return the value associated with the key, or <code>null</code>
	 * if not found.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public Object get( Object key ) {
		
		// Null keys not allowed.
		if( key == null ) {
			throw new NullPointerException( "Key cannot be null." );
		}
		
		// Locate the index into the hash table where
		// this key is mapped to.
		int index = _findKey( key );

		// Key not found!
		if( index == -1 ) {
			return null;	
		}
		
		// Return the value for this key.
		return _table[index].getValue();

	}

	/**
	 * Returns the hash code for this map, which is simply the sum of all
	 * the Entry's <code>hashCodes</code>.
	 *
	 * @return the hash code.
	 */
	public int hashCode() {
		
		int hashCode = 0;
		
		// Iterate through the specified map, and simply
		// sum the hash codes for each key-value pair.
		Iterator it = entrySet().iterator();
		while( it.hasNext() ) {
			Entry entry = (Entry)it.next();
			hashCode += entry.hashCode();
		}
		
		return hashCode;
	}
	
	/**
	 * Returns <code>true</code> if the hash table contains no mappings.
	 *
	 * @return <code>true</code> if the hash table contains no mappings.
	 */
	public boolean isEmpty() {
		
		return ( _mappings == 0 );
		
	}
	
	/**
	 * Returns a set view of the keys contained in the hash table.
	 *
	 * @return the set view of the keys contained in the hash table.
	 */
	public Set keySet() {
		
		return new _keySetView();
		
	}

	/**
	 * Returns the hash table load factor.
	 * 
	 * @return the hash table load factor.
	 */
	public float loadFactor() {
		
		return _loadFactor;
		
	}
	
	/**
	 * Associates the specified value with the specified key in this map.
	 * If the key already exists, the value is replaced with the new
	 * value. If a collision occurs, the next available slot is located
	 * by means of quadratic probing. This available slot is either a new,
	 * never used slot or one that is specially marked with AVAILABLE.
	 *
	 * @param key the key to be mapped.
	 * @param value the value associated with the key.
	 * @return the previous value associated with the key, or <code>null</code>.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public Object put( Object key, Object value ) {

		// Null keys not allowed.
		if( key == null ) {
			throw new NullPointerException( "Key cannot be null." );
		}

		// Initialization.		
		Object oldValue = null;
		
		// Find the next available location where we can put
		// a new key-value pair. If the key already exists,
		// isKey will be set.
		int loc = _findIndex( key );

		// The key exists, so save the old value before
		// replacing it with the new value.
		if( _isKey ) {
			oldValue = _table[loc].getValue();
		}

		// Insert the key-value pair into hash table.		
		_table[loc] = new Entry( key, value );
		
		// Update mappings for unique keys only.
		if( !_isKey ) {
			_mappings++;
		}

		// Check if we need to resize the hash table.		
		if( (float)_mappings / _table.length > _loadFactor ) {

			int oldSize = _table.length;
			Entry[] old = _table;
			_mappings = 0;
			
			// Create the new hash table, roughly twice the size
			// of the old hash table. Of course, make sure new size
			// is a prime.
			int newSize = _getPrime( 2 * oldSize );
			_table = new Entry[newSize];

			// Transfer the old hash table to the new one.			
			for( int i = 0; i < oldSize; i++ ) {
				
				// Null cells can be ignored, as can cells marked
				// as AVAILABLE (since we're rehashing everything).
				if( old[i] == null || old[i] == AVAILABLE ) {
					continue;
				}
				
				// Rehash then reinsert!
				loc = _findIndex( old[i].getKey() );
				_table[loc] = new Entry( old[i].getKey(), old[i].getValue() );
				_mappings++;
				
			}
			
		}
		
		return oldValue;
		
	}
	
	/**
	 * Copies specified map to current map.
	 *
	 * @param t the map to be copied.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public void putAll( Map t ) {

		// Null maps not allowed.
		if( t == null ) {
			throw new NullPointerException( "Map cannot be null." );
		}
		
		// Iterate through the specified map, and simply
		// put the key-value pair into the new map.
		Iterator it = t.entrySet().iterator();
		while( it.hasNext() ) {
			Entry entry = (Entry)it.next();
			put( entry.getKey(), entry.getValue() );
		}
		
	}
	
	/**
	 * Removes the key-value pair associated with the specified key in this map.
	 *
	 * @param key the key to be removed.
	 * @return the value associated with the key, or <code>null</code>
	 * if not found.
	 * @throws NullPointerException if the key is <code>null</code>.
	 */
	public Object remove( Object key ) {
		
		// Null keys not allowed.
		if( key == null ) {
			throw new NullPointerException( "Key cannot be null." );
		}
		
		// Locate the index into the hash table where
		// this key is mapped to.
		int index = _findKey( key );

		// Key not found!
		if( index == -1 ) {
			return null;	
		}
		
		// Remove the key-value pair, but save old value first.
		Object oldValue = _table[index].getValue();
		_table[index] = AVAILABLE;
		_mappings--;
		
		return oldValue;

	}

	/**
	 * Returns the number of mappings in the hash table.
	 * 
	 * @return the number of mappings in the hash table.
	 */
	public int size() {
		
		return _mappings;
		
	}

	/**
	 * Returns the collection view of the values contained in the hash table.
	 *
	 * @return the collection view of the values contained in the hash table.
	 */
	public Collection values() {
		
		return new _valuesView();
		
	}
	
	/**
	 * Inner class entrySet support.
	 */
	private class _entrySetView extends AbstractSet {
		
		/**
		 * Returns the number of items in the set.
		 *
		 * @return the number of items in the set.
		 */
		public int size() {
			
			return MondoHashTable.this.size();
			
		}

		/**
		 * Returns the iterator over the elements contained in this set.
		 *
		 * @return the iterator over the elements contained in this set.
		 */
		public Iterator iterator() {
			
			return new _entrySetIterator();
			
		}
		
		/**
		 * Generic iterator implementation.
		 */
		private class _entrySetIterator extends AbstractIterator {

			
			/**
			 * No-arg constructor.
			 */
			_entrySetIterator() {
				
				super(_table);
				
			}
			
			/**
			 * Returns the current element.
			 *
			 * @return the current element.
			 */
			public Object next() {
				
				Object elt = super.next();
				return elt;
				
			}
			
		}
		
	}

	/**
	 * Locates the position within the hash table where a new key-value
	 * pair can be inserted. If a collision occurs, the next available 
	 * slot is located by means of quadratic probing. This available slot 
	 * is either a new, never used slot or one that is specially marked 
	 * with AVAILABLE.
	 *
	 * @param key the key to be mapped.
	 * @return the hash table location.
	 * @throws HashTableFullException if the hash table is full.
	 */
	private int _findIndex( Object key ) {
		
		// Initialization section.
		_isKey = false;
		int count = 0;						// Collision count.
		int avail = 0;						// Location of first AVAILABLE slot.
		
		// Calculate index into the hash table. 
		// Range 0 - table length.
		int index = _index( key );

		// There is no mapping at this location,
		// it's a valid location.
		if( _table[index] == null ) {
			return index;
		}

		// Key is already in the hash table, so
		// this is a valid location.
		if( key.equals( _table[index].getKey() ) ) {
			_isKey = true;
			return index;
		}
		
		// We have a collision (either some other key or an AVAILABLE),
		// so locate the next available slot we can use.
		while( _table[index] != null && !key.equals( _table[index].getKey() ) && _table[index] != AVAILABLE ) {
			
			count++;
			index = _probe( index, count );
			if( count == _table.length ) {
				throw new HashTableFullException( "Hash table is full." );
			}
			
		}
		
		// An AVAILABLE slot has been found. Store its location
		// and keep searching until null or the key is found.
		if( _table[index] == AVAILABLE ) {
			
			avail = index;
			count = 0;
			
			// Skip all AVAILABLE and other collisions until key is found
			// or an empty slot appears.
			while( _table[index] != null && !key.equals( _table[index].getKey() ) ) {
				count++;
				index = _probe( index, count );
				if( count == _table.length ) {
					throw new HashTableFullException( "Hash table is full." );
				}
			}
			
			return ( _table[index] == null ? avail : index );
			
		}
		
		if( _table[index] != null && key.equals( _table[index].getKey() ) ) {
			_isKey = true;
		}
		
		return index;

	}

	/**
	 * Locates the key within the hash table. If a collision occurs, the 
	 * next available slot is located by means of quadratic probing. 
	 *
	 * @param key the key to be found.
	 * @return the hash table location, or <code>-1</code> if not found.
	 */
	private int _findKey( Object key ) {
		
		// Initialization section.
		_isKey = false;
		int count = 0;						// Collision count.
		
		// Calculate index into the hash table. 
		// Range 0 - table length.
		int index = _index( key );

		// There is no key at this location.
		if( _table[index] == null ) {
			return -1;
		}

		// We have a collision, try and locate key using quadratic probing.
		while( _table[index] != null && !key.equals( _table[index].getKey() ) ) {
			count++;
			index = _probe( index, count );
		}
		
		// Verify this is the key we're looking for.
		if( _table[index] != null && key.equals( _table[index].getKey() ) ) {
			return index;
		}
		
		return -1;

	}

	/**
	 * Returns the next highest prime (if necessary).
	 *
	 * @param val the value to test for primeness. 
	 * @return the next highest prime.
	 */
	private static int _getPrime( int val ) {

		if( val % 2 == 0 )
			val++;

		while( !_isPrime( val ) ) {
			val += 2;
		}

		return val;
		
	}

	/**
	 * Calculate index into the hash table. Range 0 to table length.
	 * The key's hash code will come straight from Java.
	 * 
	 * @param key the key to calculate the index on.
	 * @return index into the hash table.
	 */
	private int _index( Object key ) {
		
		return Math.abs( key.hashCode() % _table.length );
		
	}
	
	/**
	 * Tests whether a value is prime or not.
	 *
	 * @param val the value to test for primeness. 
	 * @return <code>true</code> if the value is prime,
	 * <code>false</code> if not.
	 */
	private static boolean _isPrime( int val ) {

		if( val % 2 == 0 )
			return false;

		// Test i through root of val, making this
		// somewhat more efficient.
		for( int i = 3; i * i <= val; i += 2 ) {
			if( val % i == 0 ) {
				return false;
			}
		}

		return true;
		
	}

	/**
	 * Inner class keySet support.
	 */
	private class _keySetView extends AbstractSet {
		
		/**
		 * Returns the number of items in the set.
		 *
		 * @return the number of items in the set.
		 */
		public int size() {
			
			return MondoHashTable.this.size();
			
		}

		/**
		 * Returns the iterator over the elements contained in this set.
		 *
		 * @return the iterator over the elements contained in this set.
		 */
		public Iterator iterator() {
			
			return new _keySetIterator();
			
		}
		
		/**
		 * Generic iterator implementation.
		 */
		private class _keySetIterator extends AbstractIterator {

			/**
			 * No-arg constructor.
			 */
			_keySetIterator() {
				
				super(_table);
				
			}
			
			/**
			 * Returns the current element.
			 *
			 * @return the current element.
			 */
			public Object next() {
				
				Entry elt = (Entry)super.next();
				return elt.getKey();
				
			}
			
		}
		
	}
	
	/**
	 * Returns the new index into the hash table based on the 
	 * current probing scheme (quadratic probing).
	 * 
	 * @param index the current index into the hash table.
	 * @param count the current collision count.
	 * @return the new index into the hash table.
	 */
	private int _probe( int index, int count ) {
		
		return ( index + ( count * count ) ) % _table.length;
		
	}
	
	/**
	 * Inner class values support.
	 */
	private class _valuesView extends AbstractSet {
		
		/**
		 * Returns the number of items in the set.
		 *
		 * @return the number of items in the set.
		 */
		public int size() {
			
			return MondoHashTable.this.size();
			
		}

		/**
		 * Returns the iterator over the elements contained in this set.
		 *
		 * @return the iterator over the elements contained in this set.
		 */
		public Iterator iterator() {
			
			return new _valuesIterator();
			
		}
		
		/**
		 * Generic iterator implementation.
		 */
		private class _valuesIterator extends AbstractIterator {

			/**
			 * No-arg constructor.
			 */
			_valuesIterator() {
				
				super(_table);
				
			}
			
			/**
			 * Returns the current element.
			 *
			 * @return the current element.
			 */
			public Object next() {
				
				Entry elt = (Entry)super.next();
				return elt.getValue();
				
			}
			
		}
		
	}
	
}