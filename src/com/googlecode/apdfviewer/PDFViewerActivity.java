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
/**
 * Modified the apdfviewer to customize it for nook. - Hari.
 */
package com.googlecode.apdfviewer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.googlecode.apdfviewer.PDFController.ViewEnum;
import com.nookdevs.common.nookBaseActivity;

/**
 * @author Li Wenhao
 */
public class PDFViewerActivity extends nookBaseActivity {
	public static final int E_INK_WIDTH = 600;
	public static final int E_INK_HEIGHT = 760;
	
	public static final int TOUCH_PAD_WIDTH = 488;
	public static final int TOUCH_PAD_HEIGHT = 144;
	
	public abstract class ViewUpdateRequest{
		public abstract void Execute();
	}
	
	private static final String TAG = "PDFViewerActivity";
	
	PDFController controller;
	private BitmapDrawingView eInkImageView;
	private BitmapDrawingView touchPadImageView;
	private ViewAnimator viewAnimator;
	private ImageButton optionsButton;
	private ToggleButton zoomButton;
	private CheckBox flipPagesCheckBox;
	
	private SeekBar seekPageSeekBar;
	private TextView seekPageMessage;
	
	private GestureDetector gestureDetector;
	private Orientation orientation=Orientation.TOP_BOTTOM;
	private boolean zoomMode;
	
	protected void test() {
      
        try {
            Cursor c =
                getContentResolver().query(Uri.parse(READING_NOW_URL), null, null, null, null);
            System.out.println("column names:");
            for (String name: c.getColumnNames()){
            	System.out.println(name);
            }
            System.out.println("Data:");
            if (c.moveToFirst()){
	            do{
	            	for (int i=0; i<c.getColumnCount(); i++){
	            		System.out.print(c.getBlob(i));
	            		System.out.print("|");
	            	}
	            	System.out.println();
	            } while (c.moveToNext());
            }
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test();
        
        // set the main view
        setContentView(R.layout.main);

        // create controller
        controller=new PDFController(this,getApplicationContext());
        
        // initialize the view
        initView();
        
        // update the screen sizes ont the controller
        writeRectangleSizesToController();
        setOrientation(orientation);
        
        // read the initial view from the controller
        updateShownView();
        
        // register update handlers
        registerUpdateHandlers();
        
        // retrieve the intent and open the file
        OpenByIntent(getIntent());
        Log.d(TAG,"onCreate done");
    }

    private void registerUpdateHandlers() {
		controller.geteInkRectangle().OnRedraw.AddListener(new OnPropertyChangedListener() {
			@Override
			public void OnPropertyChanged(Object source) {
				Log.d(TAG,"Invalidate EInkImageView");
				eInkImageView.invalidate();
			}
		});
		
		controller.getTouchPadRectangle().OnRedraw.AddListener(new OnPropertyChangedListener() {
			@Override
			public void OnPropertyChanged(Object source) {
				Log.d(TAG,"Invalidate TouchPadImageView");
				touchPadImageView.invalidate();
			}
		});
	}

	/**
     * Listen for new intents, to change the displayed pdf
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
    	OpenByIntent(intent);
    };

	private void OpenByIntent(Intent intent) {
		// filter intents
		Log.d(TAG, "Intent action: <"+intent.getAction()+">");
		if (!"com.bravo.intent.action.VIEW".equals(intent.getAction())) return;
		
        
        if (intent.getData() == null) {
        	Log.d(TAG,"Intent data was null");
        	return; }
        Log.d(TAG, "Intent data: "+intent.getData());
        

        // extract the URI to open
        Uri uri = intent.getData();
        
        // open uri
        controller.Open(uri);
        Log.d(TAG,"OpenByIntent done");
	}
    
    

    private void initView(){
    	Log.d(TAG,"initView");
    	// get the views
        eInkImageView = (BitmapDrawingView) findViewById(R.id.eInkImageView);
        touchPadImageView = (BitmapDrawingView) findViewById(R.id.touchPadImageView);
        viewAnimator = (ViewAnimator) findViewById(R.id.viewanim);
        zoomButton=(ToggleButton) findViewById(R.id.toggleButtonZoom);
        
        eInkImageView.setRectangle(controller.geteInkRectangle());
        eInkImageView.setRenderController(controller.getRenderController());
        touchPadImageView.setRectangle(controller.getTouchPadRectangle());
        touchPadImageView.setRenderController(controller.getRenderController());
        eInkImageView.setSize(new Size(PDFViewerActivity.E_INK_WIDTH,PDFViewerActivity.E_INK_HEIGHT));
        touchPadImageView.setSize(new Size(PDFViewerActivity.TOUCH_PAD_WIDTH,PDFViewerActivity.TOUCH_PAD_HEIGHT));

        gestureDetector=new GestureDetector(new GestureListener(this,controller));
        
        touchPadImageView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return gestureDetector.onTouchEvent(arg1);
			}
		});
        
        ImageButton imageButton=(ImageButton) findViewById(R.id.imageButtonLR);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setOrientation(Orientation.LEFT_RIGHT);
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
        imageButton=(ImageButton) findViewById(R.id.imageButtonRL);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setOrientation(Orientation.RIGHT_LEFT);
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
        imageButton=(ImageButton) findViewById(R.id.imageButtonTB);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setOrientation(Orientation.TOP_BOTTOM);
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
        imageButton=(ImageButton) findViewById(R.id.optionsButton);
        optionsButton=imageButton;
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.setCurrentView(ViewEnum.Options);
			}
		});
        
        zoomButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				zoomMode=zoomButton.isChecked();
			}
		});
        
        flipPagesCheckBox=(CheckBox) findViewById(R.id.checkBoxFlipPages);
        flipPagesCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				controller.setFlipPages(flipPagesCheckBox.isChecked());
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
        Button button= (Button) findViewById(R.id.buttonGotoPage);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (controller.getDocument()==null) return;
				seekPageSeekBar.setMax(controller.getDocument().getPageCount()-1);
				seekPageSeekBar.setProgress(controller.getTouchPadRectangle().getPageNr()-1);
				controller.setCurrentView(ViewEnum.SeekPage);
			}
		});
        
        imageButton= (ImageButton) findViewById(R.id.page_picker_close);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
        seekPageMessage=(TextView) findViewById(R.id.page_picker_message);
        updateSeekPageMessage();
        
        seekPageSeekBar=(SeekBar) findViewById(R.id.page_picker_seeker);
        seekPageSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				controller.setPageNr(seekPageSeekBar.getProgress()+1);
				updateSeekPageMessage();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				controller.setPageNr(seekPageSeekBar.getProgress()+1);
				updateSeekPageMessage();
			}
		});    
        
        imageButton= (ImageButton) findViewById(R.id.page_picker_plus);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				seekPageSeekBar.incrementProgressBy(1);
			}
		});
        
        imageButton= (ImageButton) findViewById(R.id.page_picker_minus);
        imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				seekPageSeekBar.incrementProgressBy(-1);
			}
		});

        button= (Button) findViewById(R.id.buttonClearBookmarks);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.setCurrentView(ViewEnum.Confirm);
			}
		});
        
        button= (Button) findViewById(R.id.buttonYes);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.clearBookmarks();
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
    
        button= (Button) findViewById(R.id.buttonNo);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				controller.setCurrentView(ViewEnum.Normal);
			}
		});
        
                
    }
    
    protected void updateSeekPageMessage() {
    	if (controller.getDocument()==null) return;
    	String msg=String.valueOf(controller.getTouchPadRectangle().getPageNr()+1);
    	msg+="/";
    	msg+=controller.getDocument().getPageCount();
		seekPageMessage.setText(msg);
	}

	@Override
    public void onResume() {
        super.onResume();
        //updateTitle(NAME);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        switch (keyCode) {
            case NOOK_PAGE_UP_KEY_LEFT:
            case NOOK_PAGE_UP_KEY_RIGHT:
                controller.pageUp();
                handled = true;
                break;

            case NOOK_PAGE_DOWN_KEY_LEFT:
            case NOOK_PAGE_DOWN_KEY_RIGHT:
                controller.pageDown();
                handled = true;
                break;
            case NOOK_PAGE_UP_SWIPE:
                controller.pageUp();
                handled = true;
                break;
            case NOOK_PAGE_DOWN_SWIPE:
                controller.pageDown();
                handled = true;
                break;
            default:
                break;
        }
        return handled;
    }

    private void hadlePageUpCommand(){
    	// switch commands if in right-left mode
    	if (getOrientation()==Orientation.RIGHT_LEFT){
    		controller.pageDown();
    	}
    	else{
    		controller.pageUp();
    	}
    }
    
    private void handlePageDownCommand(){
    	// switch commands if in right-left mode
    	if (getOrientation()==Orientation.RIGHT_LEFT){
    		controller.pageUp();
    	}
    	else{
    		controller.pageDown();
    	}
    }
    
	
	public void updateShownView() {
		int viewId;
		// get the id of the view to show
		switch(controller.getCurrentView()){
			case Normal: viewId=R.id.normalView; break;
			case Options: viewId=R.id.optionsView; break;
			case SeekPage: viewId=R.id.seekPageView; break;
			case Confirm: viewId=R.id.confirmView; break;
			default:
				Log.e(TAG,"update shown view: Enum member not known not known");
				return;
		}
		
		// retrieve the view, to compare against later
		View newView=findViewById(viewId);
		
		// iterate through all views and display the one to be shown
		for (int i=0; i<viewAnimator.getChildCount(); i++){
			if (viewAnimator.getChildAt(i)==newView){
				viewAnimator.setDisplayedChild(i);
				break;
			}
		}
	}

	public void setZoomMode(boolean b) {
		zoomButton.setChecked(b);
	}

	public void setOrientation(Orientation newOrientation) {
		// set the zoom such that the old visible page rectangle width
		// is equal to the new visible page rectangle width
		float zoom=controller.getTouchPadRectangle().getZoom();
		if (newOrientation!=Orientation.TOP_BOTTOM && orientation==Orientation.TOP_BOTTOM){
			controller.getTouchPadRectangle().setZoom(zoom*E_INK_WIDTH/TOUCH_PAD_WIDTH);
		}
		if (newOrientation==Orientation.TOP_BOTTOM && orientation!=Orientation.TOP_BOTTOM){
			controller.getTouchPadRectangle().setZoom(zoom*TOUCH_PAD_WIDTH/E_INK_WIDTH);
		}
		
		// apply the new orientation
		this.orientation = newOrientation;
		eInkImageView.setOrientation(newOrientation);
		touchPadImageView.setOrientation(newOrientation);
		writeRectangleSizesToController();
	}

	private void writeRectangleSizesToController() {
		//Log.d(TAG,"writeRectangleSizesToController");
		switch (orientation){
			case LEFT_RIGHT:
			case RIGHT_LEFT:
				
				Size touchPadSize = new Size(TOUCH_PAD_HEIGHT,TOUCH_PAD_WIDTH);
				//Log.d(TAG, "horizontal: "+touchPadSize.toString());
				controller.geteInkRectangle().getRenderedSize().set(
						new Size(E_INK_HEIGHT,E_INK_WIDTH));
				controller.getTouchPadRectangle().getRenderedSize().set(
						touchPadSize);				
			break;
			default:
				Size tpsize = new Size(TOUCH_PAD_WIDTH,TOUCH_PAD_HEIGHT);
				//Log.d(TAG, "default: "+tpsize.toString());
				controller.geteInkRectangle().getRenderedSize().set(
						new Size(E_INK_WIDTH,E_INK_HEIGHT));
				controller.getTouchPadRectangle().getRenderedSize().set(
						tpsize);
			break;
		}
		Log.d(TAG,"touchPad "+controller.getTouchPadRectangle().getRenderedSize());
		Log.d(TAG,"eink "+controller.geteInkRectangle().getRenderedSize());
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public boolean isZoomMode() {
		return zoomMode;
	}

}


