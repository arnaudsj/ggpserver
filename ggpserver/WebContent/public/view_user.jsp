<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewUser"
	class="tud.ggpserver.formhandlers.ViewUser" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewUser" property="userName" />
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
<div id="ctitle">View user</div>

<%
if (viewUser.getUser() == null) {
	response.sendError(404);
	return;
}
%>
<h1 class="notopborder">Information on user ${viewUser.user.userName}</h1>
<table>
	<tbody>
		<tr>
			<td><b>user name</b></td>
			<td><c:out value="${viewUser.user.userName}"></c:out></td>
		</tr>
	</tbody>
</table>

</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
