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

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lexer for Game Description Language.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class Lexer
{
    private static final Logger logger_ = Logger.getLogger("stanfordlogic.gdl");
    
	private final InputStream in_;
	private int lineNumber_;
	private SymbolTable symbolTable_;
	private Stack<Integer> bufferedTokens_; // for the 'unget' facility
    
    int markedCharacter_; // for the end-of-identifier character.
    private static final int NO_MARK = -2;
	
	StringBuilder identBuf_;
    
    public static final int EOF = -1;
	
	/**
	 * Construct a lexer using <i>input</i> as its input stream.
	 * 
	 * @param input The data input stream. Must support 'mark' operation.
	 * @param symbolTable The symbol table to use for identifiers.
	 */
	public Lexer(InputStream input, SymbolTable symbolTable)
	{
		in_ = input;
		
		symbolTable_ = symbolTable;
		lineNumber_ = 0;
		
		bufferedTokens_ = new Stack<Integer>();
		bufferedTokens_.ensureCapacity(2);
        
        markedCharacter_ = NO_MARK;
	}
	
	public void unget(int token)
	{
		bufferedTokens_.push(token);
	}
	
	/**
	 * Get the next token. Returns -1 for EOF. A character literal
	 * between 0 and 255 is returned as is. Numbers 256 and above
	 * are for reserved tokens.
	 * 
	 * @return The value of the next token, or -1 for EOF.
	 */
	public int token()
	{
        int token = getNextToken();
        
        if (logger_.isLoggable(Level.FINER))
        {
            if (token == EOF) {
                logger_.finest("Lexer got EOF");
            }
            else if (token < 256) {
                logger_.finest("Lexer got character '" + (char) token + "' (ascii "
                              + token + ")");
            }
            else {
                logger_.finest("Lexer got token '" + symbolTable_.get(token)
                              + "' (token #" + token + ")");
            }
        }
        
        return token;
    }
    
    private int getNextToken()
    {
		// If we have a buffered token, return it
		if (bufferedTokens_.isEmpty() == false) {
            return bufferedTokens_.pop();
        }
		
		int c;
        
		try
		{
			while ( true )
			{
                // if we didn't have a mark, then read a character
                if (( c = markedCharacter_ ) == NO_MARK)
                    c = in_.read();
                
                // else, reset the mark
                else
                    markedCharacter_ = NO_MARK;
                
                
				
				if (c == -1) {
					return EOF; // EOF.
                }

				// Count lines
				if (c == '\n') {
                    // FIXME: this won't count lines for Mac line endings (\r)
					lineNumber_++;
                }
                
                // Comments; munch till end of line
                else if (c == ';')
                {
                    while (!isNewline(c = in_.read())) {
                        /* munch munch! */;
                    }
                    
                    // If we didn't read EOF, then be sure to return to that character
                    if ( c != EOF ) {
                        markedCharacter_ = c;
                    }
                }
                
				// Block comments:
                else if ( c == '#' )
                {
                    c = in_.read();
                    
                    if (c != '|') {
                        reportError("# must be followed by a |, not " + c + " (char: "
                              + (char) c + ")");
                    }
                    
                    // read until we get a "|#"
                    
                    boolean gotBar = false;
                    while(true)
                    {
                        c = in_.read();
                        
                        if (c == EOF) {
                            reportError("EOF in block comment");
                        }
                        
                        if (gotBar && c == '#') {
                            break;
                        }
                        
                        if (c == '|') {
                            gotBar = true;
                        }
                        else {
                            gotBar = false;
                        }
                    }
                }
				
				// Ignore white-space.
				else if (Character.isWhitespace(c)) {
					/* munch */;
                }
				
				// Return parentheses and question marks as-is
				else if (c == '(' || c == ')' || c == '?') {
					return c;
                }
				
				// Identifiers
				else if (isIdentifierChar(c))
                {
					// make a buffer of size 32
					identBuf_ = new StringBuilder(32);
					
					identBuf_.append( (char) Character.toLowerCase(c) );
					
					while ( isIdentifierChar( c = in_.read() ) ) {
						identBuf_.append( (char) Character.toLowerCase(c) );
					}
					
					// If we didn't read EOF, then be sure to return to that character
					if ( c != EOF ) {
						markedCharacter_ = c;
                    }
					
					// Look up the identifier in our symbol table
					int token = symbolTable_.get(identBuf_.toString());
					
					return token;
				}
				
				// Unknown characters
				else {
					reportError("Cannot handle character: " + c + " (char: " + (char) c + ")");
				}
			}
		}
		catch ( IOException e )
		{
            logger_.severe("I/O error: " + e);
			return EOF; // Error
		}
	}
	
	private boolean isIdentifierChar(int c)
	{
		// NOTE: new identifier characters
		return Character.isLetterOrDigit(c) || c == '<' || c == '>'
			|| c == '=' || c == '_' || c == '-' || c == '.';
	}
    
    private boolean isNewline(int c)
    {
        return c == '\n' || c == '\r';
    }
    
    public int getLineNumber()
    {
        return lineNumber_;
    }
    
    private void reportError(String str)
    {
        String message = "Lexer error (line " + lineNumber_ + "): " + str;
        throw new RuntimeException(message);
    }
}
