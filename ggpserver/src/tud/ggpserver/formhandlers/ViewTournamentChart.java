/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

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
import java.util.List;

import javax.servlet.http.HttpSession;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.statistics.GameStatisticsChartCreator;
import tud.ggpserver.datamodel.statistics.TournamentStatistics;

public class ViewTournamentChart {
	
	private HttpSession session;

	private String tournamentID;
	
	private ImageInfo imageInfo = null;
	
	/**
	 * the minimal number of matches that a player must have played for it to be shown in the chart 
	 */
	private long minMatchNumber = 10;
	
	private double smoothingFactor = 0.1;
	
	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) throws SQLException {
		this.tournamentID = tournamentID;
	}

	public void setMinMatchNumber(long minMatchNumber) {
		this.minMatchNumber = minMatchNumber;
	}

	public long getMinMatchNumber() {
		return minMatchNumber;
	}

	public void setSmoothingFactor(double smoothingFactor) {
		this.smoothingFactor = smoothingFactor;
	}

	public double getSmoothingFactor() {
		return smoothingFactor;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public ImageInfo getChart() throws SQLException, UnsupportedEncodingException, IOException {
		if (imageInfo  == null) {
			GameStatisticsChartCreator chartCreator = new GameStatisticsChartCreator(session, null, tournamentID, null, null, minMatchNumber, smoothingFactor);
			imageInfo = new ImageInfo(chartCreator.getImageID(), chartCreator.getImageMap(), chartCreator.getImageMapID());
		}
		return imageInfo;
	}


	public class ImageInfo {
		private String imageID;
		private String imageMap;
		private String imageMapID;
		
		public ImageInfo(String imageID, String imageMap, String imageMapID) {
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
	}

}
