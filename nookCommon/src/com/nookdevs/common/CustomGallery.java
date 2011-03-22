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

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class CustomGallery extends Gallery {
    
    float m_Alpha = 1.0f;
    float m_Size = 1.25f;
    
    public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public CustomGallery(Context context) {
        super(context);
    }
    
    public CustomGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setSize(float size) {
        m_Size = size;
    }
    
    @Override
    public void setUnselectedAlpha(float alpha) {
        m_Alpha = alpha;
        super.setUnselectedAlpha(alpha);
    }
    
    @Override
    public boolean getChildStaticTransformation(View child, Transformation t) {
        View view = getSelectedView();
        if (view != null && !view.equals(child)) {
            t.clear();
            t.setAlpha(m_Alpha);
            t.setTransformationType(Transformation.TYPE_BOTH);
//            int idx = this.getSelectedItemPosition();
//            int idxc = this.getPositionForView(child);
//            Matrix mat = t.getMatrix();
//            if( idxc < idx)
//                mat.postTranslate(-29,0);
        } else {
            t.clear();
            t.setAlpha(m_Alpha);
            t.setTransformationType(Transformation.TYPE_BOTH);
            Matrix mat = t.getMatrix();
            Matrix mat1 = new Matrix();
            int centerx, centery;
            centery = child.getTop() + child.getMeasuredHeight() / 2;
            centerx = child.getLeft() + child.getMeasuredWidth() / 2;
            mat.postTranslate(-centerx, -centery);
            mat1.postScale(m_Size, m_Size);
            mat.postConcat(mat1);
            mat1 = new Matrix();
            mat1.postTranslate(centerx, centery);
            mat.postConcat(mat1);
            mat.postTranslate(50, 5);
      
        }
        return true;
        // return false;
    }
    
}
