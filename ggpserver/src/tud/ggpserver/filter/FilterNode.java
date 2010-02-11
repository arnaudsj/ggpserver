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
import java.util.logging.Logger;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.filter.DropDownMenu.Option;
import tud.ggpserver.util.IDItem;
import tud.ggpserver.util.IdPool;


public class FilterNode implements IDItem {
	public static enum FilterType {
		Default("new node"), And("AND"), Or("OR"), Player("player"), Game("game");
		private String name;
		private FilterType(String name) {
			this.name = name;
		}
		public String getName(){
			return name;
		}
	};

	private static final String deleteString = "delete";

	protected IdPool<FilterNode> ids;

	private FilterOperation parent = null;
	private long id;

	protected DropDownMenu menu;

	private FilterType type;
	
	protected FilterNode(IdPool<FilterNode> ids) {
		this(ids, FilterType.Default);
	}

	protected FilterNode(IdPool<FilterNode> ids, FilterType type) {
		super();
		this.ids = ids;
		this.type = type;
		id = ids.getNewId(this);
		menu = new DropDownMenu(getMenuOptions(), String.valueOf(getID()));
		menu.setSelectedValue(type.toString());
	}

	private static List<Option> getMenuOptions() {
		List<Option> options = new LinkedList<Option>();
		for (FilterType type : FilterType.values()) {
			options.add(new Option(type.getName(), type.toString()));
		}
		options.add(new Option(deleteString));
		return options;
	}

	public FilterType getType() {
		return type;
	}
	
	public void setParent(FilterOperation parent) {
		this.parent = parent;
	}

	public boolean isMatching(MatchInfo matchInfo) {
		if(parent == null) { // default filter is true for an "and", false for an "or" and true if it is the root
			return true;
		}
		return parent.getType().equals(FilterType.And); 
	}
	
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
		boolean changed = true;
		String menuSelection = values[0];
		if (menuSelection.equals(type.toString())) {
			changed = false;
		} else if (menuSelection.equals(deleteString)) {
			dispose();
			parent.removeSuccessor(this);
		} else {
			FilterType newFilterType = FilterType.valueOf(menuSelection);
			FilterNode newNode = FilterFactory.createFilterNode(newFilterType, ids);
			parent.replaceSuccessor(this, newNode);
			if(newNode instanceof FilterOperation) {
				if(this instanceof FilterOperation) {
					// keep the successors of the old FilterOperation
					((FilterOperation)newNode).setSuccessors(((FilterOperation)this).unlinkSuccessors());
					dispose();
				} else if(this instanceof FilterRule) {
					// add the old node as first successor to the new FilterOperation 
					((FilterOperation)newNode).insertSuccessor(this);
				}else{
					dispose();
				}
			}else{
				dispose();
			}
		}
		return changed;
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
	public long getID() {
		return id;
	}

	@Override
	public void setID(long id) {
		this.id=id;
	}

	@Override
	public String toString() {
		return "FilterNode[id:"+id+", type:"+type+"]";
	}
}
