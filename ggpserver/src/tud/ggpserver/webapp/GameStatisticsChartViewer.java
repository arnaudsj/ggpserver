/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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
*/

package tud.ggpserver.webapp;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.datamodel.statistics.StatisticsChartCreator;
import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.FilterANDOperation;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.filter.FilterSet;
import tud.ggpserver.filter.rules.GameFilterRule;
import tud.ggpserver.filter.rules.StatusFilterRule;
import tud.ggpserver.filter.rules.TournamentFilterRule;

public class GameStatisticsChartViewer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3098950635876480441L;
	
	public void init() throws ServletException {
	}

	//Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {

		try {
			// set default values
			int roleIndex = -1;
			long minMatchNumber = 0;
			double smoothingFactor = 0.1;
			Filter filter = null;

			// get request parameters
			String imageID = request.getParameter("imageID");
			String filterIdString = request.getParameter("filterID");
			String tournamentID = request.getParameter("tournamentID");
			String gameName = request.getParameter("gameName");
			String roleIndexString = request.getParameter("roleIndex");
			String minMatchNumberString = request.getParameter("minMatchNumber");
			String smoothingFactorString = request.getParameter("smoothingFactor");
			
			if(minMatchNumberString != null) {
				try {
					minMatchNumber = Long.parseLong(minMatchNumberString);
				} catch(NumberFormatException e) {
					throw new ServletException("minMatchNumber must be an integer", e);
				}
				if(minMatchNumber <= 0) {
					throw new ServletException("minMatchNumber must be positive");
				}
			}
			if(smoothingFactorString != null) {
				try {
					smoothingFactor = Double.parseDouble(smoothingFactorString);
				} catch(NumberFormatException e) {
					throw new ServletException("smoothingFactor must be an float", e);
				}
				if(smoothingFactor <= 0 || smoothingFactor>1) {
					throw new ServletException("smoothingFactor must be >0 and <=1");
				}
			}
			if(roleIndexString != null) {
				try {
					roleIndex = Integer.parseInt(roleIndexString);
				} catch(NumberFormatException e) {
					throw new ServletException("roleIndex must be an integer", e);
				}
			}
			if(filterIdString != null) {
				HttpSession session = request.getSession();
				FilterSet filterSet = (FilterSet)session.getAttribute("filterset");
				if (filterSet != null) {
					try {
						long filterId = Integer.parseInt(filterIdString);
						filter = filterSet.getFilter(filterId);
					} catch(NumberFormatException e) {
						throw new ServletException("filterID must be an integer", e);
					}
				}
			}
			if (filter == null) {
				// if a valid filter is given gameName and tournamentID are ignored
				filter = createFilter(tournamentID, gameName);
			}
			
			// make chart and send image
			StatisticsChartCreator chartCreator = new StatisticsChartCreator(request.getSession(), imageID, filter, roleIndex, minMatchNumber, smoothingFactor);
			chartCreator.sendImage(request, response);
		} catch (SQLException e) {
			e.printStackTrace(response.getWriter());
		}
	}

	public static Filter createFilter(String tournamentID, String gameName) {
		Filter filter = new Filter("no name");
		FilterANDOperation filterAnd = new FilterANDOperation(filter);
		filter.addSuccessor(filterAnd);
		if(tournamentID != null){
			FilterNode filterNode = new TournamentFilterRule(filter, true, tournamentID);
			filterAnd.addSuccessor(filterNode);
		}
		if(gameName != null) {
			FilterNode filterNode = new GameFilterRule(filter, true, gameName);
			filterAnd.addSuccessor(filterNode);
		}
		filterAnd.addSuccessor(new StatusFilterRule(filter, ServerMatch.STATUS_FINISHED));
		return filter;
	}


	//Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	//Process the HTTP Put request
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	//Clean up resources
	public void destroy() {
	}

}