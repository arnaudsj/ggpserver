package tud.ggpserver.util;

public class FilterLeaf extends FilterNode {
	private FilterRule rule;
	
	public static FilterLeaf getDefaultLeaf(IdPool<FilterNode> ids) {
		return new FilterLeaf(ids, new PlayerFilterRule());
	}
	
	protected FilterLeaf(IdPool<FilterNode> ids, FilterRule rule) {
		super(ids);
		this.rule = rule;
		rule.setLeaf(this);
	}

	@Override
	public void addNode(FilterNode node) {
		parent.addNode(node);
	}
	
	public void changeFilterRule(FilterRule newRule) {
		rule = newRule;
		rule.setLeaf(this);
	}

	@Override
	protected void dispose() {
		rule = null;		
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		return rule.isMatching(matchInfo);
	}

	@Override
	public void update(String[] values) {
		rule.update(values);
	}

	@Override
	public String getTable(int level) {
		String table = "<tr>";
		for (int i = 0; i < level; ++i)
			table += "<td/>";
		
		table += "<td>" + getFormular() + "</td>";

		return table + "</tr>\n";
	}

	@Override
	protected String getFormular() {
		return rule.getFormular();
	}

}
