package main.testers;

import main.output_cup.Parser;
import main.output_jflex.Lexer;
import main.syntaxtree.visitor.SemanticVisitorCreateTables;
import main.syntaxtree.visitor.SemanticVisitorReadTables;
import main.syntaxtree.nodes.ProgramOp;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class SemanticTester {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            Reader inFile = new FileReader(args[0]);

            Lexer lexer = new Lexer(inFile);

            Parser par = new Parser(lexer);
            ProgramOp astRoot = (ProgramOp) par.parse().value;


            /*XMLVisitor xmlGen = new XMLVisitor();
            astRoot.accept(xmlGen);*/

            //SymbolTable symbolTable = new SymbolTable();
            //1° visita
            SemanticVisitorCreateTables scopingVisitor = new SemanticVisitorCreateTables();
            astRoot.accept(scopingVisitor);

            //2° visita
            SemanticVisitorReadTables scopingVisitor2 = new SemanticVisitorReadTables(scopingVisitor.getActiveSymbolTable());
            astRoot.accept(scopingVisitor2);


            //Path percorso = Paths.get(args[0]);
            //String fileName = percorso.getFileName().toString();
            //xmlGen.printToFile("xmlout/ast_" + fileName + ".xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
