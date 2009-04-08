<%@ page language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<jsp:useBean id="register" class="tud.ggpserver.formhandlers.Register" scope="request">
	<jsp:setProperty name="register" property="*"/>
</jsp:useBean>
<%-- This bean must have an identical name (register) in process.jsp and register.jsp! --%>


<c:choose> 
	<c:when test="${register.valid}" > 
		<%
			// add user to database
			register.createUser();
		%>
		<c:choose> 
			<c:when test="${register.correctlyCreated}" > 
				<%
					response.setHeader("Cache-Control","no-store");
					response.setHeader("Pragma","no-cache");
				
					session.invalidate();
					
					String urlWithSessionID = response.encodeRedirectURL("success.jsp");
					response.sendRedirect(urlWithSessionID);
				%>
			</c:when> 
			<c:otherwise>
				<jsp:forward page="register.jsp"/>
			</c:otherwise> 
		</c:choose>
	</c:when> 
	<c:otherwise>
		<jsp:forward page="register.jsp"/>
	</c:otherwise> 
</c:choose>


