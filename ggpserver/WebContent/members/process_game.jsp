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

create_game<%= request.getUserPrincipal().getName() %> 

<jsp:useBean id="createGame" class="tud.ggpserver.formhandlers.CreateGame" scope="request">
	<c:catch>
		<jsp:setProperty name="createGame" property="gameName"/>
		<jsp:setProperty name="createGame" property="gameDescription"/>
		<jsp:setProperty name="createGame" property="stylesheet"/>
		<jsp:setProperty name="createGame" property="creatorName" value="<%= request.getUserPrincipal().getName() %>" />
		<%-- setting enabled to false is necessary because the enabled property is only send with the request if the checkbox is checked --%>
		<jsp:setProperty name="createGame" property="enabled" value="false"/>
		<jsp:setProperty name="createGame" property="enabled"/>
		<jsp:setProperty name="createGame" property="gdlVersion"/>
		<jsp:setProperty name="createGame" property="seesXMLRules"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:choose> 
	<c:when test="${createGame.valid}" > 
		<%
			// add to database
			createGame.create();
		%>
		<c:choose> 
			<c:when test="${createGame.correctlyCreated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("../public/view_game.jsp?name=" + createGame.getGameName());
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="create_game.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="create_game.jsp"/>
	</c:otherwise> 
</c:choose>


