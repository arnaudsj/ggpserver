package de.tu_dresden.inf.ggp06_2.resolver.astvisitors;

import de.tu_dresden.inf.ggp06_2.resolver.AndOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.DistinctOperator;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Implication;
import de.tu_dresden.inf.ggp06_2.resolver.NotOperator;
import de.tu_dresden.inf.ggp06_2.resolver.OrOperator;
import de.tu_dresden.inf.ggp06_2.resolver.Predicate;
import de.tu_dresden.inf.ggp06_2.resolver.Variable;

public abstract class AbstractVisitor {
    
    public abstract void visitExpressionListTopLvl( ExpressionList expList   );
    
    public abstract void visitExpressionList ( ExpressionList   expList     );
    public abstract void visitAtom           ( Atom             atom        );
    public abstract void visitVariable       ( Variable         variable    );
    public abstract void visitAnd            ( AndOperator      operator    );
    public abstract void visitOr             ( OrOperator       operator    );
    public abstract void visitNot            ( NotOperator      operator    );
    public abstract void visitDistinct       ( DistinctOperator operator    );
    public abstract void visitPredicate      ( Predicate        predicate   );
    public abstract void visitImplication    ( Implication      implication );

}
