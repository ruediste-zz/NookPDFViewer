/*
 * Copyright (C) 2009 Li Wenhao <liwenhao.g@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.apdfviewer;

import java.io.FileNotFoundException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.ConditionVariable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.googlecode.apdfviewer.PDF.Size;

/**
 * View class for render PDF document.
 * 
 * @author Li Wenhao
 */
public class PDFView extends View {
    final static String TAG = "PDFView";
    
    /**
     * Interface for listener the status of the PDF view.
     */
    public interface StatusListener {
        /**
         * Called when PDFView start to load a document.
         */
        public void onLoadingStart(PDFView v);
        
        /**
         * Called when load is finished.
         */
        public void onLoadingEnd(PDFView v);
        
        /**
         * Called when PDFView start to render a page.
         */
        public void onRenderingStart(PDFView v);
        
        /**
         * Called when rendering is finished.
         */
        public void onRenderingEnd(PDFView v);
        
        /**
         * page changed.
         */
        public void onPageChanged(PDFView v, int page);
        
        /**
         * error.
         */
        public void onError(PDFView v, String msg);
    }
    
    StatusListener m_listener;
    
    /**
     * The PDFDocument object
     */
    private PDF m_doc = null;
    
    /**
     * file descriptor of the PDF document.
     */
    private AssetFileDescriptor m_descriptor = null;
    
    /**
     * current page number, default is 1.
     */
    private int m_current_page = 0;
    
    private int m_page_count = 0;
       
    /**
     * zoom factor.
     */
    private float m_zoom_factor = 1.0F;
    
    private Point topLeftCorner=new Point();
    
    
    
    /**
     * rotate degree
     */
    private int rotation = 0;
    
    /**
     * system DPI.
     */
    private PointF m_sys_dpi = new PointF();
    
    /**
     * bitmap as cache.
     */
    private Bitmap m_cache_bitmap = null;
        
    /**
     * bitmap configure
     */
    Bitmap.Config m_bitmap_config = Bitmap.Config.ARGB_8888;
    
    ConditionVariable m_updatecache = new ConditionVariable();
    
    public static final int HEIGHT = 745;
    public static final int WIDTH = 595;
    
    /**
     * @see android.view.View#View(android.content.Context)
     */
    public PDFView(Context context) {
        super(context);
        initView();
    }
    
    /**
     * @see android.view.View#View(android.content.Context)
     */
    public PDFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initView();
    }
    
    /**
     * @see android.view.View#View(android.content.Context)
     */
    public PDFView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initView();
    }
    
    /**
     * @return the status listener
     */
    public StatusListener getStatusListener() {
        return m_listener;
    }
    
    /**
     * @param mListener
     *            the m_listener to set
     */
    public void setStatusListener(StatusListener l) {
        m_listener = l;
    }
    
    private void initView() { 
        // initialize configuration
        initConfig();        
    }
    
    private void initConfig() {
        // get system DPI.
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (manager == null) { return; }
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        m_sys_dpi.set(metrics.xdpi, metrics.ydpi);
        
        // bitmap configure
        switch (manager.getDefaultDisplay().getPixelFormat()) {
            case PixelFormat.A_8:
                m_bitmap_config = Bitmap.Config.ALPHA_8;
                break;
            case PixelFormat.RGB_565:
                m_bitmap_config = Bitmap.Config.RGB_565;
                break;
            case PixelFormat.RGBA_4444:
                m_bitmap_config = Bitmap.Config.ARGB_4444;
                break;
            case PixelFormat.RGBA_8888:
                m_bitmap_config = Bitmap.Config.ARGB_8888;
                break;
        }
    }
    
    
    
    /**
     * Open PDF contents from the URI.
     * 
     * @param uri
     */
    public void openUri(Uri uri) {
        // reset
        if (m_doc != null) {
            // TODO: clean up?
            m_doc = null;
        }
        m_current_page = 0;
        // open uri
        try {
            m_descriptor = getContext().getContentResolver().openAssetFileDescriptor(uri, "r");
            if (m_descriptor == null) {
                Log.e(TAG, "File desciptor is null.");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Open file failed.");
            return;
        }
        
        // open document
        m_doc = new PDF(m_descriptor.getFileDescriptor());
        if (m_doc == null) {
            // TODO: report error.
            return;
        }
        m_page_count = m_doc.getPageCount();
        pageChanged();
    }
    
    private void viewChanged(){
    	invalidate();
    }
    
    private void pageChanged() {
    	if (m_cache_bitmap!=null){
    		m_cache_bitmap.recycle();
    	}
    	
    	m_cache_bitmap=renderPage(m_current_page);
    	viewChanged();
    	
        m_listener.onPageChanged(this, m_current_page + 1);
    }
    
    /**
     * Show next page if exist.
     */
    public void nextPage() {
        if (m_current_page<m_page_count-1){
        	m_current_page++;
        	pageChanged();
        }
    }
    
    
    /**
     * Show previous page if exist.
     */
    public void prevPage() {
    	if (m_current_page>0){
        	m_current_page--;
        	pageChanged();
        }
    }
    
    
    /**
     * Set zoom factor.
     * 
     * @param z
     *            the zoom factor, < 0 means fit width.
     */
    public void setZoomFactor(float z) {
        if (Float.compare(m_zoom_factor, z) != 0) {
            m_zoom_factor = z;
            viewChanged();
        }
    }

    /**
     * Get current zoom factor.
     */
    public float getZoomFactor() {
        return m_zoom_factor;
    }
    
    
    private float calculateRealZoomFactor(int page, float normalizedZoom) {
        float zoom;
        if (normalizedZoom <= +0.0F) {
        	// the zoom factor is negative, calculate a zoom factor
        	// such that the whole page is visible
            if (m_doc == null) { return 1.0F; }
            
            // get the page size
            Size size = new Size();
            m_doc.getPageSize(page, size);

            zoom = Math.min((float) ((HEIGHT) * 1.0 / size.height), (float) ((WIDTH) * 1.0 / size.width));
        } else {
            zoom = normalizedZoom;
        }
        return zoom;
    }
    
    

    
    /**
     * Goto the given page.
     * 
     * @param page
     *            the page number.
     */
    public void gotoPage(int page) {
        if (page != m_current_page && page >= 0 && m_doc != null && page < m_page_count) {
            m_current_page = page;
            pageChanged();
        }
    }
    
        
    private Bitmap renderPage(int page) {
    	// get the page size
    	Size pageSize=new Size();
    	m_doc.getPageSize(page, pageSize);
    	
        // render the page to a buffer
        int[] buf = m_doc.renderPage(
        		page, // page number
        		(int) (1.0 * 1000), // zoom factor 
        		0, 0, // top left corner in the page
        		0, // rotation
        		pageSize // size of the output
        		);
        
        // create a new bitmap
    	Bitmap bitmap=Bitmap.createBitmap(pageSize.width,pageSize.height, m_bitmap_config);
    	
    	// create a canvas to render the buffer
    	Canvas canvas=new Canvas(bitmap);
    	
    	// create a paint to render the buffer
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        p.setColorFilter(filter);
        
    	// draw the buffer to the cache bitmap using the canvas and the paint        
        canvas.drawBitmap(buf, 0, pageSize.width, 0, 0, pageSize.width, pageSize.height, true, p);
        
        return bitmap;
    }
    
    /**
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
    	
        // do nothing if no document loaded.
        if (m_doc == null || m_cache_bitmap==null) { return; }
    
        // create a paint to draw the cache
        Paint p = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        p.setColorFilter(filter);
        
        // flush the canvas
        canvas.drawColor(Color.WHITE);
        
        // save the current canvas matrix
        canvas.save();
        
        // setup the translation
        canvas.rotate(getRotation()*90);
        canvas.scale(m_zoom_factor, m_zoom_factor);
        canvas.translate(topLeftCorner.x, topLeftCorner.y);

        // draw the cache
        canvas.drawBitmap(m_cache_bitmap,0,0, p);
        
        // restore the canvas matrix
        canvas.restore();
        /*
        if (m_cache_bitmap_next == null) {
            Runnable run = new Runnable() {
                public void run() {
                    nextPageCache();
                }
            };
            (new Thread(run)).start();
        }*/
    }
    
    public int getPagesCount() {
        return m_page_count;
    }
    
    public int getCurrentPage() {
        return m_current_page;
    }
    
    public void close() {
        m_doc.freeMemory();
    }

	private void setRotation(int rotation) {
		this.rotation = rotation;
	}

	private int getRotation() {
		return rotation;
	}

	public Point getTopLeftCorner() {
		return topLeftCorner;
	}
}
