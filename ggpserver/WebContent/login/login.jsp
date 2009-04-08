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
    <div id="ctitle">Login</div>

	<form method="POST" action="j_security_check">
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

</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>

