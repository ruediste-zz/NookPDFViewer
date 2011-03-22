package com.nookdevs.common;

public class GreyBarButtonLayout {
	public GreyBarButtonPosition button0;
	public GreyBarButtonPosition button1;
	public GreyBarButtonPosition button2;
	public GreyBarButtonPosition button3;
	/**
	 * Instantiate this class with the position of your 4 buttons. If you have less than 4 put null values in.
	 * 
	 * @param {@link GreyBarButtonPosition} button0
	 * @param {@link GreyBarButtonPosition} button1
	 * @param {@link GreyBarButtonPosition} button2
	 * @param {@link GreyBarButtonPosition} button3
	 */
	public GreyBarButtonLayout(GreyBarButtonPosition button0,GreyBarButtonPosition button1,GreyBarButtonPosition button2,GreyBarButtonPosition button3) {
		this.button0 = button0;
		this.button1 = button1;
		this.button2 = button2;
		this.button3 = button3;		
	}
}

