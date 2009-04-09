<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="createPlayer" class="tud.ggpserver.formhandlers.CreatePlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="createPlayer" property="playerName"/>
		<jsp:setProperty name="createPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
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
    <div id="ctitle">Create Player</div>

	<form action="<%= request.getContextPath() + response.encodeURL("/members/process_player.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Player Name
			</td>
			<td>
				<input type="text" name="playerName" size="20" value="${createPlayer.playerName}" maxlength="20"> <br>
				<c:if test="<%= createPlayer.getErrors().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createPlayer.errors}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
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
