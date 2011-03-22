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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

public class nookBaseActivity extends Activity {
    
    public static final int SOFT_KEYBOARD_CLEAR = -13;
    public static final int SOFT_KEYBOARD_SUBMIT = -8;
    public static final int SOFT_KEYBOARD_CANCEL = -3;
    public static final int SOFT_KEYBOARD_DOWN_KEY = 20;
    public static final int SOFT_KEYBOARD_UP_KEY = 19;
    protected static final int NOOK_PAGE_UP_KEY_RIGHT = 98;
    protected static final int NOOK_PAGE_DOWN_KEY_RIGHT = 97;
    protected static final int NOOK_PAGE_UP_KEY_LEFT = 96;
    protected static final int NOOK_PAGE_DOWN_KEY_LEFT = 95;
    protected static final int NOOK_PAGE_DOWN_SWIPE = 100;
    protected static final int NOOK_PAGE_UP_SWIPE = 101;
    
    PowerManager.WakeLock screenLock = null;
    boolean m_AirplaneMode = false;
    long m_ScreenSaverDelay = 600000;
    String m_WallPaper = null;
    AlertDialog m_AlertDialog = null;
    public static final String SDFOLDER = "/system/media/sdcard/";
    public static final String EXTERNAL_SDFOLDER = "/sdcard";
    public final static String UPDATE_TITLE = "com.bravo.intent.UPDATE_TITLE";
    public final static String UPDATE_STATUSBAR = "com.bravo.intent.UPDATE_STATUSBAR";
    
    public final static String STATUSBAR_ICON = "Statusbar.icon";
    public final static String STATUSBAR_ACTION = "Statusbar.action";
    public static final String READING_NOW_URL = "content://com.ereader.android/last";
    protected boolean m_FirstTime = true;
    protected String m_Version;
    protected String m_DeviceName ="";
    
    protected String getWallpaperFile() {
        return m_WallPaper;
    }
    
    protected boolean getAirplaneMode() {
        return m_AirplaneMode;
    }
    
    protected long getScreenSaverDelay() {
        return m_ScreenSaverDelay;
    }
    
    protected static String LOGTAG = "nookActivity";
    protected static String NAME =null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager power = (PowerManager) getSystemService(POWER_SERVICE);
        screenLock = power.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "nookactivity" + hashCode());
        screenLock.setReferenceCounted(false);
        readSettings();
        PackageManager manager = getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            m_Version = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            m_Version = "";
        }
        if( NAME == null) updateTitle(m_DeviceName);
        else
            updateTitle(NAME);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        try {
            if (screenLock != null) {
                screenLock.release();
            }
        } catch (Exception ex) {
            Log.e(LOGTAG, "exception in onPause - ", ex);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (!m_FirstTime) {
            readSettings();
            closeAlert();
        }
        if (screenLock != null) {
            screenLock.acquire(m_ScreenSaverDelay);
        }
        m_FirstTime = false;
        if( NAME == null) { 
            updateTitle(m_DeviceName);
        } else {
            updateTitle(NAME + " " + m_Version);
        }
    }
    
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (screenLock != null) {
            screenLock.acquire(m_ScreenSaverDelay);
        }
    }
    
    protected void goHome() {
        String action = "android.intent.action.MAIN";
        String category = "android.intent.category.HOME";
        Intent intent = new Intent();
        intent.setAction(action);
        intent.addCategory(category);
        startActivity(intent);
    }
    
    protected void goBack() {
        try {
            Intent intent = new Intent();
            if (getCallingActivity() != null) {
                intent.setComponent(getCallingActivity());
                startActivity(intent);
            } else {
                goHome();
            }
        } catch (Exception ex) {
            goHome();
        }
    }
    
    public void closeAlert() {
        if (m_AlertDialog != null) {
            m_AlertDialog.dismiss();
        }
    }
    protected Intent getReadingNow() {
        Intent intent = null;
        try {
            Cursor c =
                getContentResolver().query(Uri.parse(READING_NOW_URL), null, null, null, null);
            if (c != null) {
                c.moveToFirst();
                byte[] data = c.getBlob(0);
                c.close();
                c.deactivate();
                if (data == null) { return null; }
                DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));
                intent = new Intent();
                String tmp = din.readUTF();
                intent.setAction(tmp);
                tmp = din.readUTF();
                String tmp1 = din.readUTF();
                if (tmp != null && tmp.length() > 0) {
                    Uri uri = Uri.parse(tmp);
                    if (tmp1 != null && tmp1.length() > 0) {
                        intent.setDataAndType(uri, tmp1);
                    } else {
                        intent.setData(uri);
                    }
                }
                byte b = din.readByte();
                if (b > 0) {
                    tmp = din.readUTF();
                    tmp1 = din.readUTF();
                    intent.putExtra(tmp, tmp1);
                }
            }
        } catch (Exception ex) {
            intent = null;
        }
        return intent;
    }
    protected void updateReadingNow(Intent intent) {
        try {
            ContentValues values = new ContentValues();
            ByteArrayOutputStream aout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(aout);
            dout.writeUTF(intent.getAction());
            dout.writeUTF(intent.getDataString());
            String tmp = intent.getDataString();
            int idx = tmp.indexOf('?');
            if (idx != -1) tmp = tmp.substring(0, idx);
            File f = new File(tmp.substring(6));
            f.setLastModified(System.currentTimeMillis());
            dout.writeUTF(intent.getType());
            dout.writeByte(1);
            dout.writeUTF("BN/Bravo_PATH");
            dout.writeUTF(tmp);
            byte[] data = aout.toByteArray();
            dout.close();
            values.put("data", data);
            getContentResolver().insert(Uri.parse(READING_NOW_URL), values);
        } catch (Exception ex) {
            Log.e(LOGTAG, "Exception while updating reading now data - ", ex);
        }
    }
    
    public void displayAlert(String title, String msg, final int type, AlertDialog.OnClickListener listener,
        int drawable) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        if (type == 1) {
            builder.setNegativeButton(android.R.string.cancel, listener).setCancelable(true);
            if (drawable != -1) {
                builder.setIcon(drawable);
            }
        } else if (type == 2 || type == 3) {
            builder.setPositiveButton(android.R.string.ok, listener);
            if (drawable != -1) {
                builder.setIcon(drawable);
            }
        }
        m_AlertDialog = builder.show();
    }
    
    protected void updateTitle(String title) {
        try {
            Intent intent = new Intent(UPDATE_TITLE);
            String key = "apptitle";
            intent.putExtra(key, title);
            sendBroadcast(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected void showPageNumber(int curpage, int maxpage) {
        Intent msg = new Intent(UPDATE_STATUSBAR);
        msg.putExtra(STATUSBAR_ICON, 7);
        msg.putExtra(STATUSBAR_ACTION, 1);
        msg.putExtra("current", curpage);
        msg.putExtra("max", maxpage);
        sendBroadcast(msg);
    }
    
    protected void readSettings() {
        String[] values = {
            "value"
        };
        String name = null;
        String[] fields = {
            "airplane_mode_on", "bnScreensaverDelay", "bnWallpaper","bnDeviceName"
        };
        
        try {
            for (@SuppressWarnings("unused")
            String field : fields) {
                if (name == null) {
                    name = "name=?";
                } else {
                    name += " or name=?";
                }
            }
            Cursor c = getContentResolver().query(Uri.parse("content://settings/system"), values, name, fields, "name");
            if (c != null) {
                c.moveToFirst();
                int value = c.getInt(0);
                if (value == 0) {
                    m_AirplaneMode = false;
                } else {
                    m_AirplaneMode = true;
                }
                c.moveToNext();
                m_DeviceName = c.getString(0);
                if ( m_DeviceName == null || m_DeviceName.trim().equals("")) {
                    m_DeviceName="My Nook";
                }
                c.moveToNext();
                long lvalue = c.getLong(0);
                if (lvalue > 0) {
                    m_ScreenSaverDelay = lvalue;
                }
                c.moveToNext();
                m_WallPaper = c.getString(0);
                Log.d(LOGTAG, "m_Wallpaper = " + m_WallPaper);
            }
            c.close();
            c.deactivate();
            
        } catch (Exception ex) {
            Log.e(LOGTAG, "Error reading system settings... keeping hardcoded values");
            ex.printStackTrace();
        }
    }
    
    public boolean waitForNetwork(ConnectivityManager.WakeLock lock) {
        try {
            ConnectivityManager cmgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            lock.acquire();
            NetworkInfo info = cmgr.getActiveNetworkInfo();
            boolean connection = (info == null) ? false : info.isConnected();
            int attempts = 0;
            while (!connection && attempts < 20) {
                attempts++;
                try {
                    Thread.sleep(3000);
                } catch (Exception ex) {
                    
                }
                info = cmgr.getActiveNetworkInfo();
                connection = (info == null) ? false : info.isConnected();
            }
            if (connection) return true;
        } catch (Exception ex) {
            Log.e("BNBooks", "Exception while checking for connection", ex);
        }
        if (lock != null && lock.isHeld()) lock.release();
        return false;
    }
    
}