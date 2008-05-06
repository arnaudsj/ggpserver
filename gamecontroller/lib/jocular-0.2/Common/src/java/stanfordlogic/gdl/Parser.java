///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.gdl;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Parser for the Game Description Language. Note that you can only run one
 * parse at a time. This is to help save on memory allocation, by recycling the
 * same data structures as much as possible.
 * 
 * <p>
 * The parser should be reset at the end of every game to clear the symbol
 * table. 
 * 
 * <p>
 * I'm not sure if it's actually necessary to reset the parser. Why not just let
 * the symbol table grow? We have 2^31-256 values, so we'll only have overflow
 * issues after seeing that many unique tokens. We should only get new tokens
 * during game initializations. Assuming that every game creates 5,000 new
 * tokens, we would need to play on the order of 429,496 games to fill up our
 * symbol table...
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class Parser
{
    final SymbolTable symbolTable_;
    
    public int TOK_ROLE;
    public int TOK_INIT;
    public int TOK_TRUE;
    public int TOK_DOES;
    public int TOK_NEXT;
    public int TOK_LEGAL;
    public int TOK_GOAL;
    public int TOK_TERMINAL;
    public int TOK_DISTINCT;
    public int TOK_NIL;
    
    // GDL operators
    public int TOK_IMPLIEDBY;
    public int TOK_OR_OP;
    public int TOK_NOT_OP;
    
    public Parser()
    {
        symbolTable_ = new SymbolTable();
        initSymbolTable();
    }
    
    // Fill the symbol table with the reserved keywords, so that given a token 
    // number, we know if it's one of the reserved tokens.
    private void initSymbolTable()
    {
        TOK_ROLE      = symbolTable_.get("role");
        TOK_INIT      = symbolTable_.get("init");
        TOK_TRUE      = symbolTable_.get("true");
        TOK_DOES      = symbolTable_.get("does");
        TOK_NEXT      = symbolTable_.get("next");
        TOK_LEGAL     = symbolTable_.get("legal");
        TOK_GOAL      = symbolTable_.get("goal");
        TOK_TERMINAL  = symbolTable_.get("terminal");
        TOK_DISTINCT  = symbolTable_.get("distinct");
        TOK_NIL       = symbolTable_.get("nil");
        
        TOK_IMPLIEDBY = symbolTable_.get("<=");
        TOK_OR_OP     = symbolTable_.get("or");
        TOK_NOT_OP    = symbolTable_.get("not");
    }
    
    synchronized public void reset()
    {
        symbolTable_.clear();
        initSymbolTable();
    }
    
    /**
     * Parse a string of input. Note that you should not use this method if
     * you have an input stream available; use the InputStream specialized
     * method instead. This method creates an input stream specifically for
     * reading the string <tt>input</tt>.
     * 
     * @param input The string to parse.
     * @return The GdlExpression result.
     */
    public GdlList parse(String input)
    {
        return parse( new ByteArrayInputStream(input.getBytes()) );
    }
    
    public GdlList parse(InputStream input)
    {
        final Lexer lexer = new Lexer(input, symbolTable_);
        
        // Top-level is a list of expressions.
        ArrayList<GdlExpression> exprs = new ArrayList<GdlExpression>();
        
        while ( true )
        {
            final int t = lexer.token();
            
            if ( t == -1 )
                break;
            
            lexer.unget(t);
            exprs.add(parseExpression(lexer));
        }
        
        return new GdlList(symbolTable_, exprs.toArray(new GdlExpression [exprs.size()]));
    }
    
    private GdlExpression parseExpression(Lexer lexer)
    {
        // Get the token:
        final int t = lexer.token();
        
        // If it's a (, then we must have a new expression
        if ( t == '(' )
            return parseList(lexer);
        
        // If it's an identifier, then we have an atom
        else if ( t > 255 )
            return new GdlAtom(symbolTable_, t);
        
        // If it's a question mark, then we have a variable
        else if ( t == '?' )
            return parseVariable(lexer);
        
        // else, it must be something bogus.
        else {
            reportError("Expression: can't handle token " + t + " (char: " + (char)t +")");
            return null;
        }
    }
    
    private GdlList parseList(Lexer lexer)
    {
        ArrayList<GdlExpression> arr = new ArrayList<GdlExpression>();
        
        int token;
        
        while ( (token = lexer.token()) != ')' )
        {
            if (token == Lexer.EOF) {
                reportError("Unexpected end of file in parseList");
            }
            
            // put this token back, and parse the expression
            lexer.unget(token);
            arr.add( parseExpression(lexer) );
        }
        
        // Convert the ArrayList to an array, and return a new GdlList object.
        return new GdlList(symbolTable_, arr.toArray(new GdlExpression [arr.size()]) );
    }
    
    private GdlVariable parseVariable(Lexer lexer)
    {
        int token = lexer.token();
        
        // Make sure we actually got an identifier
        if ( token <= 255 )
            reportError("? token must be followed by an identifier token in variable parsing");
        
        return GdlVariable.getGdlVariable(symbolTable_, token);
    }
    
    public SymbolTable getSymbolTable()
    {
        return symbolTable_;
    }
    
    
    public static void main(String [] args)
    {
        Parser p = new Parser();
        
        try {
            FileInputStream input;
            
            if (args.length == 0)
                input = new FileInputStream("def/tictactoe.kif");
            else
                input = new FileInputStream(args[0]);
        
            GdlExpression exp = p.parse( new BufferedInputStream(input) );
            
            System.out.println(exp);
        }
        catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    private void reportError(String str)
    {
        reportError(str, null);
    }
    
    private void reportError(String str, Lexer l)
    {
        String message;
        
        if (l != null) {
            message = "Parser error (line " + l.getLineNumber() + "): " + str;
        }
        else {
            message = "Parser error: " + str;
        }
        
        throw new RuntimeException(message);
    }
}
