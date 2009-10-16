<%--
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>,
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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

<jsp:useBean id="adminPage" class="tud.ggpserver.formhandlers.AdminPage" scope="request">
	<c:catch>
		<jsp:setProperty name="adminPage" property="nextPlayedGameName"/>
		<jsp:setProperty name="adminPage" property="startclockMin"/>
		<jsp:setProperty name="adminPage" property="startclockMax"/>
		<jsp:setProperty name="adminPage" property="startclockMean"/>
		<jsp:setProperty name="adminPage" property="startclockStdDeviation"/>
		<jsp:setProperty name="adminPage" property="playclockMin"/>
		<jsp:setProperty name="adminPage" property="playclockMax"/>
		<jsp:setProperty name="adminPage" property="playclockMean"/>
		<jsp:setProperty name="adminPage" property="playclockStdDeviation"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
	
	String urlWithSessionID = response.encodeRedirectURL("index.jsp");
	response.sendRedirect(urlWithSessionID);
%>