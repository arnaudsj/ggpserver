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

<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="saveFilter" class="tud.ggpserver.formhandlers.SaveFilter" scope="page">
      <c:catch>
	<jsp:setProperty name="saveFilter" property="show_matches" />
      </c:catch>
</jsp:useBean>

<% if (session.getAttribute("filter") == null) { %>
      <jsp:forward page="show_filter.jsp"/>
<%   } %>
  

<% saveFilter.setFilter(session.getAttribute("filter")); %>
<% saveFilter.parseParameterMap(request.getParameterMap()); %>

<c:if test="${saveFilter.show_matches == 1}">
	  <jsp:forward page="show_filter.jsp?show_matches=1"/>
</c:if>

<c:if test="${saveFilter.show_matches == 0}">
	  <jsp:forward page="show_filter.jsp?show_matches=0"/>
</c:if>



