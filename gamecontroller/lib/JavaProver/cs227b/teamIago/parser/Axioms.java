/*
 * Axioms.java
 * Created on Apr 7, 2005
 *
 */

package cs227b.teamIago.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import cs227b.teamIago.resolver.ExpList;

public class Axioms {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(Axioms.class.getName());

	ArrayList Statements = new ArrayList();


	boolean parseFromString(String gdl){
		boolean parseSuccess = true;
		// gdl = gdl.trim();
		Tokenizer tk = new Tokenizer(gdl);
		while (tk.x < tk.numTokens()){
			Statement s = new Statement();
			if (s.parse(tk))
				Statements.add(s);
			else {
				logger.severe("String - Error parsing token: " + tk.next() + " at line " + tk.sourceLine() + " - exception: " + null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				parseSuccess = false;
			}
		}
		return parseSuccess;
	}

	void display() {
		for (int i = 0; i < Statements.size(); i++)
		{
			Statement s = (Statement) Statements.get(i);
			Statement.expand(s);
		}
	}

public static String loadStringFromFile(String filename){
	StringBuffer sb = new StringBuffer();
	try{
	BufferedReader br = new BufferedReader(new FileReader(filename));
	String line;

	while((line=br.readLine())!=null){
		line = line.trim();
		sb.append(line + "\n"); // artificial EOLN marker
	}
	} catch (IOException e){
		logger.severe("String - " + e); //$NON-NLS-1$
		System.exit(-1);
	}
	return sb.toString();
}


public static void main(String[] args){
//	String filename;
//	if (args.length > 0)
//		filename = args[0];
//	else filename = "../files/tictactoe.gdl";
//
//	System.err.print("Loading axioms...");
//	String str = loadStringFromFile(filename);
//	Axioms a = new Axioms();
//	boolean parsed = a.parseFromString(str);
//	if (!parsed) {
//		System.err.println("Errors encountered during parsing--aborting.");
//		System.exit(-1);
//	}
//	System.err.println("done.");
//	System.err.println("<List of axioms>");
//	a.display();
//	System.err.println("<End of axioms>");
Axioms a = new Axioms();
String str = "((mark 3 2) noop )";
ExpList e= Parser.parseExpList(str);

}
}
