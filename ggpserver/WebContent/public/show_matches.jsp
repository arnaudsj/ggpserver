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
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowMatches" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="playerName"/>
		<jsp:setProperty name="pager" property="gameName"/>
		<jsp:setProperty name="pager" property="tournamentID"/>
		<jsp:setProperty name="pager" property="owner"/>
		<jsp:setProperty name="pager" property="page"/>
	</c:catch>
</jsp:useBean>

<c:set var="title">
	<c:choose>
		<c:when test="${ pager.gameName != null }">
			${pager.gameName} matches
	    </c:when>
	    <c:otherwise>
	    	Matches
	    </c:otherwise>
    </c:choose>
    <c:if test="${ pager.tournamentID != null }">
		of ${pager.tournamentID}
    </c:if>
    <c:if test="${ pager.playerName != null }">
		for ${pager.playerName}
    </c:if>
</c:set>
<jsp:directive.include file="/inc/pager_header.jsp" />

<%-- real content - start --%>
<jsp:directive.include file="/inc/match_table.jsp" />
<%-- real content - end --%>

<jsp:directive.include file="/inc/footer.jsp" />
