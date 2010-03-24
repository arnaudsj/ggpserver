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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ScheduledMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.util.Utilities;
import cs227b.teamIago.util.GameState;

/**
 * starts a scheduled match whenever all players of a match are available
 *
 */
public class MatchRunner<TermType extends TermInterface, ReasonerStateInfoType> implements AvailablePlayersListener {
	private static final long DELAY_BETWEEN_MATCHES = 5000;   // time between two matches with the same players
	
	private static final Logger logger = Logger.getLogger(MatchRunner.class.getName());

	// singleton pattern
	private static MatchRunner<Term, GameState> instance = null;
	public synchronized static MatchRunner<Term, GameState> getInstance() {
		if(instance == null)
			instance = new MatchRunner<Term, GameState>(DBConnector.getInstance());
		return instance;
	}

	/**
	 * status flag
	 */
	private boolean running;
	
	/**
	 * is set to true to stop the main loop
	 */
	private boolean stop;
	
	/**
	 * the thread running the main loop
	 */
	private Thread matchRunnerThread;

	/**
	 * the list of scheduled matches (matches waiting to be run)
	 */
	private final Map<String, ScheduledMatch<TermType, ReasonerStateInfoType>> scheduledMatches;

	/**
	 * the threads of the currently running matches 
	 */
	private final Map<String, MatchThread<TermType,ReasonerStateInfoType>> matchThreads;

	private AvailablePlayersTracker<TermType, ReasonerStateInfoType> availablePlayersTracker;

	/**
	 *  private constructor
	 */
	private MatchRunner(AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		availablePlayersTracker = new AvailablePlayersTracker<TermType, ReasonerStateInfoType>(db);
		matchRunnerThread = null;
		// scheduledMatches must be synchronized because it is also accessed from the runMatches thread
		scheduledMatches = Collections.synchronizedMap(new LinkedHashMap<String, ScheduledMatch<TermType, ReasonerStateInfoType>>());
		// and matchThreads map
		matchThreads = Collections.synchronizedMap(new HashMap<String, MatchThread<TermType,ReasonerStateInfoType>>());
	}

	/**
	 * starts MatchRunner's main loop
	 */
	public synchronized void start() {
		stop = false;
		if( !running ) {
			logger.info("starting MatchRunner");
			running = true;
			matchRunnerThread = new Thread("MatchRunner") {
				@Override
				public void run() {
					try {
						runMatches();
					} catch (Exception e) {
						logger.severe("exception in MatchRunner: " + e);
						e.printStackTrace();
					}
					running = false;
				}
			};
			matchRunnerThread.start();
		}
	}
	
	/**
	 * stops MatchRunner's main loop, currently running matches are finished
	 */
	public void stop() {
		logger.info("stopping MatchRunner");
		stop = true;
		if(matchRunnerThread != null){
			matchRunnerThread.interrupt();
			try {
				matchRunnerThread.join(3000);
			} catch (InterruptedException e) {
				logger.severe("interrupted while stopping matchRunner: " + e);
			}
			matchRunnerThread = null;
		}
	}

	/**
	 * stops MatchRunner's main loop; currently running matches are aborted
	 */
	public void abort() {
		logger.info("aborting MatchRunner");
		stop();
		logger.info("stopping all running matches");
		for(Thread t:matchThreads.values()) {
			t.interrupt();
			try {
				t.join(3000);
			} catch (InterruptedException e) {
				logger.severe("interrupted while aborting match: " + e);
			}
		}
	}
	
	public RunningMatch<TermType, ReasonerStateInfoType> getRunningMatch (String matchID) {
		if (!matchThreads.containsKey(matchID)) return null;
		return matchThreads.get(matchID).getMatch();
	}
	
	/**
	 * the main loop
	 * @throws SQLException
	 */
	private void runMatches() throws SQLException {
		logger.info("matchRunner started");
		while( !stop ) {
			try {
				// find a runnable match (all players available)
				ScheduledMatch<TermType, ReasonerStateInfoType> scheduledMatch = waitForRunnableMatch();
				// run the match
				startMatch(scheduledMatch.toRunning());
			} catch (InterruptedException e) {
				logger.info("matchRunner interrupted");
			}
		}
		logger.info("matchRunner stopped");
	}

	private synchronized ScheduledMatch<TermType, ReasonerStateInfoType> waitForRunnableMatch() throws InterruptedException {
		ScheduledMatch<TermType, ReasonerStateInfoType> runnableMatch = null;

		while (runnableMatch == null) {
			runnableMatch = getRunnableMatch();
			if(runnableMatch != null){
				logger.info("found runnable match: " + runnableMatch.getMatchID());
			}else if(scheduledMatches.isEmpty()) {
				logger.info("no scheduled match -> stop MatchRunner");
					stop = true;
				throw new InterruptedException();
			}else{
				logger.info("no runnable match found (but " + scheduledMatches.size() + " scheduled) -> wait");
				// wait for a new match or a player to become available 
				waitForChanges();
			}
		}
		return runnableMatch;
	}

	/**
	 * gets the next runnable match and removes it from the list of scheduled matches 
	 */
	private synchronized ScheduledMatch<TermType, ReasonerStateInfoType> getRunnableMatch() {
		ScheduledMatch<TermType, ReasonerStateInfoType> runnableMatch = null;
		for(ScheduledMatch<TermType, ReasonerStateInfoType> match : scheduledMatches.values()){
			runnableMatch = match;
			for(PlayerInfo player:match.getPlayerInfos()) {
				if(availablePlayersTracker.isPlaying(player.getName())) {
					runnableMatch = null;
					break;
				}
				if (! Utilities.areCompatible(player, match.getGame().getGdlVersion())) {
					runnableMatch = null;
					break;
				}
				if(player instanceof RemotePlayerInfo){
					RemotePlayerInfo remotePlayer = (RemotePlayerInfo)player;
					if(		! remotePlayer.isAvailableForManualMatches() &&
							match.getTournamentID().equals(Tournament.MANUAL_TOURNAMENT_ID) &&
							! remotePlayer.getOwner().equals(match.getOwner()) ) {
						runnableMatch = null;
						break;
					}
				}
			}
			if(runnableMatch != null) {
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
				availablePlayersTracker.notifyStartPlaying(p.getName());
			}
		}
		MatchThread<TermType, ReasonerStateInfoType> thread = new MatchThread<TermType, ReasonerStateInfoType>("runSingleMatch("+match.getMatchID()+")", match) {
			@Override
			public void run(){
				runSingleMatch(match);
			}
		};
		matchThreads.put(match.getMatchID(), thread);
		thread.start();
	}

	private void runSingleMatch(RunningMatch<TermType, ReasonerStateInfoType> runningMatch) {
		String matchID = runningMatch.getMatchID();
		logger.info("Thread for match " + matchID + " - START");
		try {
			try {
				GameController<TermType, ReasonerStateInfoType> gameController 
						= new GameController<TermType, ReasonerStateInfoType>(runningMatch);
				gameController.addListener(runningMatch);
				gameController.runGame();
				runningMatch.toFinished();
			} catch (InterruptedException e1) {
				logger.info("Thread for match " + matchID + " - INTERRUPT");
				runningMatch.notifyErrorMessage(new GameControllerErrorMessage(GameControllerErrorMessage.ABORTED, "The match was aborted."));
				logger.info("Thread for match " + matchID + " - set match to aborted");
				runningMatch.toAborted();
			}
		} catch (SQLException e2) {
			logger.severe("exception: " + e2);
		}

		// wait for some time to give players a chance to cleanup
		//   we wait in the MatchThread, such that the RoundRobinScheduler
		//   (who waits for the thread to finish) doesn't start the next round before the players are available again
		logger.info("Thread for match " + matchID + " - delay");
		try {
			Thread.sleep(DELAY_BETWEEN_MATCHES);
		} catch (InterruptedException e) {
		}
		
		// players are free again
		logger.info("Thread for match " + matchID + " - set players to available");
		for(PlayerInfo p:runningMatch.getPlayerInfos()) {
			if(p instanceof RemotePlayerInfo) {
				availablePlayersTracker.notifyStopPlaying(p.getName());
			}
		}

		logger.info("Thread for match " + matchID + " - remove thread");
		matchThreads.remove(runningMatch.getMatchID());

		// notify MatchRunner about changed player state
		
		notifyAboutChanges();  
		logger.info("Thread for match " + matchID + " - END");
	}
	
	/**
	 * schedules the match for running as soon as all players in the match are finished playing any other matches
	 * (active status of the players is not checked)
	 * @param match
	 * @throws SQLException
	 */
	public synchronized void scheduleMatch(NewMatch<TermType, ReasonerStateInfoType> match) throws SQLException{
		// TODO: check match status to make sure that the match is not scheduled twice (or was already run)
		String matchID = match.getMatchID();
		scheduledMatches.put(matchID, match.toScheduled());
		logger.info("match " + matchID + " scheduled");
		start();
		notifyAboutChanges();
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

	/**
	 * waits until the match with the given matchID is finished or aborted<br/>
	 * returns immediately if there is no such match
	 * @param matchID
	 * @throws InterruptedException
	 */
	private void waitForMatch(String matchID) throws InterruptedException {
		while(scheduledMatches.containsKey(matchID)) {
			waitForChanges();
		}
		Thread matchThread = matchThreads.get(matchID);
		if(matchThread != null){
			matchThread.join();
		}
	}

	/**
	 * @see abort(String matchID)
	 * @param match
	 */
	public void abort(RunningMatch<TermType, ReasonerStateInfoType> match) {
		abort(match.getMatchID());
	}

	/**
	 * aborts the match with the given matchID if it is currently running
	 * @param match
	 */
	public void abort(String matchID) {
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

	/**
	 * deletes the match from the list of scheduled matches if it is not running yet or aborts the match if it is currently running
	 * @param match
	 * @throws SQLException 
	 */
	public void delete(ServerMatch<TermType, ReasonerStateInfoType> match) throws SQLException {
		delete(match.getMatchID());
	}

	/**
	 * deletes the match with the given matchID from the list of scheduled matches if it is not running yet or aborts the match if it is currently running
	 * @param matchID
	 * @throws SQLException 
	 */
	public void delete(String matchID) throws SQLException {
		// TODO: synchronize on scheduledMatches only
		if(scheduledMatches.containsKey(matchID)){
			scheduledMatches.remove(matchID).toNew(); // if the match was not running yet, set its status to new
		}else{
			abort(matchID);
		}
		notifyAboutChanges();
	}

	/**
	 * 
	 * @param match
	 * @return true if match is currently running
	 */
	public boolean isRunning(ServerMatch<TermType, ReasonerStateInfoType> match) {
		return matchThreads.containsKey(match.getMatchID());
	}
	
	private synchronized void notifyAboutChanges() {
		this.notifyAll();
	}

	/**
	 * waits until there is a change in the scheduled matches or players currently playing a match
	 * @throws InterruptedException (if interrupt() was called on the waiting Thread)
	 */
	private synchronized void waitForChanges() throws InterruptedException {
		this.wait();
	}
	
	/**
	 * waits until there is at least one player available (active and not currently playing a match)
	 * @return a collection of all currently available players
	 * @throws InterruptedException (if interrupt() was called on the waiting Thread)
	 */
	public Collection<tud.ggpserver.datamodel.RemotePlayerInfo> waitForPlayersAvailableForRoundRobin() throws InterruptedException {
		// TODO: compatible gdl
		return availablePlayersTracker.waitForPlayersAvailableForRoundRobin();
	}

	@Override
	public void notifyAvailable(RemotePlayerInfo playerInfo) {
		// TODO: only call notifyAll if we are interested in this player
		this.notifyAll();
	}

}
