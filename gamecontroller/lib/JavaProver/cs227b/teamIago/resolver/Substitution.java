/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.resolver;
import java.util.ArrayList;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Substitution {
	protected ArrayList associations;
	
	/**
	 * 
	 */
	public Substitution() {
		associations = new ArrayList();
	}
	
	public Substitution (Substitution copy)
	{
		associations = new ArrayList();
		for (int i = 0; i < copy.associations.size(); i++)
		{
			associations.add(copy.associations.get(i));
		}
	}
	
	public Substitution (Substitution copy, Variable v, Expression sub)
	{
		this(copy);
		Association a = new Association(v,sub);
		associations.add(a);
	}
	
	public boolean assigns(Variable v)
	{
		return (maps(v) != null);
	}
	
	public Expression maps(Variable v)
	{
		for (int i = 0; i < associations.size(); i++)
		{
			Association a = (Association) associations.get(i);
			if (a.assigns(v)) return a.getSub();
		}
		return null;
	}
	
	public void addAssocNoIdentCheck(Variable v, Expression sub) {
		Expression exp = maps(v);
		if (exp == null) 
		{
			Association a = new Association(v,sub);
			associations.add(a);
			return;
		}
		else if (exp.equals(sub)) return;
		else
		{
			/*
			    System.err.println("No match: variable reassigned in substitution \"" 
					+ v.toString() + " -> " + sub.toString() + "\"");
			*/
			return;
		}		
	}
	
	public void addAssoc(Variable v, Expression sub) {
		if (v.equals(sub)) return;
		addAssocNoIdentCheck(v,sub);
	}
	
	public boolean refines(Substitution other)
	{
		// return associations.containsAll(other.associations);
		// This was a vast oversimplification.
		// For instance, {X->a,Y->b} should be a
		// refinement of {X->a, Y->W}, but by the logic
		// above, since it isn't {X->a, Y->W, W->b} then
		// the second isn't a refinement of the first.

		
		for (int i = 0; i < other.associations.size(); ++i) {
			Association vSub = (Association)other.associations.get(i);
			Variable v = vSub.getVar();
			Variable v2 = vSub.getVar();
			Expression vA, vB;
			
			while (true) {
				vA = maps(v);
				if (vA == null) {
					vA = v;
					break;
				} else if (vA instanceof Variable) v = (Variable) vA;
				else break;
			}
			while (true) {
				vB = other.maps(v);
				if (vB == null) {
					vB = v;
					break;
				} else if (vB instanceof Variable) v = (Variable) vB;
				else break;
			}
			if (vB instanceof Variable) continue;
			else if (!vB.equals(vA)) return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object match) {
		if (! (match instanceof Substitution)) return false;
		Substitution other = (Substitution) match;
		if (!refines(other)) return false;
		if (!other.refines(this)) return false;
		return true;
	}
	
	public boolean empty() {
		return (associations.size() == 0);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		String s = new String();

		s = s + "[ ";
		for (int i = 0; i < associations.size(); i++)
		{
			s += associations.get(i).toString();
			if ( i < associations.size() - 1) s += ", ";
		}
		s += " ]";
		return s;
	}
	
	// Apply the substitution to itself repeatedly
	// until every mapping maps out of the domain of
	// the substitution
	public Substitution factor() {
		Substitution psi = new Substitution();
		for (int i = 0; i < associations.size(); ++i) {
			Association vSub = (Association)associations.get(i);
			Variable v;
			Expression mapNew, mapOld;;
			mapNew = mapOld = v = vSub.getVar();
			
			while (true) {
				mapNew = mapOld.apply(this);
				if (mapNew == null) {
					mapNew = mapOld;
					break;
				} 
				else if (mapNew.equals(mapOld)) break;
				else mapOld = mapNew;
			}
			psi.addAssoc(v,mapNew);
		}
		return psi;
	}
	
	// Restict a substitution to only the variables
	// occurring in expression e
	public Substitution restrict(Expression e) {
		Substitution psi = factor();
		ExpList domain = e.getVars();
		Substitution sigma = new Substitution();
		for (int i= 0; i < domain.size(); ++i) {
			Variable v = (Variable) domain.get(i);
			Expression map = psi.maps(v);
			sigma.addAssoc(v,map);
		}
		return sigma;		
	}
	
	public Substitution apply(Substitution other) {
		Substitution otherC = other.factor();
		Substitution psi = new Substitution();
		for (int i=0; i < associations.size(); ++i) {
			Association vSub = (Association) associations.get(i);
			Variable v = vSub.getVar();
			Expression oldMap = vSub.getSub();
			psi.addAssoc(v,oldMap.apply(otherC));
		}
		for (int i=0; i < other.associations.size(); ++i) {
			Association vSub = (Association) other.associations.get(i);
			Variable v = vSub.getVar();
			Expression oldMap = vSub.getSub();
			if (!psi.assigns(v)) psi.addAssoc(v,oldMap.apply(otherC));			
		}
		return psi;
	}
	
}
