/*
  File Name: lcalc_extended.flex
  Extended lexer for the compiler project
  To Create: java -cp jflex-1.8.2.jar jflex.Main lcalc_extended.flex
*/

/* --------------------------Usercode Section------------------------ */

package org.example;
import java_cup.runtime.*;
import org.example.sym;
      
%%
   
/* -----------------Options and Declarations Section----------------- */
   
%class Lexer
%line
%column
%cup
   
/*
  Declarations
*/
%{   
    /* To create a new java_cup.runtime.Symbol with information about
       the current token, the token will have no value in this case. */
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    
    /* Also creates a new java_cup.runtime.Symbol with information
       about the current token, but this object has a value. */
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
   
/*
  Macro Declarations
*/
   
/* A line terminator is a \r (carriage return), \n (line feed), or \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace     = {LineTerminator} | [ \t\f]
   
/* A literal integer */
dec_int_lit = 0 | [1-9][0-9]*
   
/* A identifier */
identifier = [A-Za-z_][A-Za-z_0-9]*
   
%%
/* ------------------------Lexical Rules Section---------------------- */
   
<YYINITIAL> {
   
    /* Keywords */
    "int"              { System.out.print(" INT "); return symbol(sym.INT); }
    "bool"             { System.out.print(" BOOL "); return symbol(sym.BOOL); }
    "void"             { System.out.print(" VOID "); return symbol(sym.VOID); }
    "main"             { System.out.print(" MAIN "); return symbol(sym.MAIN); }
    "return"           { System.out.print(" RETURN "); return symbol(sym.RETURN); }
    "true"             { System.out.print(" TRUE "); return symbol(sym.TRUE, Boolean.TRUE); }
    "false"            { System.out.print(" FALSE "); return symbol(sym.FALSE, Boolean.FALSE); }
    
    /* Operators and delimiters */
    ";"                { System.out.print(" ; "); return symbol(sym.SEMI); }
    "="                { System.out.print(" = "); return symbol(sym.ASSIGN); }
    "+"                { System.out.print(" + "); return symbol(sym.PLUS); }
    "-"                { System.out.print(" - "); return symbol(sym.MINUS); }
    "*"                { System.out.print(" * "); return symbol(sym.TIMES); }
    "/"                { System.out.print(" / "); return symbol(sym.DIVIDE); }
    "("                { System.out.print(" ( "); return symbol(sym.LPAREN); }
    ")"                { System.out.print(" ) "); return symbol(sym.RPAREN); }
    "{"                { System.out.print(" { "); return symbol(sym.LBRACE); }
    "}"                { System.out.print(" } "); return symbol(sym.RBRACE); }
   
    /* Numbers */
    {dec_int_lit}      { System.out.print(yytext());
                         return symbol(sym.NUMBER, new Integer(yytext())); }
   
    /* Identifiers */
    {identifier}       { System.out.print(yytext());
                         return symbol(sym.ID, yytext()); }
   
    /* Skip whitespace */
    {WhiteSpace}       { /* just skip what was found, do nothing */ }   
}

/* Error handling */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }