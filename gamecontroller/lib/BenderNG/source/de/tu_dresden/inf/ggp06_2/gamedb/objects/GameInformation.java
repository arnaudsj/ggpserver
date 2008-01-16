package de.tu_dresden.inf.ggp06_2.gamedb.objects;

import java.util.List;

/**
 * The GameInformation class is a data container for the game class. It contains
 * all information that can be retrieved from the game description and is
 * related to the game.
 * 
 * The methods of this class are plain getter and setter methods and should not
 * contain any calculation.
 * 
 * If you want to add data here then make sure that the information is 
 * decoupled. This means that only POJ objects or objects from a class of this
 * package are included.
 * 
 * @author Ingo Keller
 *
 */
public class GameInformation {
    
    /**
     * The ident attribute contains the unique id of the game within the 
     * database.
     */
    int          ident;
    
    /**
     * The gdl attribute contains the game description string without any 
     * changes.
     */
    String       gdl;

    /**
     * The roles attribute contains the role names that are resolved from the
     * game description.
     */
    List<String> roles;
    
    /**
     * The turnTaking attribute is true if we could identify that this game is
     * a turn taking game. Nonetheless this could be a turn taking game even
     * if we could not identify it.
     */
    boolean      turnTaking = false;

    /**
     * The valid attribute is true if the gdl passed the validation test.
     */
    boolean      valid;
    
    public boolean isTurnTaking() {
        return turnTaking;
    }

    public void setTurnTaking(boolean turnTaking) {
        this.turnTaking = turnTaking;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getGdl() {
        return gdl;
    }
    
    public int getIdent() {
        return ident;
    }
    
    public void setGdl(String gdl) {
        this.gdl = gdl;
    }
    
    public void setIdent(int ident) {
        this.ident = ident;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}
