/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;

public class PlayerFactory {

	public static <TermType extends TermInterface> Player<TermType> createRemotePlayer(RemotePlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gameScrambler) {
		return new RemotePlayer<TermType>(info.getName(), info.getHost(), info.getPort(), movefactory, gameScrambler);
	}
	public static <TermType extends TermInterface> Player<TermType> createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer<TermType>(info.getName());
	}
	public static <TermType extends TermInterface> Player<TermType> createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer<TermType>(info.getName());
	}
	public static <TermType extends TermInterface> Player<TermType> createPlayer(PlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> termfactory, GameScramblerInterface gameScrambler) {
		if(info instanceof RemotePlayerInfo){
			return createRemotePlayer((RemotePlayerInfo)info, termfactory, gameScrambler);
		}else if(info instanceof RandomPlayerInfo){
			return createRandomPlayer((RandomPlayerInfo)info);
		}else if(info instanceof LegalPlayerInfo){
			return createLegalPlayer((LegalPlayerInfo)info);
		}
		return null;
	}


}
