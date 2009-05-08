<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)

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

<!-- Navigation -->
<div id="navigation">

<div id="ntitle">&nbsp;</div>
<a href="<%= request.getContextPath() + response.encodeURL("/index.jsp") %>">Start Page</a>
<a href="<%= request.getContextPath() + response.encodeURL("/public/show_matches.jsp") %>">Show Matches</a>
<a href="<%= request.getContextPath() + response.encodeURL("/public/show_games.jsp") %>">Show Games</a>
<a href="<%= request.getContextPath() + response.encodeURL("/public/show_players.jsp") %>">Show Players</a>
<a href="<%= request.getContextPath() + response.encodeURL("/public/show_users.jsp") %>">Show Users</a>
<a href="<%= request.getContextPath() + response.encodeURL("/public/contact.jsp") %>">Contact</a>
</div>

