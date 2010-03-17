/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
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

package tud.ggpserver.datamodel;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.term.TermInterface;

public class Game<TermType extends TermInterface, ReasonerStateInfoType>
		extends tud.gamecontroller.game.impl.Game<TermType, ReasonerStateInfoType> {

	private boolean enabled;
	
	private User creator;
	
	public Game(String gameDescription, String name,
			ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory,
			GDLVersion gdlVersion,
			String stylesheet,
			String seesXMLRules,
			boolean enabled, User creator) {
		super(gameDescription, name, reasonerFactory, gdlVersion, stylesheet, seesXMLRules);
		this.enabled=enabled;
		this.creator=creator;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public User getCreator() {
		return creator;
	}

}
