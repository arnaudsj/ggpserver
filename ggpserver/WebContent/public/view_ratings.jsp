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

<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewRatings"
	class="tud.ggpserver.formhandlers.ViewRatings" scope="page">
</jsp:useBean>

<c:set var="title">Player ratings</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<% viewRatings.computeRatings(request.getSession()); %>
	${viewRatings.ratingsHtmlTable}
	<c:url value="/servlet/ChartViewer" var="chartURL"/>
	<img src="${chartURL}" usemap="#chartImageMap">
	${viewRatings.chartImageMap}

<jsp:directive.include file="/inc/footer.jsp" />
