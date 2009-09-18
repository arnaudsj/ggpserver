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

package tud.ggpserver.formhandlers;

import ggpratingsystem.Configuration;
import ggpratingsystem.output.CachingOutputBuilder;
import ggpratingsystem.ratingsystems.ConstantLinearRegressionStrategy;
import ggpratingsystem.ratingsystems.RatingException;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.ratingsystem.ChartOutputBuilder;
import tud.ggpserver.ratingsystem.DBMatchSetReader;
import tud.ggpserver.ratingsystem.HtmlOutputBuilder;

public class ViewRatings {
	
	private final Logger logger = Logger.getLogger(AdminPage.class.getName());

	private Configuration configuration = null;

	private String ratingsHtmlTable = null;
	
	private String chartImageMap;
	
	public void computeRatings(HttpSession session) throws SQLException, IOException, RatingException {
		logger.info("create configuration");
		configuration = new Configuration();
		
		// TODO: get previous ratings from database
		// configuration.setPreviousRatings(previousRatings);
		
		// don't change the ratings of random and legal players
		configuration.getPlayerSet().setPlayerConstantRating(AbstractDBConnector.PLAYER_RANDOM);
		configuration.getPlayerSet().setPlayerConstantRating(AbstractDBConnector.PLAYER_LEGAL);
		
		// add rating system 
		logger.info("add rating system");
		configuration.addRatingSystem(new ConstantLinearRegressionStrategy(0.01));
	
		// set match reader 
		logger.info("set match reader");
		configuration.setMatchReader(new DBMatchSetReader(configuration));
	
		// add output builders
		logger.info("add output builders");
		StringWriter writer = new StringWriter();
		configuration.addOutputBuilder(RatingSystemType.CONSTANT_LINEAR_REGRESSION, new CachingOutputBuilder(new HtmlOutputBuilder(configuration, writer)));

		ChartOutputBuilder chartOutputBuilder = new ChartOutputBuilder();
		configuration.addOutputBuilder(RatingSystemType.CONSTANT_LINEAR_REGRESSION, chartOutputBuilder);
		
		// compute
		logger.info("compute");
		configuration.run();
		
		// write results
		logger.info("close output builders");
		configuration.closeOutputBuilders();

		logger.info("write table");
		ratingsHtmlTable = writer.toString();

		JFreeChart chart = chartOutputBuilder.getChart();
		
		ChartRenderingInfo chartRenderingInfo = new ChartRenderingInfo();  
		BufferedImage chartImage = chart.createBufferedImage(640, 400, chartRenderingInfo);

		// putting chart as BufferedImage in session, 
		// thus making it available for the image reading action Action.
		session.setAttribute("chartImage", chartImage);

		writer = new StringWriter();
		ChartUtilities.writeImageMap(new PrintWriter(writer), "chartImageMap", chartRenderingInfo, false);
		chartImageMap = writer.toString();
	}
	
	public String getRatingsHtmlTable() {
		return ratingsHtmlTable;
	}

	public String getChartImageMap() {
		return chartImageMap;
	}

}
