package main.testers;

import main.output_cup.Parser;
import main.output_jflex.Lexer;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.visitor.CVisitor;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorFirstVisit;
import main.syntaxtree.visitor.semanticVisitor.SemanticVisitorSecondVisit;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Toy2ToC {
    public static void main(String[] args) throws IOException, TransformerConfigurationException, ParserConfigurationException {
        try {
            Reader inFile = new FileReader(args[0]);
            Lexer lexer = new Lexer(inFile);

            Parser par = new Parser(lexer);
            ProgramOp astRoot = (ProgramOp) par.parse().value;


            /*XMLVisitor xmlGen = new XMLVisitor();
            astRoot.accept(xmlGen);*/

            SemanticVisitorFirstVisit scopingVisitor = null;
            SemanticVisitorSecondVisit scopingVisitor2 = null;

            //1° visita
            /*SemanticVisitorFirstVisit */scopingVisitor = new SemanticVisitorFirstVisit();
            astRoot.accept(scopingVisitor);

            //2° visita
            /*SemanticVisitorSecondVisit*/ scopingVisitor2 = new SemanticVisitorSecondVisit(scopingVisitor.getActiveSymbolTable());
            astRoot.accept(scopingVisitor2);

            //Visitor C
            CVisitor cVisitor = new CVisitor(scopingVisitor.getFuncMap(), scopingVisitor.getProcMap());
            astRoot.accept(cVisitor);

            Path percorso = Paths.get(args[0]);
            String fileName = percorso.getFileName().toString();
            String cFileName = fileName.substring(0,fileName.length()-4) ;
            String cFileNamePlusExtension = cFileName+"_out.c";
            //System.out.println("test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension);
            cVisitor.printToFile("test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension);

            //ESECUZIONE DI GCC PER CREARE IL FILE OUTPUT.EXE A PARTIRE DAL FILE.C
            ProcessBuilder builder = new ProcessBuilder("gcc", "-o", cFileName+"_out.exe", cFileNamePlusExtension);
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

            /*//ESECUZIONE E STAMPA DEL FILE OUTPUT.EXE
            ProcessBuilder processBuilder = new ProcessBuilder("c_out"+File.separator+cFileName+".exe");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Stampa l'output nel terminale
            }*/

        } catch (Exception e) {
            //System.err.println(e);
            e.printStackTrace();
            Path percorso = Paths.get(args[0]);
            String fileName = percorso.getFileName().toString();
            String cFileName = fileName.substring(0,fileName.length()-4) ;
            String cFileNamePlusExtension = cFileName+"_out.c";
            String path = "test_files"+File.separator+"c_out"+File.separator+cFileNamePlusExtension;
            // Crea un oggetto File
            File file = new File(path);

            // Crea un oggetto FileWriter per scrivere nel file
            FileWriter fileWriter = new FileWriter(file);

            // Crea un oggetto BufferedWriter per scrivere in modo efficiente nel file
            BufferedWriter writer = new BufferedWriter(fileWriter);

            // Scrive il testo dell'eccezione nel file
            writer.write(createFileException(e.getMessage()));

            // Chiude il writer
            writer.close();
        }
    }

    private static String createFileException(String message) {
        return "#include <stdio.h>\n int main() {\n printf(\""+message+"\");\nreturn 0;\n}";
    }
}
