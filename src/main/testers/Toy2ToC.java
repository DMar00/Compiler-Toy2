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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Toy2ToC {
    public static void main(String[] args) throws Exception{
        Reader inFile = new FileReader(args[0]);
        Lexer lexer = new Lexer(inFile);

        Parser par = new Parser(lexer);
        ProgramOp astRoot = (ProgramOp) par.parse().value;

        /*XMLVisitor xmlGen = new XMLVisitor();
            astRoot.accept(xmlGen);*/

        SemanticVisitorFirstVisit scopingVisitor = null;
        SemanticVisitorSecondVisit scopingVisitor2 = null;

        //1° visita
        /*SemanticVisitorFirstVisit */
        scopingVisitor = new SemanticVisitorFirstVisit();
        astRoot.accept(scopingVisitor);

        //2° visita
        /*SemanticVisitorSecondVisit*/
        scopingVisitor2 = new SemanticVisitorSecondVisit(scopingVisitor.getActiveSymbolTable());
        astRoot.accept(scopingVisitor2);

        //Visitor C
        CVisitor cVisitor = new CVisitor(scopingVisitor.getFuncMap(), scopingVisitor.getProcMap());
        astRoot.accept(cVisitor);

        Path percorso = Paths.get(args[0]);
        String fileName = percorso.getFileName().toString();
        String fileNameNoExt = fileName.substring(0, fileName.length() - 4);
        String cFileName = fileNameNoExt + ".c";
        String c_out_dir = "test_files" + File.separator + "c_out";
        Path cFilePath = Path.of(c_out_dir, cFileName);

        try{
            Files.createDirectories(Path.of(c_out_dir));
            Files.writeString(cFilePath, cVisitor.getResultProgram());
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            String exeDir = "test_files" + File.separator + "c_exe";
            Files.createDirectories(Path.of(exeDir));
            String exeFileName =  fileName + "Exe";
            Path exeFilePath = Path.of(exeDir, exeFileName);
            String compileLine = "gcc "+cFilePath+" -o " + exeFilePath;
            Process prc = Runtime.getRuntime().exec(compileLine);
            int exitCode = prc.waitFor();
        }catch(IOException | InterruptedException e){
            //e.getMessage();
            System.err.println(e.getMessage());
        }

    }
}
