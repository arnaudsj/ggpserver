<%--
    Copyright (C) 2010 Peter Steinke (peter.steinke@inf.tu-dresden.de)
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
<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="saveFilter" class="tud.ggpserver.formhandlers.ShowMatchFilter" scope="page"/>
<%
	if (session.getAttribute("filterset") == null) {
		%> <jsp:forward page="show_filter.jsp"/> <%
	} else {
		saveFilter.setFilterSet((tud.ggpserver.filter.FilterSet)session.getAttribute("filterset")); 
		saveFilter.parseParameterMap(request.getParameterMap());
	
		String urlWithSessionID = response.encodeRedirectURL("show_filter.jsp?showMatches=" + saveFilter.getShowMatches() + "&filterID=" + saveFilter.getFilterID() );
		response.sendRedirect(urlWithSessionID);
	}
%>
