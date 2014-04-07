package ldf.java_cup;
import ldf.java_cup.runtime.TokenFactory;
import ldf.java_cup.runtime.Symbol;

import java.lang.Error;
import java.io.InputStreamReader;
import java.io.IOException;

%%

%class Lexer
%implements sym
%implements ldf.java_cup.runtime.Scanner
%public
%unicode
%line
%column
%type ldf.java_cup.runtime.Symbol
%function next_token
%{
    public Lexer(TokenFactory tf){
        this(new InputStreamReader(System.in));
        tokenFactory = tf;
    }

    private StringBuffer sb;
    private TokenFactory tokenFactory;
    private int csline,cscolumn,csoffset;

    public Symbol symbol(String name, int code){
        return symbol(name, code, null);
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

    protected void emit_warning(String message){
        ErrorManager.getManager().emit_warning(
                "Scanner at " + (yyline+1) +
                "(" + (yycolumn+1) + "): " +
                message
        );
    }
    protected void emit_error(String message){
        ErrorManager.getManager().emit_error(
                "Scanner at " + (yyline+1) +
                "(" + (yycolumn+1) + "): " +
                message
        );
    }
%}

Newline = \r | \n | \r\n
Whitespace = [ \t\f] | {Newline}

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment = "/*" {CommentContent} \*+ "/"
EndOfLineComment = "//" [^\r\n]* {Newline}
CommentContent = ( [^*] | \*+[^*/] )*

ident = ([:jletter:] | "_" ) ([:jletterdigit:] | [:jletter:] | "_" )*


%eofval{
    return eof();
%eofval}

%state CODESEG

%%

<YYINITIAL> {

  {Whitespace}* { whitespace();                                }
  "?"           { return symbol("QESTION",QUESTION);           }
  ";"           { return symbol("SEMI",SEMI);                  }
  ","           { return symbol("COMMA",COMMA);                }
  "*"           { return symbol("STAR",STAR);                  }
  "."           { return symbol("DOT",DOT);                    }
  "|"           { return symbol("BAR",BAR);                    }
  "["           { return symbol("LBRACK",LBRACK);              }
  "]"           { return symbol("RBRACK",RBRACK);              }
  ":"           { return symbol("COLON",COLON);                }
  "::="         { return symbol("COLON_COLON_EQUALS",COLON_COLON_EQUALS);   }
  "%prec"       { return symbol("PERCENT_PREC",PERCENT_PREC);  }
  ">"           { return symbol("GT",GT);                      }
  "<"           { return symbol("LT",LT);                      }
  {Comment}     { comment();                                   }
  "{:"          {
                    sb = new StringBuffer();
                    csline   = yyline + 1;
                    cscolumn = yycolumn + 1;
                    csoffset = yychar;
                    yybegin(CODESEG);
                }
  "package"     { return symbol("PACKAGE",PACKAGE);            }
  "import"      { return symbol("IMPORT",IMPORT);              }
  "code"        { return symbol("CODE",CODE);                  }
  "action"      { return symbol("ACTION",ACTION);              }
  "parser"      { return symbol("PARSER",PARSER);              }
  "terminal"    { return symbol("PARSER",TERMINAL);            }
  "non"         { return symbol("NON",NON);                    }
  "nonterminal" { return symbol("NONTERMINAL",NONTERMINAL);    }
  "init"        { return symbol("INIT",INIT);                  }
  "scan"        { return symbol("SCAN",SCAN);                  }
  "with"        { return symbol("WITH",WITH);                  }
  "start"       { return symbol("START",START);                }
  "precedence"  { return symbol("PRECEDENCE",PRECEDENCE);      }
  "left"        { return symbol("LEFT",LEFT);                  }
  "right"       { return symbol("RIGHT",RIGHT);                }
  "nonassoc"    { return symbol("NONASSOC",NONASSOC);          }
  "extends"     { return symbol("EXTENDS",EXTENDS);            }
  "super"       { return symbol("SUPER",SUPER);                }
  {ident}       { return symbol("ID",ID,yytext());             }

}

<CODESEG> {
  ":}"         {
                    yybegin(YYINITIAL);
                    Symbol tok = tokenFactory.newToken(
                        "CODE_STRING", CODE_STRING,
                        csline,  cscolumn, csoffset,
                        yyline+1, yycolumn+1+yylength(), yychar
                    );
                    tok.value = sb.toString();
                    return tok;
               }
  .|\n         { sb.append(yytext()); }
}

// error fallback
.|\n          { emit_warning("Unrecognized character '" +yytext()+"' -- ignored"); }
