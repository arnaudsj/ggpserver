package cs227b.teamIago.parser;
import java.util.ArrayList;
import java.util.List;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Implication;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.resolver.Variable;
import cs227b.teamIago.resolver.OrOp;
import cs227b.teamIago.resolver.DistinctOp;
import cs227b.teamIago.resolver.AndOp;
import cs227b.teamIago.resolver.NotOp;


/*
 * Parser.java
 * Created on Apr 20, 2005
 *
 */

/**
 * @author mike
 *
 */
public class Parser {
public static void main(String args[]){
	//String filename = "data\\tictactoe.gdl";
	//ExpList e = parse(filename);
}

public static ExpList parseFile(String filename){
	String strContent = Axioms.loadStringFromFile(filename);
	Axioms a = new Axioms();
	a.parseFromString(strContent);
	ArrayList stmts = a.Statements;
	
	ExpList explist = parseExpList(stmts);
	return explist;
}

public static ExpList parseDesc(String desc){
	Axioms a = new Axioms();
	a.parseFromString(desc);
	ArrayList stmts = a.Statements;
	
	ExpList explist = parseExpList(stmts);
	return explist;
}

static ExpList parseExpList(List stmts){
	ExpList res=new ExpList();
	for (int i=0;i<stmts.size();i++)
		res.add(parseExpression(stmts.get(i)));	
	return res;
}

// Takes inputs like ((mark 3 2) noop )
static public ExpList parseExpList(String gdl){
        gdl = gdl.trim();
        gdl = gdl.substring(1, gdl.length()-1).trim();
	Axioms a = new Axioms();
	a.parseFromString(gdl);
	ArrayList stmts = a.Statements;
	
	ExpList explist = parseExpList(stmts);
	return explist;
    
    
    /*
	gdl = gdl.trim();
	gdl = gdl.substring(1, gdl.length()-1).trim();
	StringBuffer sb = new StringBuffer();
	int depth=0;
	ArrayList statements = new ArrayList();
	for (int i=0;i<gdl.length();i++){
		char c = gdl.charAt(i);
		sb.append(c);
		if (c=='(')
			depth++;
		else if (c==')')
			depth--;
		if ((c=='(' && depth==1 && i!=0)|| (depth==0 && i==gdl.length()-1 )){
			String st = sb.toString().trim();
			statements.add(st);
			sb = new StringBuffer();
		}
		if (c==')' && depth==0){
			String stmtStr = sb.toString().trim();
			if (stmtStr.length()==0)
				continue;
			Statement s = new Statement();
			s.parse(new Tokenizer(stmtStr));
			statements.add(s);
			sb = new StringBuffer();
		}
	}
	return parseExpList(statements);
    */
}
/**
 * Takes in either Statements 
 * Can also take in Strings, which will parse to Atoms.
 * 
 * This is because of how we parsed to Statements.
 * @param _s
 * @return
 */
public static Expression parseExpression(Object _s){
	if (_s instanceof ArrayList)
		_s = ((ArrayList)_s).get(0);
	if (_s instanceof String){
		if (((String)_s).startsWith("?"))
			return new Variable(new Atom((String)_s));
		return new Atom((String)_s);
	}
	Statement s = (Statement) _s;
	String opt = s.operator.toUpperCase();
	Expression res=null;
	
	// TYPES OF EXPRESSIONS
	// 1. Implication
	if (opt.equals("<=")){ // type implication
		Expression consequence = parseExpression(s.members.get(0));
		ExpList premises = parseExpList(s.members.subList(1, s.members.size()));
		res = new Implication(consequence, premises);
	} else if (opt.equals("=>")){ // type implication
		Expression consequence = parseExpression(s.members.get(s.members.size()-1));
		ExpList premises = parseExpList(s.members.subList(0, s.members.size()-1));
		res = new Implication(consequence, premises);
	} 
	else if (opt.equals("OR") && s.members.size() >= 2)
	{
//		if (s.members.size() < 2) 
//		{
//			System.err.println("Parse error: No handling for OR of arity < 2.");
//			System.exit(-1);
//		}
//		else 
			res = new OrOp(parseExpList(s.members));
	}
	else if (opt.equals("AND") && s.members.size() >= 2)
	{
//		if (s.members.size() < 2) 
//		{
//			System.err.println("Parse error: No handling for AND of arity < 2.");
//			System.exit(-1);
//		}
//		else
			res = new AndOp(parseExpList(s.members));
	}
	else if (opt.equals("DISTINCT") && s.members.size() == 2)
	{
//		if (s.members.size() != 2) 
//		{
//			System.err.println("Parse error: No handling for non-binary DISTINCT.");
//			System.exit(-1);
//		}
//		else 
			res = new DistinctOp(
				parseExpression(s.members.get(0)),
				parseExpression(s.members.get(1)));
	}
	else if (opt.equals("NOT") && s.members.size() == 1)
	{
//		if (s.members.size() != 1) 
//		{
//			System.err.println("Parse error: No handling for non-unary NOT.");
//			System.exit(-1);
//		}
//		else 
			res = new NotOp(parseExpression(s.members.get(0)));
	}
	// 2. Associations
	else {
		res = new Predicate(opt, parseExpList(s.members));
	}
	return res;
}
static Variable parseVariable(String str){
	Variable v=null;
	v = new Variable(new Atom(str));
	return v;
}
static Atom parseAtom(String str){
	Atom a = null;
	a = new Atom(str);
	return a;
}
}
