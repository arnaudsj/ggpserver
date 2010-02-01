package tud.ggpserver.util;

import java.util.List;



public class FilterOROperation extends FilterOperation{

	protected FilterOROperation(IdPool<FilterNode> ids, DropDownMenu menu,
			List<FilterNode> successors) {
		super(ids, Operations.OR, menu, successors);
		menu.setSelected(Operations.OR.toString());
	}

	
	protected FilterOROperation(IdPool<FilterNode> ids) {
		super(ids, Operations.OR);
		menu.setSelected(Operations.OR.toString());
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		for (FilterNode node : successors) {
			if (node.isMatching(matchInfo))
				return true;
		}
		
		return false;
	}
}
