package main.testers;

import main.output_cup.Parser;
import main.output_jflex.Lexer;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.visitor.CVisitor;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorFirstVisit;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorSecondVisit;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Toy2ToC {
    public static void main2(String[] args) throws FileNotFoundException {
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
            CVisitor cVisitor = new CVisitor(scopingVisitor.getFuncMap(), scopingVisitor.getProcMap());
            astRoot.accept(cVisitor);

            Path percorso = Paths.get(args[0]);
            String fileName = percorso.getFileName().toString();
            String cFileName = fileName.substring(0,fileName.length()-4) ;
            String cFileNamePlusExtension = cFileName+".c";
            //System.out.println("test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension);
            cVisitor.printToFile("c_out"+File.separator+cFileNamePlusExtension);


            //ESECUZIONE DI GCC PER CREARE IL FILE OUTPUT.EXE A PARTIRE DAL FILE.C
            ProcessBuilder builder = new ProcessBuilder("gcc", "-o", cFileName+".exe", cFileNamePlusExtension);
            builder.directory(new File("c_out"));
            builder.redirectErrorStream(true);
            Process p = builder.start();

            Scanner sc = new Scanner(p.getInputStream());
            if (sc.hasNextLine()) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    System.out.println(line);
                }
            }

            /*//ESECUZIONE E STAMPA DEL FILE OUTPUT.EXE
            ProcessBuilder processBuilder = new ProcessBuilder("c_out"+File.separator+cFileName+".exe");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Stampa l'output nel terminale
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        try {
            main2(args);
        } catch(Throwable e) {
            System.err.println(e);
        }
    }
}
