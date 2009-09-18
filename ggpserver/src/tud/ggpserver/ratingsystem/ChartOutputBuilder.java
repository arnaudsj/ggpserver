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

package tud.ggpserver.ratingsystem;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.ratingsystems.Rating;

import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartOutputBuilder implements OutputBuilder {

	private JFreeChart chart; 
	private DefaultCategoryDataset dataset;
	private int matchSetNb;
	private MatchSetIdentifier matchSetIdentifier;
	
	public ChartOutputBuilder() {
		matchSetNb = 1;
		dataset = new DefaultCategoryDataset();
		chart = ChartFactory.createLineChart("Player Ratings", "Match Sets",
	            "Rating", dataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot plot = (CategoryPlot)chart.getPlot();
		// ((LineAndShapeRenderer)plot.getRenderer()).setBaseShapesVisible(true);
		plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		// plot.getDomainAxis().setMaximumCategoryLabelLines(3);
		((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#beginMatchSet(ggpratingsystem.MatchSet)
	 */
	@Override
	public void beginMatchSet(MatchSet matchSet) {
		matchSetIdentifier = new MatchSetIdentifier(matchSetNb, matchSet.getId(), matchSet.getGame().getName());
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#endMatchSet(ggpratingsystem.MatchSet)
	 */
	@Override
	public void endMatchSet(MatchSet matchSet) {
		matchSetNb++;
		matchSetIdentifier = null;
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#initialize(java.util.List)
	 */
	@Override
	public void initialize(List<Player> players) throws IOException {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#ratingUpdate(ggpratingsystem.ratingsystems.Rating)
	 */
	@Override
	public void ratingUpdate(Rating rating) {
		dataset.addValue(rating.getCurRating(), rating.getPlayer(), matchSetIdentifier);
	}
	
	public JFreeChart getChart() {
		return chart;
	}

	private class MatchSetIdentifier implements Comparable<MatchSetIdentifier> {
		private int matchSetNb;
		private String matchSetId;
		private String gameName;
		
		public MatchSetIdentifier(int matchSetNb, String matchSetId, String gameName) {
			this.matchSetNb = matchSetNb;
			this.matchSetId = matchSetId;
			this.gameName = gameName;
		}

		@Override
		public int compareTo(MatchSetIdentifier o) {
			return matchSetNb - o.matchSetNb; 
		}
		
		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof MatchSetIdentifier) {
				return matchSetNb == ((MatchSetIdentifier)o).matchSetNb;
			}else{
				return false;
			}
		}

		@Override
		public String toString() {
			return matchSetId + " (" + gameName + ")";
		}
	}
}
