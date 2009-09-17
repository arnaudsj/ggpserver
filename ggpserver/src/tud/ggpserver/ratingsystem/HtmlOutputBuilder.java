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

package tud.ggpserver.ratingsystem;

import ggpratingsystem.Configuration;
import ggpratingsystem.Game;
import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.ratingsystems.LinearRegressionGameInfo;
import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;

public class HtmlOutputBuilder implements OutputBuilder {
		private Configuration configuration;
		private List<Player> players;
    	private PrintWriter writer;
    	private HashMap<Player, Double> playerRatings;
    	
    	public HtmlOutputBuilder(Configuration configuration, Writer rawwriter) {
    		this.configuration = configuration;
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

//    		// print scores of single matches
//    		writer.println("<table>");
//    		writer.println("  <tr>");
//    		for(Player player : players){
//   				writer.println("    <th>" + player.getName() + "</th>");
//    		}
//    		writer.println("  </tr>");
//    		for (Match m : matchSet.getMatches()) {
//        		writer.println("  <tr>");
//        		for(Player player : players){
//        			int i = m.getPlayers().indexOf(player);
//        			if (i != -1) {
//            			writer.println("    <td>" + m.getScores().get(i) + "(" + matchSet.getGame().getRoles().get(i) + ")</td>");
//        			} else {
//        				writer.println("    <td></td>");
//        			}
//        		}
//        		writer.println("  </tr>");
//    		}
//    		writer.println("</table>");

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