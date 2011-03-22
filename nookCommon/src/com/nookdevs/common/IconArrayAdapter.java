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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconArrayAdapter<E> extends ArrayAdapter<E> {
    int[] m_Icons;
    int m_TextFieldId;
    int m_ImageFieldId;
    int m_ListItemId;
    int m_SubTextFieldId = -1;
    private TextView[] m_SubTextFields = null;
    private String[] m_SubTextValues = null;
    private boolean[] m_EnableFields;
    
    public IconArrayAdapter(Context context, int textViewResourceId, List<E> objects, int[] icons) {
        super(context, textViewResourceId, objects);
        m_ListItemId = textViewResourceId;
        m_Icons = icons;
        m_EnableFields = new boolean[objects.size()];
        java.util.Arrays.fill(m_EnableFields, true);
    }
    
    public void setIcons(int[] icons) {
        m_Icons = icons;
    }
    
    public void setImageField(int id) {
        m_ImageFieldId = id;
    }
    
    public void setTextField(int id) {
        m_TextFieldId = id;
    }
    
    public void setSubTextField(int id) {
        m_SubTextFieldId = id;
        m_SubTextFields = new TextView[getCount()];
        m_SubTextValues = new String[getCount()];
    }
    
    public void setSubText(int idx, String val) {
        if (m_SubTextFields[idx] != null) {
            m_SubTextFields[idx].setText(val);
        }
        m_SubTextValues[idx] = val;
    }
    
    public String getSubText(int id) {
        return m_SubTextValues[id];
    }
    
    public void setEnabled(int idx, boolean val) {
        m_EnableFields[idx] = val;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(m_ListItemId, parent, false);
        } else {
            row = convertView;
        }
        TextView label = (TextView) row.findViewById(m_TextFieldId);
        label.setText(getItem(position).toString());
        ImageView icon = (ImageView) row.findViewById(m_ImageFieldId);
        if (m_Icons.length > position && m_Icons[position] != -1) {
            icon.setImageResource(m_Icons[position]);
        } else {
            icon.setImageDrawable(null);
        }
        if (m_SubTextFieldId != -1) {
            TextView sub = (TextView) row.findViewById(m_SubTextFieldId);
            String val = m_SubTextValues[position];
            if (val != null) {
                sub.setText(val);
            } else {
                sub.setText(" ");
            }
            try {
                int oldidx = Integer.valueOf((String) sub.getHint());
                m_SubTextFields[oldidx] = null;
            } catch (Exception ex) {
            }
            sub.setHint(String.valueOf(position));
            m_SubTextFields[position] = sub;
        }
        if (m_EnableFields.length > position && !m_EnableFields[position]) {
            row.setVisibility(View.INVISIBLE);
        } else {
            row.setVisibility(View.VISIBLE);
        }
        return (row);
    }
    
}
