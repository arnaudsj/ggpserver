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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import tud.ggpserver.util.IdPool;

public abstract class FilterOperation extends FilterNode{
	private static final Logger logger = Logger.getLogger(FilterOperation.class.getName());

	protected List<FilterNode> successors = new LinkedList<FilterNode>();
	
	protected FilterOperation(IdPool<FilterNode> ids, FilterType type, Collection<FilterNode> successors) {
		super(ids, type);
		if(successors!=null){
			addSuccessors(successors);
		} else {
			addSuccessor(FilterFactory.getDefaultNode(ids));
		}
	}

	public synchronized void removeSuccessor(FilterNode node) {
		successors.remove(node);
	}
	
	public synchronized void addSuccessor(FilterNode node) {
		node.setParent(this);
		successors.add(node);
	}
	
	public synchronized void insertSuccessor(FilterNode node) {
		node.setParent(this);
		successors.add(0, node);
	}

	public synchronized void addSuccessors(Collection<FilterNode> nodes) {
		for (FilterNode node : nodes) {
			node.setParent(this);
		}
		successors.addAll(nodes);
	}

	/**
	 * like addSuccessors but deletes all current successors first
	 * @param nodes
	 */
	public synchronized void setSuccessors(Collection<FilterNode> nodes) {
		disposeSuccessors();
		addSuccessors(nodes);
	}

	public List<FilterNode> getSuccessors() {
		return Collections.unmodifiableList(successors);
	}

	/**
	 * resets the successors of this FilterOperation and returns the old successors   
	 * @return
	 */
	public synchronized List<FilterNode> unlinkSuccessors() {
		List<FilterNode> result=successors;
		this.successors=new LinkedList<FilterNode>();
		for (FilterNode node : result) {
			node.setParent(null);
		}
		return result;
	}

	protected void disposeSuccessors() {
		for (FilterNode node : successors) {
			node.dispose();
		}
		successors.clear();
	}
	
	@Override
	protected void dispose() {
		disposeSuccessors();
		super.dispose();
	}

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.getHtml());
		sb.append("<br/>");
		sb.append("<ul>");
		for (FilterNode node : successors) {
			sb.append("<li>");
			sb.append(node.getHtml());
			sb.append("</li>");
		}
		sb.append("</ul>");
		return sb.toString();
	}

	public void replaceSuccessor(FilterNode node, FilterNode newNode) {
		boolean found = false;
		ListIterator<FilterNode> i = successors.listIterator();
		while(i.hasNext()) {
			FilterNode currentNode = i.next();
			if (currentNode.equals(node)) {
				i.set(newNode);
				newNode.setParent(this);
				found = true;
				break;
			}
		}
		if (found) {
			if(!isRoot() && !i.hasNext() && !newNode.getType().equals(FilterType.Default)) {
				// the type of last node is changed -> add a new "new node"  
				addSuccessor(FilterFactory.getDefaultNode(ids));
			}
		} else {
			logger.warning("node "+node+" is not a successor");
			insertSuccessor(newNode);
		}
	}

	@Override
	public String toString() {
		String s="FilterOperation[id:"+getID()+", type:"+getType()+", successors:[";
		for(FilterNode n:successors) {
			s += n.toString()+", ";
		}
		s+="]]";
		return s;
	}

}
