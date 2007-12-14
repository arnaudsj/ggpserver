package ggpratingsystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MatchSet {

	private final String id;
		
	private final int year;
	private final int round;
	private final int day;
	private final int matchSetNumber;	// the number of the MatchSet played on this day (1 for the first etc.)  
	
	private final Game game;
	
	private List<Match> matches = new LinkedList<Match>();

	public MatchSet(String id, int year, int round, int day, int matchSetNumber, Game game) {
		super();
		this.id = id;
		this.year = year;
		this.round = round;
		this.day = day;
		this.matchSetNumber = matchSetNumber;
		this.game = game;
	}

	public int getDay() {
		return day;
	}

	public Game getGame() {
		return game;
	}

	public int getMatchSetNumber() {
		return matchSetNumber;
	}

	public String getId() {
		return id;
	}

	public int getRound() {
		return round;
	}

	public int getYear() {
		return year;
	}
	
	public List<Match> getMatches() {
		LinkedList<Match> result = new LinkedList<Match>();
		result.addAll(matches);
		return result;
	}

	public void addMatch(Match match) {
		matches.add(match);
	}

	@Override
	public String toString() {
		String result = "";
		result += getId() + ";";
		result += getGame().toString() + ";";
		result += getYear() + ";";
		result += getRound() + ";";
		result += getDay() + ";";
		result += getMatchSetNumber() + ";";
		result += getMatches();
		return result;
	}

	/**
	 * @return overall (sum) score for all players in the match set
	 */
	public Map<Player, Double> overallScores() {
		Map<Player, Double> overallScores = new HashMap<Player, Double>();
		
		for (Match match : matches) {
			List<Player> players = match.getPlayers();
			
			for (int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				Double overallScore = overallScores.get(player);
				if (overallScore == null) {
					overallScore = 0.0;
				}
				
				overallScore += match.getScores().get(i);
				overallScores.put(player, overallScore);
			}
		}

		return overallScores; 
	}
	
	/**
	 * @return the number of matches that each player in the set has played
	 */
	public Map<Player, Integer> numMatchesPerPlayer() {
		Map<Player, Integer> numMatchesPerPlayer = new HashMap<Player, Integer>();
		
		for (Match match : matches) {
			List<Player> players = match.getPlayers();
			
			for (int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				Integer numMatches = numMatchesPerPlayer.get(player);
				if (numMatches == null) {
					numMatches = 0;
				}
				
				numMatches++;
				numMatchesPerPlayer.put(player, numMatches);
			}
		}

		return numMatchesPerPlayer; 
	}
	
	/**
	 * @return the average score per match per player.
	 */
	public double averageScorePerMatch() {
		return averageScorePerPlayer() / averageNumMatches();
	}

	/**
	 * @return the average score per player, summed up over all matches the player played.
	 */
	public double averageScorePerPlayer() {
		return average(overallScores());
	}
	
	/**
	 * @return the average number of matches per player.
	 */
	public double averageNumMatches() {
		return average(numMatchesPerPlayer());
	}
	
	/**
	 * @param map
	 * @return the average of the numbers in the map.
	 */
	private <K, V extends Number> double average(Map<K, V> map) {
		Set<Entry<K, V>> entries = map.entrySet();
		
		double sum = 0.0;
		for (Entry<K, V> entry : entries) {
			sum += entry.getValue().doubleValue();
		}
		
		return sum / entries.size();
	}
}