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

import tud.gamecontroller.game.RoleInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.statistics.GameStatisticsChartCreator;

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
			AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();

			// get request parameters
			String imageID = request.getParameter("imageID");
			String tournamentID = request.getParameter("tournamentID");
			String gameName = request.getParameter("gameName");
			String roleIndexString = request.getParameter("roleIndex");
			String minMatchNumberString = request.getParameter("minMatchNumber");
			String smoothingFactorString = request.getParameter("smoothingFactor");
			RoleInterface<?> role = null;
			Game<?,?> game = null;
			int roleIndex = -1;
			long minMatchNumber = 0;
			double smoothingFactor = 0.1;
			if(gameName != null) {
				game = db.getGame(gameName);
				if (game == null) {
					throw new ServletException("no such game: \"" + gameName + "\"");
				}
			}else{
				roleIndexString = null; // ignore the roleIndex if no game is given
			}
			if(roleIndexString != null) {
				try {
					roleIndex = Integer.parseInt(roleIndexString);
				} catch(NumberFormatException e) {
					throw new ServletException("roleIndex must be an integer", e);
				}
				if(roleIndex < 0 || roleIndex >= game.getNumberOfRoles()) {
					throw new ServletException("roleIndex must be between 0 and " + (game.getNumberOfRoles() - 1) + " for game " + gameName);
				}
				role = game.getOrderedRoles().get(roleIndex);
			}
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
			// make chart and send image
			GameStatisticsChartCreator chartCreator = new GameStatisticsChartCreator(request.getSession(), imageID, tournamentID, game, role, minMatchNumber, smoothingFactor);
			chartCreator.sendImage(request, response);
		} catch (SQLException e) {
			e.printStackTrace(response.getWriter());
		}
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