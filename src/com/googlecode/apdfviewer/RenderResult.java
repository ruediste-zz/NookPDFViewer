package com.googlecode.apdfviewer;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class RenderResult extends PdfScreenMappedRectangle {
	
	private Size sizeOnPage=new Size();
	private Bitmap bitmap;
	private int references;
	public RenderResult() {
		super();
	}
	public void setBitmap(Bitmap bitmap) {
		if (this.bitmap!=null){
			this.bitmap.recycle();
		}
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setParametersFromScreenRectangle(PdfScreenMappedRectangle screenRectangle){
		getPageOrigin().set(screenRectangle.getPageOrigin());
		setPageNr(screenRectangle.getPageNr());
		getSizeOnPage().set(screenRectangle.getSizeOnPage());
		setZoom(screenRectangle.getZoom());
	}

	@Override
	public Size getSizeOnPage() {
		return sizeOnPage;
	}

	@Override
	public Size getRenderedSize() {
		return new Size(sizeOnPage.getWidth()*getZoom(),sizeOnPage.getHeight()*getZoom());
	}
	@Override
	protected void setSizeFrom(PdfScreenMappedRectangle r) {
		sizeOnPage.set(r.getSizeOnPage());
	}
	public void increaseReferences() {
		references++;
	}
	public void decreaseReferences() {
		references--;
		if (references==0) {
			if (bitmap!=null){
				bitmap.recycle();
			}
		}
		
	}
}
