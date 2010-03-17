<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2010 Peter Steinke (peter.steinke@inf.tu-dresden.de)
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${pageURL==null || pageURL==''}"> 
	<c:set var="pageURL" value="${pageContext.request.requestURI}"/>
</c:if>

<form name="form" action="${pageURL}" method="post">
	<c:forEach var="paramName" items="${editMatchFilter.parametersToKeep}">
		<c:forEach var="paramValue" items="${paramValues[paramName]}">
			<input type="hidden" name="${paramName}" value="${paramValue}"/>
		</c:forEach>
	</c:forEach>
	<input type="hidden" name="showFilter" value="${editMatchFilter.showFilter}" />
	<input type="hidden" name="applyFilter" value="${editMatchFilter.applyFilter}" />

	<%-- default submit button (is used on ENTER) --%>
	<input type="submit" name="save_filter" value="save filter" style="position: absolute; left:-999px; top:-999px; height:0; width:0;" />

	<%-- hide/show the filter --%>
	<c:choose>
		<c:when test="${editMatchFilter.showFilter}">
			<input type="submit" name="hide_filter" value="hide filter(s)"/>
			<br/>
			<%-- select the filter --%>
			<span class="heading">Choose a filter:</span> 
			<select name="filterId" onchange="form.submit()">
				<set var="selectedFilterName" value="none"/>
				<c:forEach var="filter" items="${editMatchFilter.filters}">
					<c:choose>
						<c:when test="${filter.id == editMatchFilter.filterId}">
							<option value="${filter.id}" selected=\"selected\">${filter.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${filter.id}">${filter.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			<c:if test="${editMatchFilter.filter.editable}">
				<input type="submit" name="delete_filter" value="delete" />
			</c:if>
			<input type="submit" name="add_new_filter" value="add new" />
			<%-- show the selected filter for editing --%> 
			${editMatchFilter.filterHtml}
			<%-- apply filter button --%>
			<input type="submit" name="apply_filter" value="Apply filter" />
		</c:when>
		<c:otherwise>
			<input type="hidden" name="filterId" value="${editMatchFilter.filterId}"/>
			<input type="submit" name="show_filter" value="show filter(s)"/>
		</c:otherwise>
	</c:choose>
</form>
