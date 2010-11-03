<%--
    Copyright (C) 2010 Stephan Schiffel (stephan.schiffel@gmx.de)
                       
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

<c:set var="title">Statistics for the seeding phase of the German GGP Competition 2010</c:set>
<jsp:directive.include file="/inc/header.jsp" />

    <p>Get more information about the competition at the <a href="http://www.ggp-potsdam.de/wiki/gggpc2010">official competition page</a>.</p>
	<table>
		<thead>
			<tr>
				<th>Place</th>
				<th>Player</th>
				<th>Avg1</th>
				<th>Avg2</th>
				<th>Avg3</th>
				<th>#matches played</th>
				<th>#matches played with &gt;=3 errors</th>
				<th>error ratio</th>
			</tr>
		</thead>
		<tbody>
<tr class="odd"><td>1</td><td><a href='view_player.jsp?name=ATAX_test'>ATAX_test</a></td><td>64.6813</td><td>65.2008</td><td>65.0797</td><td>251</td><td>2</td><td>0.0080</td></tr>
<tr class="even"><td>2</td><td><a href='view_player.jsp?name=Fluxplayer_test'>Fluxplayer_test</a></td><td>62.1614</td><td>62.9307</td><td>62.1614</td><td>409</td><td>5</td><td>0.0122</td></tr>
<tr class="odd"><td>3</td><td><a href='view_player.jsp?name=Power-Gamer'>Power-Gamer</a></td><td>60.6079</td><td>62.4037</td><td>61.4173</td><td>278</td><td>8</td><td>0.0288</td></tr>
<tr class="even"><td>4</td><td><a href='view_player.jsp?name=Nexplayer'>Nexplayer</a></td><td>59.4496</td><td>62.2005</td><td>60.6781</td><td>407</td><td>18</td><td>0.0442</td></tr>
<tr class="odd"><td>5</td><td><a href='view_player.jsp?name=tut'>tut</a></td><td>44.1429</td><td>44.9628</td><td>45.5106</td><td>329</td><td>6</td><td>0.0182</td></tr>
<tr class="even"><td>?</td><td><a href='view_player.jsp?name=Der_General'>Der_General</a></td><td>26.3793</td><td>34.7727</td><td>29.1379</td><td>29</td><td>7</td><td>0.2414</td></tr>
<tr class="odd"><td>?</td><td><a href='view_player.jsp?name=Skynet'>Skynet</a></td><td>0.0000</td><td>NULL</td><td>100.0000</td><td>2</td><td>2</td><td>1.0000</td></tr>
<tr class="even"><td>?</td><td><a href='view_player.jsp?name=centurio'>centurio</a></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr class="odd"><td>?</td><td><a href='view_player.jsp?name=ACO'>ACO</a></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
<tr class="even"><td>?</td><td><a href='view_player.jsp?name=LuckyLemming_23'>LuckyLemming_23</a></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
		</tbody>
	</table>
	
	<h2>Legend</h2>
	<dl>
		<dt>Avg1</dt>
		<dd>Average score of all matches, matches with 3 or more errors by the player count zero for the player.</dd>
		<dt>Avg2</dt>
		<dd>Average score of all matches in which the player had less than 3 errors.</dd>
		<dt>Avg3</dt>
		<dd>Average score of all matches.</dd>
	</dl>

<jsp:directive.include file="/inc/footer.jsp" />