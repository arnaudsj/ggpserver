<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="register" class="tud.ggpserver.formhandlers.Register" scope="request" />
<%-- This bean must have an identical name (register) in process.jsp and register.jsp! --%>

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
    <div id="ctitle">User Registration</div>

	<form action="<%= request.getContextPath() + response.encodeURL("/register/process.jsp") %>" method="post">
	<table cellpadding="4" cellspacing="2" border="0">
		<tr>
			<td valign="top" align="right">
				User Name
			</td>
			<td>
				<input type="text" name="userName" size="20" value="${register.userName}" maxlength="20"> <br>
				<ul>
			    	<c:forEach var="errormessage" items="${register.errorsUserName}">
						<li class="validationerror">${errormessage}</li>
			    	</c:forEach>
	      		</ul>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Password
			</td>
			<td>
				<input type="password" name="password1" size="20" value="${register.password1}" maxlength="20"> <br>
				<ul>
			      <c:forEach var="errormessage" items="${register.errorsPassword1}">
					<li class="validationerror">${errormessage}</li>
			      </c:forEach>
	      		</ul>
			</td>
		</tr>
		<tr>
			<td valign="top" align="right">
				Confirm Password
			</td>
			<td>
				<input type="password" name="password2" size="20" value="${register.password2}" maxlength="20"> <br>
				<ul>
			      <c:forEach var="errormessage" items="${register.errorsPassword2}">
					<li class="validationerror">${errormessage}</li>
			      </c:forEach>
	      		</ul>
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
</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>
