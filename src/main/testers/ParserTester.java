package main.testers;



import main.output_cup.Parser;
import main.output_jflex.Lexer;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ParserTester {
    public static void main(String[] args) throws FileNotFoundException {
        String filePath = args[0];

        try {
            Lexer lexer = new Lexer(new FileReader(filePath));

            Parser parser = new Parser(lexer);
            parser.debug_parse();
            //parser.parse();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
