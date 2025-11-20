

package org.example;
import java_cup.runtime.*;
import org.example.sym;

%%

%class Lexer
%line
%column
%cup

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
dec_int_lit = 0 | [1-9][0-9]*
identifier = [A-Za-z_][A-Za-z_0-9]*

%%

<YYINITIAL> {


    "int"              { System.out.print(" INT "); return symbol(sym.INT); }
    "bool"             { System.out.print(" BOOL "); return symbol(sym.BOOL); }
    "void"             { System.out.print(" VOID "); return symbol(sym.VOID); }
    "main"             { System.out.print(" MAIN "); return symbol(sym.MAIN); }
    "func"             { System.out.print(" FUNC "); return symbol(sym.FUNC); }
    "return"           { System.out.print(" RETURN "); return symbol(sym.RETURN); }
    "if"               { System.out.print(" IF "); return symbol(sym.IF); }
    "else"             { System.out.print(" ELSE "); return symbol(sym.ELSE); }
    "while"            { System.out.print(" WHILE "); return symbol(sym.WHILE); }
    "true"             { System.out.print(" TRUE "); return symbol(sym.TRUE, Boolean.TRUE); }
    "false"            { System.out.print(" FALSE "); return symbol(sym.FALSE, Boolean.FALSE); }


    ";"                { System.out.print(" ; "); return symbol(sym.SEMI); }
    ","                { System.out.print(" , "); return symbol(sym.COMMA); }
    "="                { System.out.print(" = "); return symbol(sym.ASSIGN); }
    "+"                { System.out.print(" + "); return symbol(sym.PLUS); }
    "-"                { System.out.print(" - "); return symbol(sym.MINUS); }
    "*"                { System.out.print(" * "); return symbol(sym.TIMES); }
    "/"                { System.out.print(" / "); return symbol(sym.DIVIDE); }
    "("                { System.out.print(" ( "); return symbol(sym.LPAREN); }
    ")"                { System.out.print(" ) "); return symbol(sym.RPAREN); }
    "{"                { System.out.print(" { "); return symbol(sym.LBRACE); }
    "}"                { System.out.print(" } "); return symbol(sym.RBRACE); }

   
    "=="               { System.out.print(" == "); return symbol(sym.EQ); }
    "!="               { System.out.print(" != "); return symbol(sym.NE); }
    "<="               { System.out.print(" <= "); return symbol(sym.LE); }
    ">="               { System.out.print(" >= "); return symbol(sym.GE); }
    "<"                { System.out.print(" < "); return symbol(sym.LT); }
    ">"                { System.out.print(" > "); return symbol(sym.GT); }

  
    "&&"               { System.out.print(" && "); return symbol(sym.AND); }
    "||"               { System.out.print(" || "); return symbol(sym.OR); }
    "!"                { System.out.print(" ! "); return symbol(sym.NOT); }
   
  
    {dec_int_lit}      { System.out.print(yytext());
                         return symbol(sym.NUMBER, new Integer(yytext())); }
   
   
    {identifier}       { System.out.print(yytext());
                         return symbol(sym.ID, yytext()); }
   
   
    {WhiteSpace}       {  }   
}


[^]                    { throw new Error("Illegal character <"+yytext()+">"); }