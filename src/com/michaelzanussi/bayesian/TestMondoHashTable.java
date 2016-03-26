package com.michaelzanussi.bayesian;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Unit test module for MondoHashTable.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (6 Feb 2004) 
 */
public class TestMondoHashTable {
	
	@Test
	public void test() {
		// init
		Object result = null;
		// test instantiation
		MondoHashTable m = new MondoHashTable(13); assertEquals(13, m.capacity());
		System.out.println("Instantiate with 13, already prime. Capacity is " + m.capacity());
		m = new MondoHashTable(12); assertEquals(13, m.capacity());
		System.out.println("Instantiate with 12, is not prime. Capacity now " + m.capacity());
		// test put
		System.out.println("Capacity before: " + m.capacity());
		System.out.println("Size before:     " + m.size() + " elts"); assertEquals(0, m.size());
		System.out.println("Is Empty?        " + m.isEmpty()); assertEquals(true, m.isEmpty());
		System.out.println("...put data...");
		result = m.put("apple", new Long(12185123L)); assertEquals(null, result);
		result = m.put("berry", new Long(66637527L)); assertEquals(null, result);
		result = m.put("apricot", new Long(88270572L)); assertEquals(null, result);
		result = m.put("apple", "pie"); assertEquals(12185123L, result);
		result = m.put("x", new Integer(1)); assertEquals(null, result);
		result = m.put("t", new Integer(2)); assertEquals(null, result);
		result = m.put("a", new Integer(3)); assertEquals(null, result);
		result = m.put("z", new Integer(4)); assertEquals(null, result);
		result = m.put("y", new Integer(1)); assertEquals(null, result);
		System.out.println("Capacity after : " + m.capacity());
		System.out.println("Size after:      " + m.size() + " elts"); assertEquals(8, m.size());
		System.out.println("Is Empty?        " + m.isEmpty()); assertEquals(false, m.isEmpty());
		// test a value replacement
		result = m.put("apple", "jacks"); assertEquals("pie", result);
		result = m.get("apple"); assertEquals("jacks", result);
		// test contains a key
		result = m.containsKey("apple");
		System.out.println("Contains key 'apple'? " + result); assertEquals(true, result);
		result = m.containsKey("toast");
		System.out.println("Contains key 'toast'? " + result); assertEquals(false, result);
		// test contains a value
		result = m.containsValue("jacks");
		System.out.println("Contains value 'jacks'? " + result); assertEquals(true, result);
		result = m.containsValue("pie");
		System.out.println("Contains value 'pie'? " + result); assertEquals(false, result);
		result = m.containsValue(new Integer(1));
		System.out.println("Contains value 1? " + result); assertEquals(true, result);
		result = m.containsValue(new Integer(5));
		System.out.println("Contains value 5? " + result); assertEquals(false, result);
		// test putAll
		System.out.println("...create new table...");
		Map newMap = new MondoHashTable();
		System.out.println("Size before:     " + newMap.size() + " elts"); assertEquals(0, newMap.size());
		System.out.println("Is Empty?        " + newMap.isEmpty()); assertEquals(true, newMap.isEmpty());
		System.out.println("...putAll data...");
		newMap.putAll(m);
		System.out.println("Size after:      " + newMap.size() + " elts"); assertEquals(8, newMap.size());
		System.out.println("Is Empty?        " + newMap.isEmpty()); assertEquals(false, newMap.isEmpty());
		// retest contains a key
		result = newMap.containsKey("apple");
		System.out.println("Contains key 'apple'? " + result); assertEquals(true, result);
		result = newMap.containsKey("toast");
		System.out.println("Contains key 'toast'? " + result); assertEquals(false, result);
		// retest contains a value
		result = newMap.containsValue("jacks");
		System.out.println("Contains value 'jacks'? " + result); assertEquals(true, result);
		result = newMap.containsValue("pie");
		System.out.println("Contains value 'pie'? " + result); assertEquals(false, result);
		result = newMap.containsValue(new Integer(1));
		System.out.println("Contains value 1? " + result); assertEquals(true, result);
		result = newMap.containsValue(new Integer(5));
		System.out.println("Contains value 5? " + result); assertEquals(false, result);
		// test equals
		System.out.println("new == old? " + newMap.equals(m)); assertEquals(true, newMap.equals(m));
		System.out.println("old == new? " + m.equals(newMap)); assertEquals(true, m.equals(newMap));
		// test remove
		result = newMap.remove("z");
		System.out.println("remove 'z': " + result); assertEquals(4, result); 
		System.out.println("new == old? " + newMap.equals(m)); assertEquals(false, newMap.equals(m));
		System.out.println("old == new? " + m.equals(newMap)); assertEquals(false, m.equals(newMap));
		// test hashcode
		result = m.hashCode();
		System.out.println("m.hashcode= " + result); assertEquals(-544910588, result);
		result = newMap.hashCode();
		System.out.println("newMap.hashcode= " + result); assertEquals(-544910714, result);
		// keyset test
		System.out.println("...keyset...");
		Set s = m.keySet();
		Iterator i = s.iterator();
		while( i.hasNext() ) {
			String str = (String)i.next();
			System.out.println( "key: " + str );
		}
		// values test
		System.out.println("...values...");
		Collection c = m.values();
		Iterator k = c.iterator();
		while( k.hasNext() ) {
			Object ii = k.next();
			System.out.println( "value: " + ii );
		}
		// entryset test
		System.out.println("...entryset...");
		Set e = m.entrySet();
		Iterator l = e.iterator();
		while( l.hasNext() ) {
			MondoHashTable.Entry ee = (MondoHashTable.Entry)l.next();
			System.out.println( "key-value pair: " + ee.getKey() + ", " + ee.getValue() );
		}
		// test clearing table
		System.out.println("...clear data...");
		newMap.clear();
		System.out.println("Size after:      " + newMap.size() + " elts"); assertEquals(0, newMap.size());
		System.out.println("Is Empty?        " + newMap.isEmpty()); assertEquals(true, newMap.isEmpty());
	}
	
}
