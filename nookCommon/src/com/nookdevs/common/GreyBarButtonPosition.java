package com.nookdevs.common;

/**
 * GreyBarButtonPosition defines the left and right bounds of a "GreyBarButton". 
 * The left and right bounds of the visual component. Therefore this number must be between 0 and 600.
 * Also, they may not cover the Home button area. 260-340 are off limits.
 * 
 * @param int x0 
 * @param int x1
 * 
 */
public class GreyBarButtonPosition {
	int x0,x1;
	public GreyBarButtonPosition(int x0, int x1) throws GreyBarButtonPositionException {
		this.x0 = x0;
		this.x1 = x1;
		if(x0==x1)
			throw new GreyBarButtonPositionException("x0 must be different than x1");
		if(getLeftBound() < 0 || getRightBound() > 480)
			throw new GreyBarButtonPositionException("The bounds of this GreyBarButton must be between 0 and 600");
		if(getLeftBound() > 208 && getLeftBound() < 272)
			throw new GreyBarButtonPositionException("The left side of this GreyBarButton covers the Home button");
		if(getRightBound() > 208 && getRightBound() < 272)
			throw new GreyBarButtonPositionException("The right side of this GreyBarButton covers the Home button");
	}
	/**
	 * getLeftBound() returns the touchscreen translated button position. 
	 */
	public int getLeftBound() {
		if(x0 < x1)
			return greybarfactor(x0);
		else
			return greybarfactor(x1);
	}
	/**
	 * getRightBound() returns the touchscreen translated button position. 
	 */
	public int getRightBound() {
		if(x0 < x1)
			return greybarfactor(x1);
		else
			return greybarfactor(x0);
	}
	private int greybarfactor(int x) {
		return (int) Math.round(x*0.8);
	}
}
