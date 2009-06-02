package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import tud.ggpserver.datamodel.Tournament;

public class EditTournament extends ShowMatches {
	private Tournament<?, ?> tournament;

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
		// TODO: tournament could be null
		return "edit_tournament.jsp?tournamentID=" + tournament.getTournamentID();
	}

	@Override
	protected boolean excludeNewMatches() {
		return false;
	}
}
