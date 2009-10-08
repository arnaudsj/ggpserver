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
		<jsp:setProperty name="viewErrors" property="playerName"/>
		<% // <jsp:setProperty name="viewErrors" property="stepNumber" /> %> 
	</c:catch>
</jsp:useBean>

<c:set var="match" value="${viewErrors.match}" />

<c:set var="title">Errors for ${match.matchID}</c:set>
<jsp:directive.include file="/inc/header.jsp" />

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
					<td>
						<a name='step<c:out value="${stepNumber}" />'> </a>
						step <c:out value="${stepNumber}" />
					</td>
					<td>
						
						<ul>
							<c:forEach var="error" items="${viewErrors.errorMessages}">
								<li>
									<c:choose>
										<c:when test="${ error.playerName == viewErrors.playerName }">
											<span class="highlight"><c:out value="${error.message}" /></span>
										</c:when>
										<c:otherwise>
											<c:out value="${error.message}" />
										</c:otherwise>
									</c:choose>
								</li>
							</c:forEach>
						</ul>
					</td>
				</tr>
			</c:if>
		</c:forEach>
	</tbody>
</table>

<jsp:directive.include file="/inc/footer.jsp" />