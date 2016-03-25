package com.michaelzanussi.bayesian;

import java.io.File;
import java.util.*;

/**
 * Driver for analyzing MondoHashTable. Beware, no error checking!
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (6 Feb 2004) 
 */
public class TestHarness {

	public static void main(String[] args) {

		// Performance analysis

		// check args
		if (args.length != 3) {
			System.out.println("Usage: java TestHarness document.txt tableCapacity loadFactor");
			System.out.println("   ex: java TestHarness test/t8.shakespeare.txt 101 0.5");
			System.exit(1);
		} 

		// Create hash table		
		int capacity = Integer.parseInt(args[1]);
		float load = (float)Float.parseFloat(args[2]);
		MondoHashTable m = new MondoHashTable(capacity, load);
		
		System.out.println("STARTING");
		System.out.println("Table Capacity : " + m.capacity());
		System.out.println("Load factor    : " + m.loadFactor() + "\n");
		
		// Open text file.
		File file = new File(args[0]);
		TextFileReader fr = new TextFileReader();
		fr.open(file);

		// Process the file.
		String input = null;
		String target = null;
		while ((input = fr.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(input, " ");
			while (st.hasMoreTokens()) {
				target = st.nextToken().toLowerCase().trim();
				m.put(target, target);
			}
		}
		fr.close();

		System.out.println("Mappings       : " + m.size() );
		System.out.println("# hashes       : " + m.lHash );
		System.out.println("# puts         : " + m.lPuts );
		System.out.println("RATIO          : " + ((double)m.lHash/m.lPuts) );
		System.out.println("# collisions   : " + m.nColl );
		System.out.println("RATIO w/puts   : " + ((double)m.nColl/m.lPuts));
		System.out.println("Overall RATIO  : " + ((double)(m.nColl+m.lHash)/m.lPuts));
		
		System.out.println("\nENDING");
		System.out.println("Table Capacity : " + m.capacity() );
		System.out.println("Load factor    : " + m.loadFactor());
		
		System.out.println("\nDone!");
		m = null;

	}
}
