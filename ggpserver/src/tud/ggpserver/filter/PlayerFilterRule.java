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

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.util.IdPool;
import tud.ggpserver.util.PlayerInfo;

public class PlayerFilterRule extends StringMatchFilterRule{
		
	public PlayerFilterRule(IdPool<FilterNode> ids) {
		super(ids, FilterType.Player);
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		boolean foundPlayer = false;
		for (PlayerInfo player : matchInfo.getPlayers()) {
			if (patternMatches(player.getPlayerName())) {
				foundPlayer = true;
				break;
			}
		}
		
		if (isMenu.getSelectedValue().equals("is"))
			return foundPlayer;
		else
			return !foundPlayer;
	}

	@Override
	public String toString() {
		return "FilterRule[id:"+getID()+", player "+isMenu.getSelectedValue()+" "+patternTextBox.getValue()+"]";
	}
}
