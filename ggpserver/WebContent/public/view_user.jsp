<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewUser"
	class="tud.ggpserver.formhandlers.ViewUser" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewUser" property="userName" />
	</c:catch>
</jsp:useBean>

<c:set var="title">User ${viewUser.user.userName}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<%
if (viewUser.getUser() == null) {
	response.sendError(404, "That user doesn't exist.");
	return;
}
%>

<table>
	<tbody>
		<tr>
			<th>user name</th>
			<td><c:out value="${viewUser.user.userName}"></c:out></td>
		</tr>
		<tr>
			<th>players</th>
			<td>
				<c:forEach var="player" items="${viewUser.players}">
					<c:url value="view_player.jsp" var="playerURL">
						<c:param name="name" value="${player.name}" />
					</c:url>
					<a href='<c:out value="${playerURL}" />'>${player.name}</a><br>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<th>matches</th>
			<td>
				<c:url value="show_matches.jsp" var="matchesURL">
					<c:param name="owner" value="${viewUser.user.userName}" />
				</c:url>
				<a href='<c:out value="${matchesURL}" />'>show matches started by ${viewUser.user.userName}</a><br>
			</td>
		</tr>
		<tr>
			<th>tournaments</th>
			<td>
				<c:forEach var="tournament" items="${viewUser.tournaments}">
					<c:url value="view_tournament.jsp" var="tournamentURL">
						<c:param name="tournamentID" value="${tournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${tournamentURL}" />'>${tournament.tournamentID}</a><br>
				</c:forEach>
			</td>
		</tr>
	</tbody>
</table>

<jsp:directive.include file="/inc/footer.jsp" />