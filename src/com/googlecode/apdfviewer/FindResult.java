/* 
 * Copyright 2010 nookDevs
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
//Code from apv project in google projects and modified for nook.
package com.googlecode.apdfviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Rect;
import android.util.Log;

/**
 * Find result.
 */
public class FindResult {
    
    /**
     * Logging tag.
     */
    public static final String TAG = "cx.hell.android.pdfview";
    
    /**
     * Page number.
     */
    public int page;
    
    /**
     * List of rects that mark find result occurences. In page dimensions (not
     * scalled).
     */
    public List<Rect> markers;
    
    /**
     * Add marker.
     */
    public void addMarker(int x0, int y0, int x1, int y1) {
        if (x0 >= x1) { throw new IllegalArgumentException("x0 must be smaller than x1: " + x0 + ", " + x1); }
        if (y0 >= y1) { throw new IllegalArgumentException("y0 must be smaller than y1: " + y0 + ", " + y1); }
        if (markers == null) {
            markers = new ArrayList<Rect>();
        }
        Rect nr = new Rect(x0, y0, x1, y1);
        if (markers.isEmpty()) {
            markers.add(nr);
        } else {
            markers.get(0).union(nr);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("FindResult(");
        if (markers == null || markers.isEmpty()) {
            b.append("no markers");
        } else {
            Iterator<Rect> i = markers.iterator();
            Rect r = null;
            while (i.hasNext()) {
                r = i.next();
                b.append(r);
                if (i.hasNext()) {
                    b.append(", ");
                }
            }
        }
        b.append(")");
        return b.toString();
    }
    
    @Override
    public void finalize() {
        Log.i(TAG, this + ".finalize()");
    }
}
