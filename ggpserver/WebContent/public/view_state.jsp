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