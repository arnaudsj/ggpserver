package tud.ggpserver.formhandlers.inc;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

import tud.gamecontroller.auxiliary.Pair;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.MatchRunner;

public class MatchesAccessor {
	protected final AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
	private User user = null;
	List<Pair<ServerMatch<?,?>,Boolean>> scheduledMatches = null;
	List<ServerMatch<?,?>> runningMatches = null;
	
	public void setUserName(String userName) throws SQLException {
		user = getDBConnector().getUser(userName);
	}
	
	public boolean isSomeRunningMatches () throws SQLException {
		return getMyRunningMatches().size() != 0;
	}
	
	public List<ServerMatch<?,?>> getMyRunningMatches () throws SQLException {
		if (runningMatches == null)
			runningMatches = getMatches(ServerMatch.STATUS_RUNNING);
		return runningMatches;
	}
	
	public boolean isSomeScheduledMatches () throws SQLException {
		return getMyScheduledMatches().size() != 0;
	}
	
	public boolean isAtLeastOneAcceptedScheduledMatch () throws SQLException {
		for (Pair<ServerMatch<?,?>,Boolean> p: getMyScheduledMatches())
			if (p.getRight()) return true;
		return false;
	}
	
	public List<Pair<ServerMatch<?,?>,Boolean>> getMyScheduledMatches () throws SQLException {
		if (scheduledMatches == null) {
			scheduledMatches = new LinkedList<Pair<ServerMatch<?,?>,Boolean>>();
			List<ServerMatch<?,?>> matches = getMatches(ServerMatch.STATUS_SCHEDULED);
			for (ServerMatch<?,?> match: matches)
				scheduledMatches.add(new Pair<ServerMatch<?,?>,Boolean>(match, MatchRunner.getInstance().hasAccepted(match.getMatchID(), user.getUserName())));
		}
		return scheduledMatches;
	}
	
	@SuppressWarnings("unchecked")
	private List<ServerMatch<?,?>> getMatches (String status) throws SQLException {
		List<? extends ServerMatch<?,?>> matches = db.getMatches(0, 20, user.getUserName(), null, null, null, status, false);
		Iterator<ServerMatch<?,?>> it = (Iterator<ServerMatch<?,?>>) matches.iterator();
		while(it.hasNext()) {
			ServerMatch<?,?> m = it.next();
			if (matches.lastIndexOf(m) != matches.indexOf(m))
				it.remove();
		}
		return (List<ServerMatch<?,?>>) matches;
	}
	
}
