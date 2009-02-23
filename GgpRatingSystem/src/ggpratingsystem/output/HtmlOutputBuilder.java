/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem.output;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingFactory;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class only writes the final ratings, along with the difference in ranks
 * and points, into an HTML table.
 * 
 * @author martin
 * 
 */
public class HtmlOutputBuilder implements OutputBuilder {
	private final RatingSystemType type;
	private List<Player> players;
	private PrintWriter writer;

	public HtmlOutputBuilder(Writer rawwriter, RatingSystemType type) {
		this.type = type;
		this.writer = new PrintWriter(rawwriter);
	}

	public void readInitialRatings() {
		
	}
	
	public void initialize(List<Player> players) throws IOException {
		this.players = players;
		
		// write table header
		writer.println("	<table class=\"ratingtable\">");
		writer.println("		<tr>");
		writer.println("			<th colspan=\"2\">Rank</th>");
		writer.println("			<th width=\"80%\">Player</th>");
		writer.println("			<th colspan=\"2\">Rating</th>");
		writer.println("		</tr>");
	}

	public void beginMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void endMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void ratingUpdate(Rating rating) {
		// nothing to do
	}

	public void finish() {
		// calculate difference in points and ranks for each player
		
		// get final ratings
		List<Rating> finalRatings = new LinkedList<Rating>();
		for (Player player : players) {
			try {
				finalRatings.add((Rating) player.getRating(type).clone());
			} catch (CloneNotSupportedException e) {
				throw new InternalError("Rating not cloneable!");
			}
		}
		Collections.sort(finalRatings, Collections.reverseOrder());
		
		// get initial ratings
		
		List<Rating> initialRatings = RatingFactory.getInitialRatings(type);
		Collections.sort(initialRatings, Collections.reverseOrder());
		
		// write one row for each player
		int newrank = 0; 
		for (Rating finalRating : finalRatings) {
			newrank++;

			int oldrank = 0;
			int rankdiff = 0;
			long ratingdiff = 0;
			boolean found = false;
			for (Rating initialRating : initialRatings) {
				oldrank++;
				if (initialRating.getPlayer().equals(finalRating.getPlayer())) {
					rankdiff = oldrank - newrank;
					ratingdiff = Math.round(finalRating.getCurRating() - initialRating.getCurRating());
					found = true;
					break;
				}
			}

			writer.print("		<tr>\n");

			// col 1: new rank
			writer.printf("			<td class=\"rank\">%d</td>\n", newrank);
			
			// col 2: rank difference
			if (!found) {
				writer.print("			<td class=\"rankdiff new\">(*)</td>\n");
			} else if (rankdiff < 0) {
				writer.printf("			<td class=\"rankdiff negative\">(%+d)</td>\n", rankdiff);
			} else if (rankdiff == 0) {
				writer.print("			<td class=\"rankdiff neutral\">(=)</td>\n");
			} else {    // rankdiff > 0
				writer.printf("			<td class=\"rankdiff positive\">(%+d)</td>\n", rankdiff);
			}
			
			// col 3: player name
			writer.printf("			<td class=\"playername\">%s</td>\n", finalRating.getPlayer());
			
			// col 4: new rating
			writer.printf("			<td class=\"rating\">%d</td>\n", Math.round(finalRating.getCurRating()));
			
			// col 5: rating difference
			if (!found) {
				writer.print("			<td class=\"ratingdiff new\">(*)</td>\n");
			} else if (ratingdiff < 0) {
				writer.printf("			<td class=\"ratingdiff negative\">(%+d)</td>\n", ratingdiff);
			} else if (ratingdiff == 0) {
				writer.print("			<td class=\"ratingdiff neutral\">(=)</td>\n");
			} else {    // ratingdiff > 0
				writer.printf("			<td class=\"ratingdiff positive\">(%+d)</td>\n", ratingdiff);
			}			
			
			writer.print("		</tr>\n");
		}

		// write table footer
		writer.println("	</table>");
		
		// close output file
		writer.close();
	}
}
