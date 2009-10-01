<%--
    Copyright (C) 2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="createTournament" class="tud.ggpserver.formhandlers.CreateGermanComp09Tournament" scope="request">
	<c:catch>
		<jsp:setProperty name="createTournament" property="tournamentID"/>
		<jsp:setProperty name="createTournament" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
	</c:catch>
</jsp:useBean>

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
    <div id="ctitle">Create Tournament</div>

	<form action="<%= response.encodeURL("process_create_german_comp09_tournament.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Tournament ID
			</td>
			<td>
				<input type="text" name="tournamentID" size="40" value="${createTournament.tournamentID}" maxlength="40"> <br>
				<c:if test="<%= createTournament.getErrors().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${createTournament.errors}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Finals
			</td>
			<td>
				<input type="checkbox" name="finalRound" value="checked">
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Players
			</td>
			<td>
				<c:forTokens items="1,2,3,4,5,6,7" delims="," var="i">
				    <select name="players" size="1">
						<c:forEach var="playerinfo" items="${createTournament.playerInfos}">
						    <option value="${playerinfo.name}"><c:out value="${playerinfo.name}" /></option>
						</c:forEach>
				    </select><br>
				</c:forTokens>
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
