/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GameController;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class TournamentScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	private static final Logger logger = Logger.getLogger(TournamentScheduler.class.getName());

	/**
	 * Since this collection also gets accessed from inside the match threads,
	 * synchronization on the TournamentScheduler instance is not enough, and
	 * this needs to be a synchronized map.
	 */
	private final Map<String, Thread> matchThreads 
			= Collections.synchronizedMap(new HashMap<String, Thread>());

	public synchronized void start(final NewMatch<TermType, ReasonerStateInfoType> newMatch) {
		if (isRunning(newMatch)) {
			throw new IllegalStateException("Match " + newMatch.getMatchID() + " already running!");
		}
		
		Thread thread = new Thread(){
			@Override
			public void run() {
				String matchID = newMatch.getMatchID();
				logger.info("Thread for match " + matchID + " - START");
				try {
					RunningMatch<TermType, ReasonerStateInfoType> match = newMatch.toRunning();
			
					try {
						GameController<TermType, ReasonerStateInfoType> gameController 
								= new GameController<TermType, ReasonerStateInfoType>(match);
						gameController.addListener(match);
						gameController.runGame();
						match.toFinished();
						matchThreads.remove(match.getMatchID());    // removing matchThreads on normal termination
					} catch (InterruptedException e1) {
						logger.info("Thread for match " + matchID + " - INTERRUPT");
						match.notifyErrorMessage(new GameControllerErrorMessage(GameControllerErrorMessage.ABORTED, "The match was aborted."));
						match.toAborted();
						// when aborting, don't remove this from matchThreads to prevent deadlock. abort() must do that. 
					}
				} catch (SQLException e2) {
					logger.severe("exception: " + e2);
				}
				logger.info("Thread for match " + matchID + " - END");
			}
		};
		thread.start();
		matchThreads.put(newMatch.getMatchID(), thread);
	}

	public synchronized void abort(RunningMatch<TermType, ReasonerStateInfoType> match) {
		Thread matchThread = matchThreads.get(match.getMatchID());
		if (matchThread == null) {
//			ServerMatch<TermType, ReasonerStateInfoType> next = matchThreads.keySet().iterator().next();
//			System.out.println("next: " + next);
//			System.out.println("match: " + match);
//			System.out.println(next.equals(match));
			throw new IllegalStateException("Match " + match.getMatchID() + " not running!");
		}

		try {
			matchThread.interrupt();
			matchThread.join();
			matchThreads.remove(match.getMatchID());
		} catch (InterruptedException e1) {
			logger.severe("interrupted while aborting match: " + e1); //$NON-NLS-1$
		}
	}
	
	public boolean isRunning(ServerMatch<TermType, ReasonerStateInfoType> match) {
		return matchThreads.get(match.getMatchID()) != null;
	}
}
