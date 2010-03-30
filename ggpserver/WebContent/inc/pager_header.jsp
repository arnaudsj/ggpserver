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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:if test="${pager.numberOfPages > 1}">
	<c:set var="title">
		${title} (${pager.page}/${pager.numberOfPages}) ${pager.pageTitle}
	</c:set>
</c:if>
<jsp:directive.include file="/inc/header.jsp" />

<!-- pager -->
<jsp:directive.include file="/inc/pager_title.jsp" />
