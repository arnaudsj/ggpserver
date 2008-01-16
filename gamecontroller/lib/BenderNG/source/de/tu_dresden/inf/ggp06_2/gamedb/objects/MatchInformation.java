package de.tu_dresden.inf.ggp06_2.gamedb.objects;

import java.util.List;


public class MatchInformation {
    
    int             ident;    // general unique identifier
    String          matchId;  // general matchId from the gamemaster
    String          role;     // the role of the player of this match
    boolean         finished; // stores the information if the match is really
                              // finished
    
    List<String> moves;       // history of taken moves
    List<String> states;      // history of states where the first element is the
                              // state after the first move    
    
    GameInformation game;     // reference to a game

    public int getNumberOfMoves() {
        return moves.size();
    }
    
    /**
     * @return the ident
     */
    public int getIdent() {
        return ident;
    }
    
    /**
     * @return the matchId
     */
    public String getMatchId() {
        return matchId;
    }
    
    /**
     * @param ident the ident to set
     */
    public void setIdent(int ident) {
        this.ident = ident;
    }
    
    /**
     * @param matchId the matchId to set
     */
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    /**
     * @return the moves
     */
    public List<String> getMoves() {
        return moves;
    }

    /**
     * @return the states
     */
    public List<String> getStates() {
        return states;
    }

    /**
     * @param moves the moves to set
     */
    public void setMoves(List<String> moves) {
        this.moves = moves;
    }

    /**
     * @param states the states to set
     */
    public void setStates(List<String> states) {
        this.states = states;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
    
}
