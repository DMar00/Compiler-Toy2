package main.testers;

import main.output_cup.Parser;
import main.output_jflex.Lexer;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.visitor.CVisitor;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorFirstVisit;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorSecondVisit;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class Toy2ToC {
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
            SemanticVisitorFirstVisit scopingVisitor = new SemanticVisitorFirstVisit();
            astRoot.accept(scopingVisitor);

            //2° visita
            SemanticVisitorSecondVisit scopingVisitor2 = new SemanticVisitorSecondVisit(scopingVisitor.getActiveSymbolTable());
            astRoot.accept(scopingVisitor2);

            //Visitor C
            for (Map.Entry<String, List<Type>> m : scopingVisitor.getFuncMap().entrySet())
                System.out.println(m);
            CVisitor cVisitor = new CVisitor(scopingVisitor.getFuncMap());
            astRoot.accept(cVisitor);


            //Path percorso = Paths.get(args[0]);
            //String fileName = percorso.getFileName().toString();
            //xmlGen.printToFile("xmlout/ast_" + fileName + ".xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
