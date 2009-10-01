<%--
    Copyright (C) 2009 Martin G�nther (mintar@gmx.de)

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

<%@ page contentType="application/xml" %>
<%-- TODO: we're in a little dilemma here as to which content type to choose.

"application/xhtml+xml"
     Is the correct content type, works with Firefox, doesn't work with IE 6-8.

"application/xml":
	Also correct, should be more or less equivalent to "application/xhtml+xml".
	In theory. Works with Firefox and IE6.
	
"text/html":
     Is incorrect. Works with Internet Explorer 6.0, doesn't work with Firefox.
     
"text/xml":
	This is the EVIL content type (the rules how to determine the correct 
	encoding are completely braindead). Works with IE6, Firefox. --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewState"
	class="tud.ggpserver.formhandlers.ViewState" scope="page">
	<c:catch var="e">
		<jsp:setProperty name="viewState" property="matchID" />
	</c:catch>
	<c:catch>
		<jsp:setProperty name="viewState" property="stepNumber" />
	</c:catch>
</jsp:useBean>
<c:if test="${e != null}">
<% 
	response.sendError(404, "No match with this match ID exists.");
	if (1 + 1 == 2) {  // necessary to convince the compiler that the code below this return is indeed reachable.
		return;
	}
%>
</c:if>
<% out.clearBuffer(); // to remove newline characters up to here %>${viewState.xmlState}