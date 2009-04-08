
<%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragma","no-cache");

	session.invalidate();
	// request.getRequestDispatcher("logout_success.jsp").forward(request,response);
	
	String urlWithSessionID = response.encodeRedirectURL("logout_success.jsp");
	response.sendRedirect(urlWithSessionID);
%>
