package com.googlecode.apdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.view.View;

public class BitmapDrawingView extends View{

	private ScreenRectangle rectangle;
	private Orientation orientation;
	private RenderController renderController;
	private Size size;
	
	public BitmapDrawingView(Context context) {
		super(context);
	}
	public BitmapDrawingView(Context context, AttributeSet attrs){
		super(context,attrs);
	}
	public BitmapDrawingView(Context context, AttributeSet arg1, int  defstyle){
		super(context,arg1, defstyle);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (rectangle==null) return;
		if (renderController==null) return;
		if (getOrientation()==null) return;
		
		canvas.save();
        
        switch (getOrientation()){
			case LEFT_RIGHT:
				canvas.translate(0,size.getHeight());
				canvas.rotate(-90,0,0);
			break;
			case RIGHT_LEFT:
				canvas.translate(size.getWidth(),0);
				canvas.rotate(90,0,0);		
			break;
		}
	        
		if (rectangle.isRenderResultObsolete()){
			renderController.RenderFromExistingResults(rectangle, canvas);
		}
		else {
			// create a paint to render the buffer
	        Paint p = new Paint();
	        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
	        p.setColorFilter(filter);
	       
	        // draw the render result
			canvas.drawBitmap(rectangle.getRenderResult().getBitmap(), 0, 0, p);
		}
		canvas.restore();
	}

	public void setRectangle(ScreenRectangle rectangle) {
		this.rectangle = rectangle;
	}

	public ScreenRectangle getRectangle() {
		return rectangle;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setRenderController(RenderController renderController) {
		this.renderController = renderController;
	}

	public RenderController getRenderController() {
		return renderController;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Size getSize() {
		return size;
	}

}
