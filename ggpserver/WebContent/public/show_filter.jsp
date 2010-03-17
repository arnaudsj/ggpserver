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

<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowMatchFilter" scope="page"/>

<jsp:directive.include file="/inc/set_match_filter_params.jsp" />

<c:choose>
	<c:when test="${editMatchFilter.applyFilter}">
		<jsp:setProperty name="pager" property="filter" value="${editMatchFilter.filter}"/>
		<%-- it is necessary to set the page number after the filter, because computing the number of pages depends on the filter --%>
		<jsp:setProperty name="pager" property="page"/>
		<c:set var="title">Matches filtered with &quot;${editMatchFilter.filter.name}&quot;</c:set>
		<c:if test="${pager.numberOfPages > 1}">
			<c:set var="title">
				${title} (${pager.page}/${pager.numberOfPages})
			</c:set>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="title">Filtered Matches</c:set>
	</c:otherwise>
</c:choose>	
<jsp:directive.include file="/inc/header.jsp" />

<%-- show the filter/filter set --%> 
<c:url value="/public/show_filter.jsp" var="pageURL" />
<jsp:setProperty name="editMatchFilter" property="keepParam" value="page"/>
<jsp:directive.include file="/inc/edit_match_filter.jsp" />

<%-- show the filtered list of matches --%> 
<c:if test="${editMatchFilter.applyFilter}">
	<%-- pager title --%>
	<jsp:directive.include file="/inc/pager_title.jsp" />
	<%-- the filtered list of matches --%> 
	<jsp:directive.include file="/inc/match_table.jsp" />
</c:if>

<jsp:directive.include file="/inc/footer.jsp" />
