<%--
    Copyright (C) 2010 Nicolas JEAN <njean42@gmail.com>

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

<%@ page contentType="application/xml" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="availability" class="tud.ggpserver.formhandlers.inc.Availability" scope="page">
	<c:catch>
		<jsp:setProperty name="availability" property="matchID" />
		<jsp:setProperty name="availability" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
		<jsp:setProperty name="availability" property="available" />
	</c:catch>
</jsp:useBean>
<jsp:useBean id="play"
	class="tud.ggpserver.formhandlers.Play" scope="page">
	<!-- <c:catch> -->
		<jsp:setProperty name="play" property="matchID" />
		<jsp:setProperty name="play" property="userName" value="<%= request.getUserPrincipal().getName() %>" />
		<jsp:setProperty name="play" property="role"/>
		<jsp:setProperty name="play" property="forStepNumber"/> <%-- the step in the match for which the chosenMove/confirm is meant --%>
		<jsp:setProperty name="play" property="chosenMove"/>
		<jsp:setProperty name="play" property="confirm"/>
		<jsp:setProperty name="play" property="quickConfirm"/>
	<!-- </c:catch> -->
</jsp:useBean>
<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
<c:choose>
	<c:when test="${play.playing}">
<% out.clearBuffer(); // to remove newline characters up to here %>${play.xmlState}
	</c:when>
	<c:when test="${play.finished}">
		<%
			String urlWithSessionID = response.encodeRedirectURL("../public/view_state.jsp?matchID="+play.getMatchID()+"&stepNumber=final&role=RANDOM");
			response.sendRedirect(urlWithSessionID);
		%>
	</c:when>
	<c:otherwise>
		<%
			String urlWithSessionID = response.encodeRedirectURL("../public/view_match.jsp?matchID="+play.getMatchID());
			response.sendRedirect(urlWithSessionID);
		%>
	</c:otherwise>
</c:choose>