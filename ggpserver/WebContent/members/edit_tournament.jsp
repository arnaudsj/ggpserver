<%--
    Copyright (C) 2009 Martin Günther (mintar@gmx.de)
                  2009 Stephan Schiffel (stephan.schiffel@gmx.de)

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

<jsp:useBean id="pager"
	     class="tud.ggpserver.formhandlers.EditTournament" scope="page">
    <c:catch>
	<% // this is for catching NumberFormatExceptions and the like  %>
	<jsp:setProperty name="pager" property="playerName"/>
	<jsp:setProperty name="pager" property="gameName"/>
	<jsp:setProperty name="pager" property="tournamentID"/>
	<jsp:setProperty name="pager" property="owner"/>
	<% // we set the userName after the owner, such that the owner gets overwritten by the userName for non-admin users %>
	<jsp:setProperty name="pager" property="userName" value="<%= request.getUserPrincipal().getName()%>" />
	<jsp:setProperty name="pager" property="page" />
    </c:catch>
</jsp:useBean>
<%@page import="tud.ggpserver.formhandlers.EditTournament"%>

<c:set var="title">Edit Tournament ${pager.tournament.tournamentID}</c:set>

<c:choose>
	<c:when test="${pager.allow}">
		<c:set var="omitNavigation">true</c:set>
	
	    <%-- <c:set var="additionalHeadContent"> --%>
	    <script type="text/javascript" language="JavaScript">
	    
		    function confirm_delete(matchid, url) {
			    var result = confirm("Do you really want to delete the finished or running match " + matchid + "? The player's rewards from that match will be removed from the statistics!");
			    if (result == true) {
				    window.location=url;
			    }
		    }
	    
	    </script>
	    <%--  </c:set>  --%>
	    <jsp:directive.include file="/inc/pager_header.jsp" />
	
	    <c:if test='${navigationUserBean.user != null}'>
			<a href="<%= request.getContextPath() + response.encodeURL("/members/profile.jsp") %>">&lt;&lt;&lt; back to profile page</a>
	    </c:if>
	
	    <br>
	
	    <jsp:directive.include file="/inc/pager.jsp" />
	
	    <c:url value="process_save_tournament.jsp" var="saveChangesURLWithNewContent">
			<c:param name="tournamentID" value="${pager.tournamentID}"/>
			<c:param name="page" value="${pager.page}" />
			<c:param name="newContent" value="true" />
	    </c:url>

	    <c:url value="process_save_tournament.jsp" var="saveChangesURLWithNoNewContent">
			<c:param name="tournamentID" value="${pager.tournamentID}"/>
			<c:param name="page" value="${pager.page}" />
			<c:param name="newContent" value="false" />
	    </c:url>

	    <form name="theForm" action="" method="post">
		
		<table>
		    <thead>
			<tr>
			    <th>match/game</th>
			    <th>status</th>
			    <th>start clock</th>
			    <th>play clock</th>
			    <th>players</th>
			    <th>scrambled</th>
			    <th>goal values</th>
			    <c:if test="${pager.goalEditable}">
			    	<th>weight</th>
			    </c:if>
			    <th>errors</th>
			    <th colspan="4">actions</th>
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
	
				<!-- match/game -->
				<td>
				    <c:choose>
					<c:when test="${match.status == 'new'}">
					    <select name="gameName+${match.matchID}" size="1" onChange="theForm.action='${saveChangesURLWithNewContent}'; theForm.submit();" style="max-width:120px;">
						<c:forEach var="game" items="${pager.games}">
							<c:choose>
								<c:when test='${game.name == match.game.name}'>
									<option value="${game.name}" selected="selected" ><c:out value="${game.name}" /></option>
								</c:when>
								<c:otherwise>
									<option value="${game.name}"><c:out value="${game.name}" /></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					    </select>							
					</c:when>
					<c:otherwise>
					    <c:out value="${match.matchID}" />
					</c:otherwise>
				    </c:choose>
				</td>
				
				<!-- status -->
				<td><c:out value="${match.status}" /></td>
				
				<!-- start clock -->
				<td>
				    <c:choose>
					<c:when test="${match.status == 'new'}">
					    <input type="text" name="startclock+${match.matchID}" size="3" value="${match.startclock}" maxlength="4" style="text-align: right;" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();">
					</c:when>
					<c:otherwise><c:out value="${match.startclock}" /></c:otherwise>
				    </c:choose>
				</td>
				
				<!-- play clock -->
				<td>
				    <c:choose>
					<c:when test="${match.status == 'new'}">
						<input type="text" name="playclock+${match.matchID}" size="3" value="${match.playclock}" maxlength="4" style="text-align: right;" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();">
					</c:when>
					<c:otherwise><c:out value="${match.playclock}" /></c:otherwise>
				    </c:choose>
				</td>
				
				<!-- players -->
				<td>
				    <c:forEach var="selectedplayerinfo" items="${match.orderedPlayerInfos}">
					<c:choose>
					    <c:when test="${match.status == 'new'}">
							<select name="playerInfos+${match.matchID}" size="1" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();" style="max-width:120px;">
							    <c:forEach var="playerinfo" items="${pager.playerInfos}">
									<c:choose>
									    <c:when test='${playerinfo.name == selectedplayerinfo.name}'>
									    	<c:set var="playerSelected">selected="selected"</c:set>
									    </c:when>
									    <c:otherwise>
									    	<c:set var="playerSelected"/>
									    </c:otherwise>
									</c:choose>
									<c:set var="playerStyle"/>
									<c:if test="${!playerinfo.usable}">
										<c:set var="playerStyle">color:#D3D3D3;${playerStyle}</c:set>
									</c:if>
									<c:if test="${playerinfo.owner == navigationUserBean.user}">
										<c:set var="playerStyle">font-weight:bold;${playerStyle}</c:set>
									</c:if>
									<option value="${playerinfo.name}" ${playerSelected} style="${playerStyle}">
										<c:out value="${playerinfo.name}" />
									</option>
							    </c:forEach>
							</select>
					    </c:when>
					    <c:otherwise>
							<c:url value="../public/view_player.jsp" var="playerURL">
							    <c:param name="name" value="${selectedplayerinfo.name}" />
							</c:url>
							<a href='<c:out value="${playerURL}" />'><c:out value="${selectedplayerinfo.name}" /></a>
							<br>
					    </c:otherwise>
					</c:choose>
				    </c:forEach>
				</td>
				
				<!-- scrambled -->
				<td>
				    <c:if test="${!(match.status == 'new')}"><c:set var="scrambling_disabled" value="disabled" /></c:if>
				    <c:if test="${match.scrambled}"><c:set var="scrambling_checked" value="checked" /></c:if>
				    <input type="checkbox" name="scrambled+${match.matchID}" value="checked" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();" ${scrambling_disabled} ${scrambling_checked}>
				    
				    <%-- clean variables up for next iteration --%>
				    <c:set var="scrambling_disabled" value="" />
				    <c:set var="scrambling_checked" value="" />
				</td>
				
				<!-- goal values -->
				<td style="text-align:right;">
					<c:choose>
						<c:when test="${match.goalValues == null}">
						    ---
						</c:when>
						<c:otherwise>
						    <c:forEach var="roleindex" begin="0" end="${match.game.numberOfRoles - 1}">
						    	<c:choose>
							    	<c:when test="${pager.goalEditable}">
						    			<input type="text" name="goalvalue+${match.matchID}" size="3" value="${match.orderedGoalValues[roleindex]}" maxlength="4" style="text-align: right;" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();">
						    		</c:when>
						    		<c:otherwise>
						    			<input type="hidden" name="goalvalue+${match.matchID}" value="${match.orderedGoalValues[roleindex]}">
						    		</c:otherwise>
						    	</c:choose>
								<br>
						    </c:forEach>
						</c:otherwise>
					</c:choose>
				</td>
	
				<!-- weight -->
			    <c:if test="${pager.goalEditable}">
					<td>
					    <input type="text" name="weight+${match.matchID}" size="3" value="${match.weight}" maxlength="10" style="text-align: right;" onChange="theForm.action='${saveChangesURLWithNoNewContent}'; theForm.submit();">
				    </td>
				</c:if>
				
				<!-- errors -->
				<td>
				    <center><c:choose>
					    <c:when test="${match.hasErrors}">
							<c:url value="../public/view_errors.jsp" var="errorURL">
							    <c:param name="matchID" value="${match.matchID}" />
							</c:url>
							<div class="errors"><a href='<c:out value="${errorURL}" />'><span>errors</span></a></div>
					    </c:when>
					    <c:otherwise>
							<div class="no_errors"></div>
					    </c:otherwise>
				    </c:choose></center>
				</td>
	
				<!-- actions -->
				
				<%-- action "view" [all] --%>
				<td class="nopadding">
					<c:url value="../public/view_match.jsp" var="viewURL">
						<c:param name="matchID" value="${match.matchID}" />
				    </c:url>
				    <a href='<c:out value="${viewURL}" />'><div class="view" title="view match"><span>view</span></div></a>
				</td>
				
				<%-- action "start" [only NEW] --%>
				<td class="nopadding"><c:choose>
					<c:when test="${ match.status == 'new' }">
					    <c:url value="process_edit_tournament.jsp" var="startURL">
							<c:param name="tournamentID" value="${pager.tournamentID}"/>
							<c:param name="action" value="<%= EditTournament.START_MATCH %>"/>
							<c:param name="matchID" value="${match.matchID}" />
							<c:param name="page" value="${pager.page}" />
					    </c:url>
					    <a href='<c:out value="${startURL}" />'><div class="start" title="start match"><span>start</span></div></a>
					</c:when>
					<c:when test="${ match.status == 'running' || match.status == 'scheduled' }">
					    <c:url value="process_edit_tournament.jsp" var="abortURL">
							<c:param name="tournamentID" value="${pager.tournamentID}"/>
							<c:param name="action" value="<%= EditTournament.ABORT_MATCH %>"/>
							<c:param name="matchID" value="${match.matchID}" />
							<c:param name="page" value="${pager.page}" />
					    </c:url>
					    <a href='<c:out value="${abortURL}" />'><div class="abort" title="abort match"><span>abort</span></div></a>
					</c:when>
					<c:otherwise>
						    <div class="start-bw"></div>
					</c:otherwise>
				</c:choose></td>
				
				<%-- action "delete" [all, but warn on finished/running] --%>
				<td class="nopadding">
				    <c:url value="process_edit_tournament.jsp" var="deleteURL">
						<c:param name="tournamentID" value="${pager.tournamentID}"/>
						<c:param name="action" value="<%= EditTournament.DELETE_MATCH %>"/>
						<c:param name="matchID" value="${match.matchID}" />
						<c:param name="page" value="${pager.page}" />
				    </c:url>
					    
				    <c:choose>
						<c:when test="${ match.status == 'finished' || match.status == 'running' }">
							    <c:set var="realDeleteURL" value="javascript:confirm_delete('${match.matchID}', '${deleteURL}')"></c:set>							
						</c:when>
						<c:otherwise>
							    <c:set var="realDeleteURL" value="${deleteURL}"></c:set>
						</c:otherwise>
				    </c:choose>
					    
				    <a href='<c:out value="${realDeleteURL}" />'><div class="delete" title="delete match"><span>delete</span></div></a>
				</td>
				
				<%-- action "clone" [all] --%>
				<td class="nopadding">
				    <c:url value="process_edit_tournament.jsp" var="cloneURL">
						<c:param name="tournamentID" value="${pager.tournamentID}"/>
						<c:param name="action" value="<%= EditTournament.CLONE_MATCH %>"/>
						<c:param name="matchID" value="${match.matchID}" />
						<c:param name="page" value="${pager.page}" />
				    </c:url>
				    <a href='<c:out value="${cloneURL}" />'><div class="clone" title="clone match"><span>clone</span></div></a>
				</td>
			    </tr>
			</c:forEach>
			
			<%-- "add new match" --%>
			<c:choose>
			    <c:when test='${rowClass == "odd"}'>
					<c:set var="rowClass" value="even" />
			    </c:when>
			    <c:otherwise>
					<c:set var="rowClass" value="odd" />
			    </c:otherwise>
			</c:choose>
			<tr class="${rowClass}">
				<c:choose>
					<c:when test="${pager.goalEditable}">
					    <td colspan="13">
					</c:when>
					<c:otherwise>
						<td colspan="12">
					</c:otherwise>
				</c:choose>
					<c:url value="process_edit_tournament.jsp" var="addMatchURL">
					    <c:param name="tournamentID" value="${pager.tournamentID}"/>
					    <c:param name="action" value="<%= EditTournament.ADD_MATCH %>"/>
					    <c:param name="page" value="${pager.page}" />
					</c:url>
					<a href='<c:out value="${addMatchURL}" />'>Add new match</a>
			    </td>		
			</tr>		
		    </tbody>
		</table>
	    </form>
			    
	    <jsp:directive.include file="/inc/pager.jsp" />
	
	    <c:if test="${pager.playerName != null}">
		    <h1>Legend</h1>
		    <div class="errors"></div> &ndash; some players produced errors, including player ${pager.playerName} <br>
		    <div class="errors_bw"></div> &ndash; some other players produced errors
	    </c:if>
			    
	    <h1>Notes</h1>
	    <ul>
			<li>New matches won't show up on the publicly visible 
			    public/show_matches.jsp page; only running, aborted and 
				finished matches do.</li>
			<li>Players shown in gray in the selection box are not currently available (to you). You will not be able to start the match if you select such a player.</li>
	    </ul>
    </c:when>

	<c:otherwise>
			<h1 class="notopborder">Access forbidden</h1>
	</c:otherwise> 
</c:choose>

<jsp:directive.include file="/inc/footer.jsp" />
