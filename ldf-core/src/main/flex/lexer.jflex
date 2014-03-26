package ldf.parser.gen;
import ldf.java_cup.runtime.TokenFactory;
import ldf.java_cup.runtime.Symbol;

import java.io.Reader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

%eofval{
    return eof();
%eofval}

%{
    private TokenFactory tokenFactory;

    public Lexer(Reader isr, TokenFactory tf){
        this(isr);
        tokenFactory = tf;
    }

    public Symbol symbol(String name, int code){
        return symbol(name, code, null);
    }

    public Symbol symbol(String name, int code, Object value){

        Symbol tok = tokenFactory.newToken(
            name, code,
            yyline + 1,
            yycolumn + 1,
            yychar
        );

        tok.value = value;
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

    public static final Map<Integer, String> tokenNames;

    static {
         Map<Integer, String> tokenNames_;
         tokenNames_ = new LinkedHashMap<Integer, String>();
         for (java.lang.reflect.Field f: sym.class.getFields()) {
             if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                 try {
                     tokenNames_.put(f.getInt(null), f.getName());
                 } catch (IllegalAccessException ignored) {
                 }
             }
         }
         tokenNames = Collections.unmodifiableMap(tokenNames_);
    }

    public Symbol symbol(int symCode) {
        return symbol(tokenNames.get(symCode), symCode);
    }

    public Symbol symbol(int symCode, Object value) {
        return symbol(tokenNames.get(symCode), symCode, value);
    }

    private Symbol symbolInteger(String lexeme, int radix) {
        try {
            return symbol(NUMBER, Long.parseLong(lexeme, radix));
        } catch (NumberFormatException e) {
            return symbol(error, "Invalid number format");
        }
    }

    private Symbol symbolBoolean(boolean value) {
        return symbol(BOOLEAN, Boolean.valueOf(value));
    }


%}

/* whitespace */
NewLine = \r | \n | \r\n
WhiteSpace = [ \t\f] | {NewLine}

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment = "/*" {CommentContent} \*+ "/"
EndOfLineComment = "//" [^\r\n]* {NewLine}
CommentContent = ( [^*] | \*+[^*/] )*

/* identifier */
IdFirst = [_$[:letter:]]
IdNext  = [_$[:letter:][:digit:]]
Identifier = {IdFirst} {IdNext}*
IdentifierInvalid = [0-9]{IdNext}+

/* literals */
HexDigit = [0-9a-fA-F]

LiteralIntDecimal = 0|[1-9][0-9]*
LiteralIntOctal   = 0[0-7]+
LiteralIntHex     = 0[xX]{HexDigit}+
LiteralIntNoFollow  = [0-9a-zA-Z_]
LiteralInvalidInt = ({LiteralIntDecimal}|{LiteralIntOctal}|{LiteralIntHex}){LiteralIntNoFollow}+

LiteralFloat = [+-]?(([0-9]+\.([0-9]+)?|\.[0-9]+)([eE][+-]?[0-9]+)?|{LiteralIntDecimal})

/* strings */
StrEscapeSequenceAscii   = \\[xX]{HexDigit}{2}
StrEscapeSequenceUnicode = \\[uU]{HexDigit}{4}
StrCommonEscapeSequences = \\(r|n|f|t|\'|\"|\\)
StrChar = [^\\\'\"]|{StrEscapeSequenceAscii}|{StrEscapeSequenceUnicode}|{StrCommonEscapeSequences}

/* invalid tokens */
InvalidTokens = {LiteralInvalidInt}|{IdentifierInvalid}

%state YYSTRING
%state YYCHARACTER

%%

<YYINITIAL> {

    "grammar"            { return symbol(GRAMMAR);              }
    "nterm"              { return symbol(NTERM);                }
    "where"              { return symbol(WHERE);                }
    "if"                 { return symbol(IF);                   }
    "else"               { return symbol(ELSE);                 }
    "for"                { return symbol(FOR);                  }
    "do"                 { return symbol(DO);                   }
    "while"              { return symbol(WHILE);                }
    "break"              { return symbol(BREAK);                }
    "continue"           { return symbol(CONTINUE);             }
    "switch"             { return symbol(SWITCH);               }
    "case"               { return symbol(CASE);                 }
    "default"            { return symbol(DEFAULT);              }
    "return"             { return symbol(RETURN);               }
    "new"                { return symbol(NEW);                  }
    "try"                { return symbol(TRY);                  }
    "catch"              { return symbol(CATCH);                }
    "finally"            { return symbol(FINALLY);              }
    "var"                { return symbol(VAR);                  }
    "def"                { return symbol(DEF);                  }

    /* literals */
    "true"               { return symbolBoolean(true);          }
    "false"              { return symbolBoolean(false);         }
    {LiteralIntOctal}    { return symbolInteger(yytext(), 8);   }
    {LiteralIntDecimal}  { return symbolInteger(yytext(), 10);  }
    {LiteralIntHex}      { return symbolInteger(
                           yytext().substring(2), 16);
                         } // I guess there's a mistake here

    /* error token */
    {InvalidTokens}      { return symbol(error,
                           "Illegal token " + yytext() + "\""
                           ); } //stupid JFlex ?

    /* operators */
    "@"                  { return symbol(AT_SIGN);              }
    "?"                  { return symbol(Q_MARK);               }
    "+"                  { return symbol(PLUS);                 }
    "++"                 { return symbol(PLUS_PLUS);            }
    "-"                  { return symbol(MINUS);                }
    "--"                 { return symbol(MINUS_MINUS);          }
    "*"                  { return symbol(TIMES);                }
    "/"                  { return symbol(SLASH);                }
    "%"                  { return symbol(MOD);                  }
//  "^"                  { return symbol(HAT);                  }
    "&&"                 { return symbol(AMP_AMP);              }
    "||"                 { return symbol(PIPE_PIPE);            }
    "!"                  { return symbol(EXCL);                 }
//  "~"                  { return symbol(TILDE);                }
    ":"                  { return symbol(COLON);                }
    ";"                  { return symbol(SEMICOLON);            }
    ","                  { return symbol(COMMA);                }
    "."                  { return symbol(DOT);                  }
    "\""                 { yybegin(YYSTRING); return symbol(STRING_BEGIN); }
    "'"                  { yybegin(YYCHARACTER); return symbol(CHAR_BEGIN); }
    "="                  { return symbol(EQ);                   }
    "=="                 { return symbol(EQEQ);                 }
    "!="                 { return symbol(NEQ);                  }
    "<"                  { return symbol(LT);                   }
    "<="                 { return symbol(LTE);                  }
    ">"                  { return symbol(GT);                   }
    ">="                 { return symbol(GTE);                  }

    "("                  { return symbol(LPAREN);               }
    ")"                  { return symbol(RPAREN);               }
    "["                  { return symbol(LSQUARE);              }
    "]"                  { return symbol(RSQUARE);              }
    "{"                  { return symbol(LCURLY);               }
    "}"                  { return symbol(RCURLY);               }
    "|"                  { return symbol(PIPE);                 }

    /*special*/
    "::="                { return symbol(COLON_COLON_EQ);       }
    "{:"                 { return symbol(LACTION);              }
    ":}"                 { return symbol(RACTION);              }
    "{?"                 { return symbol(LGUARD);               }
    "?}"                 { return symbol(RGUARD);               }
    "{@"                 { return symbol(LCURLY_AT_SIGN);       }
    "..."                { return symbol(ELLIPSIS);             }
    "=>"                 { return symbol(EQ_GT);                }

    {Identifier}         { return symbol(IDENTIFIER, yytext()); }
    {Comment}            { comment();                           }
    {WhiteSpace}*        { whitespace();                        }

}

<YYCHARACTER> {
    (\"|{StrChar})*      { return symbol(STRING_FRAGMENT);      }
    \'                   { yybegin(YYINITIAL);
                           return symbol(CHAR_END);
                         }
}

<YYSTRING> {
    (\'|{StrChar})*     { return symbol(STRING_FRAGMENT);       }
    \"                  { yybegin(YYINITIAL);
                          return symbol(STRING_END);
                        }
}

<YYCHARACTER, YYSTRING> {
    \\.                 { return symbol(INVALID_ESCAPE_SEQ);    }
}

/* error fallback */
.|\n                    { return symbol(error,
                            "Illegal character \"" + yytext() + "\""
                          );
                        }

