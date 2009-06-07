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
<jsp:useBean id="viewErrors"
	class="tud.ggpserver.formhandlers.ViewMatch" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewErrors" property="matchID" />
		<% // <jsp:setProperty name="viewErrors" property="stepNumber" /> %> 
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">View errors</div>
<c:set var="match" value="${viewErrors.match}" />

<h1 class="notopborder">Errors for <% // step ${viewErrors.stepNumber} of match%>${match.matchID}</h1>
<table style="width: 100%;">
	<thead>
		<tr>
			<th>step</th>
			<th>errors</th>
		</tr>
	</thead>
	<tbody>
		<c:set var="rowClass" value="even" />
		
		<c:forEach var="stepNumber" begin="1" end="<%= viewErrors.getMatch().getXmlStates().size() %>">
			<jsp:setProperty name="viewErrors" property="stepNumber"
				value="${stepNumber}" />
			
			<c:if test="<%= !viewErrors.getErrorMessages().isEmpty() %>">
				<c:choose>
					<c:when test='${rowClass == "odd"}'>
						<c:set var="rowClass" value="even" />
					</c:when>
					<c:otherwise>
						<c:set var="rowClass" value="odd" />
					</c:otherwise>
				</c:choose>
				
				<tr class="${rowClass}">
					<td>step <c:out value="${stepNumber}"></c:out></td>
					<td>
						<ul>
							<c:forEach var="error" items="${viewErrors.errorMessages}">
								<li><c:out value="${error.message}"></c:out></li>
							</c:forEach>
						</ul>
					</td>
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>





</div><!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div><!-- end div "everything" -->
</body>
</html>
