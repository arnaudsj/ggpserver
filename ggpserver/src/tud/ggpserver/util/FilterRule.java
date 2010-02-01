package tud.ggpserver.util;

import java.util.ArrayList;

public abstract class FilterRule {
	protected FilterLeaf myLeaf;
	
	public enum RuleTypes {Player, Game}; 
	protected RuleTypes type;
	
	private String deleteString = "delete";
	
	public void setLeaf(FilterLeaf myLeaf) {
		this.myLeaf = myLeaf;
		
		ArrayList<String> options = new ArrayList<String>();
		RuleTypes[] values = RuleTypes.values();
		
		for (RuleTypes rtype : values) {
			options.add(rtype.toString());
		}
		
		options.add(deleteString);
		
		menu = new DropDownMenu(options, String.valueOf(myLeaf.getId()));
		
		init();
	}
	public abstract void init();
	public abstract boolean isMatching(MatchInfo matchInfo);
	public boolean update(String[] values) {
		String newType = values[0];
		
		if (newType.equals(deleteString)) {
			myLeaf.delete();
			return false;
		}
		
		if (RuleTypes.valueOf(newType).equals(type))
			return true;
		
		
			
		
		changeFilterRule(newType);
		
		return false;
	}
	
	private void changeFilterRule(String newRule) {
		RuleTypes newType = RuleTypes.valueOf(newRule);
		
		switch (newType) {
		case Player:
			myLeaf.changeFilterRule(new PlayerFilterRule());
			break;
			
		case Game:
			myLeaf.changeFilterRule(new GameFilterRule());
			break;
		}

	}
	
	protected DropDownMenu menu;

	
	public String getFormular() {
		menu.setSelected(type.toString());
		return menu.getHtml();
	}
}
