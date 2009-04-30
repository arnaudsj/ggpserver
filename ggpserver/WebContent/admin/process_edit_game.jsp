<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="editGame" class="tud.ggpserver.formhandlers.EditGame" scope="request">
	<c:catch>
		<jsp:setProperty name="editGame" property="gameName"/>
		<jsp:setProperty name="editGame" property="gameDescription"/>
		<jsp:setProperty name="editGame" property="stylesheet"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:choose> 
	<c:when test="${editGame.valid}" > 
		<%
			// update game in database
			editGame.updateGame();
		%>
		<c:choose> 
			<c:when test="${editGame.correctlyUpdated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("../public/view_game.jsp?name=" + editGame.getGameName());
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="edit_game.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="edit_game.jsp"/>
	</c:otherwise> 
</c:choose>


