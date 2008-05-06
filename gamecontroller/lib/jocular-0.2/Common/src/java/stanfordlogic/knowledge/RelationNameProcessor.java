///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.knowledge;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.GroundFact;


/**
 * FactProcessor to take a relation and change its name to something else.
 *
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class RelationNameProcessor extends FactProcessor
{
    private int               relName_;
    
    /**
     * Create a processor that will rename relations to the value of 'relName'.
     * 
     * @param relName The relation name to set relations to.
     * @param symTable The symbol table to look up the string name in.
     */
    public RelationNameProcessor(String relName, SymbolTable symTable)
    {
        relName_ = symTable.get(relName);
    }
    
    /**
     * Create a processor that will rename relations to the value of 'relName'.
     * 
     * @param relName The relation name to set relations to.
     */
    public RelationNameProcessor(int relName)
    {
        relName_ = relName;
    }
    
    /**
     * Change the name of the relation 'fact'.
     * 
     * @param fact The relation to rename.
     */
    @Override
    public GroundFact processFact(GroundFact fact)
    {
        return fact.clone(relName_);
    }
}
