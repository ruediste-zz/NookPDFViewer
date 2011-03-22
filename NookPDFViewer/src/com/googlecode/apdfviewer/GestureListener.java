package com.googlecode.apdfviewer;

import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;

/**
 * Translates gestures for the controller
 * @author ruedi
 *
 */
public class GestureListener implements OnGestureListener{
	private final String TAG="GestureLIstener";
	private PDFViewerActivity pdfViewerActivity;
	private PDFController controller;
	
	public GestureListener(PDFViewerActivity pdfViewerActivity,
			PDFController controller) {
		this.pdfViewerActivity=pdfViewerActivity;
		this.controller=controller;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"onDown");
		return true;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"onFling");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		Log.d(TAG,"onLongPress");
		
		//pdfViewerActivity.setZoomMode(!pdfViewerActivity.isZoomMode());
	}

	@Override
	public boolean onScroll(MotionEvent downEvent, MotionEvent currentEvent, float distanceX,
			float distanceY) {
		//Log.d(TAG,"onScroll");
		
		if (pdfViewerActivity.isZoomMode()){
			// handle a zoom
			float orig=1, current=1;
			switch(pdfViewerActivity.getOrientation()){
				case TOP_BOTTOM:
				case LEFT_RIGHT:
					current=currentEvent.getX();
					orig=current-distanceX;
				break;
				case RIGHT_LEFT:
					current=PDFViewerActivity.TOUCH_PAD_WIDTH-currentEvent.getX();
					orig=current+distanceX;
				break;
			}
			orig=Math.abs(orig);
			
			if (orig>5){
				controller.multiplyZoomFactor(orig/current);
				
			}
		}
		else{
			// handle a scroll
			float dx=0,dy=0;
			switch(pdfViewerActivity.getOrientation()){
				case TOP_BOTTOM:
					dx=distanceX;
					dy=distanceY;
				break;
				case LEFT_RIGHT:
					dx=-distanceY;
					dy=distanceX;
				break;
				case RIGHT_LEFT:
					dx=distanceY;
					dy=-distanceX;
				break;
			}
			controller.OffsetPosition(dx,dy);
		}
		return true;
	}
	
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"onSingleTapUp");
		//controller.triggerUpdate();
		
		return true;
	}
}
