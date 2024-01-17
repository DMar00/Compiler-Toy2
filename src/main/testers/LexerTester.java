package main.testers;

import JFlex.sym;
import java_cup.runtime.Symbol;
import main.output_cup.Token;
import main.output_jflex.Lexer;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class LexerTester {
    public static String getToken(int id){
        return Token.terminalNames[id];
    }

    public static void main(String[] args) throws FileNotFoundException {
        Reader input = new FileReader(args[0]);
        Lexer lexer = new Lexer(input);

        Symbol token;
        try {
            while (!lexer.yyatEOF()) {  //while(true)
                token = lexer.next_token();

                if(token.sym == sym.EOF){
                    break;  //inutile questo if ?!
                }else{
                    if( token.value != null){
                        System.out.println("<" + getToken(token.sym) + " , \"" + token.value.toString() + "\">");
                    }else{
                        System.out.println("<" + getToken(token.sym) + ">");
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}