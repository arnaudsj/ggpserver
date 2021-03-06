<%--
    Copyright (C) 2009 Martin G�nther (mintar@gmx.de)

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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="navigationUserBean"
	class="tud.ggpserver.formhandlers.ViewUser" scope="page">
	<c:catch>
		<jsp:setProperty name="navigationUserBean" property="userName" value="<%= request.getUserPrincipal().getName()%>" />
	</c:catch>
</jsp:useBean>

<c:if test="${omitNavigation != 'true'}">

	<!-- Navigation -->
	<div id="navigation">

		<div id="ntitle">&nbsp;</div>
		<a href="<%= request.getContextPath() + response.encodeURL("/index.jsp") %>">Start Page</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_matches.jsp") %>">Matches</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_filter.jsp") %>">Filter</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_games.jsp") %>">Games</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_players.jsp") %>">Players</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_users.jsp") %>">Users</a>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_tournaments.jsp") %>">Tournaments</a>
		
		<c:if test='${navigationUserBean.user != null}'>
			<c:if test='${navigationUserBean.user.admin}'>
				<a href="<%= request.getContextPath() + response.encodeURL("/admin/index.jsp") %>">Admin Page</a>
			</c:if>
			<a href="<%= request.getContextPath() + response.encodeURL("/members/profile.jsp") %>">User Profile</a> 
			<a href="<%= request.getContextPath() + response.encodeURL("/members/create_game.jsp") %>">Create Game</a> 
		</c:if>
		<a href="<%= request.getContextPath() + response.encodeURL("/public/contact.jsp") %>">Contact</a>
	</div>

</c:if>
