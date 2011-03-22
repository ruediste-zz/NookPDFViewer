package com.nookdevs.common;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
/**
 * GreyBarTouchListener class is a utility class that helps translate touching on the grey bar to actions within an Activity.
 * @see
 * {@link GreyBarTouchInterface}
 *
 */
public class GreyBarTouchListener implements OnTouchListener {
	GreyBarTouchInterface parentView;
	GreyBarButtonLayout buttonLayout;
	/**
	 * GreyBarTouchListener
	 * This constructor is a default setup. Two 150px buttons on the outside, two 90px buttons in the middle. 20 pixel space in between them.
	 * @param {@link GreyBarTouchInterface} pv
	 */
	public GreyBarTouchListener(GreyBarTouchInterface pv) {
		GreyBarButtonLayout layout = null;
		try {
			layout = new GreyBarButtonLayout(new GreyBarButtonPosition(0,150),new GreyBarButtonPosition(170,260),new GreyBarButtonPosition(340,430),new GreyBarButtonPosition(450,600));
		} catch (Exception e) {

		}
		parentView = pv;
		buttonLayout = layout;		
	}
	/**
	 * GreyBarTouchListener
	 * This constructor allows you to define the position of all your buttons.
	 * @param {@link  #GreyBarTouchInterface} pv
	 * @param {@link  #GreyBarButtonLayout}  layout
	 */
	
	public GreyBarTouchListener(GreyBarTouchInterface pv, GreyBarButtonLayout layout) {
		parentView = pv;
		buttonLayout = layout;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		try {
	    	float x = event.getRawX();
	    	if(buttonLayout.button0 != null)
	    		if(x >= buttonLayout.button0.getLeftBound() && x <= buttonLayout.button0.getRightBound()) {
	    			return parentView.button0(v,event);
	    		}
	    	if(buttonLayout.button1 != null)
	    		if(x >= buttonLayout.button1.getLeftBound() && x <= buttonLayout.button1.getRightBound()) {
	    			return parentView.button1(v,event);
	    		}
	    	if(buttonLayout.button2 != null)
	    		if(x >= buttonLayout.button2.getLeftBound() && x <= buttonLayout.button2.getRightBound()) {
	    			return parentView.button2(v,event);
	    		}
	    	if(buttonLayout.button3 != null)
	    		if(x >= buttonLayout.button3.getLeftBound() && x <= buttonLayout.button3.getRightBound()) {
	    			return parentView.button3(v,event);
	    		}
		} catch (Throwable t) {
			t.printStackTrace();
		}
    	return false;
	}

}
