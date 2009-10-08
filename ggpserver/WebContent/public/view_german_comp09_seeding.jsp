<%--
    Copyright (C) 2009 Stephan Schiffel (stephan.schiffel@gmx.de)
                       
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

<c:set var="title">Statistics for the seeding phase of the German GGP Competition 2009</c:set>
<jsp:directive.include file="/inc/header.jsp" />

    <p>Get more information about the competition at the <a href="http://www.ggp-potsdam.de/wiki/GGGPC">official competition page</a>.</p>
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
		     <tr class="odd">
				<td>1</td>
				<td><a href='view_player.jsp?name=Fluxplayer_test'>Fluxplayer_test</a></td>
				<td>57.1289</td>
				<td>58.9784</td>
				<td>57.5993</td>
				<td>287</td>
				<td>9</td>
				<td>0.0314</td>
			</tr>
		     <tr class="even">
				<td>2</td>
				<td>
					<a href='view_player.jsp?name=Nexplayer'>Nexplayer</a>
				</td>
				<td>50.4808</td>
				<td>59.377</td>
				<td>54.23</td>
				<td>287</td>
				<td>43</td>
				<td>0.1498</td>
			</tr>
		     <tr class="odd">
				<td>3</td>
				<td>
					<a href='view_player.jsp?name=Gamer'>Gamer</a>
				</td>
				<td>46.6714</td>
				<td>58.8649</td>
				<td>55.85</td>
				<td>140</td>
				<td>29</td>
				<td>0.2071</td>
			</tr>
		     <tr class="even">
				<td>4</td>
				<td>
					<a href='view_player.jsp?name=Codepfuscher'>Codepfuscher</a>
				</td>
				<td>45.25</td>
				<td>53.2353</td>
				<td>48.9167</td>
				<td>120</td>
				<td>18</td>
				<td>0.15</td>
			</tr>
		     <tr class="odd">
				<td>5</td>
				<td>
					<a href='view_player.jsp?name=Bremer-Gamer'>Bremer-Gamer</a>
				</td>
				<td>33.1776</td>
				<td>65.7407</td>
				<td>53.9907</td>
				<td>107</td>
				<td>53</td>
				<td>0.4953</td>
			</tr>
		     <tr class="even">
				<td>6</td>
				<td>
					<a href='view_player.jsp?name=LuckyLemming_23'>LuckyLemming_23</a>
				</td>
				<td>18.2953</td>
				<td>40.0603</td>
				<td>32.1693</td>
				<td>254</td>
				<td>138</td>
				<td>0.5433</td>
			</tr>
		     <tr class="odd">
				<td>?</td>
				<td>
					<a href='view_player.jsp?name=centurio'>centurio</a>
				</td>
				<td>54.0</td>
				<td>59.2941</td>
				<td>58.9107</td>
				<td>56</td>
				<td>5</td>
				<td>0.0893</td>
			</tr>
		     <tr class="even">
				<td>?</td>
				<td>
					<a href='view_player.jsp?name=SGPlayer'>SGPlayer</a>
				</td>
				<td>40.2778</td>
				<td>45.3125</td>
				<td>45.8333</td>
				<td>36</td>
				<td>4</td>
				<td>0.1111</td>
			</tr>
		     <tr class="odd">
				<td>?</td>
				<td>
					<a href='view_player.jsp?name=Goaltreeplayer'>Goaltreeplayer</a>
				</td>
				<td>25.961</td>
				<td>37.717</td>
				<td>31.8052</td>
				<td>77</td>
				<td>24</td>
				<td>0.3117</td>
			</tr>
		     <tr class="even">
				<td>?</td>
				<td>
					<a href='view_player.jsp?name=Cobalt'>Cobalt</a>
				</td>
				<td>0.0</td>
				<td>0.0</td>
				<td>10.0</td>
				<td>1</td>
				<td>1</td>
				<td>1.0</td>
			</tr>
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