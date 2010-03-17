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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowGames" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="page"/>
	</c:catch>
</jsp:useBean>
<c:set var="title">Games</c:set>
<jsp:directive.include file="/inc/pager_header.jsp" />
<jsp:directive.include file="/inc/pager.jsp" />

<table>
	<thead>
		<tr>
			<th>name</th>
			<th>number of players</th>
			<th>stylesheet</th>
			<th>enabled</th>
			<th>GDL</th>
		</tr>
	</thead>
	<tbody>
      <c:forEach var="game" items="${pager.games}" varStatus="lineInfo">
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
				<c:url value="view_game.jsp" var="gameURL">
					<c:param name="name" value="${game.name}" />
				</c:url>
				<a href='<c:out value="${gameURL}" />'>${game.name}</a>				
			</td>
			<td><c:out value="${game.numberOfRoles}"></c:out></td>
			<td><c:out value="${game.stylesheet}"></c:out></td>
			<td>
				<c:choose>
					<c:when test="${game.enabled}">
						<input type="checkbox" name="enabled" checked disabled>
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="enabled" disabled>
					</c:otherwise>
				</c:choose>
			</td>
			<td><c:out value="${game.gdlVersion}"></c:out></td>
		</tr>
      </c:forEach>
	</tbody>
</table>

<jsp:directive.include file="/inc/pager.jsp" />
<jsp:directive.include file="/inc/footer.jsp" />