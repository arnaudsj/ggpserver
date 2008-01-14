/*
 * Created on May 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cs227b.teamIago.util;

/**
 * @author Nick
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ForNest {
	protected int nestDepth;
	protected int [] count;
	protected int [] max;
	protected int totalCount;
	protected int totalMax;

	public ForNest() {
	};
	
	public ForNest(int[] limits) {
		setLimits(limits);
		totalCount = 0;
	}
	
	public void setLimits(int [] limits) {
		totalMax = 1;
		nestDepth = limits.length;
		count = new int[nestDepth];
		max = new int[nestDepth];
		for (int i = 0; i < nestDepth; i++)
		{
			count[i] = 0;
			max[i] = limits[i];
			totalMax *= limits[i];
		}		
	}
	
	public void reset() {
		totalCount = 0;
		for (int i = 0; i < nestDepth; i++)
			count[i] = 0;
	}
	
	public int getCounter(int i) {
		return count[i];
	}
	
	public int getMax(int i) {
		return max[i];
	}
	
	
	// returns true if there are still more permutations left
	public boolean isMore() {
		return totalCount < totalMax;
	}

	// increments counters appropriately for arbitrary-depth loop.
	// return is equivalent to a call to "isMore," but saves the extra
	// loop
	public boolean inc() {
		boolean carry = false;
		for (int i = 0; i < nestDepth; i++)
		{
			if (carry) {
				if (count[i] < max[i] - 2) {
					count[i] += 2;
					carry = false;
				} else {
					count[i] = count[i] + 2 - max[i];
					carry = true;
				}
			} else {
				if (count[i] < max[i] - 1) {
					count[i] += 1;
					carry = false;
				} else {
					count[i] = 0;
					carry = true;
				}
			}
		}
		totalCount++;
		return !carry;
	}

}
