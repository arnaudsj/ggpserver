package tud.ggpserver.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerFilterRule extends FilterRule{
		
	private DropDownMenu isMenu;
	private TextBox pattern;
	private Pattern playerPattern;
	private Matcher playerMatcher;
	public PlayerFilterRule() {
		super();
		type = RuleTypes.Player;
	}
	
	private void init_matcher() {
		String patternString;
		patternString = pattern.getValue().replace('?', '.');
		patternString = patternString.replace("*", ".*");
		playerPattern = Pattern.compile(patternString);
		
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		boolean foundPlayer = false;
		for (PlayerInfo player : matchInfo.getPlayers()) {
			playerMatcher = playerPattern.matcher(player.getPlayerName());
			if (playerMatcher.matches()) {
				foundPlayer = true;
				break;
			}
		}
		
		if (isMenu.getSelected().equals("is"))
			return foundPlayer;
		else
			return !foundPlayer;
	}

	@Override
	public boolean update(String[] values) {
		if (!super.update(values))
			return false;
		
		isMenu.setSelected(values[1]);
		
		if (!values[2].equals(pattern.getValue())) {
			pattern.setValue(values[2]);
			init_matcher();
		}
		return true;
	}

	@Override
	public String getFormular() {
		return super.getFormular() + isMenu.getHtml() + pattern.getHtml();

	}



	@Override
	public void init() {
		ArrayList<String> options = new ArrayList<String>();
		options.add("is");
		options.add("is not");
		
		isMenu = new DropDownMenu(options, String.valueOf(myLeaf.getId()));
		isMenu.setSelected("is");
		
		pattern = new TextBox(String.valueOf(myLeaf.getId()));
		
		pattern.setValue("");
		init_matcher();
	}

}
