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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="register" class="tud.ggpserver.formhandlers.Register" scope="request">
	<jsp:setProperty name="register" property="*"/>
</jsp:useBean>

<c:set var="title">User Registration</c:set>
<jsp:directive.include file="/inc/header.jsp" />

	<form action="<%= request.getContextPath() + response.encodeURL("/register/process.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				User Name
			</td>
			<td>
				<input type="text" name="userName" size="20" value="${register.userName}" maxlength="20"> <br>
				<c:if test="<%= register.getErrorsUserName().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${register.errorsUserName}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Email Address
			</td>
			<td>
				<input type="text" name="emailAddress" size="60" value="${register.emailAddress}" maxlength="320"> <br>
				<c:if test="<%= register.getErrorsEmailAddress().size() > 0 %>">
					<ul>
				    	<c:forEach var="errormessage" items="${register.errorsEmailAddress}">
							<li class="validationerror">${errormessage}</li>
				    	</c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Password
			</td>
			<td>
				<input type="password" name="password1" size="20" value="${register.password1}" maxlength="20"> <br>
				<c:if test="<%= register.getErrorsPassword1().size() > 0 %>">
					<ul>
				      <c:forEach var="errormessage" items="${register.errorsPassword1}">
						<li class="validationerror">${errormessage}</li>
				      </c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Confirm Password
			</td>
			<td>
				<input type="password" name="password2" size="20" value="${register.password2}" maxlength="20"> <br>
				<c:if test="<%= register.getErrorsPassword2().size() > 0 %>">
					<ul>
				      <c:forEach var="errormessage" items="${register.errorsPassword2}">
						<li class="validationerror">${errormessage}</li>
				      </c:forEach>
		      		</ul>
	      		</c:if>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">
				<input type="submit" value="Submit">
				<input type="reset" value="Reset"> 
			</td>
		</tr>
	</table>
	</form>

<jsp:directive.include file="/inc/footer.jsp" />