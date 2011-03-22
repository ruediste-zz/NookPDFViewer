package com.googlecode.apdfviewer;

import java.io.FileNotFoundException;


import android.content.ContentResolver;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;

public class PDFDocument {
	final static String TAG = "PDFDocument";
	private PDF pdf;
	private String location;
	
	/**
    * Open PDF contents from the URI.
    * 
    * @param uri
	 * @throws Exception 
    */
   public PDFDocument(ContentResolver resolver, Uri uri) throws Exception {
	   AssetFileDescriptor descriptor;
	   this.location=uri.toString();
	   
       // open uri
       try {
    	   descriptor = resolver.openAssetFileDescriptor(uri, "r");
       } catch (FileNotFoundException e) {
           Log.e(TAG, "Open file failed.");
           throw e;
       }
       
       if (descriptor == null) {
           Log.e(TAG, "File desciptor is null.");
           throw new Exception("unable to load descriptor");
       }
       
       // open document
       pdf=new PDF(descriptor.getFileDescriptor());
   }

	public PDF getPdf() {
		return pdf;
	}

	public int getPageCount() {
		return pdf.getPageCount();
	}

	public Size getPageSize(int pageNr) {
		// get the page size
    	PDF.Size pageSize=new PDF.Size();
    	pdf.getPageSize(pageNr, pageSize);

    	// render the whole page
        return new Size(pageSize.width,pageSize.height);
	}

	public String getLocation() {
		return location;
	}
}
