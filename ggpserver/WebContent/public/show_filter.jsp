<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2010 Peter Steinke (peter.steinke@inf.tu-dresden.de)
                  2010 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowMatchFilter" scope="page"/>
<%
   if (session.getAttribute("filter") == null) {
      session.setAttribute("filter", pager.getFilter());      
   } else {
      pager.setFilter((tud.ggpserver.filter.Filter)session.getAttribute("filter"));
   }
%>
<%-- it is necessary to set the filter before setting the page number, because computing the number of pages depends on the filter --%>
<c:catch>
	<jsp:setProperty name="pager" property="showMatches"/>
	<jsp:setProperty name="pager" property="page"/>
</c:catch>

<c:set var="title">Match Filter</c:set>
<jsp:directive.include file="/inc/header.jsp" />

<form name="form" action="process_save_filter.jsp" method="post">
  ${pager.filterHtml}
  
  <% // strange bug .. submit name has to start with a number%>
  <input type="submit" name="0submit" onClick="form.submit()" value="Search for matches" />
</form>
 
<c:if test="${pager.showMatches}">
	<jsp:directive.include file="/inc/match_table.jsp" />
</c:if>

<jsp:directive.include file="/inc/footer.jsp" />
