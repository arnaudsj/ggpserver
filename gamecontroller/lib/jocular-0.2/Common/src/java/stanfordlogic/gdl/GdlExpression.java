///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

/**
 * 
 */
package stanfordlogic.gdl;

/**
 * An expression in GDL. Expressions can be either atoms, variables or lists;
 * see the approriate subclasses.
 * 
 * @see stanfordlogic.gdl.GdlAtom
 * @see stanfordlogic.gdl.GdlVariable
 * @see stanfordlogic.gdl.GdlList
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public abstract class GdlExpression
{
	/** Symbol table context for this expression. */
	final SymbolTable symbolTable_;
	
	protected GdlExpression(SymbolTable symTab)
	{
		symbolTable_ = symTab;
	}

    @Override
    public abstract String toString();
}
