<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewErrors"
	class="tud.ggpserver.formhandlers.ViewErrors" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewErrors" property="matchID" />
		<jsp:setProperty name="viewErrors" property="stepNumber" />
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">View errors</div>

<h1 class="notopborder">Errors for step ${viewErrors.stepNumber} of match ${viewErrors.matchID}</h1>
<ul>
	<c:forEach var="error" items="${viewErrors.errorMessages}">
		<li><c:out value="${error.message}"></c:out></li>
	</c:forEach>
</ul>


</div><!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div><!-- end div "everything" -->
</body>
</html>
