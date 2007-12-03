package ggpratingsystem;

import java.util.LinkedList;
import java.util.List;

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
}