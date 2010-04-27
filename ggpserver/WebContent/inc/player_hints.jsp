<%--
    Copyright (C) 2009-2010 Stephan Schiffel (stephan.schiffel@gmx.de)

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
--%>

<li>
	A player can be in one of several states:
	<ul>
		<li><b>status: active/inactive</b> - active means that, the player is online. Only players that are active can take part in new matches.</li> 
		<li><b>available for round robin play</b> - if true (and the player is active) it will take part in the automatically scheduled round-robin tournament, 
		i.e., the GGP Server will automatically schedule it for matches against other available players.</li>
		<li><b>available for manual play</b> - if true (and the player is active) it can be chosen by other users for manually created matches. You can always choose your own players even if <i>available for manual play</i> is set to <i>false</i>.</li>
	</ul>
</li>

<li>If an active player doesn't send a single legal move back for three matches in a row, it is assumed that this player has crashed, and its status is automatically set to "inactive" by the GGP Server. The GGP Server will never set a player's status back to "active". You have to do so manually.</li>
