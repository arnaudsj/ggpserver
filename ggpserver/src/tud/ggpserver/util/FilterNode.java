package tud.ggpserver.util;


public abstract class FilterNode {
	protected FilterOperation parent = null;
	private Long id;
	private Filter filter;
	
	protected void changeRootNode(FilterNode newRoot) {
		if (parent == null) {
			filter.setRootNode(newRoot);
			newRoot.setFilter(filter);
		}
	}

	
	public void setParent(FilterOperation parent) {
		this.parent = parent;
	}


	public void setFilter(Filter filter) {
		this.filter = filter;
	}


	protected IdPool<FilterNode> ids;

	protected FilterNode(IdPool<FilterNode> ids) {
		super();
		this.ids = ids;
		id = ids.getNewId();
	}
	
	public abstract boolean isMatching(MatchInfo matchInfo);
	
	public void delete() {
		dispose();
		
		if (!isRoot())
			parent.removeSuccessor(this);
	}
	
	public int getPosition() {
		if (isRoot())
			return 0;
		
		return parent.getChildPosition(id);
	}
	
	protected abstract void dispose();
	public abstract void addNode(FilterNode node);
	public abstract void update(String[] values);
	public abstract String getTable(int level);
	protected abstract String getFormular();
	
	protected boolean isRoot() {
		return parent == null;
	}

	public Long getId() {
		return id;
	}

}
