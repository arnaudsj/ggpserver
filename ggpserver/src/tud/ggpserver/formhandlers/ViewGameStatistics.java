/*
    Copyright (C) 2009-2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.ggpserver.formhandlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import tud.gamecontroller.game.RoleInterface;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.statistics.GamePlayerStatistics;
import tud.ggpserver.datamodel.statistics.GameRoleStatistics;
import tud.ggpserver.datamodel.statistics.GameStatistics;
import tud.ggpserver.datamodel.statistics.StatisticsChartCreator;
import tud.ggpserver.filter.Filter;
import tud.ggpserver.webapp.GameStatisticsChartViewer;

public class ViewGameStatistics {
	private String gameName = "";
	private Game<?, ?> game = null;
	private List<ImageInfo> charts;
	private HttpSession session;
	private GameStatistics<?, ?> gameStatistics = null;
	
	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Game<?, ?> getGame() throws SQLException {
		if(game == null){
			game = DBConnectorFactory.getDBConnector().getGame(gameName);
		}
		return game;
	}

	@SuppressWarnings("unchecked")
	public GameStatistics getStatistics() throws SQLException {
		if(gameStatistics == null){
			gameStatistics = DBConnectorFactory.getDBConnector().getGameStatistics(gameName);
		}
		return gameStatistics;
	}

	public GameRoleStatistics<?, ?> getRoleStatistics() throws SQLException {
		return getStatistics().getGameRoleStatistics();
	}

	public GamePlayerStatistics<?, ?> getPlayerStatistics() throws SQLException {
		return getStatistics().getGamePlayerStatistics();
	}

	@SuppressWarnings("unchecked")
	public Map<RoleInterface<?>, GamePlayerStatistics<?, ?>> getPlayerStatisticsPerRole() throws SQLException {
		return getStatistics().getGamePlayerStatisticsPerRole();
	}

	public List<ImageInfo> getChartsForRoles() throws SQLException, UnsupportedEncodingException, IOException{
		if(charts == null) {
			charts = new ArrayList<ImageInfo>(game.getNumberOfRoles());
			for(int roleIndex=0; roleIndex<game.getNumberOfRoles(); roleIndex++) {
				charts.add(getChart(roleIndex));
			}
		}
		return charts;
	}
	
	public void setSession(HttpSession session) {
		this.session = session;
	}
	
	private ImageInfo getChart(int roleIndex) throws SQLException, UnsupportedEncodingException, IOException {
		Filter filter = GameStatisticsChartViewer.createFilter(null, gameName);
		StatisticsChartCreator chartCreator = new StatisticsChartCreator(session, null, filter, roleIndex, 10, 0.1);
		return new ImageInfo(chartCreator.getImageID(), chartCreator.getImageMap(), chartCreator.getImageMapID(), roleIndex);
	}

	public class ImageInfo {
		private String imageID;
		private String imageMap;
		private String imageMapID;
		private int roleIndex;
		
		public ImageInfo(String imageID, String imageMap, String imageMapID, int roleIndex) {
			this.imageID = imageID;
			this.imageMap = imageMap;
			this.imageMapID = imageMapID; 
		}

		public String getImageID() {
			return imageID;
		}

		public String getImageMap() {
			return imageMap;
		}
		
		public String getImageMapID() {
			return imageMapID;
		}

		public int getRoleIndex() {
			return roleIndex;
		}
	}

}
