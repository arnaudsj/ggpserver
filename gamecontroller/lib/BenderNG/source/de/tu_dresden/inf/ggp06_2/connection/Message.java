package de.tu_dresden.inf.ggp06_2.connection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import de.tu_dresden.inf.ggp06_2.parser.Parser;
import de.tu_dresden.inf.ggp06_2.resolver.ExpressionList;

/**
 * This class handles all message related transformations.
 * @author ingo
 *
 */
public class Message {

    /* Stores the logger for this class */
    public static Logger logger = Logger.getLogger(Message.class);

    /* Message strings */
    static final String STR_START = "START";
    static final String STR_STOP  = "STOP";
    static final String STR_PLAY  = "PLAY";

    /* Message types */
    static final int ERROR = -1;
    static final int EMPTY = 0;
    static final int START = 1;
    static final int PLAY  = 2;
    static final int STOP  = 3;

    /* Regular Expressions */
    static final Pattern RE_COMMAND = Pattern.compile("\\(([^\\s]*)\\s");

    /* Message content fields */
    int            type;
    String         matchId;
    String         role;
    ExpressionList gameDescription;
    ExpressionList moves;
    int            startClock;
    int            playClock;


    /**
    * Constructs a message object from a message string.
    * @param message
    */
    public Message(String message) {
        this.parseMessage(message);
    }


    /**
    * This method parses the message string into a message object.
    * @param message
    */
    public void parseMessage(String message) {
        //logger.debug( "Start parsing the message: " + message );

        // resets the message
        this.reset();

        // no message there
        if ( message == null )
            return;

        // retrieve the command
        String   command      = getCommand( message );

        // remove closing brackets
        message = message.substring( 1, message.length() - 1 );

        // split message in its parts
        String[] messageParts = message.split( " " );
        int      length       = messageParts.length;

        // create temp string for move parsing
        String   tmpString    = "";

        /* START Message handling */
        if ( command.equals( Message.STR_START ) ) {
            this.type       = Message.START;
            this.matchId    = messageParts[1];
            this.role       = messageParts[2];
            this.startClock = Integer.parseInt( messageParts[length-2] );
            this.playClock  = Integer.parseInt( messageParts[length-1] );

            // all other groups are related to the game description
            for (int i = 3; i < length - 2; i++)
                tmpString += " " + messageParts[i];

            this.gameDescription = Message.parseString( tmpString );

        /* PLAY Message handling */
        } else if ( command.equals( Message.STR_PLAY ) ) {

            this.type    = Message.PLAY;
            this.matchId = messageParts[1];

            // all other groups are related to the move section
            for (int i = 2; i < messageParts.length; i++)
                tmpString += " " + messageParts[i];

            // GDL Parser doesn't recognize NOOP so we give him a predicate
            tmpString = tmpString.replace( "NOOP", "(NOOP)" );
            this.moves = Message.parseString( tmpString );

        /* STOP Message handling */
        } else if ( command.equals( Message.STR_STOP ) ) {

            this.type    = Message.STOP;
            this.matchId = messageParts[1];

            // STOP Message does not have to have a move list
            try {
                // all other groups are related to the move section
                for (int i = 2; i < messageParts.length; i++)
                    tmpString += " " + messageParts[i];

                // GDL Parser doesn't recognize NOOP so we give him a predicate
                tmpString = tmpString.replace( "NOOP", "(NOOP)" );
                this.moves = Message.parseString( tmpString );

            } catch ( Exception ex ) {
                this.moves = new ExpressionList();
            }

        } else {
            this.reset();
            //logger.error( "Command not found: " + command );
            this.type = Message.ERROR;
        }

        //logger.debug( "Finished parsing the message." );
    }


    /**
    * This method extracts the message command string.
    *
    * @param message
    * @return
    */
    private String getCommand(String message) {
        String command = null;
        try {

            Matcher m = RE_COMMAND.matcher(message);
            if ( m.lookingAt() ) {
                command = m.group(1);
                command = command.toUpperCase();
            }

        } catch (Exception ex) {
            //logger.error( "Pattern to extract command did not match!", ex );
            this.type = Message.ERROR;
        }

        return command;
    }

    /**
     * This method parses a gdl string into a ExpressionList.
     * If a error occurs while parsing the result will be an empty list.
     * @param gdl
     * @return
     */
    public static ExpressionList parseString( String gdl ) {

        // the first answer in a match
        if ( gdl.contains( "NIL" ) )
            return null;

        // remove additional outer brackets -> used for move parsing
        // TODO: Create a better check with a regular expression or so
        if (gdl.startsWith(" ((") )

            if ( gdl.endsWith("))") )
                gdl = gdl.substring( 2, gdl.length() - 1 );

            else
                if ( gdl.endsWith(") )" ) )
                    gdl = gdl.substring( 2, gdl.length() - 2 );

        // parse stream into game description
        return Parser.parseGDL( gdl );
    }

    /**
    * This method handles a clean reset of the message object. Essentially it
    * sets all content fields to a default empty state.
    */
    private void reset() {
        this.type    		 = Message.EMPTY;
        this.matchId 		 = "";
        this.role    		 = "";
        this.gameDescription = new ExpressionList();
        this.moves   		 = new ExpressionList();
        this.startClock		 = 0;
        this.playClock	     = 0;
    }

    @Override
    public String toString() {
        return "Type: " + this.type + "\nMatchID: " + this.matchId;
    }


    /**
     * @return the gameDescription
     */
    public ExpressionList getGameDescription() {
        return gameDescription;
    }


    /**
     * @param gameDescription the gameDescription to set
     */
    public void setGameDescription(ExpressionList gameDescription) {
        this.gameDescription = gameDescription;
    }


    /**
     * @return the matchId
     */
    public String getMatchId() {
        return matchId;
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
    public ExpressionList getMoves() {
        return moves;
    }


    /**
     * @param moves the moves to set
     */
    public void setMoves(ExpressionList moves) {
        this.moves = moves;
    }


    /**
     * @return the playClock
     */
    public int getPlayClock() {
        return playClock;
    }


    /**
     * @param playClock the playClock to set
     */
    public void setPlayClock(int playClock) {
        this.playClock = playClock;
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


    /**
     * @return the startClock
     */
    public int getStartClock() {
        return startClock;
    }


    /**
     * @param startClock the startClock to set
     */
    public void setStartClock(int startClock) {
        this.startClock = startClock;
    }


    /**
     * @return the type
     */
    public int getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

}
