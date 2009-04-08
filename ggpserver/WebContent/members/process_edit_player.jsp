<%@ page language="java" %>
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


<c:choose>
	<c:when test="${!editPlayer.validPlayer}" >
	<% 
		response.reset();
		response.sendError(403);  // 403 forbidden
	%>
	</c:when>
	<c:when test="${editPlayer.valid}" > 
		<%
			// update the player infos in the database
			editPlayer.updatePlayer();
		%>
		<c:choose> 
			<c:when test="${editPlayer.correctlyUpdated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("profile.jsp");
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="edit_player.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="edit_player.jsp"/>
	</c:otherwise> 
</c:choose>
