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

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="adminPage" class="tud.ggpserver.formhandlers.AdminPage" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="adminPage" property="action"/>
		<jsp:setProperty name="adminPage" property="cacheCleared"/>
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Dresden GGP Server</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="<%= request.getContextPath() %>/style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
<div id="everything">
<jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" />

<!-- Content -->
<div id="content">
    <div id="ctitle">Admin page</div>
    <h1 class="notopborder">Scheduler</h1>
    
    <c:choose>
    	<c:when test="${adminPage.running}">
			<c:url value="index.jsp" var="url">
				<c:param name="action" value="stop" />
			</c:url>
			The round-robin scheduler is <b>running</b>. Click <a href='<c:out value="${url}" />'>here</a> to stop it.
    	</c:when>
    	<c:otherwise>
			<c:url value="index.jsp" var="url">
				<c:param name="action" value="start" />
			</c:url>
			The round-robin scheduler is <b>not running</b>. Click <a href='<c:out value="${url}" />'>here</a> to start it.
    	</c:otherwise>
    </c:choose>
    
    <h1>Cache</h1>
	<c:url value="process_clear_cache.jsp" var="cacheURL" />
    Click <a href='<c:out value="${cacheURL}" />'>here</a> to clear the cache (forces re-reading everything from the database).

	<c:if test="${adminPage.cacheCleared}">
		<script language="javascript" type="text/javascript">
			alert ('Cache was successfully cleared.');
		</script>
	</c:if>

</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>