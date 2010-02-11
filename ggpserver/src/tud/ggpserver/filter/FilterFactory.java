/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.util.logging.Logger;

import tud.ggpserver.filter.FilterNode.FilterType;
import tud.ggpserver.util.IdPool;

public class FilterFactory {

	private static final Logger logger = Logger.getLogger(FilterFactory.class.getName());

	public static FilterNode createFilterNode(FilterType type, IdPool<FilterNode> ids) {
		FilterNode node = null;
		switch(type) {
		case Default:
			node = new FilterNode(ids);
			break;
		case And:
			node = new FilterANDOperation(ids);
			break;
		case Or:
			node = new FilterOROperation(ids);
			break;
		case Player:
			node = new PlayerFilterRule(ids);
			break;
		case Game:
			node = new GameFilterRule(ids);
			break;
		default:
			logger.severe("unknown type:"+type);
		}
		return node;
	}

	public static FilterNode getDefaultNode(IdPool<FilterNode> ids) {
		return new FilterNode(ids);
	}

}
