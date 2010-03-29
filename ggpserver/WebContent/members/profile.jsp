<%--
    Copyright (C) 2009-2010 Martin Günther (mintar@gmx.de), Nicolas JEAN <njean42@gmail.com>

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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="profile"
	class="tud.ggpserver.formhandlers.Profile" scope="request">
	<c:catch>
		<jsp:setProperty name="profile" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
		<jsp:setProperty name="profile" property="available" />
	</c:catch>
</jsp:useBean>

<jsp:useBean id="availability"
	class="tud.ggpserver.formhandlers.inc.Availability" scope="request">
	<c:catch>
		<jsp:setProperty name="availability" property="matchID" />
		<jsp:setProperty name="availability" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
		<jsp:setProperty name="availability" property="available" />
	</c:catch>
</jsp:useBean>

<jsp:useBean id="matches"
	class="tud.ggpserver.formhandlers.inc.MatchesAccessor" scope="request">
	<c:catch>
		<jsp:setProperty name="matches" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
	</c:catch>
</jsp:useBean>

<%@page import="tud.ggpserver.formhandlers.inc.MatchesAccessor"%>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:set var="title">User Profile</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<script type="text/javascript" language="JavaScript">
	
		function confirm_delete(matchid, url) {
			var result = confirm("Do you really want to delete the tournament " + matchid + "? All included matches will be deleted too!");
			if (result == true) {
				window.location=url;
			}
		}
	
	</script>

<h1 class="notopborder">My Players</h1>

<table>
	<thead>
		<tr>
			<th>player name</th>
			<th>host</th>
			<th>port</th>
			<th>status</th>
			<th>available for<br>round robin play</th>
			<th>available for<br>manual play</th>
			<th>GDL</th>
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
					<c:out value="${player.name}" />
				</td>
				<td>${player.host}</td>
				<td>${player.port}</td>
				<td><div class="playerstatus-${player.status}"><span>${player.status}</span></div></td>
				<td>${player.availableForRoundRobinMatches}</td>
				<td>${player.availableForManualMatches}</td>
				<td>${player.gdlVersion}</td>
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
			<td colspan="9">
				<div class="add-new-player"><a href='<%= response.encodeURL("create_player.jsp") %>'><span>Add new player</span></a></div>
			</td>
		</tr>
	</tbody>
</table>

<h1>My Matches</h1>
	
	<table>
		<thead>
			<tr>
				<th>tournament</th>
				<th colspan="3">actions</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="tournament" items="${profile.tournaments}" varStatus="lineInfo">
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
						<c:out value="${tournament.tournamentID}" />
					</td>
					<td>
						<c:url value="../public/show_matches.jsp" var="viewURL">
							<c:param name="tournamentID" value="${tournament.tournamentID}" />
							<c:param name="owner" value="${profile.userName}" />
						</c:url>
						<div class="view"><a href='<c:out value="${viewURL}" />'><span>view</span></a></div>
					</td>
					<td>
						<c:url value="edit_tournament.jsp" var="editURL">
							<c:param name="tournamentID" value="${tournament.tournamentID}" />
							<c:param name="owner" value="${profile.userName}" />
						</c:url>
						<div class="edit"><a href='<c:out value="${editURL}" />'><span>edit</span></a></div>
					</td>
					<td>
						<c:if test="${tournament.deletable}">
						    <c:url value="process_delete_tournament.jsp" var="deleteURL">
								<c:param name="tournamentID" value="${tournament.tournamentID}"/>
								<c:param name="returnURL" value="profile.jsp"/>
						    </c:url>

						    <c:set var="realDeleteURL" value="javascript:confirm_delete('${tournament.tournamentID}', '${deleteURL}')"></c:set>							
						    
						    <a href='<c:out value="${realDeleteURL}" />'><div class="delete" title="delete tournament"><span>delete</span></div></a>
						</c:if>
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
				<td colspan="4">
					<div><a href='<%= request.getContextPath() + response.encodeURL("/members/create_tournament.jsp") %>'><span>Add new tournament</span></a></div>
				</td>
			</tr>
		</tbody>
	</table>
	
	<br/>
	<c:url value="../public/show_matches.jsp" var="matchesURL">
		<c:param name="playerName" value="${profile.userName}" />
	</c:url>
	<a href='<c:out value="${matchesURL}" />'>Show matches in which I took part</a><br>
	

<c:if test="${matches.someRunningMatches}">

<h1>You are currently playing</h1>
	
	<ul>
	<c:forEach var="match" items="${matches.myRunningMatches}">
		<c:forEach var="i" begin="0" end="${match.game.numberOfRoles - 1}">
			<c:if test="${match.orderedPlayerNames[i] == profile.userName}">
				<li>
					<c:url value="/members/play.jsp" var="playURL">
						<c:param name="matchID" value="${match.matchID}" />
						<c:param name="role" value="${match.orderedPlayerRoles[i]}" />
				    </c:url>
				    <a href='<c:out value="${playURL}"/>'>
				    	<div class="play" title="play"></div>
				    	play <c:out value="${match.matchID}"/> as <c:out value="${match.orderedPlayerRoles[i]}"/>
				    </a>
				</li>
			</c:if>
		</c:forEach>
	</c:forEach>
	</ul>
	
</c:if>


<c:if test="${matches.someScheduledMatches}">

	<h1>You could begin playing</h1>
	
		<ul>
		<c:forEach var="match" items="${matches.myScheduledMatches}">
			<li>
				<c:out value="${match.left.matchID}"/> 
				<c:choose>
					<c:when test="${match.right}">
						<img src="../icons/other/16-loading.gif" title="waiting for other players to accept..."/>
					</c:when>
					<c:otherwise>
						<c:url value="/members/profile.jsp" var="acceptURL">
							<c:param name="matchID" value="${match.left.matchID}"/>
							<c:param name="available" value="1"/>
					    </c:url>
					    <a href='<c:out value="${acceptURL}"/>'>
					    	<div class="start" title="allow this game to begin"></div>
					    	allow this game to begin
					    </a>
					</c:otherwise>
				</c:choose>
			</li>
		</c:forEach>
		</ul>
		
		<c:if test="${matches.atLeastOneAcceptedScheduledMatch}">
			<script type="text/javascript" language="JavaScript">
		    	setTimeout("document.location.reload()", 3000);
		    </script>
		</c:if>

</c:if>


<h1>Hints</h1>
<ul>
	<jsp:directive.include file="/inc/player_hints.jsp" />
	<li>
		To <b>create and start a match manually</b> click on the edit button
		<c:url value="edit_tournament.jsp" var="editURL"><c:param name="tournamentID" value="manual_matches" /></c:url>
		(<div class="edit"><a href='<c:out value="${editURL}" />'><span>edit</span></a></div>)
		next to the <i>manual matches</i> above.
	</li>
</ul>

<jsp:directive.include file="/inc/footer.jsp" />