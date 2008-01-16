package de.tu_dresden.inf.ggp06_2.gamedb.objects;


public class StrategyInformation {
    
    int    ident;
    String name;

    /**
     * @return the ident
     */
    public int getIdent() {
        return ident;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param ident the ident to set
     */
    public void setIdent(int ident) {
        this.ident = ident;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
