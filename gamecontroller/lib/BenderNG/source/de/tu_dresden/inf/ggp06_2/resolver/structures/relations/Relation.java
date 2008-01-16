package de.tu_dresden.inf.ggp06_2.resolver.structures.relations;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import de.tu_dresden.inf.ggp06_2.resolver.Atom;

/**
 * The Relation class is a container class for all information corresponding to
 * a relation in a theory.
 *
 * @author Ingo Keller
 *
 */
public class Relation {

    /**
     * The attribute id contains the id of this relation object.
     */
    int id;

    /**
     * The attribute symbol contains the relations symbol in its string
     * representation as one can find in the GDL.
     */
    final String symbol;

    /**
     * The attribute symbol contains the relations symbol as an Atom object
     * which is used by the resolver.
     */
    final Atom atom;

    /**
     * The attribute arity contains the arity of the relation.
     */
    public final int arity;

    /**
     * This attribute contains the parameter tuples of this relation. The array
     * contains the hash code of the object constants.
     * Index 1 - Tuple Number, Index2 - Parameter Position
     */
    public int[][] elementsInt;

    /**
     * This attribute contains the parameter tuples of this relation. The array
     * contains the hash code of the object constants.
     * Index 1 - Parameter Position, Index2 - Tuple Number
     */
    public int[][] elementsIntTrans;

    /**
     * This attribute contains the information wether this relation is a fact.
     */
    private boolean             isFact;

    public final static int OP_VALID = 0; // tells us if the flags are valid
    public final static int ANTISYM  = 1;
    public final static int ASYM     = 2;
    public final static int SYM      = 3;
    public final static int REF      = 4;
    public final static int IRREF    = 5;

    public final Map<Integer, Atom>    domain = new HashMap<Integer, Atom>();
    public final BitSet         orderProperty = new BitSet(6);

    public int                  bottomInt;     // Assumption: hashCode != -1
    public Atom                 bottomAtom;

    /**
     * This constructor creates a new relation with given symbol and arity.
     * @param s
     * @param a
     */
    public Relation(String symbol, int arity) {
        this.symbol = symbol;
        this.atom   = new Atom(symbol);
        this.arity  = arity;
        this.isFact = false;
    }

    public boolean isFact() {
        return isFact;
    }


    public final void setFact() {
        isFact = true;
    }

    public final String getSymbol() {
        return symbol;
    }

    public final int getId() {
        return id;
    }

    public final void setId(int id) {
        this.id = id;
    }

}
