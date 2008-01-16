package de.tu_dresden.inf.ggp06_2.resolver;

import java.util.List;

/**
 * 
 * @author Nick (initial author of JavaProver )
 * @author Ingo Keller - General Game Playing course student at TUD
 * @author Arsen Kostenko - General Game Playing course student at TUD
 *
 */
public abstract class Connective extends Expression {

	protected final ExpressionList operands;

    public Connective() {
        operands = new ExpressionList();
    }

    /**
     * There might be two places for variables in a
     * <code>Connective</code>: operator name,
     * and all the operands.
     */
    @Override
    public List<Variable> getVariables() {
        return operands.getVariables();
    }
    
    /**
     * @return Returns the operands.
     */
    public ExpressionList getOperands() {
        return operands;
    }
    
    /**
     * Check whether given variable occurs in current 
     * connective.
     * 
     * @param var Given variable
     * @return True or false ;)
     */
    @Override
    public boolean isPresent(Variable var) {
        return operands.isPresent(var);
    }

    /**
     * @return Returns name of the first operand available
     *         or null if none of them available.
     */
    @Override
    public Term secondOperand() {
        Term op2;
        for (Expression anExpression : operands) {
            op2 = anExpression.firstOperand();
            if (op2 != null) 
                return op2;
        }
        return null;
    }
   
    @Override
    public int getOperandCount() {
        return operands.size();
    }
    
}
