<%--
    Copyright (C) 2009 Martin G�nther (mintar@gmx.de)

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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<c:if test="${pager.numberOfPages != 1}">
		<div class="pager">
			<c:set var="previousI" value="-1"/> 
			<c:forEach var="i" items="${pager.linkedPages}">
				<c:if test="${(i != previousI + 1) && (previousI != -1)}">
					...
				</c:if>
				<c:choose>
					<c:when test="${i == pager.page}" >
						<b>${i}</b>
					</c:when>
					<c:otherwise>
						<c:url value="${pager.targetJsp}" var="pageURL">
							<c:param name="page" value="${i}" />
						</c:url>
						<a href='<c:out value="${pageURL}" />'>${i}</a>
					</c:otherwise>
				</c:choose>
				<c:set var="previousI" value="${i}"/> 
			</c:forEach>
		</div>
	</c:if>
