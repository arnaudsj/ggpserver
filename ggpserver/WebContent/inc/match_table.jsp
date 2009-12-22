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
<%--
	This file prints a table of matches returned by tud.ggpserver.formhandlers.ShowMatches or a subclass thereof.
	It assumes that there is a bean called "pager" that is an instance of tud.ggpserver.formhandlers.ShowMatches in the current context.
--%>

<jsp:directive.include file="/inc/pager.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="en_US"/>
<table>
	<thead>
		<tr>
			<th>match name</th>
			<th>start &amp; play clock</th>
			<th>start time</th>
			<th>players</th>
			<th>goal values</th>
			<th>actions</th>
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
			<c:set var="numberOfPlayers" value="${match.game.numberOfRoles}"/>
			<!-- numberOfPlayers=${numberOfPlayers} -->
			<c:forEach var="playerinfo" items="${match.orderedPlayerInfos}" varStatus="playerinfoIndex">
				<tr class="${rowClass}">
			    	<c:if test="${playerinfoIndex.count==1}">
						<td rowspan="${numberOfPlayers}">  <%-- match id --%>
							<c:url value="/public/view_match.jsp" var="matchURL">
								<c:param name="matchID" value="${match.matchID}" />
							    <c:if test="${ pager.playerName != null }">
									<c:param name="playerName" value="${pager.playerName}" />
								</c:if>
							</c:url>
							<a href='<c:out value="${matchURL}" />'>${match.matchID}</a>
						</td>
						<%-- <td rowspan="${numberOfPlayers}">${match.status}</td> --%>  <%-- status --%>
						<td rowspan="${numberOfPlayers}">${match.startclock}, ${match.playclock}</td> <%-- start &amp; play clock --%>
						<td rowspan="${numberOfPlayers}"><fmt:formatDate value="${match.startTime}" pattern="dd.MM.yyyy HH:mm:ss z"/></td>  <%-- start time --%>
					</c:if>
					<td style="white-space:nowrap;">  <%-- players --%>
						<c:url value="/public/view_player.jsp" var="playerURL">
							<c:param name="name" value="${playerinfo.name}" />
						</c:url>
						<a href='<c:out value="${playerURL}" />'>
							<c:choose>
								<c:when test="${playerinfo.name == pager.playerName}">
									<span class="highlight">${playerinfo.name}</span>
								</c:when>
								<c:otherwise>
									${playerinfo.name}
								</c:otherwise>
							</c:choose>
						</a>
						<c:if test="${match.hasErrorsAllPlayers[playerinfo.name]}">
							<c:url value="/public/view_errors.jsp" var="errorURL">
								<c:param name="matchID" value="${match.matchID}" />
								<c:param name="playerName" value="${playerinfo.name}" />
							</c:url>
							<c:choose>
								<c:when test="${pager.playerName != null && pager.playerName != playerinfo.name}">
									<c:set var="errorclass" value="errors_bw"/>
								</c:when>
								<c:otherwise>
									<c:set var="errorclass" value="errors"/>
								</c:otherwise>
							</c:choose>
							<a href='<c:out value="${errorURL}"/>'><span class="${errorclass}" title="Show Errors of ${playerinfo.name}"></span></a>
						</c:if>
					</td>
					<%-- goal values / status --%>
					<c:choose>
						<c:when test="${match.goalValues==null}">
					    	<c:if test="${playerinfoIndex.count==1}">
								<td rowspan="${numberOfPlayers}">
									${match.status}
								</td>
					    	</c:if>
						</c:when>
						<c:otherwise>
							<td>
								<c:choose>
									<c:when test="${ match.orderedPlayerInfos[playerinfoIndex.count - 1].name == pager.playerName }">
										<span class="highlight">${match.orderedGoalValues[playerinfoIndex.count - 1]}</span>
									</c:when>
									<c:otherwise>
										${match.orderedGoalValues[playerinfoIndex.count - 1]}
									</c:otherwise>
								</c:choose>
							</td>
						</c:otherwise>
					</c:choose>
					<%-- actions --%>
			    	<c:if test="${playerinfoIndex.count==1}">
						<td rowspan="${numberOfPlayers}">
						    <c:url value="/public/view_state.jsp" var="viewStateURL">
							    <c:param name="matchID" value="${match.matchID}" />
							    <c:param name="stepNumber" value="final" />
							</c:url>
							<c:choose>
								<c:when test="{match.goalValues==null}">
									<c:set var="viewStateLinkTitle">View Current State</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="viewStateLinkTitle">View Final State</c:set>
								</c:otherwise>
							</c:choose>
							<a href='<c:out value="${viewStateURL}" />'><span class="view" title="${viewStateLinkTitle}"></span></a>

							<c:choose>
								<c:when test="${match.hasErrors}">
									<c:choose>
										<c:when test="${pager.playerName != null && !match.hasErrorsAllPlayers[pager.playerName]}">
											<c:set var="errorclass" value="errors_bw"/>
										</c:when>
										<c:otherwise>
											<c:set var="errorclass" value="errors"/>
										</c:otherwise>
									</c:choose>
									
									<c:url value="/public/view_errors.jsp" var="errorURL">
										<c:param name="matchID" value="${match.matchID}" />
									</c:url>
									<a href='<c:out value="${errorURL}" />'><span class="${errorclass}" title="Show Errors"></span></a>
								</c:when>
								<c:otherwise>
									<span class="no_errors"></span>
								</c:otherwise>						
							</c:choose>
						</td>
					</c:if>
				</tr>
			</c:forEach>
      </c:forEach>
	</tbody>
</table>
<jsp:directive.include file="/inc/pager.jsp" />
