package com.googlecode.apdfviewer;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class PDFService {
	private final String TAG="PDFService";
	public Bitmap renderPage(PDFDocument document, int pageNr){
		PDF pdf=document.getPdf();

    	// render the whole page
        return render(pdf, pageNr, new Point(0,0), document.getPageSize(pageNr), 1);
	}

	/**
	 * render a part of a pdf page
	 * @param pdf pdf to render
	 * @param pageNr page to render from
	 * @param renderSize size of the output
	 * @param zoom zoom factor
	 * @return
	 */
	private Bitmap render(PDF pdf, int pageNr, Point pageOrigin, Size renderSize, float zoom) {
		
		//Log.d(TAG,"render(PDF pdf, int pageNr, Size renderSize, float zoom)");
		// render the page to a buffer
        int[] buf = pdf.renderPage(
        		pageNr, // page number
        		(int) (zoom * 1000), // zoom factor 
        		(int)(pageOrigin.getX()*zoom), (int)(pageOrigin.getY()*zoom), // top left corner in the page
        		0, // rotation
        		new PDF.Size((int)renderSize.getWidth(), (int)renderSize.getHeight()) // size of the output
        		);
        
        // create a new bitmap
    	Bitmap bitmap=Bitmap.createBitmap((int)renderSize.getWidth(),(int)renderSize.getHeight(), Config.RGB_565);
    	
    	// create a canvas to render the buffer
    	Canvas canvas=new Canvas(bitmap);
    	
    	// create a paint to render the buffer
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        p.setColorFilter(filter);
        
    	// draw the buffer to the cache bitmap using the canvas and the paint        
        canvas.drawBitmap(buf, 0, (int)renderSize.getWidth(), 0, 0, (int)renderSize.getWidth(), (int)renderSize.getHeight(), true, p);
        
        return bitmap;
	}

	public void render(RenderResult result, PDFDocument pdfDocument) {
		//Log.d(TAG,"render(RenderResult,Document)");
		//Log.d(TAG,"result Page: "+FormatRectf(result.getPageRect()));

		// render the bitmap
		com.googlecode.apdfviewer.Size renderedSize = result.getRenderedSize();
		result.setBitmap(render(
				pdfDocument.getPdf(),
				result.getPageNr(),
				result.getPageOrigin(),
				new Size((int)renderedSize.getWidth(),(int)renderedSize.getHeight()),
				result.getZoom()
				));	
	}

	private String FormatRectf(RectF rectf){
		return "("+rectf.left+","+rectf.top+","+rectf.right+","+rectf.bottom+")";
	}
	public void RenderFromResultToRectangle( RenderResult sourceRenderResult, PdfScreenMappedRectangle targetRectangle, Canvas canvas) {
		/*Log.d(TAG,"RenderFromResultToRectangle target:"+targetRectangle);
		Log.d(TAG,"source Page: "+FormatRectf(sourceRenderResult.getPageRect()));
		Log.d(TAG,"target Page: "+FormatRectf(targetRectangle.getPageRect()));
		*/
		
		RectF soucePage=sourceRenderResult.getPageRect();
		RectF targetPage=targetRectangle.getPageRect();
		RectF pageIntersection=new RectF(soucePage);
		pageIntersection.intersect(targetPage);
		
		RectF clippedSrcRenderRect=sourceRenderResult.mapPageRectToRenderedRect(pageIntersection);
		RectF clippedTargetRenderRect=targetRectangle.mapPageRectToRenderedRect(pageIntersection);
		
		// create a paint to render the buffer
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        p.setColorFilter(filter);
        
    	// draw the buffer to the cache bitmap using the canvas and the paint        
        canvas.drawBitmap(
        		sourceRenderResult.getBitmap(),
        		new Rect((int)clippedSrcRenderRect.left,(int) clippedSrcRenderRect.top, (int) clippedSrcRenderRect.right,(int)clippedSrcRenderRect.bottom), // src
        		new Rect((int)clippedTargetRenderRect.left,(int) clippedTargetRenderRect.top, (int) clippedTargetRenderRect.right,(int)clippedTargetRenderRect.bottom), //dst
        		p
        	);
	}

}
