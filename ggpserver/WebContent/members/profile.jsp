<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="profile"
	class="tud.ggpserver.formhandlers.Profile" scope="request">
	<c:catch>
		<jsp:setProperty name="profile" property="userName"
			value="<%= request.getUserPrincipal().getName() %>" />
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
<div id="ctitle">Member Profile</div>
<h1 class="notopborder">My Players</h1>

<table>
	<thead>
		<tr>
			<th>player name</th>
			<th>host</th>
			<th>port</th>
			<th>status</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="player" items="${profile.players}" varStatus="lineInfo">
			<c:choose>
				<c:when test="${lineInfo.count % 2 == 0}">
					<c:set var="rowClass" value="even" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="odd" />
				</c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<td><c:url value="edit_player.jsp" var="playerURL">
					<c:param name="playerName" value="${player.name}" />
				</c:url><a href='<c:out value="${playerURL}" />'>${player.name}</a></td>
				<td>${player.host}</td>
				<td>${player.port}</td>
				<td><div id="playerstatus-${player.status}"><span>${player.status}</span></div></td>
			</tr>
		</c:forEach>

		<c:choose>
			<c:when test='${rowClass == "odd"}'>
				<c:set var="rowClass" value="even" />
			</c:when>
			<c:otherwise>
				<c:set var="rowClass" value="odd" />
			</c:otherwise>
		</c:choose>
		<tr class="${rowClass}">
			<td colspan="4">
				<div id="add-new-player"><a href='<%= response.encodeURL("create_player.jsp") %>'><span>Add new player</span></a></div>
			</td>
		</tr>

	</tbody>
</table>

<h1>Tips</h1>
<ul>
	<li>Click on a player name to edit</li>
</ul>



</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
