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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="editPlayer" class="tud.ggpserver.formhandlers.EditPlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="editPlayer" property="playerName"/>
		<jsp:setProperty name="editPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
		<jsp:setProperty name="editPlayer" property="host"/>
		<jsp:setProperty name="editPlayer" property="port"/>
		<jsp:setProperty name="editPlayer" property="status"/>
		<jsp:setProperty name="editPlayer" property="availableForRoundRobinMatches"/>
		<jsp:setProperty name="editPlayer" property="availableForManualMatches"/>
		<jsp:setProperty name="editPlayer" property="gdlVersion"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>
	
<c:set var="title">Edit Player</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<% 
		// this has to be a java "if" instead of a "<c:if>", because 
		// otherwise the compiler believes that the rest of the page 
		// is unreachable code.
		if (!editPlayer.isValidPlayer()) {
			response.sendError(403, "You are neither an admin nor the owner of this player, so you cannot edit it.");  // 403 forbidden
			return;
		}
	%>

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
				<input type="text" name="host" size="20" value="${editPlayer.host}" maxlength="255"> <br>
				<c:if test="<%= editPlayer.getErrorsHost().size() > 0 %>">
				
					<ul>
				    	<c:forEach var="errormessage" items="${editPlayer.errorsHost}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
				</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Port
			</td>
			<td>
				<input type="text" name="port" size="5" value="${editPlayer.port}" maxlength="5"> <br>
				<c:if test="<%= editPlayer.getErrorsPort().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${editPlayer.errorsPort}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
				</c:if>
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
			<td valign="top" align="right">
				available for round robin play
			</td>
			<td>
				<c:choose>
					<c:when test="${editPlayer.availableForRoundRobinMatches}">
						<input type="checkbox" name="availableForRoundRobinMatches" value="true" checked="checked">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="availableForRoundRobinMatches" value="true">
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				available for manual matches by other users
			</td>
			<td>
				<c:choose>
					<c:when test="${editPlayer.availableForManualMatches}">
						<input type="checkbox" name="availableForManualMatches" value="true" checked="checked">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="availableForManualMatches" value="true">
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				GDL version
			</td>
			<td>
				<select name="gdlVersion" size="1" >
					<c:choose>
						<c:when test="${editPlayer.gdlVersion == 1}">
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
			<td colspan="2" align="center">
				<input type="submit" value="Submit">
				<input type="reset" value="Reset"> 
			</td>
		</tr>
	</table>
	</form>

<h1>Hints</h1>
<ul>
	<jsp:directive.include file="/inc/player_hints.jsp" />
</ul>

<jsp:directive.include file="/inc/footer.jsp" />