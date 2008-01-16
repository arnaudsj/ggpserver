package de.tu_dresden.inf.ggp06_2.resolver.astvisitors;

import de.tu_dresden.inf.ggp06_2.resolver.AndOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.NotOperator;
import de.tu_dresden.inf.ggp06_2.resolver.OrOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;

public class GDLVisitor extends AbstractVisitor {

    private final StringBuilder sb;
    
    public GDLVisitor() {
        sb = new StringBuilder();
    }
    
    @Override
    public final void visitAnd(AndOperator operator) {
        sb.append("(and ");
        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            sb.append(" ");
        }
        sb.append(")");
    }

    @Override
    public final void visitAtom(Atom atom) {
        sb.append( atom.toString().toLowerCase() );
    }

    @Override
    public final void visitDistinct(DistinctOperator operator) {
        sb.append("(distinct ");
        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            sb.append(' ');
        }
        sb.deleteCharAt(sb.length()-1).append(')');
    }

    @Override
    public final void visitExpressionList(ExpressionList expList) {
        for (Expression exp : expList) {
            exp.processVisitor(this);
            sb.append(' ');
        }
    }

    @Override
    public final void visitExpressionListTopLvl(ExpressionList expList) {
        for (Expression exp : expList) {
            exp.processVisitor(this);
            sb.append('\n');
        }
    }

    @Override
    public final void visitImplication(Implication implication) {
        sb.append("(<= ");
        implication.getConsequence().processVisitor(this);
        sb.append('\n');
        final ExpressionList tmpExpList = implication.getPremises();
        for (Expression exp : tmpExpList) {
            sb.append('\t');
            exp.processVisitor(this);
            sb.append('\n');
        }
        sb.deleteCharAt(sb.length()-1).append(')');
    }

    @Override
    public final void visitNot(NotOperator operator) {
        sb.append("(not ");
        operator.getOperand().processVisitor(this);
        sb.append(')');
    }

    @Override
    public final void visitOr(OrOperator operator) {
        sb.append("(or ");
        final ExpressionList tmpExpList = operator.getOperands();
        for (Expression exp : tmpExpList) {
            exp.processVisitor(this);
            sb.append(' ');
        }
        sb.deleteCharAt(sb.length()-1).append(')');
    }

    @Override
    public final void visitPredicate(Predicate predicate) {
        sb.append('(');
        predicate.getOperator().processVisitor(this);
        sb.append(' ');
        predicate.getOperands().processVisitor(this);
        sb.append(')');
    }

    @Override
    public final void visitVariable(Variable variable) {
        sb.append(variable.toString());
    }

    @Override
    public final String toString() {
        return sb.toString();
    }
    
}
