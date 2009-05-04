<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="createGame" class="tud.ggpserver.formhandlers.CreateGame" scope="request">
	<c:catch>
		<jsp:setProperty name="createGame" property="gameName"/>
		<jsp:setProperty name="createGame" property="gameDescription"/>
		<jsp:setProperty name="createGame" property="stylesheet"/>
		<%-- setting enabled to false is necessary because the enabled property is only send with the request if the checkbox is checked --%>
		<jsp:setProperty name="createGame" property="enabled" value="false"/>
		<jsp:setProperty name="createGame" property="enabled"/>
	</c:catch>
</jsp:useBean>

<%
	response.setHeader("Cache-Control","private");
	response.setHeader("Pragma","no-cache");
%>

<c:choose> 
	<c:when test="${createGame.valid}" > 
		<%
			// add to database
			createGame.create();
		%>
		<c:choose> 
			<c:when test="${createGame.correctlyCreated}" >
				<%
					String urlWithSessionID = response.encodeRedirectURL("../public/view_game.jsp?name=" + createGame.getGameName());
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="create_game.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="create_game.jsp"/>
	</c:otherwise> 
</c:choose>


