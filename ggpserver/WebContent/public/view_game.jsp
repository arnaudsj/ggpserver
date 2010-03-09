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
<jsp:useBean id="viewGame"
	class="tud.ggpserver.formhandlers.ViewGame" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewGame" property="name" />
	</c:catch>
</jsp:useBean>
<jsp:useBean id="viewGameUserBean"
	class="tud.ggpserver.formhandlers.ViewUser" scope="page">
	<c:catch>
		<jsp:setProperty name="viewGameUserBean" property="userName" value="<%= request.getUserPrincipal().getName()%>" />
	</c:catch>
</jsp:useBean>

<c:set var="title">Game ${viewGame.name}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<table>
	<tbody>
		<tr>
			<th>name</th>
			<td><c:out value="${viewGame.name}"></c:out>
				<c:if test='<%= viewGameUserBean.getUser() != null && viewGameUserBean.getUser().hasRole("admin") %>'>
					<c:url value="../admin/edit_game.jsp" var="editURL">
						<c:param name="gameName" value="${viewGame.name}" />
					</c:url>
					<div class="edit" title="Edit game information"><a href='<c:out value="${editURL}"/>'><span>edit</span></a></div>
				</c:if>
			</td>
		</tr>
		<tr>
			<th>creator</th>
			<td>
				<c:url value="view_user.jsp" var="creatorURL">
					<c:param name="userName" value="${viewGame.game.creator.userName}" />
				</c:url>
				<a href='<c:out value="${creatorURL}" />'><c:out value="${viewGame.game.creator.userName}"/></a>
			</td>
			<td>
			</td>
		</tr>
		<tr>
			<th>number of roles</th>
			<td><c:out value="${viewGame.game.numberOfRoles}"></c:out></td>
		</tr>
		<tr>
			<th>stylesheet</th>
			<td><c:out value="${viewGame.game.stylesheet}"></c:out></td>
		</tr>
		<tr>
			<th>enabled</th>
			<td>
				<c:choose>
					<c:when test="${viewGame.game.enabled}">
						<input type="checkbox" name="enabled" checked disabled>
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="enabled" disabled>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<th>matches</th>
			<td>
				<c:url value="show_matches.jsp" var="matchesURL">
					<c:param name="gameName" value="${viewGame.name}" />
				</c:url>
				<a href='<c:out value="${matchesURL}" />'>show matches</a>
			</td>
		</tr>
		<tr>
			<th>statistics</th>
			<td>
				<c:url value="view_game_statistics.jsp" var="URL">
					<c:param name="gameName" value="${viewGame.name}" />
				</c:url>
				<a href='<c:out value="${URL}" />'>show game statistics</a>
			</td>
		</tr>
	</tbody>
</table>

<h2>Game Description</h2>
<c:url value="download_gdl.jsp" var="downloadURL">
	<c:param name="name" value="${viewGame.name}" />
</c:url>
<div class="download">
	<a href='<c:out value="${downloadURL}" />'><span>Download</span></a>
</div>
<pre><code><c:out value="${viewGame.game.gameDescription}" /></code></pre>

<jsp:directive.include file="/inc/footer.jsp" />