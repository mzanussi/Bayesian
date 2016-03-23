package com.michaelzanussi.bayesian;

import java.io.File;

/**
 * Generic I/O interface for such classes as TextFileReader,
 * TextFileWriter, etc.
 * 
 * @author <a href="mailto:iosdevx@gmail.com">Michael Zanussi</a>
 * @version 1.0 (20 Feb 2004) 
 */
public interface BasicIO {

	/**
	 * Open specified file.
	 * 
	 * @param file file to open.
	 * @return <code>true</code> if successful.
	 */
	public boolean open(File file);

	/**
	 * Close file.
	 * 
	 * @return <code>true</code> if successful.
	 */
	public boolean close();
	
}
