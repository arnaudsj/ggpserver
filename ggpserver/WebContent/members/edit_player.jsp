<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="editPlayer" class="tud.ggpserver.formhandlers.EditPlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="editPlayer" property="playerName"/>
		<jsp:setProperty name="editPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
		<jsp:setProperty name="editPlayer" property="host"/>
		<jsp:setProperty name="editPlayer" property="port"/>
		<jsp:setProperty name="editPlayer" property="status"/>
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
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
	<% 
		// this has to be a java "if" instead of a "<c:if>", because 
		// otherwise the compiler believes that the rest of the page 
		// is unreachable code.
		if (!editPlayer.isValidPlayer()) {
			response.sendError(403);  // 403 forbidden
			return;
		}
	%>

    <div id="ctitle">Edit Player</div>

	<form action="<%= request.getContextPath() + response.encodeURL("/members/process_edit_player.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Player Name
			</td>
			<td>
				<input type="hidden" name="playerName" value="${editPlayer.playerName}">
				<b><c:out value="${editPlayer.playerName}"></c:out></b>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Host
			</td>
			<td>
				<input type="text" name="host" size="20" value="${editPlayer.host}" maxlength="255"> <br />
				<ul>
			    	<c:forEach var="errormessage" items="${editPlayer.errorsHost}">
						<li class="validationerror">${errormessage}</li>
			    	</c:forEach>
	      		</ul>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Port
			</td>
			<td>
				<input type="text" name="port" size="5" value="${editPlayer.port}" maxlength="5"> <br />
				<ul>
			    	<c:forEach var="errormessage" items="${editPlayer.errorsPort}">
						<li class="validationerror">${errormessage}</li>
			    	</c:forEach>
	      		</ul>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Status
			</td>
			<td>
				<select name="status" size="1" >
					<c:choose>
						<c:when test='${editPlayer.status == "active"}'>
							<option value="active" selected>active</option>
							<option value="inactive">inactive</option>
						</c:when>
						<c:otherwise>
							<option value="active">active</option>
							<option value="inactive" selected>inactive</option>
						</c:otherwise>
					</c:choose>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="submit" value="Submit">
				<input type="reset" value="Reset"> 
			</td>
		</tr>
	</table>
	</form>
</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>
