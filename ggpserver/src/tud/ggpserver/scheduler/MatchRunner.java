/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RemotePlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ScheduledMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import cs227b.teamIago.util.GameState;

/**
 * starts a scheduled match whenever all players of a match are available
 *
 */
public class MatchRunner<TermType extends TermInterface, ReasonerStateInfoType> {
	
	private static final Logger logger = Logger.getLogger(MatchRunner.class.getName());

	// singleton pattern
	private static MatchRunner<Term, GameState> instance = null;
	public static MatchRunner<Term, GameState> getInstance() {
		if(instance == null)
			instance = new MatchRunner<Term, GameState>();
		return instance;
	}

	private boolean running;
	private boolean stop;

	private final List<String> scheduledMatchIDs;
	private final Set<String> playingPlayers;
	private final Map<String, Thread> matchThreads;
	private final Map<String, ScheduledMatch<TermType, ReasonerStateInfoType>> scheduledMatches;

	/**
	 *  private constructor
	 */
	private MatchRunner() {
		running = false;
		// scheduledMatches must be synchronized because it is also accessed from the runMatches thread
		scheduledMatchIDs = Collections.synchronizedList(new LinkedList<String>());
		scheduledMatches = Collections.synchronizedMap(new HashMap<String, ScheduledMatch<TermType, ReasonerStateInfoType>>());
		// the same holds for playingPlayers
		playingPlayers = Collections.synchronizedSet(new HashSet<String>());
		// and matchThreads map
		matchThreads = Collections.synchronizedMap(new HashMap<String, Thread>());
	}

	/**
	 * starts MatchRunner's main loop
	 */
	public void start() {
		stop = false;
		if( !running ) {
			running = true;
			new Thread() {
				@Override
				public void run() {
					try {
						runMatches();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					running = false;
				}
			}.start();
		}
	}
	
	/**
	 * stops MatchRunner's main loop after currently running matches are finished
	 */
	public void stop() {
		stop = true;
		scheduledMatches.notifyAll();
	}

	/**
	 * stops MatchRunner's main loop; currently running matches are aborted
	 */
	public synchronized void abort() {
		stop = true;
		for(Thread t:matchThreads.values()) {
			try {
				t.interrupt();
				t.join();
			} catch (InterruptedException e1) {
				logger.severe("interrupted while aborting match: " + e1); //$NON-NLS-1$
			}
		}
		scheduledMatches.notifyAll();
	}

	private void runMatches() throws SQLException {
		while( !stop ) {
			// find a runnable match (all players available)
			ScheduledMatch<TermType, ReasonerStateInfoType> scheduledMatch = getRunnableMatch();
			if(scheduledMatch != null){
				// run the match
				startMatch(scheduledMatch.toRunning());
			}else{
				// wait for a new match or a player to become available 
				try {
					scheduledMatches.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * gets the next runnable match and removes it from the list of scheduled matches 
	 */
	private synchronized ScheduledMatch<TermType, ReasonerStateInfoType> getRunnableMatch() {
		ScheduledMatch<TermType, ReasonerStateInfoType> runnableMatch = null;
		for(String matchID:scheduledMatchIDs){
			ScheduledMatch<TermType, ReasonerStateInfoType> match = scheduledMatches.get(matchID);
			runnableMatch = match;
			for(PlayerInfo player:match.getPlayerInfos()) {
				if(playingPlayers.contains(player.getName())) {
					runnableMatch = null;
					break;
				}
			}
			if(runnableMatch != null) {
				scheduledMatchIDs.remove(runnableMatch.getMatchID());
				scheduledMatches.remove(runnableMatch.getMatchID());
				break;
			}
		}
		return runnableMatch;
	}

	private void startMatch(final RunningMatch<TermType, ReasonerStateInfoType> match) {
		// remember that the players in the match are currently unavailable for other matches
		for(PlayerInfo p:match.getPlayerInfos()) {
			if(p instanceof RemotePlayerInfo) {
				playingPlayers.add(p.getName());
			}
		}
		final String matchID = match.getMatchID();
		Thread thread = new Thread(){
			@Override
			public void run() {
				
				logger.info("Thread for match " + matchID + " - START");
				try {
					try {
						GameController<TermType, ReasonerStateInfoType> gameController 
								= new GameController<TermType, ReasonerStateInfoType>(match);
						gameController.addListener(match);
						gameController.runGame();
						match.toFinished();
					} catch (InterruptedException e1) {
						logger.info("Thread for match " + matchID + " - INTERRUPT");
						match.notifyErrorMessage(new GameControllerErrorMessage(GameControllerErrorMessage.ABORTED, "The match was aborted."));
						match.toAborted();
						// when aborting, don't remove this from matchThreads to prevent deadlock. abort() must do that. 
					}
				} catch (SQLException e2) {
					logger.severe("exception: " + e2);
				}
				// players are free again
				for(PlayerInfo p:match.getPlayerInfos()) {
					if(p instanceof RemotePlayerInfo) {
						playingPlayers.remove(p.getName());
					}
				}
				matchThreads.remove(match.getMatchID());
				// notify MatchRunner about changed player state
				scheduledMatches.notifyAll();
				logger.info("Thread for match " + matchID + " - END");
			}
		};
		matchThreads.put(matchID, thread);
		thread.start();
	}
	
	public synchronized void scheduleMatch(NewMatch<TermType, ReasonerStateInfoType> match) throws SQLException{
		// TODO: check match status to make sure that the match is not scheduled twice (or was already run)
		String matchID = match.getMatchID();
		scheduledMatchIDs.add(matchID);
		scheduledMatches.put(matchID, match.toScheduled());
		scheduledMatches.notifyAll();
	}

	/**
	 * schedules the matches and waits until they are finished<br/>
	 * aborts the matches in case of an InterruptedException (e.g., if Thread.interrupt() was called)
	 */
	public void runMatches(Collection<NewMatch<TermType, ReasonerStateInfoType>> matches) throws SQLException {
		Set<String> matchIDs = new HashSet<String>();
		// schedule matches
		for(NewMatch<TermType, ReasonerStateInfoType> match:matches){
			matchIDs.add(match.getMatchID());
			scheduleMatch(match);
		}
		try {
			// wait for matches to finish
			for(String matchID:matchIDs){
					waitForMatch(matchID);
			}
		} catch (InterruptedException e) {
			for(String matchID:matchIDs){
				delete(matchID);
			}
		}
	}

	private void waitForMatch(String matchID) throws InterruptedException {
		while(scheduledMatches.containsKey(matchID)) {
			scheduledMatches.wait();
		}
		Thread matchThread = matchThreads.get(matchID);
		if(matchThread != null){
			matchThread.join();
		}
	}

	public void abort(RunningMatch<TermType, ReasonerStateInfoType> match) {
		abort(match.getMatchID());
	}

	public synchronized void abort(String matchID) {
		Thread matchThread = matchThreads.get(matchID);
		if (matchThread != null) {
			try {
				matchThread.interrupt();
				matchThread.join();
			} catch (InterruptedException e1) {
				logger.severe("interrupted while aborting match: " + e1); //$NON-NLS-1$
			}
		}
	}

	public void delete(ServerMatch<TermType, ReasonerStateInfoType> match) {
		delete(match.getMatchID());
	}

	public synchronized void delete(String matchID) {
		scheduledMatchIDs.remove(matchID);
		scheduledMatches.remove(matchID);
		abort(matchID);
	}

	public boolean isRunning(ServerMatch<TermType, ReasonerStateInfoType> match) {
		return matchThreads.containsKey(match.getMatchID());
	}
	
}
