package de.tu_dresden.inf.ggp06_2.resolver.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.tu_dresden.inf.ggp06_2.resolver.Const;
import de.tu_dresden.inf.ggp06_2.resolver.Expression;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;

public class ResolutionMemorizer {
    
    Map<Expression, List<Substitution>> proven;
    Set<Expression> disproven;
    
    public ResolutionMemorizer(){
        this.proven = new HashMap<Expression, List<Substitution>>();
        this.disproven = new HashSet<Expression>();
    }
    
    public ResolutionMemorizer(
            Map<Expression, List<Substitution>> provenTrans, 
            Set<Expression>                      disprovenTrans ) {

        this.proven    = provenTrans;
        this.disproven = disprovenTrans;
    }

    public List<Substitution> getProven( Substitution sigma, Expression e ) {

        Substitution canon = e.deriveCanon();
        Expression canonical = e.apply( canon );
        
        if (!proven.containsKey( canonical )) 
            return Const.emptySubstitutionList;
        
        List<Substitution> proovedSubs = proven.get(canonical);
                
        List<Substitution> returned = new ArrayList<Substitution>();
        if (proovedSubs == null) {
            returned.add( sigma );
            return returned;
        }        
        for (Substitution aProof : proovedSubs ) {
            Substitution psi = canon.apply( aProof );
            returned.add( sigma.apply( psi.restrict( e ) ) );
        }
        return returned;
    }
    
    public boolean isDisproven(Expression expression) {
        return this.disproven.contains( expression );
    }    
    
    public boolean isProven(Expression e){
        return proven.containsKey( e.canonize() );
    }

    public void setDisproven(Expression expression) {
        this.disproven.add( expression );
    }
    
    public void setProven(Expression e, List<Substitution> subs){
        ///*
        // Cut this to only the portion
        // necessary to ground e and match it with its
        // proof
        Substitution canon = e.deriveCanon();
        Expression canonical = e.apply(canon);
        
        List<Substitution> expressionRestictedSubs = new ArrayList<Substitution>();
        for ( Substitution psi : subs ) {
            Substitution sigma = psi.restrict(e);
            sigma = sigma.canonize(canon);
            
            if (!expressionRestictedSubs.contains(sigma)) 
                expressionRestictedSubs.add(sigma);
        }
        
        proven.put( canonical, expressionRestictedSubs );
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("proven:\n");
        for (Expression key : proven.keySet()){
            sb.append( key ).append( " : " );
            
            for (Substitution psi : proven.get( key ))
                sb.append( psi ).append( " ;" );
            
            sb.append( "\n" );
        }
        
        sb.append( "disproven:\n" );
        for(Expression key : disproven)
            sb.append( key ).append( "\n" );
        
        return sb.toString();
    }
}
