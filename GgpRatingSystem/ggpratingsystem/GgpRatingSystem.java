package ggpratingsystem;

import flanagan.analysis.Regression;


/**
 * @author martin
 *
 */
public class GgpRatingSystem {

	public static void main(String[] args) {
		////// Multiple Linear Regression Test //////
		
		/* generate test data */
		final int TEST_SET_SIZE = 50;
		final int NUM_VARS = 3;
		final int X_RANGE = 100;
		
		double[][] xdata = new double[NUM_VARS][TEST_SET_SIZE] ;
		double[] ydata = new double[TEST_SET_SIZE];
		
		// Y = 10 + 1 * X_1 + 2 * X_2 + 3 * X_3		
		for (int i = 0; i < TEST_SET_SIZE; i++) {
			ydata[i] = 10  ; //+ Math.random() * 40;
			
			for (int j = 0; j < NUM_VARS; j++) {
				xdata[j][i] = Math.random() * X_RANGE;
				ydata[i] += (j + 1) * xdata[j][i]; 
			}
		}
		
		Regression reg = new Regression(xdata, ydata);
		reg.linear();
		double [] coeff = reg.getCoeff();
		
		for (double d : coeff) {
			System.out.println(d);
		}
		
		reg.print("/home/martin/workspace/GgpRatingSystem/data/regression.txt");
		reg.linearPlot();
	}
}
