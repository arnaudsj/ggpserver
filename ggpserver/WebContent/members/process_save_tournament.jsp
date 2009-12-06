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
      <c:catch>
	<jsp:setProperty name="saveTournament" property="page" />
	<jsp:setProperty name="saveTournament" property="newContent" />
      </c:catch>
</jsp:useBean>

<% saveTournament.parseParameterMap(request.getParameterMap()); %>

<%-- <c:url var="forwardURL" value="edit_tournament.jsp">
	<c:param name="tournamentID" value="${saveTournament.tournamentID}"/>
	<c:param name="page" value="${saveTournament.page}" />
</c:url> --%>
<c:choose> 
  <c:when test="${saveTournament.correctlyPerformed}" > 
    <c:choose> 
      <c:when test="${saveTournament.newContent}" > 
	  <jsp:forward page="edit_tournament.jsp"/>
      </c:when> 
      <c:otherwise>
	<% response.setStatus(204); %>
      </c:otherwise>
    </c:choose>
  </c:when> 
  <c:otherwise>
    <c:set var="title">Input error</c:set>
    <jsp:directive.include file="/inc/header.jsp" />	
    ${saveTournament.errorString}
    <br />
    <a href="<%= request.getContextPath() + response.encodeURL("/members/edit_tournament.jsp?tournamentID=" + request.getParameter("tournamentID")) %>">&lt;&lt;&lt; back to edit tournament page</a>
  </c:otherwise>
</c:choose>  


