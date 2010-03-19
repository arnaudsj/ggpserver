<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<c:set var="title">Welcome to the Dresden GGP Server!</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowMatches" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="page"/>
		<jsp:setProperty name="pager" property="status" value="running"/>
	</c:catch>
</jsp:useBean>
<c:if test = "${pager.rowCount > 0}">
	<h1 class="notopborder">Currently playing:</h1>
	<c:set var="show_tournament" value="true" />
	<c:set var="show_goal_values" value="false" />
	<jsp:directive.include file="/inc/match_table.jsp" />
</c:if>

<h1 class="notopborder">What is it all about?</h1>
	The purpose of the server is to help development of <a href="http://www.general-game-playing.de/" target="_blank">General Game Playing</a> systems.
	The server provides an easy way to test general game players on a wide range of games against other players. The idea is that you just register
	your player and leave it online. The server will automatically pit players against each other on all games that are on the server. Of course, you can also 
	start matches manually.

<h1 class="notopborder">How to connect your player</h1>
	We do not host your player. To participate in a match, your player has to run on a computer with a working internet connection.
	After <a href="<%= request.getContextPath() + response.encodeURL("/register/register.jsp") %>">registration</a>/<a href="<%= request.getContextPath() + response.encodeURL("/login/login.jsp") %>">login</a>, 
	you can add your player (you have to enter host name or IP address and the port your player is running on). Your player will automatically be pitted
	against other players if you set its status to "active".

<h1 class="notopborder">Recent changes</h1>
	<ul>
		<li>You can now define arbitrary filters for the matches you want to see. For the filtered matches you can view a chart showing the performance of the players over time.</li>
		<li>Support for GDL with incomplete information (GDL-II/GDL v2) and non-determinism was added. However, you need a GDL-II compliant player to play GDL-II games. &quot;Normal&quot; GDL games and players will still work as before. Please wait for our paper which will explain all the details about GDL-II.</li>
		<li>A few changes under the hood should make the creation of style sheets for the visualization easier.</li>
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

<jsp:directive.include file="/inc/footer.jsp" />
