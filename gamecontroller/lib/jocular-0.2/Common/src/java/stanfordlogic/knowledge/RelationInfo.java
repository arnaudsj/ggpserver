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
package stanfordlogic.knowledge;

import stanfordlogic.game.GameManager;

/**
 * Contains information about a relation.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class RelationInfo implements Comparable<RelationInfo>
{
    final private int name_;
    final private int arity_;
    
    public RelationInfo(int name, int arity)
    {
        name_ = name;
        arity_ = arity;
    }
    
    /**
     * @return Returns the arity of this relation.
     */
    public int getArity()
    {
        return arity_;
    }

    /**
     * @return Returns the name of this relation.
     */
    public int getName()
    {
        return name_;
    }

    public int compareTo( RelationInfo o )
    {
        return Integer.signum(name_ - o.name_);
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj instanceof RelationInfo == false )
            return false;
        
        return name_ == ((RelationInfo) obj).name_;
    }

    @Override
    public String toString()
    {
        return GameManager.getSymbolTable().get(name_);
    }
    
}
