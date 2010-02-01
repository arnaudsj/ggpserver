package tud.ggpserver.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tud.ggpserver.util.MatchInfoRule.GameType;
import tud.ggpserver.util.MatchInfoRule.Type;

public class PlayerRule extends MatchInfoRule {
	private Matcher playerMatcher;
	
	


	public PlayerRule(String playerString) {
		super();
		playerType = PlayerType.played;
		ruleType = Type.player;
		this.playerString = playerString;
		playerString = playerString.replace('?', '.');
		playerString = playerString.replace("*", ".*");
		this.pattern = Pattern.compile(playerString);
	}

	@Override
	public boolean isMatching(MatchInfo matchinfo) {
		boolean ret = false;
		for (PlayerInfo player : matchinfo.getPlayers()) {
			playerMatcher = pattern.matcher(player.getPlayerName());
			if (playerMatcher.matches())
				ret = true;
		}
				
		if (playerType.equals(PlayerType.played))
			return ret;
		else
			return !ret;
	}

	@Override
	public String getString(Long idd) {
		Long id = 42l;
		String selectString = getTypSelect(id);
		LinkedList<String[]> options = new LinkedList<String[]>();
		selectString += getTextBox("playerpattern="+id, "playerpattern="+id, 10, playerString);
		String[] played = {"played the game", "played"};
		String[] playedNot = {"played not the game", "playedNot"};
		options.add(played);
		options.add(playedNot);
		
		String select = "";
		switch(playerType) {
		case played:
			select = "played the game";
			break;
		case playedNot:
			select = "played not the game";
			break;
		}
		
		selectString += getOptionSelect("playeroption="+id, "playeroption="+id, select, "theForm.action='process_save_filter.jsp'; theForm.submit();",options);
		
		
		return selectString;
	}
}
