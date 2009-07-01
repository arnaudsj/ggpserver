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
<jsp:useBean id="viewMatch" class="tud.ggpserver.formhandlers.ViewMatch"
	scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewMatch" property="matchID" />
		<jsp:setProperty name="viewMatch" property="playerName" />
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
<div id="ctitle">View match</div>
<c:set var="match" value="${viewMatch.match}" />

<h1 class="notopborder">Information on match ${match.matchID}</h1>
<table>
	<tbody>
		<tr>
			<th>match name</th>
			<td><c:out value="${match.matchID}" /></td>
		</tr>
		<tr>
			<th>game</th>
			<td>
				<c:url value="view_game.jsp" var="gameURL">
					<c:param name="name" value="${match.game.name}" />
				</c:url>
				<a href='<c:out value="${gameURL}" />'>${match.game.name}</a>
			</td>
		<tr>
			<th>status</th>
			<td><c:out value="${match.status}" /></td>
		</tr>
		<tr>
			<th>start clock</th>
			<td><c:out value="${match.startclock}" /></td>
		</tr>
		<tr>
			<th>play clock</th>
			<td><c:out value="${match.playclock}" /></td>
		</tr>
		<tr>
			<th>start time</th>
			<td><c:out value="${match.startTime}" /></td>
		</tr>
		<tr>
			<th>players</th>
			<td>
				<c:forEach var="playerinfo"
					items="${match.orderedPlayerInfos}">
					<c:url value="view_player.jsp" var="playerURL">
						<c:param name="name" value="${playerinfo.name}" />
					</c:url>
	
					<a href='<c:out value="${playerURL}" />'>
					<c:choose>
						<c:when test="${ playerinfo.name == viewMatch.playerName }">
							<span class="highlight"><c:out value="${playerinfo.name}" /></span>
						</c:when>
						<c:otherwise>
							<c:out value="${playerinfo.name}" />
						</c:otherwise>
					</c:choose>
					</a>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<th>goal values</th>
			<td><c:choose>
				<c:when test="${match.orderedGoalValues == null}">
							---
						</c:when>
				<c:otherwise>
					<c:forEach var="roleindex" begin="0" end="${match.game.numberOfRoles - 1}">
						<c:choose>
							<c:when test="${ match.orderedPlayerInfos[roleindex].name == viewMatch.playerName }">
								<span class="highlight">${match.orderedGoalValues[roleindex]}</span>
							</c:when>
							<c:otherwise>
								${match.orderedGoalValues[roleindex]}
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:otherwise>
			</c:choose></td>
		</tr>
	</tbody>
</table>



<h1>Match history</h1>
<table>
	<thead>
		<tr>
			<!--				<th>step number</th>-->
			<th>state</th>
			<th>joint move</th>
			<th>
			<center>errors</center>
			</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="stepNumber" begin="1" end="<%= viewMatch.getMatch().getXmlStates().size() %>"
			varStatus="lineInfo">
			<jsp:setProperty name="viewMatch" property="stepNumber"
				value="${stepNumber}" />
			<c:choose>
				<c:when test="${lineInfo.count % 2 == 0}">
					<c:set var="rowClass" value="even" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="odd" />
				</c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<!--			<td><c:out value="${stepNumber}" /></td>-->
				<td>
					<c:url value="view_state.jsp" var="stateURL">
						<c:param name="matchID" value="${match.matchID}" />
						<c:param name="stepNumber" value="${stepNumber}" />
					</c:url>
					<a href='<c:out value="${stateURL}" />'>state ${stepNumber}</a>
				</td>
				<td>
					<c:forEach var="move" items="${viewMatch.moves}">
						<c:out value="${move}" />&nbsp;
					</c:forEach>
				</td>
				<td>
					<center>
						<c:choose>
							<c:when test="<%= !viewMatch.getErrorMessages().isEmpty() %>">
								<c:choose>
									<c:when test="${ viewMatch.playerName == null }">
										<c:set var="errorclass" value="errors" />
									</c:when>
									<c:when test="<%= viewMatch.hasErrorForPlayer() %>">
										<c:set var="errorclass" value="errors" />
									</c:when>
									<c:otherwise>
										<c:set var="errorclass" value="errors_bw" />
									</c:otherwise>
								</c:choose>
								<c:url value="view_errors.jsp" var="errorURL">
									<c:param name="matchID" value="${match.matchID}" />
									<c:if test="${ viewMatch.playerName != null }">
										<c:param name="playerName" value="${viewMatch.playerName}" />
									</c:if>
								</c:url>
								<div class="${errorclass}">
									<a href='<c:out value="${errorURL}" />#step<c:out value="${stepNumber}" />'>
										<span>errors</span>
									</a>
								</div>
							</c:when>
							<c:otherwise>
								<div class="no_errors" />
							</c:otherwise>
						</c:choose>
					</center>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
