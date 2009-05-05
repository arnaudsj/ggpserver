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

<jsp:useBean id="register" class="tud.ggpserver.formhandlers.Register" scope="request">
	<jsp:setProperty name="register" property="*"/>
</jsp:useBean>


<c:choose> 
	<c:when test="${register.valid}" > 
		<%
			// add user to database
			register.createUser();
		%>
		<c:choose> 
			<c:when test="${register.correctlyCreated}" > 
				<%
					response.setHeader("Cache-Control","no-store");
					response.setHeader("Pragma","no-cache");
				
					session.invalidate();
					
					String urlWithSessionID = response.encodeRedirectURL("success.jsp");
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="register.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="register.jsp"/>
	</c:otherwise> 
</c:choose>


