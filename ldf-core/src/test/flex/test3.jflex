package ldf.tests.core.test3;
import ldf.java_cup.runtime.TokenFactory;
import ldf.java_cup.runtime.Symbol;

import java.io.InputStreamReader;

%%

%class Lexer
%implements sym
%implements ldf.java_cup.runtime.Scanner
%public
%unicode
%line
%column
%char
%type ldf.java_cup.runtime.Symbol
%function next_token

%{
    private TokenFactory tokenFactory;

    public Lexer(InputStreamReader isr, TokenFactory tf){
        this(isr);
        tokenFactory = tf;
    }

    public Symbol symbol(String name, int code){
        return symbol(name, code, yytext());
    }

    public Symbol symbol(String name, int code, String lexem){

        Symbol tok = tokenFactory.newToken(
            name, code,
            yyline + 1,
            yycolumn + 1,
            yychar
        );

        tok.value = lexem;
        return tok;

    }

    public Symbol eof() {
        return tokenFactory.newEOF(
            "EOF",sym.EOF,
            yyline + 1,
            yycolumn + 1,
            yychar
        );
    }

    public void comment() {
        tokenFactory.newComment(
            yyline + 1,
            yycolumn + 1,
            yychar
        );
    }
    public void whitespace() {
        tokenFactory.signalWhitespace(
            yyline + 1,
            yycolumn + 1,
            yychar
        );
    }

%}

Newline = \r | \n | \r\n
Whitespace = [ \t\f] | {Newline}

%eofval{
    return eof();
%eofval}

%%

<YYINITIAL> {
    {Whitespace}*  { whitespace();                         }
    [0-9]+         { return symbol("INT", INT);  }
    ","            { return symbol("+", COMMA);  }
    "("            { return symbol("(", LPAREN); }
    ")"            { return symbol("(", RPAREN); }
}
