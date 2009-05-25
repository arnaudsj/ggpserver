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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">View game</div>

<h1 class="notopborder">Information on game ${viewGame.name}
<c:if test='${viewGameUserBean.user != null}'>
	<c:if test='<%= viewGameUserBean.getUser().hasRole("admin") %>'>
		<c:url value="../admin/edit_game.jsp" var="editURL">
			<c:param name="gameName" value="${viewGame.name}" />
		</c:url>
		<div class="edit"><a href='<c:out value="${editURL}"/>'><span>edit</span></a></div>
	</c:if>
</c:if>
</h1>
<table>
	<tbody>
		<tr>
			<td><b>name</b></td>
			<td><c:out value="${viewGame.name}"></c:out></td>
		</tr>
		<tr>
			<td><b>number of roles</b></td>
			<td><c:out value="${viewGame.game.numberOfRoles}"></c:out></td>
		</tr>
		<tr>
			<td><b>stylesheet</b></td>
			<td><c:out value="${viewGame.game.stylesheet}"></c:out></td>
		</tr>
		<tr>
			<td><b>enabled</b></td>
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
			<td><b>matches</b></td>
			<td>
				<c:url value="show_matches.jsp" var="matchesURL">
					<c:param name="gameName" value="${viewGame.name}" />
				</c:url>
				<a href='<c:out value="${matchesURL}" />'>show matches</a>
			</td>
		</tr>
	</tbody>
</table>

<h1>Game Description</h1>
<p style="font-family: monospace; font-size:medium">
<code>${viewGame.gameDescription}</code>
</p> 


</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
