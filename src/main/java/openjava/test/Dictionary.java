/*
 * Crosslexic, a tool for solving crossword puzzles.
 * Copyright (c) 2010-2011, Arthur Gouros
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of Arthur Gouros nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 **************************************************************************
 * Crosslexic - a tool for solving crossword puzzles.                     *
 *              Original Author: Arthur Gouros.                           *
 *                                                                        *
 *              Please consult the documentation for further information. *
 **************************************************************************
 **/

package openjava.test;

import java.io.*;

public class Dictionary {
    
	protected static RandomAccessFile d;
	protected static final String filename = "lib/resource/dictionarywords";
	protected static final int entries = 431000;
	protected static final String endoffile = "End-Of-File";
	protected static InputStream in;
	protected static BufferedReader bf;
	
	protected static boolean open() {
		boolean success = false;
		
		try {
	      in = Crosslexic.dictionaryResource.openStream();
	      bf = new BufferedReader(new InputStreamReader(in));
	      success = true;
		} catch (Exception e) {
			Crosslexic.addtolist(Crosslexic.iconAlert, "ERROR: InputStreamException:\n" + e.getMessage());
	    }
		  	      
	    return success;
	}
	
	protected static String getNextWord() {
		String nextWord;
		
		try {
			nextWord = bf.readLine();
			if (nextWord == null)
	        	nextWord = endoffile;
		} catch (IOException e) {
			Crosslexic.addtolist(Crosslexic.iconAlert, "ERROR: IOException:\n" + e.getMessage());
	    	nextWord = endoffile;
	    } 
		
		return nextWord;
	}
	
	protected static void close() {
		
		try {
			if (bf != null) bf.close();
		    if (in != null) in.close();
		} catch (IOException e) {
			Crosslexic.addtolist(Crosslexic.iconAlert, "ERROR: IOException:\n" + e.getMessage());
		}
		
		return;
	}
}
