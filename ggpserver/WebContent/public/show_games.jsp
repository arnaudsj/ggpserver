<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowGames" scope="page">
	<c:catch> <% // this is for catching NumberFormatExceptions and the like %>
		<jsp:setProperty name="pager" property="page"/>
	</c:catch>
</jsp:useBean>

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
    <div id="ctitle">Show games</div>

	<!-- pager -->
	<jsp:directive.include file="/inc/pager_title.jsp" />
	<jsp:directive.include file="/inc/pager.jsp" />

	<table>
		<thead>
			<tr>
				<th>name</th>
				<th>number of players</th>
				<th>stylesheet</th>
				<th>enabled</th>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="game" items="${pager.games}" varStatus="lineInfo">
	      	 <c:choose>
			   <c:when test="${lineInfo.count % 2 == 0}">
			     <c:set var="rowClass" value="even" />
			   </c:when> 
			   <c:otherwise>
			     <c:set var="rowClass" value="odd" />
			   </c:otherwise>
			 </c:choose> 
		     <tr class="${rowClass}"> 
				<td>
					<c:url value="view_game.jsp" var="gameURL">
						<c:param name="name" value="${game.name}" />
					</c:url>
					<a href='<c:out value="${gameURL}" />'>${game.name}</a>				
				</td>
				<td><c:out value="${game.numberOfRoles}"></c:out></td>
				<td><c:out value="${game.stylesheet}"></c:out></td>
				<td>
					<c:choose>
						<c:when test="${game.enabled}">
							<input type="checkbox" name="enabled" checked disabled>
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="enabled" disabled>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
	      </c:forEach>
		</tbody>
	</table>
	
	<!-- pager -->
	<jsp:directive.include file="/inc/pager.jsp" />
</div>  <!--end div "content"-->

<jsp:directive.include file="/inc/footer.jsp" />
</div>  <!-- end div "everything" -->
</body>
</html>