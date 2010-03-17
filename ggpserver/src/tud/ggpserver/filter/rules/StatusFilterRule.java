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
import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public class StatusFilterRule extends FilterRule{
	
	private String status;
	
	public StatusFilterRule(Filter filter) {
		this(filter, ServerMatch.STATUS_FINISHED);
	}
	
	public StatusFilterRule(Filter filter, String status) {
		super(FilterType.Status, filter);
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		return matchInfo.getStatus().equals(status);
	}

	@Override
	public boolean update(String[] values) {
		if(super.update(values)) // type has changed
			return true;
		if (values.length>=2 && !status.equals(values[1])) {
			status=values[1];
			return true;
		}
		return false;
	}
	
	@Override
	public String getHtml() {
		List<Option> options = Arrays.asList(
				new Option(ServerMatch.STATUS_NEW),
				new Option(ServerMatch.STATUS_SCHEDULED),
				new Option(ServerMatch.STATUS_RUNNING),
				new Option(ServerMatch.STATUS_FINISHED),
				new Option(ServerMatch.STATUS_ABORTED));
		DropDownMenu statusMenu = new DropDownMenu(String.valueOf(getId()), options, status);
		return super.getHtml()+statusMenu.getHtml();
	}

	@Override
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		where.append(" ").append(matchTableName).append('.').append("`status`").append("= ?");
		parameters.add(status);
		return true;
	}
}
