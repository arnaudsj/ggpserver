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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
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
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.time.Year;

import tud.gamecontroller.game.RoleInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.matches.ServerMatch;

import com.keypoint.PngEncoder;

public class GameStatisticsChartCreator {

	public static class Pair<T1, T2> {
		private T1 left;
		private T2 right;

		public Pair(T1 a, T2 b) {
			this.left = a;
			this.right = b;
		}
		
		public T1 getLeft() {
			return left;
		}

		public T2 getRight() {
			return right;
		}
	}

	private static class RunningAverage {
				
		private double average;
		private long count;
		private long countSinceLastReset;
		private double minLearingRate;
		
		public RunningAverage() {
			this(0.1); // default minimal learning rate is 0.1
		}

		public RunningAverage(double minLearingRate) {
			this.average = 0;
			this.count = 0;
			this.countSinceLastReset = 0;
			this.minLearingRate = minLearingRate;
		}

		public void addValue(double value) {
			double learningRate = getLearningRate();
			average = average  * (1 - learningRate) + value * learningRate;
			count++;
			countSinceLastReset++;
		}
		
		private double getLearningRate() {
			return Math.max(minLearingRate, 1.0/(1+count));
		}

		public double getAverage() {
			return average;
		}

		public long getCountSinceLastReset() {
			return countSinceLastReset;
		}

		public void resetCount() {
			countSinceLastReset = 0;
		}

		public long getCount() {
			return count;
		}

		public void ageing() {
			countSinceLastReset*=2;
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
	private long minNumberOfMatchesForShowingPlayer;
	private double smoothingFactor;

	public GameStatisticsChartCreator(HttpSession session, Game<?,?> game, RoleInterface<?> role, long minNumberOfMatchesForShowingPlayer) {
		this(session, null, null, game, role, minNumberOfMatchesForShowingPlayer, 0.1);
	}

	public GameStatisticsChartCreator(HttpSession session, String imageID, String tournamentID, Game<?,?> game, RoleInterface<?> role, long minNumberOfMatchesForShowingPlayer, double smoothingFactor) {
		this.session = session;
		this.tournamentID = tournamentID;
		this.game = game;
		this.role = role;
		this.minNumberOfMatchesForShowingPlayer = minNumberOfMatchesForShowingPlayer;
		this.smoothingFactor = smoothingFactor;

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
		if(minNumberOfMatchesForShowingPlayer!=0)
			sb.append("_m:").append(minNumberOfMatchesForShowingPlayer);
		if(smoothingFactor!=0.1)
			sb.append("_sF:").append(smoothingFactor);
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
			String gameName = ( game!=null ? game.getName() : null);
			
			List<? extends ServerMatch<?, ?>> matches = db.getMatches(0, Integer.MAX_VALUE, null, gameName, tournamentID, null, true);
			
			Collection<? extends RoleInterface<?>> roles = null;
			if (role != null) {
				roles = Collections.singletonList(role);
			}else if (game!=null) {
				roles = game.getOrderedRoles();
			}
			chart = makeChart(matches, chartTitle, roles);
			
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

	private JFreeChart makeChart(List<? extends ServerMatch<?, ?>> matches, String title, Collection<? extends RoleInterface<?>> roles) throws SQLException {
		// decide which time period to use
		Class<? extends RegularTimePeriod> timePeriodClass = getTimePeriodClass(matches);

		// setup chart
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, timePeriodClass.getSimpleName(), "Score", dataset, true, true, false);
		XYPlot plot = (XYPlot)chart.getPlot();
		((XYLineAndShapeRenderer)plot.getRenderer()).setBaseShapesVisible(true);
		// plot.getDomainAxis().setLabelAngle(45/360*Math.PI); //.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		// plot.getDomainAxis().setMaximumCategoryLabelLines(3);
		// ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		chart.getPlot().setOutlinePaint(Color.DARK_GRAY);
		((XYPlot)chart.getPlot()).setDomainGridlinePaint(Color.DARK_GRAY);
		((XYPlot)chart.getPlot()).setRangeGridlinePaint(Color.DARK_GRAY);
		RegularTimePeriod currentPeriod = null;
		playerScores = new HashMap<String, RunningAverage>();
		
		// fill in data
		for(ServerMatch<?, ?> match:matches){
			if(isGoodMatch(match)) {
				RegularTimePeriod matchPeriod = RegularTimePeriod.createInstance(timePeriodClass, match.getStartTime(), TimeZone.getDefault());
				if(currentPeriod == null || !matchPeriod.equals(currentPeriod)) {
					// logger.info("new period:" + matchDate);
					if (currentPeriod != null) {
						addData(dataset, currentPeriod, false);
					}
					currentPeriod = matchPeriod;
				}
				// logger.info("add goal values of match: " + match.getMatchID());
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
						runningAverage = new RunningAverage(smoothingFactor);
						playerScores.put(playerName, runningAverage);
					}
					runningAverage.addValue(match.getGoalValues().get(role).doubleValue());
				}
			}
		}

		if( currentPeriod != null) // otherwise there is no data
			addData(dataset, currentPeriod, true);
		
		return chart;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends RegularTimePeriod> getTimePeriodClass(List<? extends ServerMatch<?, ?>> matches) {
		// the maximal number of periods we could get is minNbOfPeriods * max(sizeOfTimePeriod, sizeOfNextSmallerTimePeriod)
		// that means we want to keep the steps between the different periods small such that the number of periods is relatively stable
		if ( matches.size() >= 2 ) {
			long idealNbOfPeriods = 40;
			long timespan = matches.get(matches.size()-1).getStartTime().getTime() - matches.get(0).getStartTime().getTime();
			
			long timePeriods[] = {
					1000L*60*60*24*365,
					1000L*60*60*24*30*4,
					1000L*60*60*24*30,
					1000L*60*60*24*7,
					1000L*60*60*24,
					1000L*60*60,
					1000L*60,
					1000L
				};
			Class[] timePeriodClasses = {
					Year.class,
					Quarter.class,
					Month.class,
					Week.class,
					Day.class,
					Hour.class,
					Minute.class,
					Second.class
					}; 

			double bestDiffToIdealNb = Double.POSITIVE_INFINITY;
			int i;
			for(i=0; i<timePeriods.length; i++) {
				double diffToIdealNb = ((double)timespan)/timePeriods[i] - idealNbOfPeriods;
				if( diffToIdealNb<0 ) { // less then the ideal number
					diffToIdealNb = ((double)diffToIdealNb)*diffToIdealNb/(idealNbOfPeriods/4.0);
				}
				if (diffToIdealNb>bestDiffToIdealNb) {
					break;
				}else{
					bestDiffToIdealNb = diffToIdealNb;
				}
			}
			return timePeriodClasses[i-1];
		} else {
			return Second.class;
		}
	}

	private boolean isGoodMatch(ServerMatch<?, ?> match) {
		if(match.getGoalValues()==null)
			return false;
		// TODO: filtering out matches with illegal moves is a good idea, but we have to do it in the tables in view_game_statitics.jsp as well
//		for(List<GameControllerErrorMessage> errorMessages:match.getErrorMessages()) {
//			for(GameControllerErrorMessage msg:errorMessages){
//				if(GameControllerErrorMessage.ILLEGAL_MOVE.equals(msg.getType())) {
//					return false;
//				}
//			}
//		}
		return true;
	}

	private void addData(TimeSeriesCollection dataset, RegularTimePeriod timePeriod, boolean finalPeriod) {
		// logger.info("add points to graph:" + startOfPeriod);

		// minCountSinceLastReset is the number and age of matches needed to create a new point in the chart
		// - should be 1 <= minCountSinceLastReset <= minNumberOfMatchesForShowingPlayer
		// - is used for additional smoothing of the chart and for decluttering (i.e., to have less data points in the graph)
		// - maximum is 5 at the moment, that means there is a data point if there are at least 5 matches in this period, 1 match 2 periods ago (because of aging),
		// or 2 matches 1 period ago, ... 
		long minCountSinceLastReset = Math.max(Math.min(minNumberOfMatchesForShowingPlayer / 2, 5), 1);
		for(Entry<String, RunningAverage> playerScore:playerScores.entrySet()){
			RunningAverage runningAverage = playerScore.getValue();
			// only show players with at least 1 match in the last period and with at least 10 matches in total
			if( runningAverage.getCount() >= minNumberOfMatchesForShowingPlayer ) {
				if( finalPeriod && runningAverage.getCountSinceLastReset() >= 1 || runningAverage.getCountSinceLastReset() >= minCountSinceLastReset ) {
					TimeSeries series = dataset.getSeries(playerScore.getKey());
					if (series == null) {
						series = new TimeSeries(playerScore.getKey());
						dataset.addSeries(series);
					}
					series.add(timePeriod, runningAverage.getAverage());
					// TODO: change tooltips by adding a CustomXYToolTipGenerator for each series to plot.getRenderer()
					runningAverage.resetCount();
				} else {
					runningAverage.ageing();
				}
			}
		}
	}

	public String getImageID() {
		return imageID;
	}

}