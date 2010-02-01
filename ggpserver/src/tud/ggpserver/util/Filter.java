package tud.ggpserver.util;

import java.util.List;

public class Filter {
	private FilterNode rootNode;
	private IdPool<FilterNode> ids;
	private List<String> selectedMatches;

	
	

	public List<String> getSelectedMatches() {
		return selectedMatches;
	}


	public void setSelectedMatches(List<String> selectedMatches) {
		this.selectedMatches = selectedMatches;
	}


	public Filter() {
		super();
		ids = new IdPool<FilterNode>();
		rootNode = new FilterANDOperation(ids);
		rootNode.setFilter(this);
		ids.addItem(rootNode, rootNode.getId());
	}
	
	
	public void setRootNode(FilterNode rootNode) {
		ids.removeItem(this.rootNode.getId());
		this.rootNode = rootNode;
		ids.addItem(rootNode, rootNode.getId());
	}

	public boolean isMatching(MatchInfo matchInfo) {
		return rootNode.isMatching(matchInfo);
	}

	public void testInit() {
		FilterOROperation or = new FilterOROperation(ids);
		FilterLeaf leaf = new FilterLeaf(ids, new PlayerFilterRule());
		or.addNode(leaf);
		rootNode.addNode(or);
	}
	
	public String getTable() {
		String table = "<table>\n<tbody>\n";
		table += rootNode.getTable(0);
		
		return table + "</tbody>\n</table>\n";
	}
	
	public void update(Long id, String[] values) {
		if (ids.containsItem(id))
			ids.getItem(id).update(values);
	}

}
