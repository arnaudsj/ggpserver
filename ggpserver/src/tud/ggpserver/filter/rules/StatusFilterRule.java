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

package tud.ggpserver.filter.rules;

import java.util.Arrays;
import java.util.List;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;
import tud.ggpserver.util.IdPool;

public class StatusFilterRule extends FilterRule{
	
	private DropDownMenu statusMenu;
	
	public StatusFilterRule(IdPool<FilterNode> ids) {
		super(ids, FilterType.Status);
		List<Option> options = Arrays.asList(
				new Option(ServerMatch.STATUS_NEW),
				new Option(ServerMatch.STATUS_SCHEDULED),
				new Option(ServerMatch.STATUS_RUNNING),
				new Option(ServerMatch.STATUS_FINISHED),
				new Option(ServerMatch.STATUS_ABORTED));
		statusMenu = new DropDownMenu(String.valueOf(getID()), options);
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		return matchInfo.getStatus().equals(statusMenu.getSelectedValue());
	}

	@Override
	public boolean update(String[] values) {
		if(super.update(values)) // type has changed
			return true;
		if (!statusMenu.getSelectedValue().equals(values[1])) {
			statusMenu.setSelectedValue(values[1]);
			return true;
		}
		return false;
	}
}
