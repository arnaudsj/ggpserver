<%--
    Copyright (C) 2010 Stephan Schiffel (stephan.schiffel@gmx.de)
                       
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
<jsp:useBean id="viewTournamentChart" class="tud.ggpserver.formhandlers.ViewTournamentChart" scope="page">
	<c:catch>
		<jsp:setProperty name="viewTournamentChart" property="tournamentID"/>
		<jsp:setProperty name="viewTournamentChart" property="minMatchNumber"/>
		<jsp:setProperty name="viewTournamentChart" property="smoothingFactor"/>
	</c:catch>
</jsp:useBean>

<% viewTournamentChart.setSession(request.getSession()); %>

<c:set var="title">Chart for Tournament ${viewTournamentChart.tournamentID}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<%-- chart --%> 
<c:set var="chartInfo" value="${viewTournamentChart.chart}"/>
<c:url value="/servlet/GameStatisticsChartViewer" var="URL">
	<c:param name="imageID" value="${chartInfo.imageID}"/>
	<c:param name="tournamentID" value="${viewTournamentChart.tournamentID}"/>
	<c:param name="minMatchNumber" value="${viewTournamentChart.minMatchNumber}"/>
	<c:param name="smoothingFactor" value="${viewTournamentChart.smoothingFactor}"/>
</c:url>
<img src="${URL}" usemap="#${chartInfo.imageMapID}">
${chartInfo.imageMap}
<br>
The chart shows the <a href="http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average" target="_blank">exponential moving average</a> of the scores of each player with a smoothing factor of ${viewTournamentChart.smoothingFactor}.
Only players with at least ${viewTournamentChart.minMatchNumber} matches in the tournament are shown.
You can change the smoothing factor and the minimal number of matches for a player by adding
 &amp;smoothingFactor=F&amp;minMatchNumber=N to the URL.


<jsp:directive.include file="/inc/footer.jsp" />