<%--
    Copyright (C) 2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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
<%@ page language="java" contentType="text/plain; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="tud.ggpserver.formhandlers.ViewGame" %>
<%
	ViewGame viewGame = new ViewGame();
	viewGame.setName(request.getParameter("name"));

	String gameDescription = viewGame.getGame().getGameDescription();

	//set response headers
	response.setContentType("text/plain");
	response.addHeader("Content-Disposition", "attachment; filename=\""+viewGame.getName()+".gdl\";");
	response.setContentLength(gameDescription.length());
	response.getWriter().print(gameDescription);
	response.flushBuffer();
%>