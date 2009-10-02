<%--
    Copyright (C) 2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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
<jsp:useBean id="viewGameStatistics"
	class="tud.ggpserver.formhandlers.ViewGameStatistics" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewGameStatistics" property="gameName" />
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
	<div id="everything">
		<jsp:directive.include file="/inc/header.jsp" />
		<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
		<div id="content">
			<div id="ctitle">View game statistics</div>
			
			<h1 class="notopborder">Statistics for game
				<c:url value="view_game.jsp" var="gameURL">
					<c:param name="name" value="${viewGameStatistics.gameName}"/>
				</c:url>
				<a href="${gameURL}">
					${viewGameStatistics.gameName}
				</a>
			</h1>

			<c:set var="statistics" value="${viewGameStatistics.statistics}"/>

			<h2>Statistics per role</h2>
			<table>
				<thead>
					<tr>
						<th></th>
						<th>role</th>
						<th>average score</th>
						<th>standard deviation</th>
						<th>number of matches</th>
					</tr>
				</thead>
				<tbody>
			      <c:forEach var="role" items="${statistics.orderedRoles}" varStatus="lineInfo">
			      	 <c:choose>
					   <c:when test="${lineInfo.count % 2 == 0}">
					     <c:set var="rowClass" value="even" />
					   </c:when> 
					   <c:otherwise>
					     <c:set var="rowClass" value="odd" />
					   </c:otherwise>
					 </c:choose> 
				     <tr class="${rowClass}">
						<td><c:out value="${lineInfo.count}"/></td>
						<td><c:out value="${role}"/></td>
						<td><c:out value="${statistics.informationPerRole[role].averageScore}"/></td>
						<td><c:out value="${statistics.informationPerRole[role].standardDeviation}"/></td>
						<c:if test="${lineInfo.count == 1}">
							<td rowspan="${statistics.game.numberOfRoles}"><c:out value="${statistics.informationPerRole[role].numberOfMatches}"/></td>
						</c:if>
					</tr>
			      </c:forEach>
				</tbody>
			</table>

			<h2>Statistics per player</h2>
			<table>
				<thead>
					<tr>
						<th></th>
						<th>player</th>
						<th>average score</th>
						<th>standard deviation</th>
						<th>number of matches</th>
					</tr>
				</thead>
				<tbody>
			      <c:forEach var="player" items="${statistics.sortedPlayers}" varStatus="lineInfo">
			      	 <c:choose>
					   <c:when test="${lineInfo.count % 2 == 0}">
					     <c:set var="rowClass" value="even" />
					   </c:when> 
					   <c:otherwise>
					     <c:set var="rowClass" value="odd" />
					   </c:otherwise>
					 </c:choose> 
				     <tr class="${rowClass}">
						<td><c:out value="${lineInfo.count}"/></td>
						<td>
							<c:url value="view_player.jsp" var="playerURL">
								<c:param name="name" value="${player.name}"/>
							</c:url>
							<a href='<c:out value="${playerURL}"/>'>
								<c:out value="${player.name}"/>
							</a>
						</td>
						<td><c:out value="${statistics.informationPerPlayer[player].averageScore}"/></td>
						<td><c:out value="${statistics.informationPerPlayer[player].standardDeviation}"/></td>
						<td><c:out value="${statistics.informationPerPlayer[player].numberOfMatches}"/></td>
					</tr>
			      </c:forEach>
				</tbody>
			</table>
			
		</div> <!--end div "content"-->
		<jsp:directive.include file="/inc/footer.jsp" />
	</div> <!-- end div "everything" -->
</body>
</html>
