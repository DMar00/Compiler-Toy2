package main.testers;

import main.output_cup.Parser;
import main.output_jflex.Lexer;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.visitor.XMLVisitor;
import main.table.SymbolTable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SemanticTester {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            Reader inFile = new FileReader(args[0]);

            Lexer lexer = new Lexer(inFile);

            Parser par = new Parser(lexer);
            ProgramOp astRoot = (ProgramOp) par.parse().value;


            /*XMLVisitor xmlGen = new XMLVisitor();
            astRoot.accept(xmlGen);*/

            SymbolTable symbolTable = new SymbolTable();





            Path percorso = Paths.get(args[0]);
            String fileName = percorso.getFileName().toString();
            //xmlGen.printToFile("xmlout/ast_" + fileName + ".xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
