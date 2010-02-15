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

package tud.ggpserver.filter.rules;

import org.apache.commons.lang.StringEscapeUtils;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.filter.matcher.LongMatcher;
import tud.ggpserver.filter.matcher.StringMatcher;
import tud.ggpserver.util.IdPool;
import tud.ggpserver.util.PlayerInfo;

public class PlayerFilterRule extends FilterRule{
	
	private static final String HINT = "<span class=\"hint\">" + StringEscapeUtils.escapeHtml("(role is a number >=1 or '*' for any; the player name may contain the wildcards '?' and '*')") + "</span>";
	private LongMatcher roleMatcher;  
	private StringMatcher playerMatcher;
	
	public PlayerFilterRule(IdPool<FilterNode> ids) {
		super(ids, FilterType.Player);
		roleMatcher = new LongMatcher(String.valueOf(getID()), 1, 100);
		roleMatcher.setPattern("*");
		playerMatcher = new StringMatcher(String.valueOf(getID()));
	}

	@Override
	public boolean update(String[] values) {
		if (super.update(values)) // type has changed -> no further changes to make
			return true;
		boolean changed = false;
		if(values.length>=5) {
			if(roleMatcher.update(values[1], values[2]))
				changed=true;
			if(playerMatcher.update(values[3], values[4]))
				changed=true;
		}
		return changed;
	}

	@Override
	public String getHtml() {
		return super.getHtml() + "with role " + roleMatcher.getHtml() + playerMatcher.getHtml() + "<br/>" + HINT;
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		boolean foundPlayer = false;
		for (PlayerInfo player : matchInfo.getPlayers()) {
			if(roleMatcher.isMatching(player.getRoleindex().longValue() + 1)) { // only consider the roles that match the RoleIndex
				if (playerMatcher.patternMatches(player.getPlayerName())) {
					foundPlayer = true;
					break;
				}
			}
		}
		
		if (playerMatcher.shouldMatch())
			return foundPlayer;
		else
			return !foundPlayer;
	}

	@Override
	public String toString() {
		return "FilterRule[id:"+getID()+", player at role "+roleMatcher.toString()+" "+playerMatcher.toString()+"]";
	}
}
