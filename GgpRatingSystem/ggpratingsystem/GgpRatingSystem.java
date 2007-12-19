package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;
import ggpratingsystem.output.CSVLeaderboardBuilder;
import ggpratingsystem.output.LeaderboardBuilder;
import ggpratingsystem.output.ValidatingLeaderboardBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;


/**
 * @author martin
 *
 */
public class GgpRatingSystem {

	public static void main(String[] args) throws IOException {
		List<MatchSet> matchSets = MatchReader.readSubdir("2007_preliminaries");
		
		AbstractRatingStrategy strategy = LinearRegressionStrategy.getInstance();
		
		Writer writer = new FileWriter("/tmp/ggp-rating-output.csv");
		
		List<Player> players = Player.getAllPlayers();
		
		LeaderboardBuilder builder = new ValidatingLeaderboardBuilder(
				new CSVLeaderboardBuilder(writer, players, LINEAR_REGRESSION));
		
		// TODO should the builder be passed to the MatchReader?

		for (MatchSet set : matchSets) {
			strategy.update(set);
			
			builder.beginMatchSet(set);
			
			List<Match> matches = set.getMatches();
			for (Match match : matches) {
				List<Player> playersInMatch = match.getPlayers();
				
				for (Player player : playersInMatch) {
					builder.ratingUpdate(player.getRating(LINEAR_REGRESSION));
				}
			}
			
			builder.endMatchSet(set);
		}
		
		builder.finish();
	}
}
