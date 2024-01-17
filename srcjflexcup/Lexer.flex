package main.output_jflex;

import java_cup.runtime.*;
import JFlex.sym;
import main.output_cup.Token;

%%
%cupsym Token
%cup
%public
%class Lexer
%line
%column
%unicode

%{
  private Symbol symbol (int type) {
    return new Symbol (type, yyline, yycolumn);
  }

  private Symbol symbol (int type, Object value) {
      return new Symbol (type, yyline, yycolumn, value);
  }

  public int getLine() { return yyline + 1; }

  public int getColumn() { return yycolumn + 1; }

  StringBuffer string_const = new StringBuffer();

  int s_line, s_col;
%}

LineEnd = \r|\n|\r\n
WhiteSpace = {LineEnd} | [ \t\f]

Letter = [a-zA-Z]
Letter_ = [a-zA-Z_]
Digit = [0-9]
Digit_no_zero = [1-9]

//
SEMI = ";"
COMMA = ","
COLON = ":"
LPAR = "("
RPAR  = ")"

//
ASSIGN = "^="
EQ = "="
NE = "<>"
LT = "<"
GT = ">"
LE = "<="
GE = ">="

//
AND = "&&"
OR = "||"
NOT = "!"

//
PLUS = "+"
MINUS = "-"
TIMES = "*"
DIV = "/"

//
RETURN = "return"
FUNCTION = "func"
TYPERETURN = "->"
ENDFUNCTION = "endfunc"
PROCEDURE = "proc"
ENDPROCEDURE = "endproc"
OUT = "out"
WRITE = "-->"
WRITERETURN = "-->!"
DOLLARSIGN = "$"
READ = "<--"

//
IF = "if"
THEN = "then"
ELSE = "else"
ENDIF = "endif"
ELIF = "elseif"
WHILE = "while"
DO = "do"
ENDWHILE = "endwhile"
REF = "@"

//var boolean id
VAR = "var"
ENDVAR = "\\" //backslash to avoid error with "\"

BOOLEAN = "boolean"
STRING = "string"
INTEGER = "integer"
REAL = "real"

TRUE = "true"
FALSE = "false"
INTEGER_CONST = (0 | [{Digit_no_zero}{Digit}]*) //dopo OR considera anche almeno un'occorrenza deve esserci?
//CONST = {Digits}(\.{Digitss})?([E][+-]?{Digits})?
REAL_CONST = ({INTEGER_CONST}\.{Digit}*)    //accetta 3.   se invece voglio che se c'è punto deve esserci per forza un num dopo?

ID = {Letter_}[{Letter_}{Digit}]*

%state COMMENT
%state STRING
%%


//states
<YYINITIAL> {   //stato di default quando non sono attivi altri stati
    {WhiteSpace} {/* ignore white spaces*/}

    //{SEMI} { return symbol(sym.SEMI); } if not use class Token
    {SEMI} {return symbol(Token.SEMI);}
    {COMMA} {return symbol(Token.COMMA);}
    {COLON} {return symbol(Token.COLON);}
    {LPAR} {return symbol(Token.LPAR);}
    {RPAR} {return symbol(Token.RPAR);}

    {ASSIGN} {return symbol(Token.ASSIGN);}
    {EQ} {return symbol(Token.EQ);}
    {NE} {return symbol(Token.NE);}
    {LT} {return symbol(Token.LT);}
    {GT} {return symbol(Token.GT);}
    {LE} {return symbol(Token.LE);}
    {GE} {return symbol(Token.GE);}

    {AND} {return symbol(Token.AND);}
    {OR} {return symbol(Token.OR);}
    {NOT} {return symbol(Token.NOT);}

    {PLUS} {return symbol(Token.PLUS);}
    {MINUS} {return symbol(Token.MINUS);}
    {TIMES} {return symbol(Token.TIMES);}
    {DIV} {return symbol(Token.DIV);}

    {VAR} {return symbol(Token.VAR);}
    {ENDVAR} {return symbol(Token.ENDVAR);}

    {BOOLEAN} { return symbol(Token.BOOLEAN); }
    {TRUE} { return symbol(Token.TRUE); }
    {FALSE} { return symbol(Token.FALSE); }

    {INTEGER} {return symbol(Token.INTEGER);}
    {INTEGER_CONST} {return symbol(Token.INTEGER_CONST, yytext());}

    {REAL} {return symbol(Token.REAL);}
    {REAL_CONST} {return symbol(Token.REAL_CONST, yytext());}

    {STRING} {return symbol(Token.STRING);}

    {RETURN} {return symbol(Token.RETURN);}
    {FUNCTION} {return symbol(Token.FUNCTION);}
    {TYPERETURN} {return symbol(Token.TYPERETURN);}
    {ENDFUNCTION} {return symbol(Token.ENDFUNCTION);}
    {PROCEDURE} {return symbol(Token.PROCEDURE);}
    {ENDPROCEDURE} {return symbol(Token.ENDPROCEDURE);}
    {OUT} {return symbol(Token.OUT);}
    {WRITE} {return symbol(Token.WRITE);}
    {WRITERETURN} {return symbol(Token.WRITERETURN);}
    {DOLLARSIGN} {return symbol(Token.DOLLARSIGN);}
    {READ} {return symbol(Token.READ);}
    {IF} {return symbol(Token.IF);}
    {THEN} {return symbol(Token.THEN);}
    {ELSE} {return symbol(Token.ELSE);}
    {ENDIF} {return symbol(Token.ENDIF);}
    {ELIF} {return symbol(Token.ELIF);}
    {WHILE} {return symbol(Token.WHILE);}
    {DO} {return symbol(Token.DO);}
    {ENDWHILE} {return symbol(Token.ENDWHILE);}
    {REF} {return symbol(Token.REF);}

    {ID} { return symbol(Token.ID, yytext()); }

    //start comment
    "%" {
            s_line = getLine();
            s_col = getColumn();
            yybegin(COMMENT);
    }

    //start String
    "\"" {
          string_const = new StringBuffer();
          yybegin(STRING);
      }

    // Error
    //[^] { return symbol(Token.NOT_RECOGNIZED);}
    [^] {
          throw new Error("Illegal character [" + yytext() + "] at line:" + getLine() + ", col:" + getColumn());
    }
}

<COMMENT> {
    //end comment
    "%" { yybegin(YYINITIAL);}
    [^] { /* Nothing */ }
    //se non uso s_line e s_col ma yyline e yycolumn direttamente, non mi stampa i giusti numeri
    <<EOF>> { throw new Error("Comment not close at line:" + s_line + ", col:" + s_col);}
}

<STRING> {
    //qualsiasi carattere eccetto \n \r "
    [^\n\r\"]+ {
        string_const.append(yytext());
    }
    //end String
    \" {
        yybegin(YYINITIAL);
        return symbol(Token.STRING_CONST, string_const.toString());
    }
    [^] { /* Nothing */ } //se tolgo questo non mi dà eccezioni
    <<EOF>> { throw new Error("String not close at line:" + s_line + ", col:" + s_col);}
}