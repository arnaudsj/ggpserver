///////////////////////////////////////////////////////////////////////
//                        STANFORD LOGIC GROUP                       //
//                    General Game Playing Project                   //
//                                                                   //
// Sample Player Implementation                                      //
//                                                                   //
// (c) 2007. See LICENSE and CONTRIBUTORS.                           //
///////////////////////////////////////////////////////////////////////

package stanfordlogic.util;

import java.util.TimerTask;


/**
 * A timer that operates on <i>stoppable</i> objects. When the timer is
 * trigged, calls the <tt>stopIt</tt> method of the stoppable.
 * 
 * @see stanfordlogic.util.Stoppable
 * 
 */
public class TimedTaskMonitor extends TimerTask
{
    private Stoppable           stoppable_;
    
    public TimedTaskMonitor(Stoppable stoppable)
    {
        super();
        stoppable_ = stoppable;
    }
    
    @Override
    public void run()
    {
        stoppable_.stopIt();
    }
}
