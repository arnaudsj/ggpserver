<%--
    Copyright (C) 2010 Peter Steinke (peter.steinke@inf.tu-dresden.de)

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

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="saveFilter" class="tud.ggpserver.formhandlers.SaveFilter" scope="page">
      <c:catch>
	<jsp:setProperty name="saveFilter" property="showMatches" />
      </c:catch>
</jsp:useBean>

<% if (session.getAttribute("filter") == null) { %>
      <jsp:forward page="show_filter.jsp"/>
<%   } %>
  

<% saveFilter.setFilter(session.getAttribute("filter")); %>
<% saveFilter.parseParameterMap(request.getParameterMap()); %>

<c:choose>
	<c:when test="${saveFilter.showMatches}">
		<jsp:forward page="show_filter.jsp?showMatches=true"/>
	</c:when>
	<c:otherwise>
		<jsp:forward page="show_filter.jsp?showMatches=false"/>
	</c:otherwise>
</c:choose>
