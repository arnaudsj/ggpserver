<%--
    Copyright (C) 2009 Martin G�nther (mintar@gmx.de)
                  2010 Stephan Schiffel (stephan.schiffel@gmx.de)

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
			<c:if test="${pager.previousPage != null}">
				<c:url value="${pager.targetJsp}" var="pageURL">
					<c:param name="page" value="${pager.previousPage.number}" />
				</c:url>
				<c:choose>
					<c:when test="${pager.previousPage.title != ''}">
						<a href='<c:out value="${pageURL}"/>' title="${pager.previousPage.title}">Previous</a>
					</c:when>
					<c:otherwise>
						<a href='<c:out value="${pageURL}"/>'>Previous</a>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:forEach var="linkedPage" items="${pager.linkedPages}">
				<c:if test="${(linkedPage.number != previousI + 1) && (previousI != -1)}">
					...
				</c:if>
				<c:choose>
					<c:when test="${linkedPage.number == pager.page}">
						<b>${linkedPage.number}</b>
					</c:when>
					<c:otherwise>
						<c:url value="${pager.targetJsp}" var="pageURL">
							<c:param name="page" value="${linkedPage.number}" />
						</c:url>
						<c:choose>
							<c:when test="${linkedPage.title != ''}">
								<a href='<c:out value="${pageURL}"/>' title="${linkedPage.title}">${linkedPage.number}</a>
							</c:when>
							<c:otherwise>
								<a href='<c:out value="${pageURL}"/>'>${linkedPage.number}</a>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<c:set var="previousI" value="${linkedPage.number}"/> 
			</c:forEach>
			<c:if test="${pager.nextPage != null}">
				<c:url value="${pager.targetJsp}" var="pageURL">
					<c:param name="page" value="${pager.nextPage.number}" />
				</c:url>
				<c:choose>
					<c:when test="${pager.nextPage.title != ''}">
						<a href='<c:out value="${pageURL}"/>' title="${pager.nextPage.title}">Next</a>
					</c:when>
					<c:otherwise>
						<a href='<c:out value="${pageURL}"/>'>Next</a>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
	</c:if>
