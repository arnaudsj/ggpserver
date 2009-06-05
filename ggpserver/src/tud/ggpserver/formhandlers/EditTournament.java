package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;

public class EditTournament extends ShowMatches {
	public static final String ADD_MATCH = "add_match";
	public static final String START_MATCH = "start_match";
	public static final String DELETE_MATCH = "delete_match";
	public static final String CLONE_MATCH = "clone_match";
	
	private Tournament<?, ?> tournament;
	private String action;
	private Match<?, ?> match;
	private boolean correctlyPerformed = false;

	@SuppressWarnings("unchecked")
	public List<Game<?, ?>> getGames() throws SQLException {
		return db.getAllEnabledGames();
	}

	public void setMatchID(String matchID) throws SQLException {
		this.match = db.getMatch(matchID);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Tournament<?, ?> getTournament() {
		return tournament;
	}

	@Override
	public void setTournamentID(String tournamentID) throws SQLException {
		tournament = db.getTournament(tournamentID);
		super.setTournamentID(tournamentID);
	}

	@Override
	public String getTargetJsp() {
		// tournament could be null if it was not correctly set. This should
		// only happen if the user fiddled manually with the GET parameters, so
		// he deserves the NullPointerException that he or she will get.
		return "edit_tournament.jsp?tournamentID=" + tournament.getTournamentID();
	}

	@Override
	protected boolean excludeNewMatches() {
		return false;
	}
	
	public boolean isValid() {
		if (action.equals(ADD_MATCH)) {
			return true;
		} else if (action.equals(START_MATCH) && match != null) {
			return true;
		} else if (action.equals(DELETE_MATCH) && match != null) {
			return true;
		} else if (action.equals(CLONE_MATCH) && match != null) {
			return true;
		}
		return false;
	}
	
	public void performAction() throws SQLException {
		if (!isValid()) {
			throw new InternalError("performAction() called without checking isValid() first!");
		}

		System.out.println("performing action: " + action + " " + match.getMatchID());

		if (action.equals(ADD_MATCH)) {
			addMatch();
		} else if (action.equals(START_MATCH)) {
			startMatch(match);
		} else if (action.equals(DELETE_MATCH)) {
			deleteMatch(match);
		} else {
			assert (action.equals(CLONE_MATCH));
			cloneMatch(match);
		}
	}
	
	private void addMatch() {
		// TODO Auto-generated method stub
		correctlyPerformed = true;
	}

	private void startMatch(Match<?, ?> match) {
		// TODO Auto-generated method stub
		correctlyPerformed = true;
	}

	private void deleteMatch(Match<?, ?> match) {
		// TODO Auto-generated method stub
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void cloneMatch(Match<?, ?> match) throws SQLException {
		db.createMatch(match.getGame(), match.getStartclock(), match.getPlayclock(), match.getRolesToPlayers(), tournament);
		correctlyPerformed = true;
	}

	public boolean isCorrectlyPerformed() {
		return correctlyPerformed;
	}
	
	@SuppressWarnings("unchecked")
	public List<RemotePlayerInfo> getEnabledPlayerInfos() throws SQLException {
		return db.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE);
	}
}
