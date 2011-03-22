package com.nookdevs.common;


import android.view.MotionEvent;
import android.view.View;
/**
 * GreyBarTouchInterface allows you to control up to 4 virtual buttons from an Activity.
 * 
 * @usage 
 * <pre>
 * <code>
 * public class YourActivity extends nookBaseActivity implements GreyBarTouchInterface {
 * 	public onCreate(bundle a) {
 * 		View b = (View) findViewById(R.id.activity_touchsurface);
 * 		try {
 * 			GreyBarButtonLayout layout = new GreyBarButtonLayout(
 * 				new GreyBarButtonPosition(0,150),
 * 				new GreyBarButtonPosition(170,260),
 * 				new GreyBarButtonPosition(340,430),
 * 				new GreyBarButtonPosition(450,600));	
 * 			b.setOnTouchListener(new GreyBarTouchListener(this));
 * 		} catch (GreyBarButtonPositionException e) {
 *			e.printStackTrace();
 *		}
 * 	}
 * 	public boolean button0(View v, MotionEvent event) {
 * 		if(event.getAction() == MotionEvent.ACTION_DOWN) {
 *			findViewById(R.id.FIRST_BUTTON).setPressed(true);
 *			return true;
 *		} else if(event.getAction() == MotionEvent.ACTION_UP) {
 *			findViewById(R.id.FIRST_BUTTON).setPressed(false);
 *			******DO SOMETHING USEFUL
 *			return false;
 *		}
 *		return true;
 * 	}
 * 	public boolean button1(View v, MotionEvent event) {return false;}
 * 	public boolean button2(View v, MotionEvent event) {return false;}
 * 	public boolean button3(View v, MotionEvent event) {return false;}
 * }
 * </code>
 * </pre>
 * @see
 * {@link  #GreyBarTouchListener}
 * {@link  #GreyBarButtonPosition}
 * {@link  #GreyBarButtonLayout} 
 */
public interface GreyBarTouchInterface {
	public boolean button0(View v, MotionEvent event);
	public boolean button1(View v, MotionEvent event);
	public boolean button2(View v, MotionEvent event);
	public boolean button3(View v, MotionEvent event);
}
