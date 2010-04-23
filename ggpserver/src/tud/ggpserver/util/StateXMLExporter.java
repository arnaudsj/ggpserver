/*
    Copyright (C) 2009-2010 Stephan Schiffel <stephan.schiffel@gmx.de> 

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

package tud.ggpserver.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.auxiliary.InvalidKIFException;
import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.datamodel.statistics.TournamentStatistics;

/**
 * This class can be used to export an xml file for each step of a match like created by GameController for
 * matches in the database.
 */
public class StateXMLExporter {

	public static void exportTournament(String tournamentID, ZipOutputStream zip) throws SQLException, IOException {
		AbstractDBConnector<?, ?> dbconnector=DBConnectorFactory.getDBConnector();
		exportXML(dbconnector.getTournament(tournamentID), zip, "");
	}

	public static void exportMatch(String matchID, ZipOutputStream zip) throws SQLException, IOException {
		AbstractDBConnector<?, ?> dbconnector=DBConnectorFactory.getDBConnector();
		exportXML(dbconnector.getMatch(matchID), zip, "");
	}
	
	/**
	 * creates files "step_i.xml" and "finalstate.xml" for all states of match in directory
	 */
	public static void exportXML(ServerMatch<?, ?> match, ZipOutputStream zip, String directory) throws IOException {
		String matchDir=directory+match.getMatchID()+"/";
		ZipEntry zipEntry=new ZipEntry(matchDir);
		zip.putNextEntry(zipEntry);
		String xmlState=null;
		List<Pair<Date,String>> stringStates = match.getStringStates();
		for(int step=0; step<stringStates.size(); step++) {
			xmlState = getStepXML(match, stringStates, step+1, RoleInterface.NATURE_ROLE_NAME);
			if(xmlState != null)
				exportStepXML(xmlState, "step_"+(step+1), zip, matchDir);
		}
		// export the final state twice, once as "step_N.xml" and once as "finalstate.xml" 
		if(xmlState != null)
			exportStepXML(xmlState, "finalstate", zip, matchDir);
	}
	
	/**
	 * exports the xml states files for all matches in tournament
	 * @see exportXML(Match match, File directory)
	 */
	public static void exportXML(Tournament<?, ?> tournament, ZipOutputStream zip, String directory) throws SQLException, IOException {
		String tournamentDir=directory+tournament.getTournamentID()+"/";
		ZipEntry zipEntry=new ZipEntry(tournamentDir);
		zip.putNextEntry(zipEntry);
		exportTournamentHTML(tournament, zip, tournamentDir);
		for(ServerMatch<?, ?> match:tournament.getMatches()) {
			exportXML(match, zip, tournamentDir);
		}
		// TODO: export stylesheets
	}

	/**
	 * 
	 * @param match
	 * @param stringStates
	 * @param stepNumber
	 * @param roleName from whose perspective you want to get the XML 
	 * @return the xml for the specified state of the match
	 */
	public static <TermType extends TermInterface, ReasonerStateInfoType> 
	String getStepXML(ServerMatch<TermType, ReasonerStateInfoType> match, List<Pair<Date, String>> stringStates, int stepNumber, String roleName) { 
		return getStepXML(match, stringStates, stepNumber, roleName, false, false, null, null, null);
	}
	
	public static <TermType extends TermInterface, ReasonerStateInfoType> 
	String getStepXML(ServerMatch<TermType, ReasonerStateInfoType> match, List<Pair<Date, String>> stringStates, int stepNumber, String roleName, boolean playing, boolean quickConfirm, Collection<String> legalMoves, String chosenMove, Boolean confirmed) { 
		int numberOfStates = stringStates.size();
		if(numberOfStates > 0) {
			if (stepNumber < 1 || stepNumber > numberOfStates) {
				// return the last/final state
				stepNumber = numberOfStates;
			}
			Pair<Date,String> stringState = stringStates.get(stepNumber-1);
			GameInterface<TermType, ? extends StateInterface<TermType, ?>> game = match.getGame();
			RoleInterface<TermType> role = null;

			// compute Role object from role name
			if (roleName != null) {
				role = game.getRoleByName(roleName);
			}
			if (role == null) {
				role = game.getNatureRole();
			}
			
			// get history of moves
			List<List<String>> stringMoves = match.getJointMovesStrings();
			if (stringMoves.size() > 0) {
				stringMoves = stringMoves.subList(0, stepNumber-1);
			}
			
			// get state object from string representation
			StateInterface<TermType, ?> state;
			try {
				state = match.getGame().getStateFromString(stringState.getRight());
			} catch (InvalidKIFException e) {
				Logger.getLogger(StateXMLExporter.class.getName()).severe(
						"State in database is invalid kif! match: " + match.getMatchID()
						+ ", step:" +  stepNumber + ", state:" + stringState.getRight());
				e.printStackTrace();
				return null;
			}
			
			// get Term representation of our string chosenMove
			TermInterface chosenMoveTerm;
			if (chosenMove == null) {
				chosenMoveTerm = null;
			} else {
				try {
					chosenMoveTerm = match.getGame().getTermFromString(chosenMove);
				} catch (InvalidKIFException e) {
					Logger.getLogger(StateXMLExporter.class.getName()).severe(
							"Invalid chosenMove! match: " + match.getMatchID()+": " + chosenMove);
					return null;
				}
			}
			
			// get List<Term> representation of our List<String> legalMoves
			List<TermInterface> legalTermMoves = new LinkedList<TermInterface>();
			if (legalMoves == null) {
				legalTermMoves = null;
			} else {
				try {
					for (String move: legalMoves)
						legalTermMoves.add(match.getGame().getTermFromString(move));
				} catch (InvalidKIFException e) {
					Logger.getLogger(StateXMLExporter.class.getName()).severe(
							"Invalid legalMove in legalMoves list! match: " + match.getMatchID()+": "+legalMoves);
					return null;
				}
			}
			
			// get goal values
			Map<? extends RoleInterface<TermType>, Integer> goalValues = null;
			if (state.isTerminal()) {
				goalValues = match.getGoalValues();
			}
			
			return XMLGameStateWriter.createXMLOutputStream(
					match,
					state,
					stringMoves,
					goalValues,
					match.getGame().getStylesheet(),
					role,
					stringState.getLeft(),
					playing,
					quickConfirm,
					legalTermMoves,
					chosenMoveTerm,
					confirmed
				).toString();
		} else {
			// this can only happen if the initial state wasn't created yet
			return "match " + match.getMatchID() + " has no state!"; 
		}
	}
	
	public static void exportStepXML(String xml, String step, ZipOutputStream zip, String directory) throws IOException {
		ZipEntry zipEntry=new ZipEntry(directory+step+".xml");
		zip.putNextEntry(zipEntry);
		// this is a hack to make the xml files work with the gamecontroller stylesheets (different paths)
		xml=xml.replace("<?xml-stylesheet type=\"text/xsl\" href=\"../stylesheets/", "<?xml-stylesheet type=\"text/xsl\" href=\"../../stylesheets/");
		zip.write(xml.getBytes(Charset.forName("UTF-8")));
	
	}

	private static void exportTournamentHTML(Tournament<?, ?> tournament, ZipOutputStream zip, String directory) throws SQLException, IOException {
		StringBuilder sb=new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"us-ascii\"?>\n")
			.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n")
			.append("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n")
			.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n")
			.append("<head>\n")
			.append("\t<title>Tournament:").append(tournament.getTournamentID()).append("</title>\n")
			.append("\t<style type=\"text/css\">\n")
			.append("\t\t/*<![CDATA[*/\n")
			.append("\t\t.sum {\n")
			.append("\t\tfont-weight:bold;\n")
			.append("\t}\n")
			.append("\t/*]]>*/\n")
			.append("\t</style>\n")
			.append("</head>\n")
			.append("<body>\n");
		// print scoreboard
		sb.append("\t<h1>Scoreboard</h1>\n")
			.append("\t<table>\n")
			.append("\t\t<tr> <th>Place</th> <th>Player</th> <th>Score</th> </tr>\n");
		int i=1;
		TournamentStatistics<?, ?> tournamentStatistics = DBConnectorFactory.getDBConnector().getTournamentStatistics(tournament.getTournamentID());
		for(PlayerInfo p:tournamentStatistics.getOrderedPlayers()){
			sb.append("\t\t<tr>\n")
				.append("\t\t\t<td>").append(i).append("</td>\n")
				.append("\t\t\t<td>").append(p.getName()).append("</td>\n")
				.append("\t\t\t<td>").append(tournamentStatistics.getTotalReward(p)).append("</td>\n")
				.append("\t\t</tr>\n");
			++i;
		}
		sb.append("\t</table>\n");
		// print all matches per game
		sb.append("\t<h1>Games</h1>\n");
		List<ServerMatch<?, ?>> matches = new LinkedList<ServerMatch<?, ?>>(tournament.getMatches());
		List<ServerMatch<?, ?>> remainingMatches = new LinkedList<ServerMatch<?, ?>>();
		String gameName=null;
		List<String> games = new LinkedList<String>();
		while(!matches.isEmpty()){
			for(ServerMatch<?, ?> match:matches) {
				if(gameName == null){
					gameName = match.getGame().getName();
					// output game heading+link
					sb.append("\t\t<h2>")
						.append("<a href=\"games/").append(gameName).append(".gdl\">")
						.append(gameName)
						.append("</a>")
						.append("</h2>\n")
						// table header (columns: match, players, goalvalues)
						.append("\t\t<table>\n")
						.append("\t\t\t<thead><tr><th>match id</th><th>players</th><th>goal values</th></tr></thead>\n")
						.append("\t\t\t<tbody>\n");
					// remember game
					games.add(gameName);
				}
				if(gameName.equals(match.getGame().getName())){
					// output match info
					sb.append("\t\t\t\t<tr>\n")
						// match id (link to finalstate of match)
						.append("\t\t\t\t\t<td><a href=\"").append(match.getMatchID()).append("/finalstate.xml").append("\">").append(match.getMatchID()).append("</a></td>\n")
						.append("\t\t\t\t\t<td>");
					// player names
					for(PlayerInfo p:match.getOrderedPlayerInfos()){
						sb.append(p.getName()).append("<br/>");
					}
					sb.append("</td>\n")
						.append("\t\t\t\t\t<td>");
					// goal values
					List<Integer> goalValues=match.getOrderedGoalValues();
					if(goalValues!=null) {
						for(int gv:goalValues){
							sb.append(gv).append("<br/>");
						}
					}else{
						sb.append("status: ").append(match.getStatus());
					}
					sb.append("</td>\n")
						.append("\t\t\t\t</tr>\n");
				}else{
					remainingMatches.add(match);
				}
			}
			matches = remainingMatches;
			remainingMatches = new LinkedList<ServerMatch<?, ?>>();
			gameName = null;
			sb.append("\t\t\t</tbody>\n")
				.append("\t\t</table>\n");
		}
		sb.append("</html>\n");
		
		ZipEntry zipEntry=new ZipEntry(directory+"index.html");
		zip.putNextEntry(zipEntry);
		zip.write(sb.toString().getBytes(Charset.forName("UTF-8")));
		exportGames(games, zip, directory);
	}
	
	public static void exportGames(List<String> gameNames, ZipOutputStream zip, String directory) throws SQLException, IOException {
		String gamesDir=directory+"games/";
		ZipEntry zipEntry=new ZipEntry(gamesDir);
		zip.putNextEntry(zipEntry);
		AbstractDBConnector<?, ?> dbconnector=DBConnectorFactory.getDBConnector();
		for(String gameName:gameNames){
			zipEntry=new ZipEntry(gamesDir+gameName+".gdl");
			zip.putNextEntry(zipEntry);
			zip.write(dbconnector.getGame(gameName).getGameDescription().getBytes(Charset.forName("UTF-8")));
		}
	}
}
