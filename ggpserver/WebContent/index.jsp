<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything">
<jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" />

<!-- Content -->
<div id="content">
    <div id="ctitle">Start Page</div>

	<h1 class="notopborder">Welcome to the Dresden GGP Server!</h1>
 		You can get information about 
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_matches.jsp") %>">past or running matches</a>,
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_games.jsp") %>">all available games</a>,
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_players.jsp") %>">the general game playing programs</a> and the
		<a href="<%= request.getContextPath() + response.encodeURL("/public/show_users.jsp") %>">registered users</a> 
		by clicking on the links on the left. <br>
		
		After <a href="<%= request.getContextPath() + response.encodeURL("/register/register.jsp") %>">registration</a>, 
		you can add your own general game player to be pitted against the existing ones.


</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>