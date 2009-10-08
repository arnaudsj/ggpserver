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
<jsp:useBean id="viewPlayer"
	class="tud.ggpserver.formhandlers.ViewPlayer" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewPlayer" property="name" />
	</c:catch>
</jsp:useBean>

<c:set var="title">Player ${viewPlayer.name}</c:set>
<jsp:directive.include file="/inc/header.jsp" />
<%
if (viewPlayer.getPlayer() == null) {
	response.sendError(404, "That player doesn't exist.");
	return;
}
%>

<table>
	<tbody>
		<tr>
			<th>player name</th>
			<td><c:out value="${viewPlayer.name}"></c:out></td>
		</tr>
		<tr>
			<th>owner</th>
			<td>
				<c:url value="view_user.jsp" var="userURL">
					<c:param name="userName" value="${viewPlayer.owner.userName}" />
				</c:url> <a href='<c:out value="${userURL}" />'>${viewPlayer.owner.userName}</a>
			</td>
		</tr>
		<tr>
			<th>status</th>
			<td><c:out value="${viewPlayer.status}"></c:out></td>
		</tr>
		<tr>
			<th>matches</th>
			<td>
				<c:url value="show_matches.jsp" var="URL">
					<c:param name="playerName" value="${viewPlayer.name}" />
				</c:url>
				<a href='<c:out value="${URL}" />'>show matches</a>
			</td>
		</tr>
		<tr>
			<th>tournaments</th>
			<td>
				<c:forEach var="tournament" items="${viewPlayer.tournaments}">
					<c:url value="view_tournament.jsp" var="URL">
						<c:param name="tournamentID" value="${tournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${URL}" />'>${tournament.tournamentID}</a><br>
				</c:forEach>
			</td>
		</tr>
	</tbody>
</table>

<jsp:directive.include file="/inc/footer.jsp" />