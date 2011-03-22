package com.googlecode.apdfviewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Rules: - for every visible rectangle, a render result of the whole page is
 * available
 * 
 * @author ruedi
 * 
 */
public class RenderController {

	private final String TAG="RenderController";
	
	/**
	 * Represents a screen rectangle along with the result from rendering it, if available.
	 * 
	 * @author ruedi
	 *
	 */
	private class ScreenRectangleEntry implements Runnable {
		private final ScreenRectangle screenRectangle;
		private boolean parametersChangedWhileRendering;

		@Override
		public String toString() {
			return "Entry "+screenRectangle;
		}
		
		public ScreenRectangleEntry(ScreenRectangle screenRectangle) {
			this.screenRectangle = screenRectangle;
			screenRectangle.OnParametersChanged.AddListener(new OnPropertyChangedListener() {
				@Override
				public void OnPropertyChanged(Object source) {
					// set the render result to obsolete whenever the 
					// parameters of the screen rectangle change
					parametersChangedWhileRendering=true;
				}
			});
		}

		@Override
		public void run() {
			// called when the waiting time is over

			// push to render thread
			//Log.d(TAG,"from wait to queue: "+screenRectangle);
			renderThread.Add(this);

		}

		public void setParametersChangedWhileRendering(	boolean parametersChangedWhileRendering) {
			this.parametersChangedWhileRendering = parametersChangedWhileRendering;
		}

		public boolean isParametersChangedWhileRendering() {
			return parametersChangedWhileRendering;
		}
	}

	private class RenderThread extends Thread {
		// queue holding the entries ready to be rendered ordered by their
		// render priority
		private PriorityBlockingQueue<ScreenRectangleEntry> queue = new PriorityBlockingQueue<ScreenRectangleEntry>(
				5, new Comparator<ScreenRectangleEntry>() {
					@Override
					public int compare(ScreenRectangleEntry a,
							ScreenRectangleEntry b) {
						//Log.d(TAG, "compare a:"+a+" b:"+b);
						Integer aOrdinal = a.screenRectangle
								.getRenderPriority().ordinal();
						Integer bOrdinal = b.screenRectangle
								.getRenderPriority().ordinal();
						return aOrdinal.compareTo(bOrdinal);
					}
				});

		public RenderThread(String title) {
			super(title);
		}

		/**
		 * adds a runnable to the render queue. synchronized
		 * 
		 * @param entry
		 */
		public void Add(ScreenRectangleEntry entry) {
			queue.add(entry);
		}

		/**
		 * removes a runnable from the render queue. synchronized
		 * 
		 * @param entry
		 */
		public boolean Remove(ScreenRectangleEntry entry) {
			return queue.remove(entry);
		}

		/**
		 * takes a runnable from the render queue. synchronized
		 * 
		 * @param runnable
		 */
		private ScreenRectangleEntry take() {
			ScreenRectangleEntry result = null;
			while (result == null) {
				try {
					result = queue.take();
				} catch (InterruptedException e) {
					// ignore exception
				}
			}
			return result;
		}

		@Override
		public void run() {
			//Log.d(TAG,"Entering Render Loop");
			while (true) {
				// take an entry out of the queue
				final ScreenRectangleEntry entry = take();
				
				/*Log.d(TAG,"rendering: "+entry);
				Log.d(TAG,"render size: "+entry.screenRectangle.getRenderedSize());
				Log.d(TAG,"size on page: "+entry.screenRectangle.getSizeOnPage());
				Log.d(TAG,"zoom: "+entry.screenRectangle.getZoom());
				*/
				
				if (pdfDocument==null) continue;
				if (entry.screenRectangle.getRenderedSize().anyZero()) continue;
				if (entry.screenRectangle.getSizeOnPage().anyZero()) continue;
				
				// render the entry
				if (entry.screenRectangle.isRenderResultObsolete()) {
					final RenderResult renderResult=new RenderResult();
					
					doInGuiThread(new Runnable() {
						@Override
						public void run() {
							// extract the parameters
							renderResult.setParametersFromScreenRectangle(entry.screenRectangle);							
							entry.setParametersChangedWhileRendering(false);
						}
					});
					
					
					// render the screen rectangle
					try{
						pdfService.render(renderResult, pdfDocument);
					}
					catch( Throwable t){
						t.printStackTrace();
						continue;
					}
					
					doInGuiThread(new Runnable() {
						
						@Override
						public void run() {
							// set the render result
							entry.screenRectangle.setRenderResult(renderResult);
							entry.screenRectangle.setRenderResultObsolete(entry.isParametersChangedWhileRendering());
						}
					});
							
					
					// propagate the render result to all other visible screen
					// rectangles. Do that in the gui thread
					doInGuiThread(new Runnable() {
						@Override
						public void run() {
							// try to render entries from the newly rendered entry
							//renderEntriesFromNewEntry(entry);

							entry.screenRectangle.OnRedraw.OnPropertyChanged();
							//updateAffectedBitmaps(renderResult);
						}
					});

				
				}
			}
		}
	}

	private void doInGuiThread(final Runnable r){
		
		final Semaphore executionCompleted=new Semaphore(0);
		//Log.d(TAG,"doInGuiThread enter");
		guiThreadHandler.post(new Runnable() {
			@Override
			public void run() {
				
				try{
					//Log.d(TAG,"doInGuiThread start runnable in gui thread");
					// run the runnable in the gui thread
					r.run();
				}
				catch (Throwable t){
					t.printStackTrace();
				}
				finally{
					//Log.d(TAG,"doInGuiThread release");
					// signal that the execution is completed
					executionCompleted.release();
	
				}
				
			}
		});
		
		boolean completed=false;
		while (!completed){
			// wait until the execution is completed
			try {
				executionCompleted.acquire();
				completed=true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Log.d(TAG,"doInGuiThread leave");
		
	}
	
	private LinkedList<ScreenRectangleEntry> entries = new LinkedList<ScreenRectangleEntry>();

	private HandlerThread waitingThread;
	private Handler waitingThreadHandler;

	private RenderThread renderThread;

	private PDFService pdfService;
	private PDFDocument pdfDocument;

	private Handler guiThreadHandler;

	public RenderController(PDFService pdfService) {
		this.pdfService = pdfService;
		guiThreadHandler=new Handler();

		// start the waiting thread
		waitingThread = new HandlerThread("Waiting Thread");
		waitingThread.start();
		waitingThreadHandler = new Handler(waitingThread.getLooper());

		// start the render thread
		renderThread = new RenderThread("Render Thread");
		renderThread.start();
	}

	/**
	 * update visible, immediately rendered entries affected by the new result
	 * @param entryWithNewResult
	 */
	

	

	public void RenderFromExistingResults(ScreenRectangle screenRectangle, Canvas canvas){
		//Log.d(TAG,"RenderEntryFromExistingResults "+screenRectangle);
		
		// check if the entry is not rendered already
		// otherwise, the bitmap would be set from the render result
		if (!screenRectangle.isRenderResultObsolete()) return;
		
		ArrayList<ScreenRectangleEntry> intersecting=new ArrayList<ScreenRectangleEntry>();
		
		// get all intersecting rectangles
		for (ScreenRectangleEntry entry2 : entries) {
			// skip if there is no render result
			if (entry2.screenRectangle.getRenderResult()==null) continue;
			
			// skip if there is no intersection on the page
			if (!RectF.intersects(screenRectangle.getPageRect(),entry2.screenRectangle.getRenderResult().getPageRect())) continue;
			if (screenRectangle.getPageNr()!=entry2.screenRectangle.getRenderResult().getPageNr()) continue;
			
			intersecting.add(entry2);
		}
		
		// sort by increasing zoom factors
		Collections.sort(intersecting,new Comparator<ScreenRectangleEntry>() {

			@Override
			public int compare(ScreenRectangleEntry a, ScreenRectangleEntry b) {
				return Float.compare(a.screenRectangle.getRenderResult().getZoom(), b.screenRectangle.getRenderResult().getZoom());
			}
		});
		
		// TODO: filter out rectangles which do not contribute to the result 
		
		// render the bitmap		
		// get all intersecting rectangles
		for (ScreenRectangleEntry entry2 : intersecting) {
			//pdfService.RenderFromResultToRectangle(entry2.screenRectangle.getRenderResult(), screenRectangle, canvas);
		}
		
		if (screenRectangle.getRenderResult()!=null){
			pdfService.RenderFromResultToRectangle(screenRectangle.getRenderResult(), screenRectangle, canvas);
		}
	}
	
	public void setPdfDocument(PDFDocument pdfDocument) {
		//Log.d(TAG,"setPdfDocument: "+pdfDocument);
		this.pdfDocument = pdfDocument;
		
		// clear all render results
		for (ScreenRectangleEntry entry: entries){
			entry.screenRectangle.setRenderResult(null);
			entry.screenRectangle.setRenderResultObsolete(true);
		}
	}

	public PDFDocument getPdfDocument() {
		return pdfDocument;
	}

	public void addScreenRectangle(ScreenRectangle rectangle) {
		//Log.d(TAG,"addScreenRectangle: "+rectangle);
		final ScreenRectangleEntry entry = new ScreenRectangleEntry(rectangle);
		entries.add(entry);	
		rectangle.OnParametersChanged.AddListener(new OnPropertyChangedListener() {
			
			@Override
			public void OnPropertyChanged(Object source) {
				entryParametersChanged(entry);
			}	
		});
		waitingThreadHandler.postDelayed(entry, rectangle.getRenderDelay());
	}
	
	private void entryParametersChanged(ScreenRectangleEntry entry) {
		//Log.d(TAG,"entryParametersChanged: "+entry.screenRectangle);
		// reset state
		waitingThreadHandler.removeCallbacks(entry);
		renderThread.Remove(entry);
		
		if (!tryToReuseResults(entry)){
			waitingThreadHandler.postDelayed(entry, entry.screenRectangle.getRenderDelay());
		}
	}
	
	private boolean tryToReuseResults(ScreenRectangleEntry entry) {
		ScreenRectangle rectangle=entry.screenRectangle;
		for (ScreenRectangleEntry entry2: entries){
			if (entry2==entry) continue;
			RenderResult result2=entry2.screenRectangle.getRenderResult();
			if (result2==null) continue;
			
			// check if result is equal
			if (result2.getPageNr()!=rectangle.getPageNr()) continue;
			if (result2.getZoom()!=rectangle.getZoom()) continue;
			if (result2.getPageOrigin().getX()!=rectangle.getPageOrigin().getX()) continue;
			if (result2.getPageOrigin().getY()!=rectangle.getPageOrigin().getY()) continue;
			if (result2.getSizeOnPage().getWidth()!=rectangle.getSizeOnPage().getWidth()) continue;
			if (result2.getSizeOnPage().getHeight()!=rectangle.getSizeOnPage().getHeight()) continue;
		
			// result is equal
			rectangle.setRenderResult(result2);
			rectangle.setRenderResultObsolete(false);
			rectangle.OnRedraw.OnPropertyChanged();
			return true;
		}
		return false;
	}

	public void removeScreenRectangle(ScreenRectangle pop) {
		//Log.d(TAG,"removeScreenRectangle: "+pop);
		// find entry
		ScreenRectangleEntry foundEntry=findEntryByScreenRectangle(pop);
		
		// remove entry from everywhere
		entries.remove(foundEntry);
		waitingThreadHandler.removeCallbacks(foundEntry);
		renderThread.Remove(foundEntry);
	}

	private ScreenRectangleEntry findEntryByScreenRectangle(ScreenRectangle pop) {
		ScreenRectangleEntry foundEntry=null;
		for (ScreenRectangleEntry entry: entries){
			if (entry.screenRectangle==pop) foundEntry=entry;
		}
		return foundEntry;
	}

}
