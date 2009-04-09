<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<jsp:useBean id="pager" class="tud.ggpserver.formhandlers.ShowMatches" scope="page">
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
    <div id="ctitle">Show matches</div>

	<h1 class="notopborder">Showing page ${pager.page} (matches ${pager.startRow + 1} to ${pager.endRow + 1})</h1>
	<table>
		<thead>
			<tr>
				<th>match name</th>
				<th>status</th>
				<th>start clock</th>
				<th>play clock</th>
<!--				<th>start time</th>-->
				<th>players</th>
				<th>goal values</th>
				<th>errors</th>
			</tr>
		</thead>
		<tbody>
	      <c:forEach var="match" items="${pager.matches}" varStatus="lineInfo">
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
					<c:url value="view_match.jsp" var="matchURL">
						<c:param name="matchID" value="${match.matchID}" />
					</c:url>
					<a href='<c:out value="${matchURL}" />'>${match.matchID}</a>
				</td>
				<td>${match.status}</td>
				<td>${match.startclock}</td>
				<td>${match.playclock}</td>
<!--				<td>${match.startTime}</td>-->
				<td>
					<c:forEach var="playerinfo" items="${match.orderedPlayerInfos}">
						<c:url value="view_player.jsp" var="playerURL">
							<c:param name="name" value="${playerinfo.name}" />
						</c:url>
						<a href='<c:out value="${playerURL}" />'>${playerinfo.name}</a>
					</c:forEach>
				</td>
				<td>
					<c:choose>
						<c:when test="${match.orderedGoalValues == null}">
							---
						</c:when>
						<c:otherwise>
							<c:forEach var="goalvalue" items="${match.orderedGoalValues}">
								${goalvalue}
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					<center>
						<c:choose>
							<c:when test="${match.hasErrors}">
								<div class="errors"></div>
							</c:when>
							<c:otherwise>
								<div class="no_errors"></div>
							</c:otherwise>
						</c:choose>
					</center>
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