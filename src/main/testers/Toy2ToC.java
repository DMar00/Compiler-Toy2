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
    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Usage: java Toy2ToC <input_file>");
            System.exit(1);
        }

        String inputFile = args[0];

        try {
            // Leggi il file di input
            Reader inFile = new FileReader(inputFile);

            // Crea il lexer e il parser
            Lexer lexer = new Lexer(inFile);
            Parser parser = new Parser(lexer);

            // Effettua il parsing del file di input
            ProgramOp astRoot = (ProgramOp) parser.parse().value;

            // Esegui la prima visita semantica
            SemanticVisitorFirstVisit firstVisitor = new SemanticVisitorFirstVisit();
            astRoot.accept(firstVisitor);

            // Esegui la seconda visita semantica
            SemanticVisitorSecondVisit secondVisitor = new SemanticVisitorSecondVisit(firstVisitor.getActiveSymbolTable());
            astRoot.accept(secondVisitor);

            // Genera il codice C
            CVisitor cVisitor = new CVisitor(firstVisitor.getFuncMap(), firstVisitor.getProcMap());
            astRoot.accept(cVisitor);

            // Ottieni il nome del file senza estensione
            Path path = Paths.get(inputFile);
            String fileName = path.getFileName().toString();
            String cFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".c";

            // Scrivi il codice C su file
            String outputDir = "test_files/c_out/";
            File outputFile = new File(outputDir + cFileName);
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                cVisitor.printToFile(writer.toString());
            }

            // Compila il file C
            ProcessBuilder compilerBuilder = new ProcessBuilder("gcc", "-o", fileName + ".exe", cFileName);
            compilerBuilder.directory(new File(outputDir));
            Process compilerProcess = compilerBuilder.start();

            // Gestisci l'output del compilatore
            try (BufferedReader compilerReader = new BufferedReader(new InputStreamReader(compilerProcess.getInputStream()))) {
                String line;
                while ((line = compilerReader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            // Esegui il file eseguibile generato
            ProcessBuilder execBuilder = new ProcessBuilder("./" + fileName + ".exe");
            execBuilder.directory(new File(outputDir));
            Process execProcess = execBuilder.start();

            // Gestisci l'output dell'esecuzione
            try (BufferedReader execReader = new BufferedReader(new InputStreamReader(execProcess.getInputStream()))) {
                String line;
                while ((line = execReader.readLine()) != null) {
                    System.out.println(line);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + inputFile);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error occurred while processing input/output");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred");
            e.printStackTrace();
        }
        /*try {
            Reader inFile = new FileReader(args[0]);

            Lexer lexer = new Lexer(inFile);

            Parser par = new Parser(lexer);
            ProgramOp astRoot = (ProgramOp) par.parse().value;


            /*XMLVisitor xmlGen = new XMLVisitor();
            astRoot.accept(xmlGen);*//*

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

            Path path = Paths.get(inputFile);
            String fileName = path.getFileName().toString();
            String cFileName = fileName.substring(0,fileName.lastIndexOf('.')) + ".c";
            String cFileNamePlusExtension = cFileName+".c";
            //System.out.println("test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension);
            cVisitor.printToFile("test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension);


            //ESECUZIONE DI GCC PER CREARE IL FILE OUTPUT.EXE A PARTIRE DAL FILE.C
            ProcessBuilder builder = new ProcessBuilder("gcc", "-o", cFileName+".exe", cFileNamePlusExtension);
            builder.directory(new File("test_files"+File.separator+"c_out"));
            builder.redirectErrorStream(true);
            Process p = builder.start();

            Scanner sc = new Scanner(p.getInputStream());
            if (sc.hasNextLine()) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    System.out.println(line);
                }
            }

            //ESECUZIONE E STAMPA DEL FILE OUTPUT.EXE
            ProcessBuilder processBuilder = new ProcessBuilder("test_files"+File.separator+"c_out"+File.separator+cFileName+".exe");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Stampa l'output nel terminale
            }

        } catch (Exception e) {
            System.out.println("aaaaaaaaaaaaaaa"+e);
        }*/
    }
}
