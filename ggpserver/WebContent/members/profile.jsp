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

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="profile"
	class="tud.ggpserver.formhandlers.Profile" scope="request">
	<c:catch>
		<jsp:setProperty name="profile" property="userName"
			value="<%= request.getUserPrincipal().getName() %>" />
	</c:catch>
</jsp:useBean>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">Member Profile</div>
<h1 class="notopborder">My Players</h1>

<table>
	<thead>
		<tr>
			<th>player name</th>
			<th>host</th>
			<th>port</th>
			<th>status</th>
			<th colspan="2">actions</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="player" items="${profile.players}" varStatus="lineInfo">
			<c:choose>
				<c:when test="${lineInfo.count % 2 == 0}">
					<c:set var="rowClass" value="even" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="odd" />
				</c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<td>
<%--				<c:url value="edit_player.jsp" var="playerURL">--%>
<%--					<c:param name="playerName" value="${player.name}" />--%>
<%--				</c:url><a href='<c:out value="${playerURL}" />'>${player.name}</a>--%>
					<c:out value="${player.name}" />
				</td>
				<td>${player.host}</td>
				<td>${player.port}</td>
				<td><div class="playerstatus-${player.status}"><span>${player.status}</span></div></td>
				<td>
					<c:url value="../public/view_player.jsp" var="viewURL">
						<c:param name="name" value="${player.name}" />
					</c:url>
					<div class="view"><a href='<c:out value="${viewURL}" />'><span>view</span></a></div>
				</td>
				<td>
					<c:url value="edit_player.jsp" var="editURL">
						<c:param name="playerName" value="${player.name}" />
					</c:url>
					<div class="edit"><a href='<c:out value="${editURL}" />'><span>edit</span></a></div>
				</td>
			</tr>
		</c:forEach>

		<c:choose>
			<c:when test='${rowClass == "odd"}'>
				<c:set var="rowClass" value="even" />
			</c:when>
			<c:otherwise>
				<c:set var="rowClass" value="odd" />
			</c:otherwise>
		</c:choose>
		<tr class="${rowClass}">
			<td colspan="6">
				<div class="add-new-player"><a href='<%= response.encodeURL("create_player.jsp") %>'><span>Add new player</span></a></div>
			</td>
		</tr>
	</tbody>
</table>

<h1>Tips</h1>
A player can be in one of two states:
<ul>
	<li><b>active</b> - the player will take part in the round-robin tournament, 
	i.e., the GGP Server will schedule it for matches against other active players.</li>
	<li><b>inactive</b> - the opposite of active: the GGP Server will not send 
	any messages to the player. </li>
</ul>

<p>If an active player doesn't send a single legal move back for three matches in a row, it is assumed that this 
player has crashed, and its status is automatically set to "inactive" by the 
GGP Server. If this has happened to your player, the last error message of the 
last match that the player played will say so.</p>

<p>The GGP Server will never set a player's status back to "active". You have to
do so manually.</p>


</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
