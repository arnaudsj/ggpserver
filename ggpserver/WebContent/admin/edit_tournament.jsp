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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:useBean id="pager"
	class="tud.ggpserver.formhandlers.EditTournament" scope="page">
	<c:catch>
		<% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="tournamentID" />
		<jsp:setProperty name="pager" property="page" />
	</c:catch>
</jsp:useBean>
<%@page import="tud.ggpserver.formhandlers.EditTournament"%>
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
	<script type="text/javascript" language="JavaScript">
	<!--
		function confirm_delete(matchid, url) {
			var result = confirm("Do you really want to delete the finished or running match " + matchid + "? The player's rewards from that match will be removed from the statistics!");
			if (result == true) {
				window.location=url;
			}
		}
	//-->
	</script>
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<%--<jsp:directive.include file="/inc/navigation.jsp" />   // omit navigation menu   --%>
<!-- Content -->
<div id="content" style="width:auto">  <%-- width:auto is used in conjunction with removing the navigation menu --%>
<div id="ctitle">Edit Tournament</div>

<!-- pager --> <jsp:directive.include file="/inc/pager_title.jsp" /> <jsp:directive.include
	file="/inc/pager.jsp" />

<c:url value="process_save_tournament.jsp" var="saveURL">
<!--	<c:param name="tournamentID" value="${pager.tournamentID}"/>-->
</c:url>
<form name="theForm" action="${saveURL}" method="post">

<table>
	<thead>
		<tr>
			<th>match name</th>
			<th>game</th>
			<th>status</th>
			<th>start clock</th>
			<th>play clock</th>
			<th>players</th>
			<th>goal values</th>
			<th>errors</th>
			<th colspan="4">actions</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="match" items="${pager.matches}" varStatus="lineInfo">
			<c:choose>
				<c:when test="${lineInfo.count % 2 == 0}">
					<c:set var="rowClass" value="even" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="odd" />
				</c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<td><c:out value="${match.matchID}" /></td>
				<td>
					<c:choose>
						<c:when test="${match.status == 'new'}">
							<select name="gameName+${match.matchID}" size="1" onChange="theForm.submit();" style="max-width:120px;">
								<c:forEach var="game" items="${pager.games}">
									<c:choose>
										<c:when test='${game.name == match.game.name}'>
											<option value="${game.name}" selected="selected" ><c:out value="${game.name}" /></option>
										</c:when>
										<c:otherwise>
											<option value="${game.name}"><c:out value="${game.name}" /></option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>							
						</c:when>
						<c:otherwise><c:out value="${match.game.name}" /></c:otherwise>
					</c:choose>
				</td>
				<td><c:out value="${match.status}" /></td>
				<td>
					<c:choose>
						<c:when test="${match.status == 'new'}">
							<input type="text" name="startclock+${match.matchID}" size="3" value="${match.startclock}" maxlength="4" style="text-align: right;" onchange="document.theForm.submitButton.style.color = '#ff0000';">
						</c:when>
						<c:otherwise><c:out value="${match.startclock}" /></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${match.status == 'new'}">
							<input type="text" name="playclock+${match.matchID}" size="3" value="${match.playclock}" maxlength="4" style="text-align: right;" onchange="document.theForm.submitButton.style.color = '#ff0000';">
						</c:when>
						<c:otherwise><c:out value="${match.playclock}" /></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:forEach var="selectedplayerinfo" items="${match.orderedPlayerInfos}">
						<c:choose>
							<c:when test="${match.status == 'new'}">
								<select name="playerInfos+${match.matchID}" size="1" onchange="document.theForm.submitButton.style.color = '#ff0000';" style="max-width:120px;">
									<c:forEach var="playerinfo" items="${pager.enabledPlayerInfos}">
										<c:choose>
											<c:when test='${playerinfo.name == selectedplayerinfo.name}'>
												<option value="${playerinfo.name}" selected="selected" ><c:out value="${playerinfo.name}" /></option>
											</c:when>
											<c:otherwise>
												<option value="${playerinfo.name}"><c:out value="${playerinfo.name}" /></option>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</select>
							</c:when>
							<c:otherwise>
								<c:url value="../public/view_player.jsp" var="playerURL">
									<c:param name="name" value="${selectedplayerinfo.name}" />
								</c:url>
								<a href='<c:out value="${playerURL}" />'> 
								<c:out value="${selectedplayerinfo.name}" /> </a>
								<br>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</td>
				<td><c:choose>
					<c:when test="${match.goalValues == null}">
							---
						</c:when>
					<c:otherwise>
						<c:forEach var="roleindex" begin="0"
							end="${match.game.numberOfRoles - 1}">
							<c:out value="${match.orderedGoalValues[roleindex]}" />
							<br>
						</c:forEach>
					</c:otherwise>
				</c:choose></td>
				<td>
				<center><c:choose>
					<c:when test="${match.hasErrors}">
						<c:url value="../public/view_errors.jsp" var="errorURL">
							<c:param name="matchID" value="${match.matchID}" />
						</c:url>
						<div class="errors"><a href='<c:out value="${errorURL}" />'><span>errors</span></a></div>
					</c:when>
					<c:otherwise>
						<div class="no_errors"></div>
					</c:otherwise>
				</c:choose></center>
				</td>

				<%-- action "view" [all] --%>
				<td class="nopadding"><c:url value="../public/view_match.jsp" var="viewURL">
					<c:param name="matchID" value="${match.matchID}" />
				</c:url>
				<div class="view"><a href='<c:out value="${viewURL}" />'><span>view</span></a></div>
				</td>

				<%-- action "start" [only NEW] --%>
				<% // TODO: if running: action stop %>
				<td class="nopadding"><c:choose>
					<c:when test="${ match.status == 'new' }">
						<c:url value="process_edit_tournament.jsp" var="startURL">
							<c:param name="tournamentID" value="${pager.tournamentID}"/>
							<c:param name="action" value="<%= EditTournament.START_MATCH %>"/>
							<c:param name="matchID" value="${match.matchID}" />
						</c:url>
						<div class="start"><a href='<c:out value="${startURL}" />'><span>start</span></a></div>
					</c:when>
					<c:otherwise>
						<div class="start-bw"></div>
					</c:otherwise>
				</c:choose></td>

				<%-- action "delete" [all, but warn on finished/running] --%>
				<td class="nopadding">
					<c:url value="process_edit_tournament.jsp" var="deleteURL">
						<c:param name="tournamentID" value="${pager.tournamentID}"/>
						<c:param name="action" value="<%= EditTournament.DELETE_MATCH %>"/>
						<c:param name="matchID" value="${match.matchID}" />
					</c:url>

					<c:choose>
						<c:when test="${ match.status == 'finished' || match.status == 'running' }">
							<c:set var="realDeleteURL" value="javascript:confirm_delete('${match.matchID}', '${deleteURL}')"></c:set>							
						</c:when>
						<c:otherwise>
							<c:set var="realDeleteURL" value="${deleteURL}"></c:set>
						</c:otherwise>
					</c:choose>

					<div class="delete"><a href='<c:out value="${realDeleteURL}" />'><span>delete</span></a></div>
				</td>

				<%-- action "clone" [all] --%>
				<td class="nopadding">
				<c:url value="process_edit_tournament.jsp" var="cloneURL">
					<c:param name="tournamentID" value="${pager.tournamentID}"/>
					<c:param name="action" value="<%= EditTournament.CLONE_MATCH %>"/>
					<c:param name="matchID" value="${match.matchID}" />
				</c:url>
				<div class="clone"><a href='<c:out value="${cloneURL}" />'><span>clone</span></a></div>
				</td>
			</tr>
		</c:forEach>
		
		<%-- "add new match" --%>
		<c:choose>
			<c:when test='${rowClass == "odd"}'>
				<c:set var="rowClass" value="even" />
			</c:when>
			<c:otherwise>
				<c:set var="rowClass" value="odd" />
			</c:otherwise>
		</c:choose>
		<tr class="${rowClass}">
			<td colspan="12">
				<c:url value="process_edit_tournament.jsp" var="addMatchURL">
					<c:param name="tournamentID" value="${pager.tournamentID}"/>
					<c:param name="action" value="<%= EditTournament.ADD_MATCH %>"/>
				</c:url>
				<a href='<c:out value="${addMatchURL}" />'>Add new match</a>
			</td>		
		</tr>		
	</tbody>
</table>

<%-- "save changes" --%>
<c:url value="process_save_changes.jsp" var="saveChangesURL">
	<c:param name="tournamentID" value="${pager.tournamentID}"/>
</c:url>
<center><input type="submit" name="submitButton" value="Save" style="color:#c0c0c0; font-size:14pt; font-weight:bold;" onclick="window.location='${saveChangesURL}'"></center>

</form>

<!-- pager --> <jsp:directive.include file="/inc/pager.jsp" /> <c:if
	test="${pager.playerName != null}">
	<h1>Legend</h1>
	<div class="errors"></div> &ndash; some players produced errors, including player ${pager.playerName} <br>
	<div class="errors_bw"></div> &ndash; some other players produced errors
	</c:if>

<h1>Notes</h1>
<ul>
	<li>Before viewing, starting, deleting, cloning or adding 
	a match, you have to save your changes.</li>
	<li>When clicking "save changes", all changes will be stored
	persistently, including any new matches. They can still be 
	edited.</li>
	<li>New matches won't show up on the publicly visible 
	public/show_matches.jsp page; only running, aborted and 
	finished matches do.</li>
</ul>
</div><!--end div "content"-->
<jsp:directive.include file="/inc/footer.jsp" />
</div><!-- end div "everything" -->
</body>
</html>
