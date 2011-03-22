package com.googlecode.apdfviewer;

import java.util.ArrayList;
import java.util.Stack;

import android.util.Log;

import com.googlecode.apdfviewer.ScreenRectangle.RenderPriority;

public class PageReadAheadController {
	private final String TAG="PageReadAheadController";
	
	private final float MAX_EXTENT=1000;
	private final long RENDER_DELAY=2000;
	
	private ArrayList<ScreenRectangle> pages=new ArrayList<ScreenRectangle>();
	private PDFDocument pdfDocument;
	private RenderController renderController;
	
	public PageReadAheadController(RenderController renderController) {
		this.renderController=renderController;
	}

	public void setCurrentPage(int pageNr, int pageCount){
		Log.d(TAG,"setCurrentPage pageNr:"+pageNr+" pageCount:"+pageCount);
		Stack<ScreenRectangle> unneeded=new Stack<ScreenRectangle>();
		
		// create list of unneeded pages
		for (ScreenRectangle r: pages){
			if ((r.getPageNr()<pageNr-1)||(r.getPageNr()>=pageCount))
				unneeded.push(r);
		}
		
		for (int i=pageNr-1; i<pageCount && i<=pageNr+1; i++){
			if (i<0) continue;
			if (isCached(i)) continue;
			if (!unneeded.isEmpty()){
				// use unneeded page
				ScreenRectangle r=unneeded.pop();
				r.setPageNr(pageNr);
				updateParameters(r,pageNr);
			}
			else{
				// create new page
				ScreenRectangle r=new ScreenRectangle("Page");
				writeConfiguration(r);
				updateParameters(r, pageNr);
				addPage(r);
			}
			
			// delete unneeded pages
			while (!unneeded.isEmpty()){
				removePage(unneeded.pop());
			}
		}
	}
	
	private void removePage(ScreenRectangle pop) {
		pages.remove(pop);
		renderController.removeScreenRectangle(pop);
	}

	private void addPage(ScreenRectangle r) {
		pages.add(r);
		renderController.addScreenRectangle(r);
	}

	private void writeConfiguration(ScreenRectangle r) {
		r.setRenderDelay(RENDER_DELAY);
		r.setRenderPriority(RenderPriority.PAGE);
	}

	/**
	 * Calculates a size such that no side of the cached
	 * page bitmap is longer than MAX_EXTENT
	 * @param r
	 * @param pageNr
	 */
	private void updateParameters(ScreenRectangle r, int pageNr) {
		Size pageSize=pdfDocument.getPageSize(pageNr);
		float zoom=1;
		
		// adjust zoom factor such that the MAX_EXTENT is not violated
		if (pageSize.getWidth()>MAX_EXTENT){
			zoom=Math.min(zoom, MAX_EXTENT/pageSize.getWidth());
		}
		
		if (pageSize.getHeight()>MAX_EXTENT){
			zoom=Math.min(zoom, MAX_EXTENT/pageSize.getHeight());
		}
		
		pageSize.mul(zoom);
		r.getPageOrigin().set(0,0);
		r.getRenderedSize().set(pageSize);
		r.setZoom(zoom);
	}

	private boolean isCached(int pageNr){
		for (ScreenRectangle r: pages){
			if (r.getPageNr()==pageNr) return true;
		}
		return false;
	}

	public void setPdfDocument(PDFDocument pdfDocument) {
		Log.d(TAG,"setPdfDocument");
		this.pdfDocument = pdfDocument;
		
		// remove all pages, they will be recreated when the current page number is set later
		for (ScreenRectangle r: pages){
			renderController.removeScreenRectangle(r);
		}
	}

	

}
