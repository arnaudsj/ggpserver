<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="createGame" class="tud.ggpserver.formhandlers.CreateGame" scope="request">
	<c:catch>
		<jsp:setProperty name="createGame" property="gameName"/>
		<jsp:setProperty name="createGame" property="gameDescription"/>
		<jsp:setProperty name="createGame" property="stylesheet"/>
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
    <div id="ctitle">Create Game</div>

	<form action="<%= request.getContextPath() + response.encodeURL("/members/process_game.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Game Name
			</td>
			<td>
				<input type="text" name="gameName" size="40" value="${createGame.gameName}" maxlength="40"> <br>
				<c:if test="<%= createGame.getErrorsGameName().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createGame.errorsGameName}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Stylesheet
			</td>
			<td>
				<input type="text" name="stylesheet" size="40" value="${createGame.stylesheet}" maxlength="255"> <br>
				<c:if test="<%= createGame.getErrorsStylesheet().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createGame.errorsStylesheet}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Game Description
			</td>
			<td>
				<textarea rows="20" cols="80" name="gameDescription"><c:out value="${createGame.gameDescription}"></c:out></textarea><br>
				<c:if test="<%= createGame.getErrorsDescription().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createGame.errorsDescription}">
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
