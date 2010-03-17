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

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="editPlayer" class="tud.ggpserver.formhandlers.EditPlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="editPlayer" property="playerName"/>
		<jsp:setProperty name="editPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
		<jsp:setProperty name="editPlayer" property="host"/>
		<jsp:setProperty name="editPlayer" property="port"/>
		<jsp:setProperty name="editPlayer" property="status"/>
		<%-- setting availableForRoundRobinMatches to false is necessary because the property is only send with the request if the checkbox is checked --%>
		<jsp:setProperty name="editPlayer" property="availableForRoundRobinMatches" value="false"/>
		<jsp:setProperty name="editPlayer" property="availableForRoundRobinMatches"/>
		<%-- setting availableForManualMatches to false is necessary because the property is only send with the request if the checkbox is checked --%>
		<jsp:setProperty name="editPlayer" property="availableForManualMatches" value="false"/>
		<jsp:setProperty name="editPlayer" property="availableForManualMatches"/>
		<jsp:setProperty name="editPlayer" property="gdlVersion"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
	

<c:choose>
	<c:when test="${!editPlayer.validPlayer}" >
	<% 
		response.reset();
		response.sendError(403, "You are neither an admin nor the owner of this player, so you cannot edit it.");  // 403 forbidden
	%>
	</c:when>
	<c:when test="${editPlayer.valid}" > 
		<%
			// update the player infos in the database
			editPlayer.updatePlayer();
		%>
		<c:choose> 
			<c:when test="${editPlayer.correctlyUpdated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("profile.jsp");
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="edit_player.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="edit_player.jsp"/>
	</c:otherwise> 
</c:choose>
