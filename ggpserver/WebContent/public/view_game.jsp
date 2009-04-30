<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewGame"
	class="tud.ggpserver.formhandlers.ViewGame" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewGame" property="name" />
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
<div id="ctitle">View game</div>

<h1 class="notopborder">Information on game ${viewGame.name}
	<c:catch var="exception">
		<c:set var="userName" value="<%= request.getUserPrincipal().getName()%>"></c:set>		
	</c:catch>
	<c:if test='${userName == "admin"}'>
		<c:url value="../admin/edit_game.jsp" var="editURL">
			<c:param name="gameName" value="${viewGame.name}" />
		</c:url>
		<div class="edit"><a href='<c:out value="${editURL}"/>'><span>edit</span></a></div>
	</c:if>
</h1>
<table>
	<tbody>
		<tr>
			<td><b>name</b></td>
			<td><c:out value="${viewGame.name}"></c:out></td>
		</tr>
		<tr>
			<td><b>number of roles</b></td>
			<td><c:out value="${viewGame.game.numberOfRoles}"></c:out></td>
		</tr>
		<tr>
			<td><b>stylesheet</b></td>
			<td><c:out value="${viewGame.game.stylesheet}"></c:out></td>
		</tr>
	</tbody>
</table>

<h1>Game Description</h1>
<p style="font-family: monospace; font-size:medium">
<code>${viewGame.gameDescription}</code>
</p> 


</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
