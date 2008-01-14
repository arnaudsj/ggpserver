/*
 * Statement.java
 * Created on Apr 7, 2005
 *
 */


package cs227b.teamIago.parser;
import java.util.ArrayList;
//import java.util.StringTokenizer;

/**
 * @author mike
 * STATEMENT ::== LPAREN OPERATOR (LISTOF STATEMENTS or LITERALS) RPAREN
 */
public class Statement {
String operator;
ArrayList members = new ArrayList();


/**
 * Recursively/Polymorphically print out the statement
 */
 static void expand(Statement s) {
	System.err.println(s.operator);
	for (int i = 0; i < s.members.size(); i++)
		System.err.println("\t" + s.members.get(i));
	System.err.println("");
 }

 public String toString() {
	 StringBuffer sb = new StringBuffer();
	 sb.append("(").append(operator);
	 for (int i = 0; i < members.size(); i++)
	 {
		 Object oi = members.get(i);
		 String si = oi.toString();
		 sb.append(" ").append(si);
 	 }
 	 sb.append(")");
 	 return sb.toString();
 }



/**
 * Fill the statement instance by recursively parsing token stream
 * @param tk
 * @return
 */

boolean parse(Tokenizer tk){
	int start = tk.x;
	String tok = tk.next();
	boolean parseSuccess;
	if (!(tok.equals("("))){
		tk.x = start;
		return false;
	}
	operator = tk.next(); // operator
	if (operator.equals("(")){
		// Syntax error in source
		System.err.println("Error: List as function name at line " + tk.sourceLine());
		System.exit(-1);
	}
	if (operator.equals(")")){
		// Syntax error in source
		System.err.println("Error: No operator in list at line " + tk.sourceLine());
		System.exit(-1);
	}
	Statement s = new Statement();

	while((parseSuccess=s.parse(tk)) || isLiteral(tk)){
		if(parseSuccess)
			members.add(s);
		else
			members.add(tk.next());
		s = new Statement();
	}

	if (!(tk.next().equals(")"))){
		tk.x = start;
		return false;
	}
	return true;
}

	boolean isLiteral(Tokenizer tk) {
		int start=tk.x;
		String tok = tk.next();
		if (!tok.equals("(") && !tok.equals(")")){
			tk.x=start;
			return true;
		}
		else{
			tk.x=start;
			return false;
		}
	}
}