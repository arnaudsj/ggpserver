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

<jsp:useBean id="process_pager"
	class="tud.ggpserver.formhandlers.EditTournament" scope="page">
	<c:catch>
		<jsp:setProperty name="process_pager" property="tournamentID" />
		<jsp:setProperty name="process_pager" property="action" />
		<jsp:setProperty name="process_pager" property="matchID" />
		<jsp:setProperty name="process_pager" property="userName" value="<%= request.getUserPrincipal().getName()%>" />
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>


<c:choose> 
	<c:when test="${process_pager.valid}" > 
		<%
			process_pager.performAction();
		%>
		<c:choose> 
			<c:when test="${process_pager.correctlyPerformed}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("edit_tournament.jsp" 
							+ "?tournamentID=" + request.getParameter("tournamentID")
							+ "&page=" + request.getParameter("page") + "#" + request.getParameter("anchor")) ;
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<c:set var="title">Not correctly performed</c:set>
				<jsp:directive.include file="/inc/header_no_nav.jsp" />
				<h1 class="notopborder">${process_pager.errorString}</h1>
				<a href="<%= request.getContextPath() + response.encodeURL("/members/edit_tournament.jsp?tournamentID=" + request.getParameter("tournamentID")) %>">&lt;&lt;&lt; back to edit tournament page</a>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<c:set var="title">Not valid</c:set>
		<jsp:directive.include file="/inc/header_no_nav.jsp" />	
		<h1 class="notopborder">${process_pager.errorString}</h1>
		<a href="<%= request.getContextPath() + response.encodeURL("/members/edit_tournament.jsp?tournamentID=" + request.getParameter("tournamentID")) %>">&lt;&lt;&lt; back to edit tournament page</a>
	</c:otherwise> 
</c:choose>


