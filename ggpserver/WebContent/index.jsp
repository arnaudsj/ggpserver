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
	
	<h1 class="notopborder">Recent changes</h1>
		<ul>
			<li>The server now supports manual creation of tournaments. For now, this functionality is limited to admins in order to avoid the permission complexities of who may schedule what player for which tournament.</li>
			<li>The show_matches.jsp overview page now goes automatically to the last page.</li>
		</ul>

	<h1 class="notopborder">Sourceforge project</h1>
		The server is open source. Join the <a href="http://ggpserver.sourceforge.net/">GGP Server SourceForge project</a> if you want to help with the development.
	
	<%
		String ua = request.getHeader("User-Agent");
	//	boolean isFirefox = (ua != null && ua.indexOf("Firefox/") != -1);
		boolean isMSIE = (ua != null && ua.indexOf("MSIE") != -1);
		response.setHeader("Vary", "User-Agent");
	%>
	<% if (isMSIE) { %>
	<hr>
	<table border="0" cellspacing="5" cellpadding="0" bgcolor="#EEEEEE">
		<tr>
			<td><a href="http://www.mozilla.com/?from=sfx&uid=0&t=306"><img
				border="0" alt="Get Firefox!" title="Get Firefox!"
				src="gfx/get-firefox-180-60.gif" width="180" height="60"></a></td>
			<td valign="middle"><b>Unfortunately, some parts of this site don't look 
			too good in Internet Explorer. We're sorry.</b></td>
		</tr>
	</table>
	<% } %>
</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>