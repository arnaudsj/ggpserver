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
<jsp:useBean id="editGame" class="tud.ggpserver.formhandlers.EditGame" scope="request">
	<c:catch>
		<jsp:setProperty name="editGame" property="gameName"/>
		<jsp:setProperty name="editGame" property="gameDescription"/>
		<jsp:setProperty name="editGame" property="stylesheet"/>
		<jsp:setProperty name="editGame" property="enabled"/>
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
    <div id="ctitle">Edit Game</div>

	<form action="<%= request.getContextPath() + response.encodeURL("/admin/process_edit_game.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Game Name
			</td>
			<td>
				<input type="hidden" name="gameName" value="${editGame.gameName}"/>
				<b><c:out value="${editGame.gameName}"/></b>
				<c:if test="<%= editGame.getErrorsGameName().size() > 0 %>">
					<br/>
					<ul>
				    	<c:forEach var="errormessage" items="${editGame.errorsGameName}">
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
				<input type="text" name="stylesheet" size="40" value="${editGame.stylesheet}" maxlength="255"> <br>
				<c:if test="<%= editGame.getErrorsStylesheet().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${editGame.errorsStylesheet}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Enabled (for round-robin scheduler)
			</td>
			<td>
				<c:choose>
					<c:when test="${editGame.enabled}">
						<input type="checkbox" name="enabled" value="true" checked="checked">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="enabled" value="true">
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Game Description
			</td>
			<td>
				<textarea rows="20" cols="80" name="gameDescription"><c:out value="${editGame.gameDescription}"></c:out></textarea><br>
				<c:if test="<%= editGame.getErrorsDescription().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${editGame.errorsDescription}">
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
