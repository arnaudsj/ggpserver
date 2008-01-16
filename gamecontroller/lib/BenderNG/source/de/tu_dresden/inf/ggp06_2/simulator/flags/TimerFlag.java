package de.tu_dresden.inf.ggp06_2.simulator.flags;

public class TimerFlag {

    private boolean abort;
    
    /**
     * Interruptes the processing of the theory. This is done in the MatchTimer
     * class to interrupte processing if we run out of time.
     */
    public void interrupt() {
        abort = true;
    }

    /**
     * Returns wether the theory object was interrupted while doing stuff.
     * 
     * @return True if interrupted; otherwise false.
     */
    public boolean interrupted() {
        return abort;
    }

    /**
     * This method resets the interruption flag back to false value.
     *
     */
    public void reset() {        
        abort = false;
    }
}
