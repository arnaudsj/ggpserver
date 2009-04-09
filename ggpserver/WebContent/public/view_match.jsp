<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="viewMatch"
	class="tud.ggpserver.formhandlers.ViewMatch" scope="page">
	<c:catch>
		<%
		// this is for catching NumberFormatExceptions and the like
		%>
		<jsp:setProperty name="viewMatch" property="matchID" />
	</c:catch>
</jsp:useBean>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:directive.include file="/inc/headincludes.jsp" />
</head>
<body>
<div id="everything"><jsp:directive.include file="/inc/header.jsp" />
<jsp:directive.include file="/inc/navigation.jsp" /> <!-- Content -->
<div id="content">
<div id="ctitle">View match</div>
<c:set var="match" value="${viewMatch.match}" />

<h1 class="notopborder">Information on match ${match.matchID}</h1>
<table>
	<tbody>
		<tr>
			<td><b>match name</b></td>
			<td><c:out value="${match.matchID}"></c:out></td>
		</tr>
		<tr>
			<td><b>game</b></td>
			<td>					
				<c:url value="view_game.jsp" var="gameURL">
					<c:param name="name" value="${match.game.name}" />
				</c:url>
				<a href='<c:out value="${gameURL}" />'>${match.game.name}</a>	
			</td>
		<tr>
			<td><b>status</b></td>
			<td><c:out value="${match.status}"></c:out></td>
		</tr>
		<tr>
			<td><b>start clock</b></td>
			<td><c:out value="${match.startclock}"></c:out></td>
		</tr>
		<tr>
			<td><b>play clock</b></td>
			<td><c:out value="${match.playclock}"></c:out></td>
		</tr>
		<tr>
			<td><b>start time</b></td>
			<td><c:out value="${match.startTime}"></c:out></td>
		</tr>
		<tr>
			<td><b>players</b></td>
			<td><c:forEach var="playerinfo" items="${match.orderedPlayerInfos}">
				<c:url value="view_player.jsp" var="playerURL">
					<c:param name="name" value="${playerinfo.name}" />
				</c:url>
				<a href='<c:out value="${playerURL}" />'>${playerinfo.name}</a>
			</c:forEach></td>
		</tr>
		<tr>
			<td><b>goal values</b></td>
			<td><c:choose>
				<c:when test="${match.orderedGoalValues == null}">
							---
						</c:when>
				<c:otherwise>
					<c:forEach var="goalvalue" items="${match.orderedGoalValues}">
								${goalvalue}
							</c:forEach>
				</c:otherwise>
			</c:choose></td>
		</tr>
	</tbody>
</table>



<h1>Match history</h1>
<table>
	<thead>
		<tr>
			<!--				<th>step number</th>-->
			<th>state</th>
			<th>joint move</th>
			<th>
			<center>errors</center>
			</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="stepNumber" begin="1" end="${match.numberOfStates}"
			varStatus="lineInfo">
			<jsp:setProperty name="viewMatch" property="stepNumber"
				value="${stepNumber}" />
			<c:choose>
				<c:when test="${lineInfo.count % 2 == 0}">
					<c:set var="rowClass" value="even" />
				</c:when>
				<c:otherwise>
					<c:set var="rowClass" value="odd" />
				</c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<!--			<td><c:out value="${stepNumber}"></c:out></td>-->
				<td><c:url value="view_state.jsp" var="stateURL">
					<c:param name="matchID" value="${match.matchID}" />
					<c:param name="stepNumber" value="${stepNumber}" />
				</c:url> <a href='<c:out value="${stateURL}" />'>state ${stepNumber}</a></td>
				<td><c:forEach var="move" items="${viewMatch.moves}">
					<c:out value="${move}"></c:out>&nbsp;
					</c:forEach></td>
				<td>
				<center><c:choose>
					<c:when test="<%= !viewMatch.getErrorMessages().isEmpty() %>">
						<c:url value="view_errors.jsp" var="errorURL">
							<c:param name="matchID" value="${match.matchID}" />
							<c:param name="stepNumber" value="${stepNumber}" />
						</c:url>
						<div id="errors"><a href='${errorURL}'><span>errors</span></a></div>
					</c:when>
					<c:otherwise>
						<div id="no_errors"></div>
					</c:otherwise>
				</c:choose></center>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<h1>Tips</h1>
<ul>
	<li>click on the warning icon to see the detailed error messages
	that happened before or during the execution of the corresponding joint
	move</li>
</ul>

</div>
<!--end div "content"--> <jsp:directive.include file="/inc/footer.jsp" />
</div>
<!-- end div "everything" -->
</body>
</html>
