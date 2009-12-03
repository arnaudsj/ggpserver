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

<jsp:useBean id="saveTournament" class="tud.ggpserver.formhandlers.SaveTournament" scope="request">
	<jsp:setProperty name="saveTournament" property="page" />
</jsp:useBean>

<% saveTournament.parseParameterMap(request.getParameterMap()); %>

<c:url var="forwardURL" value="edit_tournament.jsp">
	<c:param name="tournamentID" value="${saveTournament.tournamentID}"/>
	<c:param name="page" value="${saveTournament.page}" />
</c:url>
<jsp:forward page="edit_tournament.jsp"/>
