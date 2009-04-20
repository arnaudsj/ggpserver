<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<!-- Header -->
<div id="header">
&nbsp;
</div>

<div id="user">
	<c:catch var="exception">
		<c:set var="userName" value="<%= request.getUserPrincipal().getName()%>"></c:set>		
	</c:catch>
	
    <c:choose>
		<c:when test="${exception != null}">
			You are not logged in. You can <a href="<%= request.getContextPath() + response.encodeURL("/members/profile.jsp") %>">login</a>
			or <a href="<%= request.getContextPath() + response.encodeURL("/register/register.jsp") %>">register</a>.
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test='${userName == "admin"}'>
					You are logged in as <b>admin</b>. 
					You can <a href="<%= request.getContextPath() + response.encodeURL("/admin/index.jsp") %>">go to the admin page</a> 
					or <a href="<%= request.getContextPath() + response.encodeURL("/login/logout.jsp") %>">logout</a>.
				</c:when>
				<c:otherwise>
					You are logged in as <b>${userName}</b>. 
					You can <a href="<%= request.getContextPath() + response.encodeURL("/members/profile.jsp") %>">edit your players</a>, 
					<a href="<%= request.getContextPath() + response.encodeURL("/members/create_game.jsp") %>">create a new game</a> 
					or <a href="<%= request.getContextPath() + response.encodeURL("/login/logout.jsp") %>">logout</a>.
				</c:otherwise>
			</c:choose>
		</c:otherwise>
    </c:choose>
</div>
