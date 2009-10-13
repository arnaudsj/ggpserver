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

package tud.ggpserver.datamodel.statistics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import tud.gamecontroller.game.RoleInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.matches.ServerMatch;

import com.keypoint.PngEncoder;

public class GameStatisticsChartCreator {

	private class RunningAverage {
				
		private static final double MIN_LEARNING_RATE = 0.1;
		private double average;
		private long count;
		
		public RunningAverage() {
			this.average = 0;
			this.count = 0;
		}

		public RunningAverage(double initialValue) {
			this.average = initialValue;
			this.count = 1;
		}

		public void addValue(double value) {
			double learningRate = getLearningRate();
			average = average  * (1 - learningRate) + value * learningRate;
			count++;
		}
		
		private double getLearningRate() {
			return Math.max(MIN_LEARNING_RATE, 1.0/(1+count));
		}

		public double getAverage() {
			return average;
		}

	}

	private static final Logger logger = Logger.getLogger(GameStatisticsChartCreator.class.getName());
	private String imageID;
	private String tournamentID;
	private Game<?,?> game;
	private RoleInterface<?> role;
	private HttpSession session;
	private JFreeChart chart = null;
	private ChartRenderingInfo chartRenderingInfo = null;
	private BufferedImage chartImage = null;
	private Map<String, RunningAverage> playerScores = null;

	public GameStatisticsChartCreator(HttpSession session, Game<?,?> game, RoleInterface<?> role) {
		this(session, null, null, game, role);
	}

	public GameStatisticsChartCreator(HttpSession session, String imageID, String tournamentID, Game<?,?> game, RoleInterface<?> role) {
		this.session = session;
		this.tournamentID = tournamentID;
		this.game = game;
		this.role = role;

		if(imageID != null) {
			this.imageID = imageID;
			chart = getCachedChart(imageID);
		}
		if(chart == null) {
			this.imageID = makeImageID();
			chart = getCachedChart(this.imageID);
		}
	}
	
	private String makeImageID() {
		StringBuilder sb = new StringBuilder("img");
		if(tournamentID!=null)
			sb.append("_t:").append(tournamentID);
		if(game!=null)
			sb.append("_g:").append(game.getName());
		if(role!=null)
			sb.append("_r:").append(role.getKIFForm().toLowerCase());
		sb.append("_").append(System.currentTimeMillis() % (1000*60*60)); // the chart is good for one hour
		// TODO: really cache the chart for some period starting at the current time 
		return sb.toString();
	}

	public void makeChart() throws SQLException {
		if (chart == null) {
			AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
			
			// make chart title
			String chartTitle = null;
//			String chartTitle = "Scores";
//			if(role != null)
//				chartTitle = role.getKIFForm().toLowerCase() + " scores";
//			if(game != null)
//				chartTitle += " for " + game.getName();
//			if(tournamentID != null)
//				chartTitle += " in " + tournamentID;

			// make the chart
			List<? extends ServerMatch<?, ?>> matches = db.getMatches(0, Integer.MAX_VALUE, null, game.getName(), tournamentID, true);
			
			Collection<? extends RoleInterface<?>> roles = null;
			if (role != null) {
				roles = Collections.singletonList(role);
			}else if (game!=null) {
				roles = game.getOrderedRoles();
			}
			chart = makeChart(db, matches, chartTitle, roles);
			
			// save chart
			session.setAttribute("gameStatisticsChart_"+imageID, new SoftReference<JFreeChart>(chart));
		}
		
		if(chartImage == null) {
			chartRenderingInfo = new ChartRenderingInfo();  
			chartImage = chart.createBufferedImage(640, 400, chartRenderingInfo);
		}
	}

	public String getImageMapID() throws UnsupportedEncodingException {
		return URLEncoder.encode(imageID, "UTF-8");
	}

	public String getImageMap() throws SQLException, IOException {
		makeChart();
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		
		String htmlEncodedImageID = URLEncoder.encode(imageID, "UTF-8");
		ChartUtilities.writeImageMap(writer, htmlEncodedImageID, chartRenderingInfo, false);
		return stringWriter.toString();
	}

	public void sendImage(HttpServletRequest request, HttpServletResponse response)	throws SQLException, IOException {
		makeChart();
		// set the content type so the browser can see this as a picture
		response.setContentType("image/png");
		// send the picture
		PngEncoder encoder = new PngEncoder(chartImage, false, 0, 9);
		response.getOutputStream().write(encoder.pngEncode());
	}

	@SuppressWarnings("unchecked")
	private JFreeChart getCachedChart(String imageID) {
		JFreeChart chart = null;
		if (imageID != null) {
			SoftReference<JFreeChart> ref = (SoftReference<JFreeChart>)session.getAttribute("gameStatisticsChart_"+imageID);
			if (ref != null)
				chart = ref.get();
				if(chart != null)
					logger.info("using cached chart: " + imageID);

		}
		return chart;
	}

	private JFreeChart makeChart(AbstractDBConnector<?, ?> db, List<? extends ServerMatch<?, ?>> matches, String title, Collection<? extends RoleInterface<?>> roles) throws SQLException {
		// setup chart
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Date", "Score", dataset, true, true, false);
		XYPlot plot = (XYPlot)chart.getPlot();
		((XYLineAndShapeRenderer)plot.getRenderer()).setBaseShapesVisible(true);
		// plot.getDomainAxis().setLabelAngle(45/360*Math.PI); //.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		// plot.getDomainAxis().setMaximumCategoryLabelLines(3);
		// ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);

		Calendar startOfNextPeriod = null, startOfPeriod = null;
		playerScores = new HashMap<String, RunningAverage>();
		Map<String, RunningAverage> oldPlayerScores = new HashMap<String, RunningAverage>();
		
		// fill in data
		
		for(ServerMatch<?, ?> match:matches){
			Calendar matchDate=Calendar.getInstance();
			matchDate.setTime(match.getStartTime());
			if(startOfPeriod == null || matchDate.compareTo(startOfNextPeriod) >= 0) {
				logger.info("new period:" + matchDate);
				if (startOfPeriod != null) {
					addData(dataset, startOfPeriod);
					for(Entry<String, RunningAverage> entry:playerScores.entrySet()) {
						oldPlayerScores.put(entry.getKey(), entry.getValue());
					}
					playerScores.clear();
				}
				startOfPeriod = (Calendar)matchDate.clone();
				// a period is one day -> set time of day to 0
				startOfPeriod.set(Calendar.HOUR, 0);
				startOfPeriod.set(Calendar.MINUTE, 0);
				startOfPeriod.set(Calendar.SECOND, 0);
				startOfPeriod.set(Calendar.MILLISECOND, 0);
				startOfNextPeriod = (Calendar)startOfPeriod.clone();
				startOfNextPeriod.add(Calendar.DAY_OF_YEAR, 1);
			}
			if(match.getGoalValues()!=null){
				logger.info("add goal values of match: " + match.getMatchID());
				Collection<? extends RoleInterface<?>> roles1;
				if(roles == null) {
					roles1 = match.getGame().getOrderedRoles();
					if(roles1 == null) {
						logger.severe("roles of game are null");
					}
				}else{
					roles1 = roles;
				}
				for(RoleInterface<?> role:roles1){
					String playerName = match.getPlayerInfo(role).getName(); 
					RunningAverage runningAverage = playerScores.get(playerName);
					if(runningAverage == null){
						runningAverage = oldPlayerScores.get(playerName);
						if(runningAverage == null) {
							runningAverage = new RunningAverage();
						}
						playerScores.put(playerName, runningAverage);
					}
					runningAverage.addValue(match.getGoalValues().get(role).doubleValue());
				}
			}
		}

		addData(dataset, startOfPeriod);
		
		return chart;
	}

	private void addData(TimeSeriesCollection dataset, Calendar startOfPeriod) {
		logger.info("add point to graph:" + startOfPeriod);
		for(Entry<String, RunningAverage> playerScore:playerScores.entrySet()){
			TimeSeries series = dataset.getSeries(playerScore.getKey());
			if (series == null) {
				series = new TimeSeries(playerScore.getKey());
				dataset.addSeries(series);
			}
			series.add(new Day(startOfPeriod.getTime()), playerScore.getValue().getAverage());
		}
	}

	public String getImageID() {
		return imageID;
	}

}