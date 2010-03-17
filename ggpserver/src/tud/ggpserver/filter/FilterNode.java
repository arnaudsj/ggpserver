/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.filter;

import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;
import tud.ggpserver.filter.rules.FilterRule;
import tud.ggpserver.util.IDItem;
import tud.ggpserver.util.IdPool;


public abstract class FilterNode implements IDItem {
	public static enum FilterType {
		And("AND"), Or("OR"), Game("game"), RoleNumber("#roles"), PlayClock("play clock"), Player("player"), Owner("owner"), StartClock("start clock"), StartTime("start time"), Status("status"), Tournament("tournament");
		private String name;
		private FilterType(String name) {
			this.name = name;
		}
		public String getName(){
			return name;
		}
	};

	private static final String deleteString = "delete";

	private IdPool<FilterNode> ids;

	protected Filter filter;

	private FilterOperation parent = null;
	private long id;

	protected DropDownMenu menu;

	private FilterType type;
	
	protected FilterNode(FilterType type, Filter filter) {
		this(filter.getIdPool(), type, filter);
	}

	protected FilterNode(IdPool<FilterNode> ids, FilterType type, Filter filter) {
		super();
		this.type = type;
		this.ids = ids;
		this.filter = filter;
		id = ids.getNewId(this);
		menu = new DropDownMenu(String.valueOf(getId()), getMenuOptions());
		menu.setSelectedValue(type.toString());
		menu.setSubmitOnChange(true);
	}

	private static List<Option> getMenuOptions() {
		List<Option> options = getTypeOptions();
		options.add(new Option(deleteString));
		return options;
	}

	public static List<Option> getTypeOptions() {
		List<Option> options = new LinkedList<Option>();
		for (FilterType type : FilterType.values()) {
			options.add(new Option(type.getName(), type.toString()));
		}
		return options;
	}

	public IdPool<FilterNode> getIdPool() {
		return ids;
	}

	public FilterType getType() {
		return type;
	}
	
	public void setParent(FilterOperation parent) {
		this.parent = parent;
	}

	public abstract boolean isMatching(MatchInfo matchInfo);
//	public boolean isMatching(MatchInfo matchInfo) {
//		if(parent == null) { // default filter is true for an "and", false for an "or" and true if it is the root
//			return true;
//		}
//		return parent.getType().equals(FilterType.And); 
//	}
	
	/**
	 * deletes this node and all successors
	 */
	protected void dispose() {
		ids.removeItem(id);
	}

	/**
	 * update the filter node with the values as given by the HTTP request 
	 * @param values
	 * @return true if some value has changed, false otherwise
	 */
	public boolean update(String[] values) {
		if(isRoot()) {
			return false; // the type of the root not can not change
		}
		if(values.length>=1) {
			boolean changed = true;
			String menuSelection = values[0];
			if (menuSelection.equals(type.toString())) {
				changed = false;
			} else if (menuSelection.equals(deleteString)) {
				dispose();
				parent.removeSuccessor(this);
			} else {
				FilterType newFilterType = FilterType.valueOf(menuSelection);
				FilterNode newNode = FilterFactory.createFilterNode(newFilterType, filter);
				parent.replaceSuccessor(this, newNode);
				if(newNode instanceof FilterOperation) {
					if(this instanceof FilterOperation) {
						// keep the successors of the old FilterOperation
						((FilterOperation)newNode).setSuccessors(((FilterOperation)this).unlinkSuccessors());
						dispose();
					} else if(this instanceof FilterRule) {
						// add the old node as successor to the new FilterOperation 
						((FilterOperation)newNode).addSuccessor(this);
					}else{
						dispose();
					}
				}else{
					dispose();
				}
			}
			return changed;
		}else{
			return false;
		}
	}

	/**
	 * @return the html representation of the filter node
	 */
	public String getHtml() {
		return menu.getHtml();
	}
	
	protected boolean isRoot() {
		return parent == null;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id=id;
	}

	@Override
	public String toString() {
		return "FilterNode[id:"+id+", type:"+type+"]";
	}
	
	public FilterOperation getParent() {
		return parent;
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	/**
	 * The method should try to add some conditions to the where part of a select statement such that the returned records are matched by the filter.
	 * The default implementation does nothing.
	 * @param matchTableName
	 * @param matchPlayerTableName
	 * @param where
	 * @param parameters
	 * @return true, if the added conditions are equivalent to the FilterNodes isMatching method. 
	 */
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		where.append(" TRUE");
		return false;
	}
}
