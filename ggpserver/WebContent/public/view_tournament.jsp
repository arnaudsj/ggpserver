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
<jsp:useBean id="viewTournament" class="tud.ggpserver.formhandlers.ViewTournament" scope="page">
	<c:catch>
		<jsp:setProperty name="viewTournament" property="tournamentID"/>
		<jsp:setProperty name="viewTournament" property="sortBy"/>
		<jsp:setProperty name="viewTournament" property="sortOrder"/>
	</c:catch>
</jsp:useBean>

<c:set var="title">Tournament ${viewTournament.tournamentID}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<table>
		<tbody>
			<tr>
				<th>tournament id</th>
				<td><c:out value="${viewTournament.tournamentID}" /></td>
			</tr>
			<tr>
				<th>owner</th>
				<td>
					<c:url value="view_user.jsp" var="userURL">
						<c:param name="userName" value="${viewTournament.tournament.owner.userName}" />
					</c:url>
					<a href='<c:out value="${userURL}" />'>${viewTournament.tournament.owner.userName}</a>
				</td>
			</tr>
			<tr>
				<th>number of matches</th>
				<td>
					<c:out value="${viewTournament.tournament.numberOfMatches}" />
				</td>
			</tr>
			<tr>
				<th>matches</th>
				<td>
					<c:url value="show_matches.jsp" var="URL">
						<c:param name="tournamentID" value="${viewTournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${URL}" />'>show matches</a>
				</td>
			</tr>
			<tr>
				<th>export</th>
				<td>
					<c:url value="export_xml.jsp" var="exportXMLURL">
						<c:param name="tournamentID" value="${viewTournament.tournamentID}" />
					</c:url>
					<a href='<c:out value="${exportXMLURL}" />'>export xml</a>
					<c:if test="${viewTournament.tournament.numberOfMatches >= 1000}">
						<span style="color:red;">Caution: This may take a long time and produce a big file!</span>
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>    
    <h2>Leader board</h2>
	<table>
		<thead>
			<tr>
				<th style="vertical-align: middle;"></th>
				<c:forEach var="field" items="${viewTournament.fieldNames}" varStatus="fieldInfo">
					<c:url value="view_tournament.jsp" var="sortURL">
					    <c:param name="tournamentID" value="${viewTournament.tournamentID}"/>
					    <c:param name="sortBy" value="${field}"/>
					    <c:choose>
					    	<c:when test="${field != viewTournament.sortBy}">
					    		<c:param name="sortOrder" value="desc" />
					    	</c:when>
					    	<c:when test="${viewTournament.sortOrder != 'desc'}">
					    		<c:param name="sortOrder" value="desc" />
					    	</c:when>
					    	<c:otherwise>
					    		<c:param name="sortOrder" value="asc" />
					    	</c:otherwise>
						</c:choose>
					</c:url>
					<th style="vertical-align: middle;">
						<a href="${sortURL}">
							${viewTournament.fieldDescriptions[fieldInfo.count - 1]}
							<c:if test="${field == viewTournament.sortBy}">
							    <c:choose>
							    	<c:when test="${viewTournament.sortOrder == 'desc'}">
							    		<img src='<%= request.getContextPath() + "/icons/other/16_downarrow.png" %>' />
							    	</c:when>
							    	<c:otherwise>
							    		<img src='<%= request.getContextPath() + "/icons/other/16_uparrow.png" %>' />
							    	</c:otherwise>
								</c:choose>
							</c:if>
						</a>
					</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="player" items="${viewTournament.orderedPlayers}" varStatus="lineInfo">
	      	 <c:choose>
			   <c:when test="${lineInfo.count % 2 == 0}">
			     <c:set var="rowClass" value="even" />
			   </c:when> 
			   <c:otherwise>
			     <c:set var="rowClass" value="odd" />
			   </c:otherwise>
			 </c:choose> 
		     <tr class="${rowClass}">
				<td>${lineInfo.count}</td>
				<td>
					<c:url value="view_player.jsp" var="playerURL">
						<c:param name="name" value="${player.name}" />
					</c:url>
					<a href='<c:out value="${playerURL}" />'>
						<c:out value="${player.name}" />
					</a>
				</td>
				<td>${viewTournament.tournamentStatistics.numberOfMatches[player]}</td>
				<td>${viewTournament.tournamentStatistics.totalReward[player]}</td>
				<td>${viewTournament.tournamentStatistics.averageReward[player]}</td>
			</tr>
	      </c:forEach>
		</tbody>
	</table>

<jsp:directive.include file="/inc/footer.jsp" />