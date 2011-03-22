package com.googlecode.apdfviewer;

import java.io.Serializable;

import android.graphics.Rect;
import android.graphics.RectF;

public abstract class PdfScreenMappedRectangle implements Serializable {

	transient public final PropertyChangedSender OnParametersChanged = new PropertyChangedSender();
	public final transient PropertyChangedSender OnPageChanged=new PropertyChangedSender();

	private float zoom = 1;
	private int pageNr = 0;
	
	// point on the page of the upper left corner which is visible
	private Point pageOrigin=new Point();

	public PdfScreenMappedRectangle() {
		pageOrigin.AddListener(OnParametersChanged);
	}

	public void setParametersFrom(PdfScreenMappedRectangle r){
		pageOrigin.set(r.getPageOrigin());
		pageNr=r.getPageNr();
		zoom=r.getZoom();
		setSizeFrom(r);
		OnParametersChanged.OnPropertyChanged();
		OnPageChanged.OnPropertyChanged();
	}
	
	protected abstract void setSizeFrom(PdfScreenMappedRectangle r);
	
	public Point getPageOrigin() {
		return pageOrigin;
	}

	public abstract Size getRenderedSize();

	public void setZoom(float zoom) {
		this.zoom = zoom;
		OnParametersChanged.OnPropertyChanged();
	}

	public float getZoom() {
		return zoom;
	}

	public void setPageNr(int pageNr) {
		if (pageNr!=this.pageNr){
			this.pageNr = pageNr;
			OnParametersChanged.OnPropertyChanged();
			OnPageChanged.OnPropertyChanged();
		}
	}

	public int getPageNr() {
		return pageNr;
	}

	public abstract Size getSizeOnPage();

	public RectF getPageRect() {
		return Point.CreateRectF(getPageOrigin(), getSizeOnPage());
	}
	
	public RectF mapPageRectToRenderedRect(RectF pageRect){
		Point renderOrigin=new Point(new Point(pageRect.left,pageRect.top));
		renderOrigin.sub(getPageOrigin());
		renderOrigin.mul(getZoom());
		
		Size renderSize=new Size(pageRect.width(),pageRect.height());
		renderSize.mul(getZoom());
		
		return new RectF(
			renderOrigin.getX(),
			renderOrigin.getY(), 
			renderOrigin.getX()+renderSize.getWidth(),
			renderOrigin.getY()+renderSize.getHeight());
	}
	
	public RectF mapRenderedRectToPageRect(RectF renderedRect){
		Point pagePoint=new Point(new Point(renderedRect.left,renderedRect.top));
		pagePoint.mul(1/getZoom());
		pagePoint.add(getPageOrigin());
		
		Size pageSize=new Size(renderedRect.width(),renderedRect.height());
		pageSize.mul(1/getZoom());
		
		return new RectF(
			pagePoint.getX(),
			pagePoint.getY(), 
			pagePoint.getX()+pageSize.getWidth(),
			pagePoint.getY()+pageSize.getHeight());
	}

	@Override
	public String toString() {
		return "Rectangle pageNr: "+pageNr+" pageOrigin:"+pageOrigin;
	}
}