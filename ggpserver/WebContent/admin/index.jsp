<%--
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>,
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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
<jsp:useBean id="adminPage" class="tud.ggpserver.formhandlers.AdminPage" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="adminPage" property="action"/>
		<jsp:setProperty name="adminPage" property="cacheCleared"/>
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Dresden GGP Server</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%= request.getContextPath() %>/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
<div id="everything">
<jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" />

<!-- Content -->
<div id="content">
    <div id="ctitle">Admin page</div>
    <h1 class="notopborder">Scheduler</h1>
    
	<c:url value="index.jsp" var="urlStopGracefully">
		<c:param name="action" value="stopGracefully" />
	</c:url>
	<c:url value="index.jsp" var="urlStop">
		<c:param name="action" value="stop" />
	</c:url>
	<c:url value="index.jsp" var="urlStart">
		<c:param name="action" value="start" />
	</c:url>
    <c:choose>
    	<c:when test="${adminPage.beingStopped}">
			The round-robin scheduler <b>will be stopped</b>. You can <a href='<c:out value="${urlStop}" />'>stop it immediately</a> or <a href='<c:out value="${urlStart}" />'>restart it</a>.
    	</c:when>
    	<c:when test="${adminPage.running}">
			The round-robin scheduler is <b>running</b>. You can <a href='<c:out value="${urlStopGracefully}" />'>stop it</a> (after the currently running matches) or <a href='<c:out value="${urlStop}" />'>stop it immediately</a>.
    	</c:when>
    	<c:otherwise>
			The round-robin scheduler is <b>not running</b>. You can <a href='<c:out value="${urlStart}" />'>start it</a>.
    	</c:otherwise>
    </c:choose>
 

	<h1>Scheduler options</h1>
	<c:url var="schedulerOptionsURL" value="process_scheduler_options.jsp" />
	<form action="${schedulerOptionsURL}" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Next game to be played
			</td>
			<td>
				<select name="nextPlayedGameName" size="1" >
					<c:set var="theNextPlayedGameName" value="${adminPage.nextPlayedGameName}" />
					
					<c:forEach var="game" items="${adminPage.allGames}">
						<c:choose>
							<c:when test='${game.name == theNextPlayedGameName}'>
								<option value="${game.name}" selected><c:out value="${game.name}" /></option>
							</c:when>
							<c:otherwise>
								<option value="${game.name}"><c:out value="${game.name}" /></option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				start clock range
			</td>
			<td>
				[ <input type="text" name="startclockMin" size="3" value="${adminPage.startclockMin}" maxlength="3"> s,  
				<input type="text" name="startclockMax" size="3" value="${adminPage.startclockMax}" maxlength="3"> s ]
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				play clock range
			</td>
			<td>
				[ <input type="text" name="playclockMin" size="3" value="${adminPage.playclockMin}" maxlength="3"> s,  
				<input type="text" name="playclockMax" size="3" value="${adminPage.playclockMax}" maxlength="3"> s ]
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="submit" value="Submit">
				<input type="reset" value="Reset"> 
			</td>
		</tr>
	</table>
	</form>	
	
	
    <h1>Cache</h1>
	<c:url value="process_clear_cache.jsp" var="cacheURL" />
    Click <a href='<c:out value="${cacheURL}" />'>here</a> to clear the cache (forces re-reading everything from the database).

	<c:if test="${adminPage.cacheCleared}">
		<script language="javascript" type="text/javascript">
			alert ('Cache was successfully cleared.');
		</script>
	</c:if>

    <h1>Tournaments</h1>
	<table>
		<thead>
			<tr>
				<th>tournament</th>
				<th>owner</th>
				<th colspan="2">actions</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="tournament" items="${adminPage.tournaments}" varStatus="lineInfo">
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
						<c:out value="${tournament.owner.userName}" />
					</td>
					<td>
						<c:url value="../public/show_matches.jsp" var="viewURL">
							<c:param name="tournamentID" value="${tournament.tournamentID}" />
						</c:url>
						<div class="view"><a href='<c:out value="${viewURL}" />'><span>view</span></a></div>
					</td>
					<td>
						<c:url value="edit_tournament.jsp" var="editURL">
							<c:param name="tournamentID" value="${tournament.tournamentID}" />
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
				<td colspan="4">
					<div><a href='<%= response.encodeURL("create_tournament.jsp") %>'><span>Add new tournament</span></a></div>
				</td>
			</tr>
		</tbody>
	</table>

</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>