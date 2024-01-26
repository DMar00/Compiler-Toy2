package main.syntaxtree.visitor;

import main.syntaxtree.enums.IOMode;
import main.syntaxtree.enums.Mode;
import main.syntaxtree.enums.Type;
import main.syntaxtree.nodes.BodyOp;
import main.syntaxtree.nodes.ProcFunParamOp;
import main.syntaxtree.nodes.ProgramOp;
import main.syntaxtree.nodes.expr.*;
import main.syntaxtree.nodes.expr.binExpr.*;
import main.syntaxtree.nodes.expr.constNodes.*;
import main.syntaxtree.nodes.expr.unExpr.MinusOp;
import main.syntaxtree.nodes.expr.unExpr.NotOp;
import main.syntaxtree.nodes.iter.FunDeclOp;
import main.syntaxtree.nodes.iter.IterOp;
import main.syntaxtree.nodes.iter.ProcOp;
import main.syntaxtree.nodes.iter.VarDeclOp;
import main.syntaxtree.nodes.stat.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Map;

public class XMLVisitor implements Visitor{
    private Document document;

    public XMLVisitor() throws ParserConfigurationException {
        this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    public void printToFile(String path) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(new File(path)));
    }

    private Element getTypeTag(Type t) {
        Element typeOpTag = document.createElement("TypeOp");
        if(t != null)
            typeOpTag.appendChild(document.createTextNode(t.toString()));
        else
            typeOpTag.appendChild(document.createTextNode(" null"));
        return typeOpTag;
    }

    private Element getModeTag(Mode m) {
        Element modeOpTag = document.createElement("ModeOp");
        modeOpTag.appendChild(document.createTextNode(m.toString()));
        return modeOpTag;
    }
    private Element getIOModeTag(IOMode m) {
        Element modeOpTag = document.createElement("IOModeOp");
        modeOpTag.appendChild(document.createTextNode(m.toString()));
        return modeOpTag;
    }

    private Node createNullNode(){
        return document.createTextNode(" (null)");
    }

    private Object createBinaryOpTag(BinaryExpr binaryExpr) {
        Element binaryOpTag = document.createElement(binaryExpr.name);
        binaryOpTag.appendChild((Node) binaryExpr.leftNode.accept(this));
        binaryOpTag.appendChild((Node) binaryExpr.rightNode.accept(this));

        return binaryOpTag;
    }

    /****************************************************************************/
    @Override
    public Object visit(ProgramOp programOp) {
        Element programOpTag = document.createElement(programOp.name);

        if(programOp.itersList!=null){
            for(IterOp i : programOp.itersList){
                //visita tutti i figli che aggiungi a programOP
                programOpTag.appendChild( (Node) i.accept(this) );
            }
        }

        document.appendChild(programOpTag);

        return programOpTag;
    }

    @Override
    public Object visit(VarDeclOp varDeclOp) {
        Element varDeclOpTag = document.createElement(varDeclOp.name);
        //type
        varDeclOpTag.appendChild(getTypeTag(varDeclOp.type));
        //hashmap<Id, Const>    n1, 3       n2,6        n3,90
        for (Map.Entry<Id, ConstNode> entry : varDeclOp.ids.entrySet()) {
            Id idName = entry.getKey();
            ConstNode value = entry.getValue();
            //TODO padre fatto bene ????
            Element node = document.createElement("Var");
            node.appendChild((Node) idName.accept(this));
            if (value!=null)
                node.appendChild((Node) value.accept(this));
            else
                node.appendChild(createNullNode());

            varDeclOpTag.appendChild(node);
        }
        return varDeclOpTag;
    }

    @Override
    public Object visit(IntConstNode constNode) {
        //if(constNode == null)
        return document.createTextNode(" (" + constNode.name + ", " + constNode.value + ")");
    }

    @Override
    public Object visit(RealConstNode constNode) {
        return document.createTextNode(" (" + constNode.name + ", " + constNode.value + ")");
    }

    @Override
    public Object visit(StringConstNode constNode) {
        return document.createTextNode(" (" + constNode.name + ", " + constNode.value + ")");
    }

    @Override
    public Object visit(BoolConstNode constNode) {
        return document.createTextNode(" (" + constNode.name + ", " + constNode.value + ")");
    }

    @Override
    public Object visit(ProcOp procOp) {
        Element procOpTag = document.createElement(procOp.name);
        //Id procedure
        procOpTag.appendChild((Node) procOp.procName.accept(this));
        //BodyOP
        procOpTag.appendChild((Node) procOp.procBody.accept(this));
        //List procedureParamList
        if(procOp.procParamsList!= null){
            for(ProcFunParamOp param : procOp.procParamsList){
                procOpTag.appendChild((Node) param.accept(this));
            }
        }
        return procOpTag;
    }

    @Override
    public Object visit(FunDeclOp funDeclOp) {
        Element funDeclOpTag = document.createElement(funDeclOp.name);
        //Id functionName
        funDeclOpTag.appendChild((Node) funDeclOp.functionName.accept(this));
        //BodyOP
        funDeclOpTag.appendChild((Node) funDeclOp.functionBody.accept(this));
        //List functionParamList
        for(ProcFunParamOp param : funDeclOp.functionParamList){
            funDeclOpTag.appendChild((Node) param.accept(this));
        }
        //List functionReturTypeList
        for(Type typeReturn : funDeclOp.functionReturTypeList){
            if(typeReturn != null)
                funDeclOpTag.appendChild(getTypeTag(typeReturn));
        }
        return funDeclOpTag;
    }

    @Override
    public Object visit(Id id) {
        return document.createTextNode("(id, "+id.idName+") ");
    }

    @Override
    public Object visit(ProcFunParamOp procFunParamOp) {
        Element procFunParamOpTag = document.createElement(procFunParamOp.name);

        //Id
        procFunParamOpTag.appendChild((Node) procFunParamOp.id.accept(this));
        //Type
        procFunParamOpTag.appendChild(getTypeTag(procFunParamOp.type));
        //Mode
        procFunParamOpTag.appendChild(getModeTag(procFunParamOp.mode));

        return procFunParamOpTag;
    }

    @Override
    public Object visit(BodyOp bodyOp) {
        Element bodyOpTag = document.createElement(bodyOp.name);
        //List VarDeclOp
        for(VarDeclOp var : bodyOp.varDeclOpList){
            bodyOpTag.appendChild((Node) var.accept(this));
        }
        //List Stat
        for(Stat stat : bodyOp.statList){
            bodyOpTag.appendChild((Node) stat.accept(this));
        }
        return  bodyOpTag;
    }

    @Override
    public Object visit(AssignOp assignOp) {
        Element assignOpTag = document.createElement(assignOp.name);
        //List Id
        for(Id id : assignOp.idList){
            assignOpTag.appendChild((Node) id.accept(this));
        }
        //List expr
        for(Expr e : assignOp.exprList){
            assignOpTag.appendChild((Node) e.accept(this));
        }
        return  assignOpTag;
    }

    @Override
    public Object visit(MinusOp minusOp) {
        Element tag = document.createElement(minusOp.name);
        tag.appendChild((Node) minusOp.rightNode.accept(this));
        return tag;
    }

    @Override
    public Object visit(NotOp notOp) {
        Element tag = document.createElement(notOp.name);
        tag.appendChild((Node) notOp.rightNode.accept(this));
        return tag;
    }

    @Override
    public Object visit(ProcExpr procExpr) {
        Element tag = document.createElement(procExpr.name);
        //Expr
        tag.appendChild((Node) procExpr.expr.accept(this));
        //Boolean
        if(procExpr.procMode)
            tag.appendChild(document.createTextNode("rif"));
        else
            tag.appendChild(document.createTextNode("val"));
        return tag;
    }

    @Override
    public Object visit(FunCallOp funCallOp) {
        Element tag = document.createElement(funCallOp.name);
        //Id
        tag.appendChild((Node) funCallOp.funName.accept(this));
        //List Expr
        for(Expr e : funCallOp.exprList){
            tag.appendChild((Node) e.accept(this));
        }
        return tag;
    }

    @Override
    public Object visit(AddOp addOp) {
        return createBinaryOpTag(addOp);
    }

    @Override
    public Object visit(AndOp andOp) {
        return createBinaryOpTag(andOp);    }

    @Override
    public Object visit(DiffOp diffOp) {
        return createBinaryOpTag(diffOp);    }

    @Override
    public Object visit(DivOp divOp) {
        return createBinaryOpTag(divOp);    }

    @Override
    public Object visit(EqOp eqOp) {
        return createBinaryOpTag(eqOp);    }

    @Override
    public Object visit(GeOp geOp) {
        return createBinaryOpTag(geOp);    }

    @Override
    public Object visit(GtOp gtOp) {
        return createBinaryOpTag(gtOp);    }

    @Override
    public Object visit(LeOp leOp) {
        return createBinaryOpTag(leOp);    }

    @Override
    public Object visit(LtOp ltOp) {
        return createBinaryOpTag(ltOp);    }

    @Override
    public Object visit(MulOp mulOp) {
        return createBinaryOpTag(mulOp);    }

    @Override
    public Object visit(NeOp neOp) {
        return createBinaryOpTag(neOp);    }

    @Override
    public Object visit(OrOp orOp) {
        return createBinaryOpTag(orOp);    }

    @Override
    public Object visit(ElifOp elifOp) {
        Element tag = document.createElement(elifOp.name);
        //Expr
        tag.appendChild((Node) elifOp.expr.accept(this));
        //BodyOp
        tag.appendChild((Node) elifOp.bodyOp.accept(this));
        return tag;
    }

    @Override
    public Object visit(IfOp ifOp) {
        Element tag = document.createElement(ifOp.name);
        //Expr
        tag.appendChild((Node) ifOp.expr.accept(this));
        //Body if
        tag.appendChild((Node) ifOp.ifBody.accept(this));
        //Else body
        tag.appendChild((Node) ifOp.elseBody.accept(this));
        //List elif
        for(ElifOp elifOp : ifOp.elifs)
            tag.appendChild((Node) elifOp.accept(this));
        return tag;
    }

    @Override
    public Object visit(ElseOp elseOp) {
        Element tag = document.createElement(elseOp.name);
        //body
        tag.appendChild((Node) elseOp.elseBody.accept(this));
        //
        return tag;
    }


    @Override
    public Object visit(IOArgsOp ioArgsOp) {
        Element tag = document.createElement(ioArgsOp.name);
        //IO mode
        tag.appendChild(getIOModeTag(ioArgsOp.mode));

        //List expr
        for(IOArgsOp.IoExpr e : ioArgsOp.exprList){
            tag.appendChild((Node) e.expression().accept(this));
        }

        return tag;
    }

    //
    @Override
    public Object visit(ConstNode constNode) {
        return null;
    }

    @Override
    public Object visit(ProcCallOp procCallOp) {
        Element tag = document.createElement(procCallOp.name);
        //Id
        tag.appendChild((Node) procCallOp.procName.accept(this));
        //List procExpr
        if(procCallOp.exprList!=null){
            for(ProcExpr e : procCallOp.exprList)
                tag.appendChild((Node) e.accept(this));
        }
        return tag;
    }

    @Override
    public Object visit(ReturnOp returnOp) {
        Element tag = document.createElement(returnOp.name);
        //Expr list
        for(Expr e : returnOp.exprList)
            tag.appendChild((Node) e.accept(this));
        return tag;
    }

    @Override
    public Object visit(WhileOp whileOp) {
        Element tag = document.createElement(whileOp.name);
        //Expr
        tag.appendChild((Node) whileOp.whileExpr.accept(this));
        //Do body
        tag.appendChild((Node) whileOp.doBody.accept(this));
        return tag;
    }


}
