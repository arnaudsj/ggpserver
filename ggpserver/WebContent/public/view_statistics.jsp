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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="viewStatistics" class="tud.ggpserver.formhandlers.ViewStatistics" scope="page">
	<c:catch>
		<jsp:setProperty name="viewStatistics" property="minMatchNumber"/>
		<jsp:setProperty name="viewStatistics" property="smoothingFactor"/>
	</c:catch>
</jsp:useBean>

<jsp:directive.include file="/inc/set_match_filter_params.jsp" />

<c:set var="title">Average Scores over Time</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<c:url value="/public/view_statistics.jsp" var="pageURL"/>
<jsp:setProperty name="editMatchFilter" property="keepParam" value="smoothingFactor"/>
<jsp:setProperty name="editMatchFilter" property="keepParam" value="minMatchNumber"/>
<jsp:directive.include file="/inc/edit_match_filter.jsp" />

<%-- show the statistics --%> 
<c:if test="${editMatchFilter.applyFilter}">
	<jsp:setProperty name="viewStatistics" property="session" value="${pageContext.session}"/>
	<%-- // viewStatistics.setSession(request.getSession()); --%>
	<jsp:setProperty name="viewStatistics" property="filter" value="${editMatchFilter.filter}"/>

	<%-- chart --%> 
	<p>
		<c:set var="chartInfo" value="${viewStatistics.chart}"/>
		<c:url value="/servlet/GameStatisticsChartViewer" var="URL">
			<c:param name="imageID" value="${chartInfo.imageID}"/>
			<c:param name="filterID" value="${viewStatistics.filterId}"/>
			<c:param name="minMatchNumber" value="${viewStatistics.minMatchNumber}"/>
			<c:param name="smoothingFactor" value="${viewStatistics.smoothingFactor}"/>
		</c:url>
		<img src="${URL}" usemap="#${chartInfo.imageMapID}">
		${chartInfo.imageMap}
		<br/>
		The chart shows the <a href="http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average" target="_blank">exponential moving average</a> of
		the scores of each player with a smoothing factor of ${viewStatistics.smoothingFactor}.
		Only players that played at least ${viewStatistics.minMatchNumber} matches of the selected matches are shown. The data is composed of the scores of ${chartInfo.numberOfMatches} matches.
	</p>
	<p>
		<form action="view_statistics.jsp" method="get">
			<input type="hidden" name="showFilter" value="${editMatchFilter.showFilter}"/>
			<input type="hidden" name="applyFilter" value="${editMatchFilter.applyFilter}"/>
			<input type="hidden" name="filterId" value="${editMatchFilter.filterId}"/>
			<table>
				<tr>
					<th>smoothing factor:</th>
					<td>
						<input type="text" name="smoothingFactor" value="${viewStatistics.smoothingFactor}" size="10" maxlength="10"/>
					</td>
				</tr>
				<tr>
					<th>minimal # of matches for showing a player:</th>
					<td>
						<input type="text" name="minMatchNumber" value="${viewStatistics.minMatchNumber}" size="10" maxlength="10"/>
					</td>
				</tr>
			</table>
			<br/>
			<input type="submit" name="save_changes" value="Update Chart"/>
		</form>
	</p>
</c:if>

<jsp:directive.include file="/inc/footer.jsp" />