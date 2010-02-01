package tud.ggpserver.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameFilterRule extends FilterRule{
	
	private DropDownMenu isMenu;
	private TextBox pattern;
	private Pattern gamePattern;
	private Matcher gameMatcher;
	
	public GameFilterRule() {
		super();
		type = RuleTypes.Game;
	}
	
	private void init_matcher() {
		String patternString;
		patternString = pattern.getValue().replace('?', '.');
		patternString = patternString.replace("*", ".*");
		gamePattern = Pattern.compile(patternString);
		
	}
	
	@Override
	public String getFormular() {
		return super.getFormular() + isMenu.getHtml() + pattern.getHtml();

	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		boolean foundGame = false;
		gameMatcher = gamePattern.matcher(matchInfo.getGameName());
		if (gameMatcher.matches())
			foundGame = true;
		
		
		if (isMenu.getSelected().equals("is"))
			return foundGame;
		else
			return !foundGame;
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
