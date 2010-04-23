/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.scrambling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tud.gamecontroller.GameController;

public class GameScrambler implements GameScramblerInterface {

	private List<String> wordlist;
	private Map<String,String> scrambling;
	private Map<String,String> descrambling;
	private Pattern identifierPattern;
	private Random random;
	private boolean firstTimeEmptyWordlist=true;
	private static final Logger logger = Logger.getLogger(GameController.class.getName());
	// it is important to use the same logger here that is used in the AbstractGameControllerRunner 

	public GameScrambler(File wordlistfile){
		this(GameScrambler.wordlistFromFile(wordlistfile));
	}

	public GameScrambler(InputStream stream){
		this(GameScrambler.wordlistFromStream(stream));
	}

	public GameScrambler(Reader reader){
		this(GameScrambler.wordlistFromReader(reader));
	}

	public GameScrambler(Set<String> wordset){
		this.wordlist=new LinkedList<String>(wordset);
		this.scrambling=new HashMap<String,String>();
		this.descrambling=new HashMap<String,String>();
		this.identifierPattern=Pattern.compile("([()?\\s]|^)([a-zA-Z][a-z0-9A-Z_\\-\\+]*)", Pattern.CASE_INSENSITIVE);
		this.random=new Random();
		addKeywords();
	}

	private void addKeywords() {
		String[] keywords=new String[]{
				"role",	"true",	"init",	"next",
				"legal", "goal", "terminal", "does",
				"or", "and", "not", "<=", "distinct"};
		for(String keyword:keywords){
			scrambling.put(keyword, keyword);
			descrambling.put(keyword, keyword);
			wordlist.remove(keyword);
		}
	}

	public String scramble(String s){
		StringBuilder scrambled=new StringBuilder();
		Matcher m=identifierPattern.matcher(s);
		String identifier, lowercaseIdentifier, scrambledIdentifier;
		int lastpos=0, nextmatchpos;
		while(m.find()){
			nextmatchpos=m.start(2);
			scrambled.append(s.substring(lastpos, nextmatchpos));
			lastpos=m.end(2);
			identifier=m.group(2);
			lowercaseIdentifier=identifier.toLowerCase();
			synchronized(this) {
				// this must be synchronized otherwise
				// if two threads call scramble at the same time
				// multiple occurrences of an identifier in s could be mapped to
				// different scrambledIdentifiers   
				scrambledIdentifier=scrambling.get(lowercaseIdentifier);
				if(scrambledIdentifier==null){
					scrambledIdentifier=getNewWord();
					scrambling.put(lowercaseIdentifier, scrambledIdentifier);
					descrambling.put(scrambledIdentifier, identifier);
					logger.info("scrambling: "+lowercaseIdentifier+" -> "+scrambledIdentifier);
				}
			}
			scrambled.append(scrambledIdentifier);
		}
		scrambled.append(s.substring(lastpos, s.length()));
		return scrambled.toString();
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.GameScramblerInterface#descramble(java.lang.String)
	 */
	public String descramble(String s){
		StringBuilder descrambled=new StringBuilder();
		Matcher m=identifierPattern.matcher(s);
		String identifier, descrambledIdentifier;
		int lastpos=0, nextmatchpos;
		while(m.find()){
			nextmatchpos=m.start(2);
			descrambled.append(s.substring(lastpos, nextmatchpos));
			lastpos=m.end(2);
			identifier=m.group(2).toLowerCase();
			descrambledIdentifier=descrambling.get(identifier);
			if(descrambledIdentifier==null){
				logger.warning("in GameScrambler.descramble: found no match for "+identifier);
				descrambledIdentifier=identifier;
			}
			descrambled.append(descrambledIdentifier);
		}
		descrambled.append(s.substring(lastpos, s.length()));
		return descrambled.toString();
	}
	
	private String getNewWord() {
		String word;
		if(wordlist.size()>0){
			int i=random.nextInt(wordlist.size());
			word=wordlist.remove(i);
		}else{
			word=generateWord();
			if(firstTimeEmptyWordlist){
				logger.warning("in GameScrambler: wordlist is empty - using generic identifiers");
				firstTimeEmptyWordlist=false;
			}
		}
		return word;
	}

	private String generateWord() {
		int i=1;
		String word;
		do{
			word="ident"+i;
			++i;
		}while(descrambling.containsKey(word));
		return word;
	}

	private static Set<String> wordlistFromFile(File wordlistfile) {
		try {
			return wordlistFromReader(new FileReader(wordlistfile));
		} catch (FileNotFoundException e) {
			logger.severe(
					"error reading file: " + wordlistfile + "\n"
					+ "GameScrambler will use generic identifiers to scramble the game!\n" 
					+ e.getMessage());
			return new HashSet<String>();
		}
	}

	private static Set<String> wordlistFromStream(InputStream stream) {
		return wordlistFromReader(new InputStreamReader(stream));
	}
	
	private static Set<String> wordlistFromReader(Reader reader) {
		Set<String> wordset = new HashSet<String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(reader);
			Pattern p = Pattern.compile("[A-Za-z][A-Za-z]*");
			Matcher m;
			String line = bufferedReader.readLine();
			while (line != null) {
				m = p.matcher(line);
				if (m.find()) {
					wordset.add(m.group().toLowerCase());
				}
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			logger.severe(
					"error reading from input reader!\n"
					+ "GameScrambler will use generic identifiers to scramble the game!\n" 
					+ e.getMessage());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return wordset;
	}

}
