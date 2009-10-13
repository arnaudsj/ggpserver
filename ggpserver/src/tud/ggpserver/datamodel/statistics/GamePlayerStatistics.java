/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.ggpserver.datamodel.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.Game;

public class GamePlayerStatistics<TermType extends TermInterface, ReasonerStateInfoType> {
	
	private Game<TermType, ReasonerStateInfoType> game;
	private Map<? extends PlayerInfo, PerformanceInformation> informationPerPlayer;
	private List<? extends PlayerInfo> sortedPlayers;
	private RoleInterface<TermType> role;

	public GamePlayerStatistics(Game<TermType, ReasonerStateInfoType> game,
			Map<? extends PlayerInfo, PerformanceInformation> informationPerPlayer) {
		this(game, null, informationPerPlayer);
	}
	
	public GamePlayerStatistics(
			Game<TermType, ReasonerStateInfoType> game,
			RoleInterface<TermType> role,
			Map<? extends PlayerInfo, PerformanceInformation> informationPerPlayer) {
		this.game = game;
		this.role = role;
		this.informationPerPlayer = informationPerPlayer;
	}

	public Game<TermType, ReasonerStateInfoType> getGame() {
		return game;
	}

	public RoleInterface<TermType> getRole() {
		return role;
	}

	public Map<? extends PlayerInfo, PerformanceInformation> getInformationPerPlayer() {
		return informationPerPlayer;
	}

	public Collection<? extends PlayerInfo> getPlayers() {
		return informationPerPlayer.keySet();
	}

	public List<? extends PlayerInfo> getSortedPlayers() {
		if (sortedPlayers == null) {
			sortedPlayers = new ArrayList<PlayerInfo>(informationPerPlayer.keySet());
			Collections.sort(sortedPlayers, new Comparator<PlayerInfo>() {
				@Override
				public int compare(PlayerInfo o1, PlayerInfo o2) {
					return Double.compare(informationPerPlayer.get(o2).getAverageScore(), informationPerPlayer.get(o1).getAverageScore());
				}
			});
		}
		return sortedPlayers;
	}

}
