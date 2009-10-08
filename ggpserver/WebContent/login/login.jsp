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

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 

<c:set var="title">Login</c:set>
<jsp:directive.include file="/inc/header.jsp" />
    
	<c:url var="loginURL" value="j_security_check" />
	<form method="POST" action="${loginURL}">
		<table>
		  <tr>
		    <td align="right">User name:</td>
		    <td><input type="text" name="j_username"></td>
		  </tr>
		  <tr>
		    <td align="right">Password:</td>
		    <td><input type="password" name="j_password"></td>
		  </tr>
		  <tr>
		      <td align="right">&nbsp;</td>
		      <td>
		        <input type="reset" value="Reset">
		        <input type="submit" value="Login">
		      </td>
		     </tr>
		</table>
	</form>

	<p>Note: Cookies must be enabled after this point.</p>

<jsp:directive.include file="/inc/footer.jsp" />