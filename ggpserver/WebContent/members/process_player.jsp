<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="createPlayer" class="tud.ggpserver.formhandlers.CreatePlayer" scope="request">
	<c:catch>
		<jsp:setProperty name="createPlayer" property="playerName"/>
		<jsp:setProperty name="createPlayer" property="userName" value="<%= request.getUserPrincipal().getName() %>"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:choose> 
	<c:when test="${createPlayer.valid}" > 
		<%
			// add player to database
			createPlayer.createPlayer();
		%>
		<c:choose> 
			<c:when test="${createPlayer.correctlyCreated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("edit_player.jsp?playerName=" + createPlayer.getPlayerName());
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="create_player.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="create_player.jsp"/>
	</c:otherwise> 
</c:choose>


