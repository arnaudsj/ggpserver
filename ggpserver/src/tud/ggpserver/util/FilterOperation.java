package tud.ggpserver.util;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterOperation extends FilterNode{
	public enum Operations {AND, OR};
	protected Operations type;
	
	protected DropDownMenu menu;
	
	protected void changeOperationType(Operations newOperation) {
		FilterNode newNode = null;
		
				
		switch (newOperation) {
		case AND:
			newNode = new FilterANDOperation(ids,menu,successors);
			break;
		case OR:
			newNode = new FilterOROperation(ids,menu,successors);
			break;
		}
		
		if (isRoot()) {
			changeRootNode(newNode);
		}
		else {
			parent.addSuccessor(newNode, getPosition());
			parent.removeSuccessor(this);
		}
	}
	
	
	
	protected FilterOperation(IdPool<FilterNode> ids, Operations type, DropDownMenu menu,
			List<FilterNode> successors) {
		super(ids);
		this.type = type;
		this.menu = menu;
		this.successors = successors;
		menu.setId(getId());
	}


	private String newNodeString = "new node";
	private String newLeafString = "new leaf";
	private String deleteString = "delete";

	protected FilterOperation(IdPool<FilterNode> ids, Operations type) {
		super(ids);
		this.type = type;
		
		ArrayList<String> options = new ArrayList<String>();
		Operations[] values = Operations.values();
		
		for (Operations operation : values) {
			options.add(operation.toString());
		}
		
		options.add(newNodeString);
		options.add(newLeafString);
		options.add(deleteString);
		
		menu = new DropDownMenu(options, String.valueOf(getId()));
	}

	protected List<FilterNode> successors = new ArrayList<FilterNode>();
	
	public void removeSuccessor(FilterNode otherNode) {
		for (int i = 0; i < successors.size(); ++i) {
			FilterNode node = successors.get(i);
			if (node.equals(otherNode)) {
				successors.remove(i);
				ids.removeItem(node.getId());
				return;
			}
		}
	}
	
	private void addSuccessor(FilterNode newNode) {
		newNode.setParent(this);
		successors.add(newNode);
		ids.addItem(newNode, newNode.getId());
	}
	
	private void addSuccessor(FilterNode newNode, int position) {
		newNode.setParent(this);
		successors.add(position, newNode);
		ids.addItem(newNode, newNode.getId());
	}
	
	public int getChildPosition(Long id) {
		for (int i = 0; i < successors.size(); ++i) {
			if (successors.get(i).getId().equals(id))
				return i;
		}
		
		return 0;
	}
	
	@Override
	protected void dispose() {
		for (FilterNode node : successors) {
			node.dispose();
			ids.removeItem(node.getId());
		}
		
		successors.clear();
	}

	@Override
	public void addNode(FilterNode node) {
		addSuccessor(node);
	}
	
	public void addNodeAt(FilterNode node, int position) {
		addSuccessor(node, position);
	}
	

	@Override
	public String getTable(int level) {
		String table = "<tr>";
		for (int i = 0; i < level; ++i)
			table += "<td/>";
		
		table += "<td>" + getFormular() + "</td></tr>\n";
		
		for (FilterNode node : successors) {
			table += node.getTable(level + 1);
		}
		return table;
	}
	
	@Override
	protected String getFormular() {
		return menu.getHtml();
	}
	
	public static FilterOperation getDefaultFilterOperation(IdPool<FilterNode> ids) {
		return new FilterANDOperation(ids);
	}
	
	@Override
	public void update(String[] values) {
		String operationType = values[0];
		if (operationType.equals(type.toString()))
			return;
		
		if (operationType.equals(newLeafString)) {
			addNode(FilterLeaf.getDefaultLeaf(ids));
			return;
		}
		
		if (operationType.equals(newNodeString)) {
			addNode(FilterOperation.getDefaultFilterOperation(ids));
			return;
		}
		
		if (operationType.equals(deleteString)) {
			delete();
			return;
		}

		changeOperationType(Operations.valueOf(operationType));
	}

}
