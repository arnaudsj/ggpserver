package ggpratingsystem.output;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ggpratingsystem.AbstractRating;
import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.RatingSystemType;

public class CSVOutputBuilder implements OutputBuilder {
	private final CSVRatingsWriter writer;
	private final List<Player> players;

	private double[] nextLine;

	public CSVOutputBuilder(Writer writer, List<Player> players, RatingSystemType type) throws IOException {
		this(writer, players);

		/* initialize nextLine with the current players' scores */
		nextLine = new double[players.size()];
		
		for (int i = 0; i < players.size(); i++) {
			double curRating = players.get(i).getRating(type).getCurRating();
			nextLine[i] = curRating;
		}
	}
	
	public CSVOutputBuilder(Writer writer, List<Player> players, double[] initialRatings) throws IOException {
		this(writer, players);

		/* initialize nextLine with the given initial scores */
		nextLine = initialRatings;
	}

	private CSVOutputBuilder(Writer writer, List<Player> players) throws IOException {
		this.writer = new CSVRatingsWriter(writer);
		this.players = players;
		
		/* write player names as column headings to the CSV writer */
		String[] headings = new String[players.size()];
		for (int i = 0; i < headings.length; i++) {
			headings[i] = players.get(i).getName();
		}
		this.writer.println(headings);
	}
	
	public void beginMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void endMatchSet(MatchSet matchSet) {
		/* write gathered data to the CSV writer */
		try {
			writer.println(nextLine);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void ratingUpdate(AbstractRating rating) {
		/* update the player's rating */
		int position = players.indexOf(rating.getPlayer());
		nextLine[position] = rating.getCurRating(); 
	}
	
	public void finish() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
