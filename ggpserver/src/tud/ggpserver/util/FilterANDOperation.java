package tud.ggpserver.util;

import java.util.List;


public class FilterANDOperation extends FilterOperation{

	protected FilterANDOperation(IdPool<FilterNode> ids, DropDownMenu menu,
			List<FilterNode> successors) {
		super(ids, Operations.AND, menu, successors);
		
		menu.setSelected(Operations.AND.toString());
	}
	
	protected FilterANDOperation(IdPool<FilterNode> ids) {
		super(ids, Operations.AND);
		menu.setSelected(Operations.AND.toString());
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		for (FilterNode node : successors) {
			if (!node.isMatching(matchInfo))
				return false;
		}
		
		
		return true;
	}


}
