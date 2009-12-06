<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<jsp:directive.include file="/inc/headincludes.jsp" />
	</head>
	<body>
		<div id="everything">
		
			<!-- header image -->
			<div id="header">
				<a href="<%= request.getContextPath() + response.encodeURL("/index.jsp") %>">
					<img src="<%= request.getContextPath() + response.encodeURL("/gfx/GGPHeaderBlau900.png") %>" width="900" height="115" alt="General Game Playing">
				</a>
			</div>
		
			<!-- user login/register/logout -->
			<div id="user">
				<c:catch var="exception">
					<c:set var="userName" value="<%= request.getUserPrincipal().getName()%>"></c:set>
				</c:catch>
			    <c:choose>
					<c:when test="${exception != null}">
						You are not logged in. 
						<a href="<%= request.getContextPath() + response.encodeURL("/members/profile.jsp") %>">login</a> 
						<a href="<%= request.getContextPath() + response.encodeURL("/register/register.jsp") %>">register</a>
					</c:when>
					<c:otherwise>
						You are logged in as <b>${userName}</b>. 
						<a href="<%= request.getContextPath() + response.encodeURL("/login/logout.jsp") %>">logout</a>
					</c:otherwise>
			    </c:choose>
			</div>
		
			<!-- navigation -->
		
			<!-- Content -->
			<c:choose>
				<c:when test="${omitNavigation == 'true'}">
					<div id="content" style="width:auto">  <%-- width:auto is used in conjunction with removing the navigation menu --%>
				</c:when>
				<c:otherwise>
					<div id="content">
				</c:otherwise>
			</c:choose>
				<div id="ctitle">${title}</div>
