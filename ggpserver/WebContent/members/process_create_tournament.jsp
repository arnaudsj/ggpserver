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

<jsp:useBean id="createTournament" class="tud.ggpserver.formhandlers.CreateTournament" scope="request">
	<c:catch>
		<jsp:setProperty name="createTournament" property="tournamentID"/>
		<jsp:setProperty name="createTournament" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:choose> 
	<c:when test="${createTournament.valid}" > 
		<%
			// add tournament to database
			createTournament.createTournament();
		%>
		<c:choose> 
			<c:when test="${createTournament.correctlyCreated}" >
				<%
					String urlWithSessionID = request.getContextPath() + response.encodeRedirectURL("/members/profile.jsp");
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="create_tournament.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="create_tournament.jsp"/>
	</c:otherwise> 
</c:choose>


