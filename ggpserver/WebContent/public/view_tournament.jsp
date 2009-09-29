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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything">
<jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" />

<!-- Content -->
<div id="content">
    <div id="ctitle">View Tournament</div>
    <h1 class="notopborder">Information on tournament ${viewTournament.tournamentID}</h1>
	<table>
		<thead>
			<tr>
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
	
</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>