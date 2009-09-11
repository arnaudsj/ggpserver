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

package tud.ggpserver.formhandlers;

import ggpratingsystem.Configuration;
import ggpratingsystem.Game;
import ggpratingsystem.Match;
import ggpratingsystem.MatchSet;
import ggpratingsystem.MatchSetReader;
import ggpratingsystem.Player;
import ggpratingsystem.output.CachingOutputBuilder;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.ratingsystems.ConstantLinearRegressionStrategy;
import ggpratingsystem.ratingsystems.LinearRegressionGameInfo;
import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingException;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class ViewRatings {
	
	private static final Logger logger = Logger.getLogger(AdminPage.class.getName());

	private static final AbstractDBConnector<?, ?> db = DBConnector.getInstance();
	
	private Configuration configuration;

	public String getRatingsHtmlTable() throws IOException, RatingException, SQLException{
	
		StringBuilder result = new StringBuilder();
		
		logger.info("create configuration");
		configuration = new Configuration();
		
		// TODO: get previous ratings from database
		// configuration.setPreviousRatings(previousRatings);
		
		// add rating system 
		logger.info("add rating system");
		configuration.addRatingSystem(new ConstantLinearRegressionStrategy(1.0));
	
		// set match reader 
		logger.info("set match reader");
		configuration.setMatchReader(new DBMatchReader());
	
		// add outputBuilder
		logger.info("add output builder");
		StringWriter writer = new StringWriter();
		configuration.addOutputBuilder(RatingSystemType.CONSTANT_LINEAR_REGRESSION, new CachingOutputBuilder(new HtmlOutputBuilder(writer)));
		
		// compute
		logger.info("compute");
		configuration.run();
		
		// write results
		logger.info("close output builders");
		configuration.closeOutputBuilders();
		
		logger.info("write table");
		result.append(writer.getBuffer());
		
		return result.toString();
	}
	

	private class DBMatchReader implements MatchSetReader {

		List<? extends ServerMatch<?,?>> matches;
		int matchSetId = 0;
		
		private final long MATCHSET_TIME = 24*60*60*1000; // maximal difference in start time of two matches such that they still belong to the same match set 
		
		public DBMatchReader() throws SQLException {
			matches = db.getMatches(0, Integer.MAX_VALUE, null, null, "round_robin_tournament", true);

			// filter matches, such that only finished matches are in the list
			Iterator<? extends ServerMatch<?, ?>> it = matches.iterator();
			while(it.hasNext()){
				if ( it.next().getStatus() != ServerMatch.STATUS_FINISHED ) {
					it.remove();
				}
			}
		}
		
		@Override
		public boolean hasNext() {
			return !matches.isEmpty();
		}
		
		@Override
		public MatchSet readMatchSet() {
			if(matches.isEmpty())
				return null;
			
			Iterator<? extends ServerMatch<?, ?>> it = matches.iterator();
			if ( !it.hasNext() ) {
				return null;
			}
			ServerMatch<?, ?> nextMatch = it.next();
			
			matchSetId++;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nextMatch.getStartTime());
			long startTimeInMillis = nextMatch.getStartTime().getTime();
			
			String gameName = nextMatch.getGame().getName();
			Game game = configuration.getGameSet().getGame(gameName);
			
			if(!game.hasRoles()) {
				List<String> roleNames = new LinkedList<String>();
				for(RoleInterface<?> role:nextMatch.getGame().getOrderedRoles()){
					roleNames.add(role.getPrefixForm());
				}
				game.setRoles(roleNames);
			}
			
			logger.info("creating MatchSet " + matchSetId + " of game " + gameName + " (start time: " + startTimeInMillis + ")");
			MatchSet matchSet = new MatchSet(""+matchSetId, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), matchSetId, game);
			
			boolean continueSearch = true;
			do {
				if ( nextMatch.getGame().getName().equals(gameName) ) {
					logger.info("add match " + nextMatch.getMatchID() + " to matchset");
					matchSet.addMatch(getRatingSystemMatch(matchSet, nextMatch));
					it.remove();
				}
				if ( it.hasNext() ) {
					nextMatch = it.next();
					if ( nextMatch.getStartTime().getTime() > startTimeInMillis + MATCHSET_TIME ) {
						continueSearch = false;
					}
				} else {
					continueSearch = false;
				}
			} while ( continueSearch );
			return matchSet;
		}
		
		private Match getRatingSystemMatch(MatchSet matchSet, ServerMatch<?, ?> match){
			List<Player> players = new LinkedList<Player>();
			for(PlayerInfo playerInfo:match.getOrderedPlayerInfos()) {
				players.add(configuration.getPlayerSet().getPlayer(playerInfo.getName()));
			}
			List<Integer> scores = match.getOrderedGoalValues();
			return new Match(matchSet, match.getMatchID(), players, scores); 
		}

	}
	
    class HtmlOutputBuilder implements OutputBuilder {
    	private List<Player> players;
    	private PrintWriter writer;
    	private HashMap<Player, Double> playerRatings;
    	
    	public HtmlOutputBuilder(Writer rawwriter) {
    		this.writer = new PrintWriter(rawwriter);
    		playerRatings = new HashMap<Player, Double>();
    	}

    	public void initialize(List<Player> players) throws IOException {
    		this.players = players;
    		writer.println("<dl>");
    	}

    	public void beginMatchSet(MatchSet matchSet) {
    	}

    	public void endMatchSet(MatchSet matchSet) {
    		
    		Game game = matchSet.getGame();

    		writer.println("<dt>match set " + matchSet.getId() + "(game: " + game.getName() + ", #matches: " + matchSet.getMatches().size() + ") </dt>");
    		
    		writer.println("<dd>");

    		// print scores of single matches
    		writer.println("<table>");
    		writer.println("  <tr>");
    		for(Player player : players){
   				writer.println("    <th>" + player.getName() + "</th>");
    		}
    		writer.println("  </tr>");
    		for (Match m : matchSet.getMatches()) {
        		writer.println("  <tr>");
        		for(Player player : players){
        			int i = m.getPlayers().indexOf(player);
        			if (i != -1) {
            			writer.println("    <td>" + m.getScores().get(i) + "(" + matchSet.getGame().getRoles().get(i) + ")</td>");
        			} else {
        				writer.println("    <td></td>");
        			}
        		}
        		writer.println("  </tr>");
    		}
    		writer.println("</table>");

    		writeRatingTable();
    		
			writer.println("</dd>");
    	}

    	public void ratingUpdate(Rating rating) {
    		playerRatings.put(rating.getPlayer(), rating.getCurRating());
    	}

    	public void finish() {
    		writer.println("</dl>");

    		// add some more information
    		writer.println("game coefficients:<br/>");
    		writer.println("<dl>");
    		for(Game game : configuration.getGameSet().getAllGames()) {
    			writer.print("<dt>");
    			writer.print(game.getName());
    			writer.println("</dt>");
    			writer.println("<dd>");
    			writeGameCoefficientsTable(game);
    			writer.println("</dd>");
    		}
    		writer.println("</dl>");

    		// close output file
    		writer.close();
    	}
    	
    	private void writeRatingTable() {
    		// write current ratings table
    		writer.println("<table>");
    		writer.println("  <tr>");
    		for(Player player : players){
   				writer.println("    <th>" + player.getName() + "</th>");
    		}
    		writer.println("  </tr>");
    		writer.println("  <tr>");
    		for(Player player : players){
    			Double rating = playerRatings.get(player);
    			writer.println("    <td>" + rating + "</td>");
    		}
    		writer.println("  </tr>");
    		writer.println("</table>");
    	}

    	private void writeGameCoefficientsTable(Game game) {
    		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo)game.getGameInfo(RatingSystemType.CONSTANT_LINEAR_REGRESSION);
			double[][] coeffs = gameInfo.getCoefficients();
			writer.println("<table> <tr> <td></td> <th>const</th>");
			for(String roleName : game.getRoles()) {
				writer.print("<th>");
				writer.print(roleName);
				writer.println("</th>");
			}
			writer.println("</tr>");
			for(int i = 0 ; i < coeffs.length; i++){
				writer.println("<tr>");
				writer.print("<th>");
				writer.print(game.getRoles().get(i));
				writer.println("</th>");
				for(int j = 0 ; j < coeffs[i].length; j++){
					writer.print("<td>");
					writer.print(coeffs[i][j]);
					writer.println("</td>");
				}
				writer.println("</tr>");
			}
			writer.println("</table>");
    	}
}


}
