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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="en_US"/>
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

<jsp:useBean id="availability" class="tud.ggpserver.formhandlers.inc.Availability"
	scope="page">
	<c:catch>
		<jsp:setProperty name="availability" property="matchID" />
		<jsp:setProperty name="availability" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
		<jsp:setProperty name="availability" property="available" />
	</c:catch>
</jsp:useBean>

<jsp:useBean id="viewState" class="tud.ggpserver.formhandlers.ViewState"
	scope="page">
	<c:catch>
		<jsp:setProperty name="viewState" property="matchID" />
		<jsp:setProperty name="viewState" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
	</c:catch>
</jsp:useBean>

<c:set var="match" value="${viewMatch.match}" />
<c:set var="title">Match ${match.matchID}</c:set>
<c:catch>
	<c:set var="userName"><%= request.getUserPrincipal().getName() %></c:set>
</c:catch>

<c:set var="matchStatus" value="${match.status}" />


<jsp:directive.include file="/inc/header.jsp" />

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
			<td>
				<c:out value="${matchStatus}" />
			</td>
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
			<th>
				<table>
					<tbody>
					<tr><td>roles</td></tr>
					<tr><td>players</td></tr>
					<c:if test="${match.status == 'scheduled'}">
						<tr><td>status</td></tr>
					</c:if>
					<c:if test="${match.orderedGoalValues != null}">
						 <tr><td> scores </td></tr>
					</c:if>
					</tbody>
				</table>
			</th>
			<td>
				<table>
					<thead>
					<tr>
						<c:forEach var="role" items="${match.orderedPlayerRoles}">
							<th>
								<c:out value="${role}"/>
							</th>
						</c:forEach>
						<c:if test="${match.weight != 1.0}">
							<td></td>
						</c:if> 
					</tr>
					</thead>
					<tbody>
					<tr class="even">
						<c:forEach var="playerinfo"	items="${match.orderedPlayerInfos}">
							<td>
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
								<img src="../icons/other/players/<c:out value="${playerinfo.type}"/>.png" title="<c:out value="${playerinfo.type}"/>" />
							</td>
						</c:forEach>
						<c:if test="${match.weight != 1.0}">
							<td></td>
						</c:if> 
					</tr>
					<c:if test="${match.status == 'scheduled'}">
						<tr class="even">
							<c:forEach var="i" begin="0" end="${match.game.numberOfRoles - 1}">
								<c:set var="role">${match.orderedPlayerRoles[i]}</c:set>
								<td>
									<c:choose>
										<c:when test="${viewMatch.statuses[i].right == 'accepted'}">
											accepted
											<c:if test="${userName == viewMatch.statuses[i].left}">
												<img src="../icons/other/16-loading.gif" title="waiting for other players to accept..."/>
												<script type="text/javascript" language="JavaScript">
											    	setTimeout("document.location.reload()", 3000);
											    </script>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${userName == viewMatch.statuses[i].left}">
													<c:url value="/public/view_match.jsp" var="acceptURL">
														<c:param name="matchID" value="${viewMatch.matchID}"/>
														<c:param name="role" value="${role}" />
														<c:param name="available" value="1"/>
												    </c:url>
												    <a href='<c:out value="${acceptURL}"/>'>
												    	<div class="start" title="allow this game to begin"></div>
												    	allow this game to begin
												    </a>
												</c:when>
												<c:otherwise>
													has not accepted yet
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
									</a>
									<img src="../icons/other/players/<c:out value="${playerinfo.type}"/>.png" title="<c:out value="${playerinfo.type}"/>" />
								</td>
							</c:forEach>
							<c:if test="${match.weight != 1.0}">
								<td></td>
							</c:if> 
						</tr>
					</c:if>
					<c:if test="${matchStatus == 'running'}">
						<tr class="even">
							<c:forEach var="i" begin="0" end="${match.game.numberOfRoles - 1}">
								<td style="align: center;">
									<c:if test="${ match.orderedPlayerNames[i] == userName }">
										<c:url value="/members/play.jsp" var="playURL">
									    	<c:param name="matchID" value="${match.matchID}" />
									    	<c:param name="role" value="${match.orderedPlayerRoles[i]}" />
									    </c:url>
									    <a href='<c:out value="${playURL}" />'><div class="play" title="play match! (as <c:out value="${match.orderedPlayerRoles[i]}"/>)"><span>play</span></div></a>
									</c:if>
								</td>
							</c:forEach>
							<c:if test="${match.weight != 1.0}">
								<td></td>
							</c:if> 
						</tr>
					</c:if>
					<c:if test="${match.orderedGoalValues != null}">
						<tr class="even">
							<c:forEach var="roleindex" begin="0" end="${match.game.numberOfRoles - 1}">
								<td>
									<c:choose>
										<c:when test="${ match.orderedPlayerInfos[roleindex].name == viewMatch.playerName }">
											<span class="highlight">${match.orderedGoalValues[roleindex]}</span>
										</c:when>
										<c:otherwise>
											${match.orderedGoalValues[roleindex]}
										</c:otherwise>
									</c:choose>
								</td>
							</c:forEach>
							<c:if test="${match.weight != 1.0}">
								<td>*${match.weight}</td>
							</c:if> 
						</tr>
					</c:if>
					</tbody>
				</table>
			</td>
		</tr>
		<tr>
			<th>owner</th>
			<td>
				<c:url value="view_user.jsp" var="viewUserURL">
					<c:param name="userName" value="${match.owner.userName}" />
				</c:url>
				<a href='<c:out value="${viewUserURL}" />'><c:out value="${match.owner.userName}" /></a>
			</td>
		</tr>
		<tr>
			<th>tournament</th>
			<td>
				<c:url value="view_tournament.jsp" var="viewTournamentURL">
					<c:param name="tournamentID" value="${match.tournamentID}" />
				</c:url>
				<a href='<c:out value="${viewTournamentURL}" />'><c:out value="${match.tournamentID}" /></a>
			</td>
		</tr>
		<tr>
			<th>export</th>
			<td>
				<c:url value="/servlet/ExportXML" var="exportXMLURL">
					<c:param name="matchID" value="${match.matchID}" />
				</c:url>
				<a href='<c:out value="${exportXMLURL}" />'>export xml</a>
			</td>
		</tr>
	</tbody>
</table>



<h1>Match history</h1>

<c:choose>
	<c:when test="${! viewState.viewable}">
		The history for this game is not available yet, because some information may not be seen by all players.
	</c:when>
	<c:otherwise>

<table>
	<thead>
		<tr>
			<!--<th>step number</th>-->
			<th align="center" rowspan="2">state</th>
			
			<th align="center" colspan="<c:out value="${match.game.numberOfRoles}"/>">moves</th>
			
			<th align="center" rowspan="2">
				errors
			</th>
			<th align="center" rowspan="2">
				date & time
			</th>
		</tr>
		
		<tr>
		
		<c:forEach var="role" items="${match.orderedPlayerRoles}">
			<th>
				<div style="align: center;">
					<c:out value="${role}"/>
					<c:if test="${viewMatch.gdlVersion == 2}">
						<c:url value="view_state.jsp" var="stateURL">
							<c:param name="matchID" value="${match.matchID}" />
							<c:param name="stepNumber" value="1" />
							<c:param name="role" value="${role}" />
						</c:url>
						<a href='<c:out value="${stateURL}" />'>
							<span class="view" title="View initial state from ${role}'s perspective"></span></a>
						</a>
					</c:if>
				</div>
			</th>
		</c:forEach>
		</tr>
		
	</thead>
	
	<tbody>
		<c:forEach var="stepNumber" begin="1" end="<%= viewMatch.getMatch().getStringStates().size() %>"
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
				<td>
					<c:url value="view_state.jsp" var="stateURL">
						<c:param name="matchID" value="${match.matchID}" />
						<c:param name="stepNumber" value="${stepNumber}" />
					</c:url>
					<a href='<c:out value="${stateURL}" />'>state ${stepNumber}</a>
				</td>
				
				<c:choose>
					<c:when test="${viewMatch.noMoves}">
						<c:forEach var="roleindex" begin="0" end="${match.game.numberOfRoles - 1}">
							<td></td>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<c:forEach var="move" items="${viewMatch.moves}">
							<td align="center">
								${move}
							</td>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				
				<td>
					<center>
						<c:choose>
							<c:when test="${!empty viewMatch.errorMessages}">
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
				<td> <%-- State timestamp --%>
					<fmt:formatDate value="${viewMatch.timestamp}" pattern="dd.MM.yyyy HH:mm:ss z"/>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

	</c:otherwise>
</c:choose>

<jsp:directive.include file="/inc/footer.jsp" />