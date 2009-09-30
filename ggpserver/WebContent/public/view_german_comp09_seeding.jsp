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
<jsp:useBean id="viewStats" class="tud.ggpserver.datamodel.GermanComp09SeedingStats" scope="page">
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
    <div id="ctitle">Statistics for the seeding phase of the German GGP Competition 2009</div>
    <p>Get more information about the competition at the <a href="http://www.ggp-potsdam.de/wiki/GGGPC">official competition page</a>.</p>
	<table>
		<thead>
			<tr>
				<th>Place</th>
				<th>Player</th>
				<th>Avg1</th>
				<th>Avg2</th>
				<th>Avg3</th>
				<th>#matches played</th>
				<th>#matches played with &gt;3 errors</th>
				<th>error ratio</th>
			</tr>
		</thead>
		<tbody>

	      <c:forEach var="entry" items="${viewStats.entries}" varStatus="lineInfo">
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
					<c:choose>
						<c:when test="${entry.nb_matches >= 100}">
							${lineInfo.count}
						</c:when>
						<c:otherwise>
							?
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:url value="view_player.jsp" var="playerURL">
						<c:param name="name" value="${entry.player}" />
					</c:url>
					<a href='<c:out value="${playerURL}" />'><c:out value="${entry.player}" /></a>
				</td>
				<td>${entry.avg}</td>
				<td>${entry.avg_of_non_error_matches}</td>
				<td>${entry.avg_of_all_matches}</td>
				<td>${entry.nb_matches}</td>
				<td>${entry.nb_matches_with_errors}</td>
				<td>${entry.error_ratio}</td>
			</tr>
	      </c:forEach>
		</tbody>
	</table>
	
	<h2>Legend</h2>
	<dl>
		<dt>Avg1</dt>
		<dd>Average score of all matches, matches with 3 or more errors by the player count zero for the player.</dd>
		<dt>Avg2</dt>
		<dd>Average score of all matches in which the player had less than 3 errors.</dd>
		<dt>Avg3</dt>
		<dd>Average score of all matches.</dd>
	</dl>

</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>