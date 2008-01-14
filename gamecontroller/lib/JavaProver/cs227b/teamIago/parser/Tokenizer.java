package cs227b.teamIago.parser;
import java.util.ArrayList;

/*
 * Tokenizer.java
 * Created on Apr 10, 2005
 *
 */

/**
 * @author mike
 *
 */
public class Tokenizer {
	ArrayList tokens;
	ArrayList lineNums;
	int x;
	int numTokens(){
		return tokens.size();
	}
	Tokenizer(String gdl){
//		tokenize into a String[]
		int lineNum = 1;
		ArrayList _lineNums = new ArrayList();
		ArrayList _tokens = new ArrayList();
		int len = gdl.length();
		int start=0;
		for (int i=0;i<len;i++){
			char ch = gdl.charAt(i);
			if (ch=='(' || ch==')'){
				if (start !=i) {
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				_tokens.add(""+ch);  //ch to String
				_lineNums.add(new Integer(lineNum));
				start=i+1;
			} else if (Character.isWhitespace(ch)) {
				if (start !=i)	{
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				if (ch == '\n') lineNum++;
			  	start = i + 1;
			} else if (ch == ';') {
				// Skip comment lines
				if (start != i) {
					_tokens.add(gdl.substring(start,i));
					_lineNums.add(new Integer(lineNum));
				}
				// Jump to next line
				start = gdl.indexOf('\n',i);
				if (start == -1) i = len; // if last line, skip remainder of string
				lineNum++;
				i = start;
				start++;
			}
		}
		tokens = _tokens;
		lineNums = _lineNums;
		x = 0;
	}

	String next(){
		if (x >= tokens.size()) {
			System.err.println("Error: No more tokens in file");
			System.exit(-1);
		}
		x++;
		return (String)tokens.get(x-1);
	}

	Integer sourceLine() {
		if (x > tokens.size()) {
			System.err.println("Error: Source line not defined for token past EOF");
			System.exit(-1);
		}
		return (Integer)lineNums.get(x - 1);
	}
}
