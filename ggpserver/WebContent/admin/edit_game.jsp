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
		<jsp:setProperty name="editGame" property="gdlVersion"/>
		<jsp:setProperty name="editGame" property="seesXMLRules"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:set var="title">Edit Game</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<c:url value="process_edit_game.jsp" var="saveChangesURL"/>
		
	<form action="${saveChangesURL}" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				Game Name
			</td>
			<td>
				<input type="hidden" name="gameName" value="${editGame.gameName}"/>
				<b><c:out value="${editGame.gameName}"/></b>
				<c:if test="<%= editGame.getErrorsGameName().size() > 0 %>">
					<br>
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
				Enabled
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
			<td valign="top" align="right">
				GDL version
			</td>
			<td>
				<select name="gdlVersion" size="1" >
					<c:choose>
						<c:when test="${editGame.gdlVersion == 1}">
							<option value="1" selected>Regular GDL (v1)</option>
							<option value="2">GDL-II (v2)</option>
						</c:when>
						<c:otherwise>
							<option value="1">Regular GDL (v1)</option>
							<option value="2" selected>GDL-II (v2)</option>
						</c:otherwise>
					</c:choose>
				</select>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				sees_XML(...) rules
			</td>
			<td>
				<textarea rows="20" cols="80" name="seesXMLRules"><c:out value="${editGame.seesXMLRules}"></c:out></textarea><br>
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

<jsp:directive.include file="/inc/footer.jsp" />