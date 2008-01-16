package de.tu_dresden.inf.ggp06_2.resolver.structures.functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;
import de.tu_dresden.inf.ggp06_2.resolver.Substitution;

/**
 * The Function class is only a container class without a sophisticated 
 * interface.
 * 
 * <ul>
 *   <li>String      symbol - function symbol</li>
 *   <li>int         arity  - arity of function</li>
 *   <li>Set<String> domain - contains all object constants within this function
 *   </li>
 * </ul>
 * 
 * @author ingo
 *
 */
public class Function {
    
    private int id;
    
    public final String      symbol;
    public final Atom        atom;
    public final int         arity;
    public final Set<Atom>   domain = new HashSet<Atom>();
    
    public final List<Set<Atom>> posDomains;
    
    public Function(String s, int a) {
        symbol      = s;
        atom        = new Atom(s);
        arity       = a;
        posDomains  = new ArrayList<Set<Atom>>();
        for (int i = 0; i < a; i++)
            posDomains.add( new HashSet<Atom>() );
    }

    public boolean isStructure() {
        return false;
    }
    
    public List<Substitution> chain(Substitution sigma, ExpressionList expList) {
        return null;
    }

    public Substitution chainOne(Substitution sigma, ExpressionList expList) {
        return null;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

}
