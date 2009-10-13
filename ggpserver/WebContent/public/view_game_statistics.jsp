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

<% viewGameStatistics.setSession(request.getSession()); %>

<c:set var="title">Statistics for ${viewGameStatistics.gameName}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<h1 class="notopborder">Statistics for game
	<c:url value="view_game.jsp" var="gameURL">
		<c:param name="name" value="${viewGameStatistics.gameName}"/>
	</c:url>
	<a href="${gameURL}">
		${viewGameStatistics.gameName}
	</a>
</h1>

<c:set var="roleStatistics" value="${viewGameStatistics.roleStatistics}"/>

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
      <c:forEach var="role" items="${roleStatistics.orderedRoles}" varStatus="lineInfo">
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
			<td><c:out value="${roleStatistics.informationPerRole[role].averageScore}"/></td>
			<td><c:out value="${roleStatistics.informationPerRole[role].standardDeviation}"/></td>
			<c:if test="${lineInfo.count == 1}">
				<td rowspan="${roleStatistics.game.numberOfRoles}"><c:out value="${roleStatistics.informationPerRole[role].numberOfMatches}"/></td>
			</c:if>
		</tr>
      </c:forEach>
	</tbody>
</table>

<c:forEach var="role" items="${viewGameStatistics.game.orderedRoles}" varStatus="roleInfo">
	<h2>Player performance for role ${role}</h2>
	<c:set var="playerStatistics" value="${viewGameStatistics.playerStatisticsPerRole[role]}"/>
	<table>
		<thead>
			<tr>
				<th></th>
				<th>player</th>
				<th>average score</th>
				<th>standard deviation</th>
				<th>number of matches</th>
				<th>actions</th>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="player" items="${playerStatistics.sortedPlayers}" varStatus="lineInfo">
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
				<td><c:out value="${playerStatistics.informationPerPlayer[player].averageScore}"/></td>
				<td><c:out value="${playerStatistics.informationPerPlayer[player].standardDeviation}"/></td>
				<td><c:out value="${playerStatistics.informationPerPlayer[player].numberOfMatches}"/></td>
				<td>
					<c:url value="show_matches.jsp" var="matchesURL">
						<c:param name="gameName" value="${viewGameStatistics.gameName}"/>
						<c:param name="playerName" value="${player.name}"/>
					</c:url>
					<a href='<c:out value="${matchesURL}"/>'>show matches</a> 
				</td>
			</tr>
	      </c:forEach>
		</tbody>
	</table>
	<!-- chart --> 
	<c:set var="chartInfo" value="${viewGameStatistics.chartsForRoles[roleInfo.count - 1]}"/>
	<c:url value="/servlet/GameStatisticsChartViewer" var="URL">
		<c:param name="imageID" value="${chartInfo.imageID}"/>
		<c:param name="gameName" value="${viewGameStatistics.gameName}"/>
		<c:param name="roleIndex" value="${chartInfo.roleIndex}"/>
	</c:url>
	<img src="${URL}" usemap="#${chartInfo.imageMapID}">
	${chartInfo.imageMap}
	<br>
	The chart shows the <a href="http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average" target="_blank">exponential moving average</a> of the scores of each player with a smoothing factor of 0.1.
</c:forEach>

<jsp:directive.include file="/inc/footer.jsp" />