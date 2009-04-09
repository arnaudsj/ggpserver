<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="scheduler" class="tud.ggpserver.formhandlers.AdminPage" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="scheduler" property="action"/>
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
    
    <c:choose>
    	<c:when test="${scheduler.running}">
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

</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>