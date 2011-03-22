package com.bravo.util;

import android.util.Log;

public class AdobeNativeInterface {
    static {
        try {
            System.loadLibrary("pdfhost");
        } catch(Exception ex) {
            Log.w("AdobeNativeInterface", ex);
        }
    }
    
    public static native void cancelProcessing();
    
    public static synchronized native String getMetaData(String param);
    
    public static synchronized native int openPDF(String name);
    
    public static synchronized native int closePDF();

    public static synchronized native void cursorDown();
    
    public static synchronized native void cursorLeft();
    
    public static synchronized native void cursorRight();
    
    public static synchronized native void cursorUp();
    
    public static synchronized native void endAnnotation();
    
    public static synchronized native boolean findNextText(String s);
    
    public static synchronized native boolean findPreviousText(String s);
    
    public static synchronized native String[] getAnnotationEndLocation();

    public static synchronized native String[] getAnnotationStartLocation();

    public static synchronized native String[] getBeginning();
    
    public static synchronized native void getBoundingBox(double[] bounds);
    
    public static synchronized native double getBoundingBoxY(String [] s1, String[] s2);
    
    public static synchronized native String[] getChapterList();
    
    public static synchronized native double getChapterPagePositionFromIndex(int chapter);
    
    public static synchronized native void getCurrentHighlightedPage(int [] val);
    
    public static synchronized native String[] getCurrentLocation();
    
    public static synchronized native void getCurrentMonoPage(byte[] val);

    public static synchronized native void getCurrentPage(int[] val);
    
    public static synchronized native double getCurrentPagePosition();

    public static synchronized native int getCurrentPageWithLinks(int[] val, int v2);

    public static synchronized native String[] getEnd();
    
    public static synchronized native String[] getHash();
    
    public static synchronized native String[] getHighlightLocation();
    
    public static synchronized native String [] getHighlightedWord();
    
    public static synchronized native void getLinkBoundingBox(int i, double[] d);
    
    public static synchronized native int getLinkCount();
    
    public static synchronized native String[] getMetaData(String[] keys_);
    
    public static synchronized native boolean getNextPage();
    
    public static synchronized native int getNumChapters();
    
    public static synchronized native int getNumPages();
    
    public static synchronized native void getPage(int idx, int[] data);
    

public static synchronized native double getPagePosition(String[] val);

public static synchronized native int getPageWithLinks(int idx, int[] data);

public static synchronized native boolean getPreviousPage();

public static synchronized native String[] getScreenEnd();

public static synchronized native  String [] getScreenStart();

public static synchronized native  void goToAnnotation(String[] s1, String[] s2);

public static synchronized native  void goToChapterIndex(int I);

public static synchronized native  void goToLocation(String[] s);

public static synchronized native  void highlightCurrentWord();

public static synchronized native  void highlightLink(int I);


public static synchronized native  void highlightSelection(String[] s1, String[] s2);

public static synchronized native  void initAnnotation();


public static synchronized native  void removeAnnotationHighlight();

public static synchronized native  void removeResetHighlight();

public static synchronized native  void resetFindText();

public static synchronized native  void selectLink(int I);

public static synchronized native  void setFontSize(int I);

public static synchronized native  void setFontStyleSheet(String fontStyle);

public static synchronized native  int setHash(String[] hash);

public static synchronized native  int setPassHash(String[]s1, String[] s2);

public static synchronized native  void setViewportSize(int x, int y);

public static synchronized native  void startAnnotation();

public static synchronized native int openEpib(String s);
public static synchronized native void setVideo(String s, int i1,int i2, int i3, int i4, int i5);

}
