package de.tu_dresden.inf.ggp06_2.parser;

/* JFlex example: part of Java language lexer specification */
import java_cup.runtime.*;

/**
 * This class is Benders understanding of preKIF format GDL.
 */

%%

%class preKIFScanner
%cup
%ignorecase
%line
%column
%full

%eofval{
  return symbol(preKIFSymbols.EOF);
%eofval}

%state PRED

%{

  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
  
%}

LineTerminator    = \r|\n|\r\n
InputCharacter    = [^\r\n]
WhiteSpace        = {LineTerminator} | [ \t\f]

Comment           = ";" {InputCharacter}* {LineTerminator}

Variable          = "?" [\_a-zA-Z] [\_a-zA-Z0-9-]*
Constant          = [\_a-zA-Z0-9-]* | "<="

%state CONST
%state VAR

%%

/* keywords */

<YYINITIAL> {

  "("                            { yybegin(PRED); 
  								   return symbol(preKIFSymbols.OPEN);  }
  ")"                            { return symbol(preKIFSymbols.CLOSE); }

  {Variable}                     { return symbol( preKIFSymbols.VARIABLE, 
                                                  yytext() ); }
  {Constant}                     { return symbol( preKIFSymbols.CONSTANT, 
                                                  yytext() ); }

  {Comment}                      { /* ignore */ }
  {WhiteSpace}                   { /* ignore */ }

}

<PRED> {

  {Constant}                     { yybegin(YYINITIAL); 
                                   return symbol( preKIFSymbols.PREDICATE, 
                                                  yytext() ); }

  {Comment}                      { /* ignore */ }
  {WhiteSpace}                   { /* ignore */ }

}

/* error fallback */
.|\n                             { throw new Error("Illegal character <"+
                                                    yytext()+">:" +yycolumn ); }
                                                    