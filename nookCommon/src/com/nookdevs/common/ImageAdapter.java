/* 
 * Copyright 2010 nookDevs
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.nookdevs.common;

import java.io.File;
import java.util.Vector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class ImageAdapter extends BaseAdapter implements SpinnerAdapter {
    
    Vector<Object> m_AppIcons;
    int[] m_Resources;
    Drawable m_CurrentImage;
    Context m_Context;
    int m_GalleryItemBackground;
    int m_Default;
    
    public ImageAdapter(Context context, Vector<Object> icons, int[] resources) {
        super();
        m_Context = context;
        if (icons == null) {
            m_AppIcons = new Vector<Object>();
        } else {
            m_AppIcons = icons;
        }
        if (resources == null) {
            m_Resources = new int[0];
        } else {
            m_Resources = resources;
        }
        
    }
    
    public void setDefault(int res) {
        m_Default = res;
    }
    
    public void setBackgroundStyle(int id) {
        m_GalleryItemBackground = id;
    }
    
    public void setCurrentImage(Drawable img) {
        m_CurrentImage = img;
    }
    
    public void setImageUrls(Vector images) {
        if (images != null) {
            m_AppIcons = images;
        }
    }
    
    public int getCount() {
        if (m_CurrentImage == null) {
            return m_AppIcons.size() + m_Resources.length;
        } else {
            return m_AppIcons.size() + m_Resources.length + 1;
        }
    }
    
    public Object getItem(int id) {
        return id;
    }
    
    public long getItemId(int id) {
        return id;
    }
    
    public String getImageUri(int pos) {
        int curr = (m_CurrentImage != null) ? 1 : 0;
        if (curr ==1 && pos == 0) { return null; }
        if (pos < m_Resources.length+curr) { return null; }
        return (String)m_AppIcons.get(pos - m_Resources.length - curr);
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView i = null;
        if (convertView == null || !(convertView instanceof ImageView)) {
            i = new ImageView(m_Context);
        } else {
            i = (ImageView) convertView;
        }
        int curr = (m_CurrentImage != null) ? 1 : 0;
        if (position == 0 && m_CurrentImage != null) {
            i.setImageDrawable(m_CurrentImage);
        } else if (position >= 0 && position < m_Resources.length+curr) {
            i.setImageResource(m_Resources[position - curr]);
        } else if (position - m_Resources.length - curr < m_AppIcons.size()) {
            if (m_AppIcons.get(position - m_Resources.length - curr) == null) {
                i.setImageResource(m_Default);
            } else {
                try {
                    Object imgUri = m_AppIcons.get(position - m_Resources.length - curr);
                    if( imgUri instanceof Integer) {
                        i.setImageResource( (Integer)imgUri);
                    } else
                        i.setImageURI(Uri.parse((String)(m_AppIcons.get(position - m_Resources.length - curr))));
                } catch( Throwable ex) {
                    i.setImageResource(m_Default);
                }
            }
        }
        /* Image should be scaled as width/height are set. */
        i.setScaleType(ImageView.ScaleType.FIT_XY);
        /* Set the Width/Height of the ImageView. */
        i.setLayoutParams(new Gallery.LayoutParams(77, 115)); // 0.8 of 96x144.
        //i.setLayoutParams(new Gallery.LayoutParams(96, 144));
        i.setDrawingCacheEnabled(true);
        i.setDrawingCacheQuality(1);
     //   i.setBackgroundResource(m_GalleryItemBackground);
        return i;
    }
    
}
