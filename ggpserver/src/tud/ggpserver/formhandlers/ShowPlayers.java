package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public class ShowPlayers extends AbstractPager {
	private static final Logger logger = Logger.getLogger(ShowPlayers.class.getName());

	protected final static AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();

	public List<PlayerInfo> getPlayers() throws NamingException, SQLException {
		List<PlayerInfo> result = db.getPlayerInfos(startRow, numDisplayedRows);
		
		if (!onlyRemotePlayerInfos(result)) {
			logger.severe("DBConnector.getPlayerInfos() returned a PlayerInfo that was not a RemotePlayerInfo!"); //$NON-NLS-1$
			// this must not happen because show_players.jsp will call getOwner() on
			// the result, which is only defined for RemotePlayerInfos.
			assert(false);  
		}
		
		return result;
	}
	
	private boolean onlyRemotePlayerInfos(List<PlayerInfo> result) {
		for (PlayerInfo info : result) {
			if (!(info instanceof RemotePlayerInfo)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getTableName() {
		return "players";
	}

	@Override
	public String getTargetJsp() {
		return "show_players.jsp";
	}
}
