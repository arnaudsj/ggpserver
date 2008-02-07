package tud.gamecontroller.scrambling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

public class GameScrambler implements GameScramblerInterface {

	private List<String> wordlist;
	private Map<String,String> scrambling;
	private Map<String,String> descrambling;
	private Pattern identifierPattern;
	private Random random;
	private boolean firstTimeEmptyWordlist=true;

	public GameScrambler(File wordlistfile){
		this(GameScrambler.wordlistFromFile(wordlistfile));
	}

	public GameScrambler(Set<String> wordset){
		this.wordlist=new LinkedList<String>(wordset);
		this.scrambling=new HashMap<String,String>();
		this.descrambling=new HashMap<String,String>();
		this.identifierPattern=Pattern.compile("([()?\\s]|^)([a-zA-Z][a-z0-9A-Z_\\-]*)", Pattern.CASE_INSENSITIVE);
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

	/* (non-Javadoc)
	 * @see tud.gamecontroller.GameScramblerInterface#scramble(java.lang.String)
	 */
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
			scrambledIdentifier=scrambling.get(lowercaseIdentifier);
			if(scrambledIdentifier==null){
				scrambledIdentifier=getNewWord();
				scrambling.put(lowercaseIdentifier, scrambledIdentifier);
				descrambling.put(scrambledIdentifier, identifier);
				Logger.getLogger("tud.gamecontroller").info("scrambling: "+lowercaseIdentifier+" -> "+scrambledIdentifier);
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
				Logger.getLogger("tud.gamecontroller").warning("in GameScrambler.descramble: found no match for "+identifier);
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
				Logger.getLogger("tud.gamecontroller").warning("in GameScrambler: wordlist is empty - using generic identifiers");
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

	public static Set<String> wordlistFromFile(File wordlistfile) {
		Set<String> wordset=new HashSet<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(wordlistfile));
			Pattern p=Pattern.compile("[a-z][a-z]*");
			Matcher m;
			String line=reader.readLine().toLowerCase();
			while(line!=null){
				m=p.matcher(line);
				if(m.find()){
					wordset.add(m.group());
				}
				line=reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("error reading file: "+wordlistfile+"\nGameScrambler will use generic identifiers to scramble the game!\n"+e.getMessage());
		} catch (IOException e) {
			System.err.println("error reading file: "+wordlistfile+"\nGameScrambler may use generic identifiers to scramble the game!\n"+e.getMessage());
		}
		return wordset;
	}

}
