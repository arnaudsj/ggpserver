package de.tu_dresden.inf.ggp06_2.resolver.astvisitors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import javax.swing.BorderFactory;

import de.tu_dresden.inf.ggp06_2.resolver.AndOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.NotOperator;
import de.tu_dresden.inf.ggp06_2.resolver.OrOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

public class JGraphVisitor extends AbstractVisitor {
    
    enum Type {
        ATOM,
        VAR,
        OP,
        PRED,
        FUNC;
    }
    
    private final ArrayList<DefaultGraphCell> graphCells;

    private Stack<DefaultGraphCell> cellStack;
    private int x = 75;
    private int y = 75;
    private int xOffset = 75;
    private int yOffset = 75;
    private Set<Atom> fAtoms;
    
    public JGraphVisitor(Set<Atom> fAtoms) {
        cellStack   = new Stack<DefaultGraphCell>();
        graphCells  = new ArrayList<DefaultGraphCell>();        
        this.fAtoms = fAtoms;
    }

    public void createCell(String name, Type type) {
        
        Color cellColor;
        switch (type) {
            case ATOM:
                cellColor = Color.gray;
                break;
            case VAR:
                cellColor = Color.red;
                break;
            case OP:
                cellColor = Color.darkGray;
                break;
            case PRED:
                cellColor = Color.blue;
                break;
            case FUNC:
                cellColor = Color.green;
                break;
            default:
                cellColor = Color.black;                
        }

        // add new cell and set attributes
        DefaultGraphCell currentCell = new DefaultGraphCell(name);        
        AttributeMap     attribute   = currentCell.getAttributes();
        
        GraphConstants.setAutoSize   ( attribute, true);
        GraphConstants.setBackground ( attribute, cellColor.darker().darker() );
        GraphConstants.setBorder     ( attribute, 
                                       BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBorderColor( attribute, cellColor );
        GraphConstants.setBounds     ( attribute, 
                                       attribute.createRect(x, y, 10, 10) ); 
        GraphConstants.setForeground ( attribute, Color.white );
        GraphConstants.setGradientColor( attribute, 
                                         cellColor
                                             .brighter().brighter().brighter());
        GraphConstants.setInset      ( attribute, 3    );
        GraphConstants.setOpaque     ( attribute, true );

        currentCell.addPort();
        
        // connect cell to last cell
        if ( !cellStack.isEmpty() ) {
            DefaultEdge edge = new DefaultEdge();
            edge.setSource( cellStack.peek().getChildAt(0) );
            edge.setTarget( currentCell.getChildAt(0) );        
            graphCells.add(edge);
        }
        
        // last cell = current cell
        graphCells.add(currentCell);

        // push new cell to the stack
        cellStack.push(currentCell);
        y += yOffset;

    }
    
    @Override
    public final void visitAnd(AndOperator operator) {
        
        createCell( Const.aAndOp.toString(), Type.OP );
        
        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            x += xOffset;
        }

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitAtom(Atom atom) {
        
        createCell( atom.toString(), Type.ATOM );

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitDistinct(DistinctOperator operator) {
        
        createCell( Const.aDistinctOp.toString(), Type.OP );
        
        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            x += xOffset;
        }

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitExpressionList(ExpressionList expList) {

        for (Expression exp : expList) {
            exp.processVisitor(this);
            x      += xOffset;
        }
    }

    @Override
    public final void visitExpressionListTopLvl(ExpressionList expList) {
        
        for (Expression exp : expList) {
            exp.processVisitor(this);
            x += xOffset;
        }
    }

    @Override
    public final void visitImplication(Implication implication) {
        
        createCell( Const.aImpOp.toString(), Type.OP );

        implication.getConsequence().processVisitor(this);

        for ( Expression exp : implication.getPremises() )
            exp.processVisitor(this);

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitNot(NotOperator operator) {

        createCell( Const.aNotOp.toString(), Type.OP );

        operator.getOperand().processVisitor(this);

        cellStack.pop();
        y -= yOffset;

    }

    @Override
    public final void visitOr(OrOperator operator) {
        
        createCell( Const.aOrOp.toString(), Type.OP );

        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            x += xOffset;
        }

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitPredicate(Predicate predicate) {
        
        if (fAtoms.contains( predicate.getKeyAtom() ) )        
            createCell( "" + predicate.firstOperand(), Type.FUNC );
        else
            createCell( "" + predicate.firstOperand(), Type.PRED );

        predicate.getOperands().processVisitor(this);

        cellStack.pop();
        y -= yOffset;
    }

    @Override
    public final void visitVariable(Variable variable) {
        
        createCell( variable.toString(), Type.VAR );

        cellStack.pop();
        y -= yOffset;
    }
    
    public final DefaultGraphCell[] getGraphCells() {
        DefaultGraphCell[] cells = new DefaultGraphCell[graphCells.size()];
        for (int i = (graphCells.size() - 1); i > -1; i--)
            cells[i] = graphCells.get(i);
        return cells;
    }
    
    public final int getCount() {
        return graphCells.size();
    }
    
}
