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

public class Search implements Runnable {
	
	/**
	 * Search for matching words in dictionary.
	 */
	private static final long serialVersionUID = 1L;

	public void run() {
		Crosslexic.app_setEnabled(false);
		wordSearch();
		Crosslexic.app_setEnabled(true);
	}
	
	protected void wordSearch() {
		int match_cntr = 0, entry_cnt = 0;
		boolean wordmatch;
		char[] displayArray;
		String nextWord;
				
		displayArray = new char[Crosslexic.getWordlength()];		
		for (int i = 0 ; i < Crosslexic.getWordlength() ; i++)
			if (Crosslexic.getLetterText()[i].getText().trim().length() > 0)
				displayArray[i] = Crosslexic.getLetterText()[i].getText().trim().toCharArray()[0];
			else
				displayArray[i] = '.';
								
		if (valid_text()&& Dictionary.open())
		{	
			Crosslexic.addtolist(null, "Searching on pattern: " + new String(displayArray));
								
			while ((nextWord = Dictionary.getNextWord())!= Dictionary.endoffile && Crosslexic.getSearchThread() != null) {
				//System.out.println("(nextWord = Dictionary.getNextWord())!= Dictionary.endoffile && Crosslexic.searchThread != null");
				wordmatch = true;
				if (++entry_cnt % 500 == 0)
					Crosslexic.getProgressBar().setValue(entry_cnt * 100 / Dictionary.entries);
				if (nextWord.length() == Crosslexic.getWordlength()) {
					for (int i = 0 ; i < Crosslexic.getWordlength(); i++)
						if (Crosslexic.getLetterText()[i].getText().trim().length() > 0) 
							if (Crosslexic.getLetterText()[i].getText().trim().toCharArray()[0] != nextWord.toLowerCase().toCharArray()[i]) 
								wordmatch = false;
				}
				else
					wordmatch = false;
				
				// Display result if any
				if (wordmatch) {
					// Display matching word
					Crosslexic.addtolist(Crosslexic.iconMatch, "   " + nextWord);
					match_cntr++;
				}
			}
			// Display summary
			if (Crosslexic.getSearchThread() != null) {
				Crosslexic.addtolist(null, "Results: " + match_cntr + " matches.");
				Crosslexic.getProgressBar().setValue(100);
			}
			else 
				Crosslexic.addtolist(null, "Results so far: " + match_cntr + " matches.");
			
			Dictionary.close();
		}
				
		return;
	}
	
	
	private boolean valid_text() {
		boolean textOk = false;
		//int i;
		//for (i = 0 ; i < Crosslexic.getWordlength() ; i++)		
		for (int i = 0 ; i < Crosslexic.getWordlength() ; i++)
			if (Crosslexic.getLetterText()[i].getText().trim().length() > 0) {
				textOk = true;
				break;
			}
		
		if (! textOk) 
			Crosslexic.addtolist_now(Crosslexic.iconAlert, "ERROR: Please enter some letters.");
				
		return textOk;
	}
}
