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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import stanfordlogic.prover.GroundFact;
import stanfordlogic.prover.Implication;
import stanfordlogic.prover.TermObject;


/**
 * Information we've extracted from the game description regarding structural,
 * syntactical and other features of a game.
 * 
 * <p>Note that the <tt>init</tt> facts are <i>not</i> in the static facts.
 * 
 * @author Based on code by Team Camembert: David Haley, Pierre-Yves Laligand
 */
public class GameInformation
{
    private Map<Integer, RelationInfo>              relations_;
    private Set<Integer>                            functionSymbols_;
    private Set<Integer>                            objectSymbols_;
    
    /** Rules used by the game. Indexed by rule head. */
    private Map<Integer, List<Implication>>                rules_;
    
    /**
     * Ground facts that were extracted during GDL examination. (This includes
     * static and init facts.)
     */
    private Map<Integer, List<GroundFact>>          groundFacts_;
    
    private Map<Integer, List<GroundFact>>          staticFacts_;
    
    private List<GroundFact>                        initFacts;
    
    private List<TermObject>                        roles_;
    
    private int                                     stepCounter_;
    private boolean                                 goalDependsOnStep_;
    private int                                     maxLength_;
    
    public GameInformation()
    {
        // Not much to do here for now.
    }
    
    /**
     * @return Returns the game's function symbols.
     */
    public Set<Integer> getFunctionSymbols()
    {
        return functionSymbols_;
    }

    /**
     * @param functionSymbols The set of function symbols used by the game.
     */
    public void setFunctionSymbols( Set<Integer> functionSymbols )
    {
        functionSymbols_ = functionSymbols;
    }

    /**
     * @return Returns the game's ground facts.
     */
    public Map<Integer, List<GroundFact>> getGroundFacts()
    {
        return groundFacts_;
    }
    
    public List<GroundFact> getAllGrounds()
    {
        List<GroundFact> grounds = new ArrayList<GroundFact>();
        
        for ( RelationInfo r : relations_.values() )
            grounds.addAll(groundFacts_.get(r.getName()));
        
        return grounds;
    }
    
    /**
     * @return A list of lists of grounds, such that all grounds of a given
     *         relation name appear in the same sub-list.
     */
    public List<List<GroundFact>> getListedGrounds()
    {
        List<List<GroundFact>> result = new ArrayList<List<GroundFact>>(groundFacts_.size());
        
        for ( List<GroundFact> list : groundFacts_.values() )
            result.add( list);
        
        return result;
    }
    
    public List<List<GroundFact>> getAllStaticGrounds()
    {
        List<List<GroundFact>> grounds = new ArrayList<List<GroundFact>>();
        
        for(List<GroundFact> list : staticFacts_.values())
            grounds.add(list);
        
        return grounds;
    }

    /**
     * @return The list of 'init' ground facts.
     */
    public List<GroundFact> getInitFacts()
    {
        return initFacts;
    }
    
    /**
     * @param groundFacts The game's ground facts.
     */
    public void setGroundFacts( Map<Integer, List<GroundFact>> groundFacts )
    {
        groundFacts_ = groundFacts;
    }

    /**
     * @param staticRelations The game's static relations.
     */
    public void setStaticFacts(Map<Integer, List<GroundFact>> staticRelations)
    {
        staticFacts_ = staticRelations;
    }
    
    
    
    public void setInitFacts(List<GroundFact> initRelation)
    {
        initFacts = initRelation;
    }
    
    /**
     * @return Returns the game's object symbols.
     */
    public Set<Integer> getObjectSymbols()
    {
        return objectSymbols_;
    }

    /**
     * @param objectSymbols The set of object symbols used by the game.
     */
    public void setObjectSymbols( Set<Integer> objectSymbols )
    {
        objectSymbols_ = objectSymbols;
    }

    /**
     * @return Returns the relations, indexed by relation name.
     */
    public Map<Integer, RelationInfo> getIndexedRelations()
    {
        return relations_;
    }
    
    public Collection<RelationInfo> getRelations()
    {
        return relations_.values();
    }

    /**
     * @param relations The relations to set.
     */
    public void setRelations( Map<Integer, RelationInfo> relations )
    {
        relations_ = relations;
    }
    
    
    public int getRoleIndex(TermObject role)
    {
        for ( int i = 0; i < roles_.size(); i++ )
        {
            if ( roles_.get(i) == role )
                return i;
        }
        
        throw new IllegalArgumentException("No such role: " + role);
    }

    /**
     * @return Returns the roles.
     */
    public List<TermObject> getRoles()
    {
        return roles_;
    }

    /**
     * @param roles The roles to set.
     */
    public void setRoles( List<TermObject> roles )
    {
        roles_ = roles;
    }

    /**
     * @return Returns the game's rules, indexed by rule head relation.
     */
    public Map<Integer, List<Implication>> getIndexedRules()
    {
        return rules_;
    }
    
    public List<Implication> getRules()
    {
        List<Implication> allRules = new ArrayList<Implication>();
        
        for (List<Implication> r : rules_.values())
            allRules.addAll(r);
        
        return allRules;
    }

    /**
     * @param rules The rules used by the game.
     */
    public void setRules( Map<Integer, List<Implication>> rules )
    {
        rules_ = rules;
    }
    
    
    /**
     * @return Returns the token of the step counter relation, or 0 if none was
     *         detected.
     */
    public int getStepCounter()
    {
        return stepCounter_;
    }
    
    public boolean hasStepCounter()
    {
        return stepCounter_ != 0;
    }

    /**
     * @param stepCounter The step counter's token number to set.
     */
    public void setStepCounter( int stepCounter )
    {
        stepCounter_ = stepCounter;
    }
    
        public boolean hasMaxLength()
    {
        return maxLength_ != 0;
    }
    
    /**
     * @return Returns true if the goal depends on the step counter.
     */
    public boolean goalDependsOnStep()
    {
        return goalDependsOnStep_;
    }

    /**
     * @param goalDependsOnStep
     *            True if the goal depends on the step counter.
     */
    public void setGoalDependsOnStep( boolean goalDependsOnStep )
    {
        goalDependsOnStep_ = goalDependsOnStep;
    }

    /**
     * @return Returns the maximum length of the game. (0 = no max length found)
     */
    public int getMaxLength()
    {
        return maxLength_;
    }

    /**
     * @param maxLength The maximum length to set.
     */
    public void setMaxLength( int maxLength )
    {
        maxLength_ = maxLength;
    }

    public boolean isRelationSymbol(int symbol)
    {
        return relations_.get( symbol ) != null;
    }
    public boolean isFunctionSymbol(int symbol)
    {
        return functionSymbols_.contains(symbol);
    }
    public boolean isObjectSymbol(int symbol)
    {
        return objectSymbols_.contains(symbol);
    }
    
    
    /**
     * Check if a relation is fully defined; that is, if it is defined
     * without any rules.
     * 
     * @param relation The relation to check.
     * @return True if there are no rules defining this relation.
     */
    public boolean isFullyDefined(int relation)
    {
        return rules_.containsKey(relation) == false;
    }
    
}
