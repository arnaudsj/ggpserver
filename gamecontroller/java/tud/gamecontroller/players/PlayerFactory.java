/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>, Nicolas JEAN <njean42@gmail.com>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.players;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;

public class PlayerFactory {

	public static <TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		Player<TermType, StateType> createRemotePlayer(RemotePlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gameScrambler, GDLVersion gdlVersion) {
		return new RemotePlayer<TermType, StateType>(info.getName(), info.getHost(), info.getPort(), movefactory, gameScrambler, gdlVersion);
	}
	
	public static <TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		Player<TermType, StateType> createRandomPlayer(RandomPlayerInfo info, GDLVersion gdlVersion) {
		return new RandomPlayer<TermType, StateType>(info.getName(), gdlVersion);
	}
	
	public static <TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		Player<TermType, StateType> createLegalPlayer(LegalPlayerInfo info, GDLVersion gdlVersion) {
		return new LegalPlayer<TermType, StateType>(info.getName(), gdlVersion);
	}
	
	public static <TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		Player<TermType, StateType> createPlayer(PlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gameScrambler, GDLVersion gdlVersion) {
		if(info instanceof RemotePlayerInfo){
			return PlayerFactory. <TermType, StateType> createRemotePlayer((RemotePlayerInfo)info, movefactory, gameScrambler, gdlVersion);
		}else if(info instanceof RandomPlayerInfo){
			return PlayerFactory. <TermType, StateType> createRandomPlayer((RandomPlayerInfo)info, gdlVersion);
		}else if(info instanceof LegalPlayerInfo){
			return PlayerFactory. <TermType, StateType> createLegalPlayer((LegalPlayerInfo)info, gdlVersion);
		}
		return null;
	}


}
