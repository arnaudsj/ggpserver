package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import tud.gamecontroller.game.impl.Game;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Match;

public class ViewState {
	private String matchID;
	private int stepNumber = -1;

	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public String getXmlState() throws SQLException, NamingException {
		AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
		Match<?, ?> match = db.getMatch(matchID);
		String stylesheet = ((Game)match.getGame()).getStylesheet();
		List<String> states = match.getXmlStates();

		// this is a hack to show old matches with the right stylesheets (e.g., if the stylesheet for a game was changed after the match)
		// we just replace the stylesheet information with the current one  
		Pattern styleSheetPattern=Pattern.compile("<\\?xml-stylesheet type=\"text/xsl\" href=\"[^\"]*\"\\?>");
		String styleSheetReplacement="<?xml-stylesheet type=\"text/xsl\" href=\""+stylesheet+"\"?>";
		
		int stepNumber;
		if (this.stepNumber < 1 || this.stepNumber > states.size()) {
			// return the last/final state
			stepNumber = states.size();
		} else {
			stepNumber = this.stepNumber;
		}
		
		return styleSheetPattern.matcher(states.get(stepNumber - 1)).replaceFirst(styleSheetReplacement);
	}
}
