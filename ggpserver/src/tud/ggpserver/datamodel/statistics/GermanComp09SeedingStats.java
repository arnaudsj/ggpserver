package tud.ggpserver.datamodel.statistics;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.DBConnectorFactory;

public class GermanComp09SeedingStats {
	
	public GermanComp09SeedingStats() throws SQLException {
		DBConnectorFactory.getDBConnector().fillGermanComp09SeedingStats(this);
	}

	public static class StatEntry implements Comparable<StatEntry> {
		private String player;
		private double avg, avg_of_non_error_matches, avg_of_all_matches;
		private int nb_matches;
		private int nb_matches_with_errors;
		private double error_ratio;

		public StatEntry(String player, double avg,
				double avg_of_non_error_matches, double avg_of_all_matches,
				int nb_matches, int nb_matches_with_errors, double error_ratio) {
			this.player = player;
			this.avg = avg;
			this.avg_of_non_error_matches = avg_of_non_error_matches;
			this.avg_of_all_matches = avg_of_all_matches;
			this.nb_matches = nb_matches;
			this.nb_matches_with_errors = nb_matches_with_errors;
			this.error_ratio = error_ratio;
		}

		public String getPlayer() {
			return player;
		}

		public double getAvg() {
			return avg;
		}

		public double getAvg_of_non_error_matches() {
			return avg_of_non_error_matches;
		}

		public double getAvg_of_all_matches() {
			return avg_of_all_matches;
		}

		public int getNb_matches() {
			return nb_matches;
		}

		public int getNb_matches_with_errors() {
			return nb_matches_with_errors;
		}

		public double getError_ratio() {
			return error_ratio;
		}

		public int compareTo(StatEntry o2) {
			if((nb_matches>=100 && o2.nb_matches>=100) || (nb_matches<100 && o2.nb_matches<100))
				return Double.compare(o2.avg, avg);
			else
				return o2.nb_matches - nb_matches;
		}
	}

	private List<StatEntry> statEntries = new LinkedList<StatEntry>();  
	
	public void addRow(StatEntry entry) {
		statEntries.add(entry);	
	}
	
	public List<StatEntry> getEntries() {
		Collections.sort(statEntries);
		return Collections.unmodifiableList(statEntries);
	}

}
