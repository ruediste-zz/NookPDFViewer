package com.googlecode.apdfviewer;

import com.googlecode.apdfviewer.ScreenRectangle.RenderPriority;
import com.googlecode.apdfviewer.ScreenRectangle.UpdatePolicy;

import android.R.bool;
import android.content.Context;
import android.graphics.*;
import android.net.Uri;
import android.util.Log;

public class PDFController {
	private final long E_INK_RENDER_DELAY=500;
	private final long TOUCH_PAD_RENDER_DELAY=500;
	private final long READ_AHEAD_RENDER_DELAY=1000;
	
	public enum ViewEnum{ 
		Normal,
		Options,
		SeekPage,
		Confirm
	}
	private final String TAG="PDFController";
	
	private boolean flipPages;
	
	private PDFViewerActivity activity;
	private Context context;
	private PDFDocument document;
	
	private ViewEnum currentView=ViewEnum.Normal;
	
	private RenderController renderController;
	private DocumentPositionController documentPositionController;
	PDFService pdfService=new PDFService();
	//PageReadAheadController readAheadController;

	private ScreenRectangle touchPadRectangle=new ScreenRectangle("TouchPadRectangle");
	private ScreenRectangle eInkRectangle=new ScreenRectangle("EInkRectangle");
	
	public PDFController(PDFViewerActivity pdfViewerActivity, Context context) {
		this.activity=pdfViewerActivity;
		this.context=context;
		
		renderController=new RenderController(pdfService);
		documentPositionController=new DocumentPositionController(context);
		//readAheadController=new PageReadAheadController(renderController);
		
		writeTouchPadConfiguration();
		renderController.addScreenRectangle(touchPadRectangle);
		
		writeEInkConfiguration();
		renderController.addScreenRectangle(eInkRectangle);
		
		writeReadAheadConfiguration(prevTouchPadPageRectangle);
		//renderController.addScreenRectangle(prevTouchPadPageRectangle);
		writeReadAheadConfiguration(nextTouchPadPageRectangle);
		//renderController.addScreenRectangle(nextTouchPadPageRectangle);
		
		writeReadAheadConfiguration(prevEInkPageRectangle);
		//renderController.addScreenRectangle(prevEInkPageRectangle);
		writeReadAheadConfiguration(nextEInkPageRectangle);
		renderController.addScreenRectangle(nextEInkPageRectangle);
		
		touchPadRectangle.OnParametersChanged.AddListener(new OnPropertyChangedListener() {
			
			@Override
			public void OnPropertyChanged(Object source) {
				if (document==null) return;
				//if (readAheadController==null) return;
				
				updateEInkFromTouchPad();
				//readAheadController.setCurrentPage(touchPadRectangle.getPageNr(), document.getPageCount());
				prevTouchPadPageRectangle.setParametersFrom(calcPrevRectangle(touchPadRectangle));
				nextTouchPadPageRectangle.setParametersFrom(calcNextRectangle(touchPadRectangle));
				nextEInkPageRectangle.setParametersFrom(calcPrevRectangle(eInkRectangle));
				nextEInkPageRectangle.setParametersFrom(calcNextRectangle(eInkRectangle));
			}
		});
		
		touchPadRectangle.OnPageChanged.AddListener(new OnPropertyChangedListener() {			
			@Override
			public void OnPropertyChanged(Object source) {
				documentPositionController.savePosition(document.getLocation(), touchPadRectangle);
			}
		});
	}

	private void writeTouchPadConfiguration(){
		touchPadRectangle.setRenderDelay(TOUCH_PAD_RENDER_DELAY);
		touchPadRectangle.setRenderPriority(RenderPriority.TOUCHPAD);
		touchPadRectangle.setUpdatePolicy(UpdatePolicy.IMMEDIATE);
	}
	
	private void writeEInkConfiguration(){
		eInkRectangle.setRenderDelay(E_INK_RENDER_DELAY);
		eInkRectangle.setRenderPriority(RenderPriority.EINK);
		eInkRectangle.setUpdatePolicy(UpdatePolicy.AFTER_RENDER);
	}
	private void writeReadAheadConfiguration(ScreenRectangle r){
		r.setRenderDelay(READ_AHEAD_RENDER_DELAY);
		r.setRenderPriority(RenderPriority.READ_AHEAD);
		r.setUpdatePolicy(UpdatePolicy.AFTER_RENDER);
	}
	
	
	public void Open(Uri uri) {
		Log.d(TAG,"Open");
		try {
			document=new PDFDocument(context.getContentResolver(), uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//readAheadController.setPdfDocument(document);
		renderController.setPdfDocument(document);
		
		// try to read the stored positions
		ScreenRectangle storedPosiation=documentPositionController.getPosition(document.getLocation());
		if (storedPosiation==null){
			// initialize with default parameters
			touchPadRectangle.setPageNr(0);
			setNewTouchPadParameters(new Point(0,0),1);}
		else{
			// initialize from stored parameters
			touchPadRectangle.setParametersFrom(storedPosiation);
		}
	}
	
	private ScreenRectangle nextTouchPadPageRectangle =new ScreenRectangle("NextPageReadAhead");
	private ScreenRectangle prevTouchPadPageRectangle =new ScreenRectangle("PrevPageReadAhead");
	private ScreenRectangle nextEInkPageRectangle =new ScreenRectangle("NextPageReadAhead");
	private ScreenRectangle prevEInkPageRectangle =new ScreenRectangle("PrevPageReadAhead");

	public void setPageNr(int newPageNr){
		if (document==null) return;
		int nr=newPageNr;
		if (nr<0) nr=0;
		if (nr>=document.getPageCount()) nr=document.getPageCount()-1;
		touchPadRectangle.setPageNr(nr);
	}
	public void pageUp() {
		if (document==null) return;
		//touchPadRectangle.OnParametersChanged.HoldPropertyChangedEvents();
		touchPadRectangle.setParametersFrom(calcPrevRectangle(touchPadRectangle));
		//eInkRectangle.setParametersFrom(calcPrevRectangle(eInkRectangle));
		//touchPadRectangle.OnParametersChanged.ReleasePropertyChangedEvents();
	}

	public void pageDown() {	
		if (document==null) return;
		Log.d(TAG,"pageDown pageNr: "+touchPadRectangle.getPageNr());
		//touchPadRectangle.OnParametersChanged.HoldPropertyChangedEvents();
		touchPadRectangle.setParametersFrom(calcNextRectangle(touchPadRectangle));
		//eInkRectangle.setParametersFrom(calcNextRectangle(eInkRectangle));
		//touchPadRectangle.OnParametersChanged.ReleasePropertyChangedEvents();
	}

	private ScreenRectangle calcPrevRectangle(ScreenRectangle r){
		float pos=r.getPageOrigin().getY();
		float height=eInkRectangle.getRenderedSize().getHeight();
		
		double newPos;
		int newPageNr;
		
		// update previous page rectangle
		if (flipPages){
			newPos=pos;
			if (r.getPageNr()>0){
				newPageNr=r.getPageNr()-1;
			}
			else{
				newPageNr=r.getPageNr();
			}
		}
		else{
			if (pos<height*0.05 && r.getPageNr()>0){
				//go to previous page	
				newPageNr=r.getPageNr()-1;
				newPos=height;
			}
			else{
				// just move up
				newPageNr=r.getPageNr();
				newPos=pos-height*0.9;
			}
		}
		ScreenRectangle result=new ScreenRectangle("tmp");
		
		result.setPageNr(newPageNr);
		result.getPageOrigin().set(new Point(r.getPageOrigin().getX(),(float)newPos));
		result.getPageOrigin().set(calculateRestrictedPageOrigin(result, document.getPageSize(newPageNr)));
		result.getRenderedSize().set(r.getRenderedSize());
		result.setZoom(r.getZoom());
		return result;
	}
	
	private ScreenRectangle calcNextRectangle(ScreenRectangle r){
		float pos=r.getPageOrigin().getY();
		float height=eInkRectangle.getSizeOnPage().getHeight();
		
		double newPos;
		int newPageNr;
		// update next page rectangle
		if (flipPages){
			newPos=pos;
			if (r.getPageNr()<document.getPageCount()-1){
				newPageNr=r.getPageNr()+1;
				
			}
			else{
				newPageNr=r.getPageNr();
			}
		}
		else{
			if ((pos+height)>(document.getPageSize(r.getPageNr()).getHeight()*0.95) 
					&& r.getPageNr()<document.getPageCount()-1){
				//go to next page	
				newPageNr=r.getPageNr()+1;
				newPos=0;
			}
			else{
				// just move down
				newPageNr=r.getPageNr();
				newPos=pos+height*0.9;
			}
		}
		
		ScreenRectangle result=new ScreenRectangle("tmp");
		
		result.setPageNr(newPageNr);
		result.getPageOrigin().set(new Point(r.getPageOrigin().getX(),(float)newPos));
		result.getPageOrigin().set(calculateRestrictedPageOrigin(result, document.getPageSize(newPageNr)));
		result.getRenderedSize().set(r.getRenderedSize());
		result.setZoom(r.getZoom());
		return result;		
	}

	public void setNewTouchPadParameters(Point positionArg, float zoomFactorArg){
		
		// set the raw input parameters
		Size pageSize=document.getPageSize(touchPadRectangle.getPageNr());
		
		// don't allow to zoom out such that there is empty space around the page
		float minZoomFactor = getMinimalZoomFactor(
				eInkRectangle.getRenderedSize().getWidth(), 
				eInkRectangle.getRenderedSize().getHeight(),
				pageSize.getWidth(), pageSize.getHeight())
				*488/600;
		
		float zoom=zoomFactorArg;
		if (zoom<minZoomFactor){
			zoom=minZoomFactor;
		}

		touchPadRectangle.OnParametersChanged.HoldPropertyChangedEvents();
		
		touchPadRectangle.setZoom(zoom);
		// constraint page origin
		touchPadRectangle.getPageOrigin().set(positionArg);
		touchPadRectangle.getPageOrigin().set(calculateRestrictedPageOrigin(touchPadRectangle, pageSize));
				
		touchPadRectangle.OnParametersChanged.ReleasePropertyChangedEvents();
		touchPadRectangle.OnRedraw.OnPropertyChanged();
	}

	/**
	 * calculate a page origin such that the view cannot go outside of the page
	 * @param viewPosition
	 * @param pageWidth
	 * @param pageHeight
	 * @param viewWidth
	 * @param viewHeight
	 * @param zoomFactor
	 * @return restricted position
	 */
	private Point calculateRestrictedPageOrigin(ScreenRectangle rectangle,
			Size pageSize) {
		// calculate the rectangle size on the page
		RectF pageRect = rectangle.getPageRect();
		
		float rw=pageRect.width();
		float rh=pageRect.height();
		
		float xmin,ymin,xmax,ymax;
		
		if (rw<pageSize.getWidth()){
			// visible width smaller than page height
			xmin=0;
			xmax=pageSize.getWidth()-rw;
		}
		else
		{
			xmin=rw-pageSize.getWidth();
			xmax=0;
		}
		
		if (rh<pageSize.getHeight()){
			//visible height smaller than page height
			ymin=0;
			ymax=pageSize.getHeight()-rh;
		}
		else
		{
			ymin=rh-pageSize.getHeight();
			ymax=0;
		}
			
		//Log.d(TAG,xmin+" "+xmax+" "+ymin+" "+ymax);
		
		// restrict the position
		Point restrictedPosition=new Point();
		restrictedPosition.set(rectangle.getPageOrigin());
		
		restrictedPosition.setX(Math.max(restrictedPosition.getX(), xmin));
		restrictedPosition.setY(Math.max(restrictedPosition.getY(), ymin));
		restrictedPosition.setX(Math.min(restrictedPosition.getX(), xmax));
		restrictedPosition.setY(Math.min(restrictedPosition.getY(), ymax));
		return restrictedPosition;
	}



	private float getMinimalZoomFactor(float viewWidth, float viewHeight,
			float pageWidth, float pageHeight) {
		float minZoomFactor;
		
		
		minZoomFactor=viewWidth/pageWidth;
		
		minZoomFactor=Math.min(minZoomFactor, viewHeight/pageHeight);
		return minZoomFactor;
	}
	
	

	
	
	private void updateEInkFromTouchPad(){
		if (document==null) return;
		
		eInkRectangle.OnParametersChanged.HoldPropertyChangedEvents();
		
		eInkRectangle.setZoom(touchPadRectangle.getZoom()*600/488);
		eInkRectangle.setPageNr(touchPadRectangle.getPageNr());
		
		// constraint the position
		eInkRectangle.getPageOrigin().set(touchPadRectangle.getPageOrigin());
		eInkRectangle.getPageOrigin().set(calculateRestrictedPageOrigin(eInkRectangle, document.getPageSize(eInkRectangle.getPageNr())));

		eInkRectangle.OnParametersChanged.ReleasePropertyChangedEvents();
	}

	public void setCurrentView(ViewEnum currentView) {
		this.currentView = currentView;
		activity.updateShownView();
	}

	public ViewEnum getCurrentView() {
		return currentView;
	}

	public void multiplyZoomFactor(float f) {
		setNewTouchPadParameters(new Point(touchPadRectangle.getPageOrigin()), touchPadRectangle.getZoom()*f);
	}

	/**
	 * Offset the position. dx and dy are the distances on the touch pad
	 * @param dx
	 * @param dy
	 */
	public void OffsetPosition(float dx, float dy) {
		float touchPadZoomFactor=touchPadRectangle.getZoom();
		Point newOrigin=new Point(touchPadRectangle.getPageOrigin());
		newOrigin.offset(dx/touchPadZoomFactor, dy/touchPadZoomFactor);
		setNewTouchPadParameters(newOrigin,touchPadZoomFactor);
	}

	void setTouchPadRectangle(ScreenRectangle touchPadRectangle) {
		this.touchPadRectangle = touchPadRectangle;
	}

	ScreenRectangle getTouchPadRectangle() {
		return touchPadRectangle;
	}

	ScreenRectangle geteInkRectangle() {
		return eInkRectangle;
	}

	public RenderController getRenderController() {
		return renderController;
	}

	public void setFlipPages(boolean flipPages) {
		this.flipPages = flipPages;
	}

	public boolean isFlipPages() {
		return flipPages;
	}

	public PDFDocument getDocument() {
		return document;
	}

	public void clearBookmarks() {
		documentPositionController.clearPositions();
	}

	
}

