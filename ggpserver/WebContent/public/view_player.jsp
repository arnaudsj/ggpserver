<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewPlayer"
	class="tud.ggpserver.formhandlers.ViewPlayer" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewPlayer" property="name" />
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
<div id="ctitle">View player</div>
<%
if (viewPlayer.getPlayer() == null) {
	response.sendError(404);
	return;
}
%>

<h1 class="notopborder">Information on player ${viewPlayer.player.name}</h1>
<table>
	<tbody>
		<tr>
			<td><b>player name</b></td>
			<td><c:out value="${viewPlayer.player.name}"></c:out></td>
		</tr>
		<tr>
			<td><b>owner</b></td>
			<td>
				<c:url value="view_user.jsp" var="userURL">
					<c:param name="userName" value="${viewPlayer.player.owner.userName}" />
				</c:url> <a href='<c:out value="${userURL}" />'>${viewPlayer.player.owner.userName}</a>
			</td>
		</tr>
		<tr>
			<td><b>status</b></td>
			<td><c:out value="${viewPlayer.player.status}"></c:out></td>
		</tr>

	</tbody>
</table>

</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
