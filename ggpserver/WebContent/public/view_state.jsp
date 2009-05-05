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

<%@ page contentType="application/xhtml+xml" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewState"
	class="tud.ggpserver.formhandlers.ViewState" scope="page">
	<c:catch>
		<jsp:setProperty name="viewState" property="matchID" />
		<jsp:setProperty name="viewState" property="stepNumber" />
	</c:catch>
</jsp:useBean>
<% out.clearBuffer(); // to remove newline characters up to here %>${viewState.xmlState}