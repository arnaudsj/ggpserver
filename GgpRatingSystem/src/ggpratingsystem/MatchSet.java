package ggpratingsystem;

import java.util.HashMap;
import java.util.HashSet;
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
	
	private Set<Player> players = new HashSet<Player>();
	
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
		List<Player> players = match.getPlayers();
		for (Player player : players) {
			this.players.add(player);
		}
	}

	@Override
	public String toString() {
//		String result = "";
//		result += getId() + ";";
//		result += getGame().toString() + ";";
//		result += getYear() + ";";
//		result += getRound() + ";";
//		result += getDay() + ";";
//		result += getMatchSetNumber() + ";";
//		result += getMatches();
//		return result;
		return getId();
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
			
			for (Player player : players) {
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
	 * Calculates kind of a "prior probability" of the player scores in this
	 * MatchSet: The expected score without taking the player's ratings into
	 * account.
	 * 
	 * The formula used is: 
	 *    score of player p = (number of games where p has played role r) 
	 *                             x (mean score for role r)
	 *                             
	 */
	public Map<Player, Double> expectedScoreWithoutRatings() {
		Map<Player, List<Integer>> roleCounts = countRoles();
		List<Double> averageRoleScore = averageRoleScore();
		
		Map<Player, Double> result = new HashMap<Player, Double>();
		
		assert(this.getPlayers().equals(roleCounts.keySet()));
		
		Set<Player> players = this.getPlayers();
		for (Player player: players) {
			List<Integer> roleCountsThisPlayer = roleCounts.get(player);
			
			int numRoles = roleCountsThisPlayer.size();
			assert (numRoles == averageRoleScore.size());
			
			Double expectedScore = 0.0;
			for (int i = 0; i < numRoles; i++) {
				expectedScore += roleCountsThisPlayer.get(i) * averageRoleScore.get(i);
			}
			result.put(player, expectedScore);
		}
		
		return result;
	}

	/**
	 * this method is simply for debugging purposes
	 */
	public void printMatches() {
		for (Match match : matches) {
			match.print();
		}
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
	 * @return list of scores indicating what average score a role received
	 */
	public List<Double> averageRoleScore() {
		List<Double> result = new LinkedList<Double>();
		
		int numRoles = game.getRoles().size();
		
		for (int i = 0; i < numRoles; i++) {
			double sum = 0.0; 
			for (Match match: matches) {
				sum += match.getScores().get(i);
			}
			result.add(Double.valueOf(sum / matches.size()));
		}
		return result;
	}
	
	
	public Set<Player> getPlayers() {
		return players;
	}

	/**
	 * @return a list of integers, indicating how many times a player played a
	 *         certain role
	 */
	private Map<Player, List<Integer>> countRoles() {
		Map<Player, List<Integer>> roleCounts = new HashMap<Player, List<Integer>>();
		
		for (Match match : matches) {
			List<Player> players = match.getPlayers();
			
			for (int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				List<Integer> roleCountsThisPlayer = roleCounts.get(player);
				
				// if list doesn't exist, create it and fill if with the correct
				// number of Integers (one Integer for each role)
				if (roleCountsThisPlayer == null) {
					roleCountsThisPlayer = new LinkedList<Integer>();
					for (int j = 0; j < players.size(); j++) {
						roleCountsThisPlayer.add(Integer.valueOf(0));
					}
				}
				
				Integer roleCount = roleCountsThisPlayer.get(i);
				roleCount++;
				roleCountsThisPlayer.set(i, roleCount);
				roleCounts.put(player, roleCountsThisPlayer);
			}
		}

		return roleCounts; 
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