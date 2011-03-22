package com.googlecode.apdfviewer;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Represents the rectangle seen by a view
 * @author ruedi
 *
 */
public class ScreenRectangle extends PdfScreenMappedRectangle implements Serializable{
	transient public String name;
	
	public enum RenderPriority{
		READ_AHEAD,
		PAGE,
		TOUCHPAD,
		EINK
	}
	
	public enum UpdatePolicy{
		IMMEDIATE,
		AFTER_RENDER
	}
	
	private transient long renderDelay;

	public final transient PropertyChangedSender OnRedraw=new PropertyChangedSender();
	
	private final Size renderedSize = new Size();
	transient private RenderPriority renderPriority;
	transient private UpdatePolicy updatePolicy;
	
	public ScreenRectangle(String name){
		super();
		this.name=name;
		renderedSize.AddListener(OnParametersChanged);
		
		OnParametersChanged.AddListener(new OnPropertyChangedListener() {
			@Override
			public void OnPropertyChanged(Object source) {
				// set the render result to obsolete whenever the 
				// parameters of the screen rectangle change
				renderResultObsolete=true;
			}
		});
	}
	
	public void setRenderPriority(RenderPriority renderPriority) {
		this.renderPriority = renderPriority;
	}

	public RenderPriority getRenderPriority() {
		return renderPriority;
	}

	public void setUpdatePolicy(UpdatePolicy updatePolicy) {
		this.updatePolicy = updatePolicy;
	}

	public UpdatePolicy getUpdatePolicy() {
		return updatePolicy;
	}


	public Size getSizeOnPage() {
		return new Size(renderedSize.getWidth()/getZoom(),renderedSize.getHeight()/getZoom());
	}

	@Override
	public Size getRenderedSize() {
		return renderedSize;
	}

	public void setRenderDelay(long renderDelay) {
		this.renderDelay = renderDelay;
	}

	public long getRenderDelay() {
		return renderDelay;
	}
	
	@Override
	public String toString() {
		return name+super.toString()+" renderPriority: "+renderPriority;
	}
	
	transient private RenderResult renderResult;
	transient private boolean renderResultObsolete=true;

	public boolean isRenderResultObsolete() {
		return renderResultObsolete;
	}

	public void setRenderResult(RenderResult renderResult) {
		if (this.renderResult!=null){
			this.renderResult.decreaseReferences();
		}
		this.renderResult = renderResult;
		if (this.renderResult!=null){
			this.renderResult.increaseReferences();
		}
	}

	public RenderResult getRenderResult() {
		return renderResult;
	}

	public void setRenderResultObsolete(boolean renderResultObsolete) {
		this.renderResultObsolete = renderResultObsolete;
	}

	@Override
	protected void setSizeFrom(PdfScreenMappedRectangle r) {
		renderedSize.set(r.getRenderedSize());
	}
}
