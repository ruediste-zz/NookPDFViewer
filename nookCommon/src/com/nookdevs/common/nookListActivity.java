package com.nookdevs.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;

// TODO: Auto-generated Javadoc
/**
 * The Class nookListActivity.
 */
public class nookListActivity extends ListActivity {
    
    /** The Constant SOFT_KEYBOARD_CLEAR. */
    public static final int SOFT_KEYBOARD_CLEAR = -13;
    
    /** The Constant SOFT_KEYBOARD_SUBMIT. */
    public static final int SOFT_KEYBOARD_SUBMIT = -8;
    
    /** The Constant SOFT_KEYBOARD_CANCEL. */
    public static final int SOFT_KEYBOARD_CANCEL = -3;
    
    /** The Constant SOFT_KEYBOARD_DOWN_KEY. */
    public static final int SOFT_KEYBOARD_DOWN_KEY = 20;
    
    /** The Constant SOFT_KEYBOARD_UP_KEY. */
    public static final int SOFT_KEYBOARD_UP_KEY = 19;
    
    /** The Constant NOOK_PAGE_UP_KEY_RIGHT. */
    protected static final int NOOK_PAGE_UP_KEY_RIGHT = 98;
    
    /** The Constant NOOK_PAGE_DOWN_KEY_RIGHT. */
    protected static final int NOOK_PAGE_DOWN_KEY_RIGHT = 97;
    
    /** The Constant NOOK_PAGE_UP_KEY_LEFT. */
    protected static final int NOOK_PAGE_UP_KEY_LEFT = 96;
    
    /** The Constant NOOK_PAGE_DOWN_KEY_LEFT. */
    protected static final int NOOK_PAGE_DOWN_KEY_LEFT = 95;
    
    /** The Constant NOOK_PAGE_DOWN_SWIPE. */
    protected static final int NOOK_PAGE_DOWN_SWIPE = 100;
    
    /** The Constant NOOK_PAGE_UP_SWIPE. */
    protected static final int NOOK_PAGE_UP_SWIPE = 101;
    
    /** The screen lock. */
    PowerManager.WakeLock screenLock = null;
    
    /** The m_ airplane mode. */
    boolean m_AirplaneMode = false;
    
    /** The m_ screen saver delay. */
    long m_ScreenSaverDelay = 600000;
    
    /** The m_ wall paper. */
    String m_WallPaper = null;
    
    /** The m_ alert dialog. */
    AlertDialog m_AlertDialog = null;
    
    /** The Constant SDFOLDER. */
    public static final String SDFOLDER = "/system/media/sdcard/";
    
    /** The Constant EXTERNAL_SDFOLDER. */
    public static final String EXTERNAL_SDFOLDER = "/sdcard";
    
    /** The Constant UPDATE_TITLE. */
    public final static String UPDATE_TITLE = "com.bravo.intent.UPDATE_TITLE";
    
    /** The Constant UPDATE_STATUSBAR. */
    public final static String UPDATE_STATUSBAR = "com.bravo.intent.UPDATE_STATUSBAR";
    
    /** The Constant STATUSBAR_ICON. */
    public final static String STATUSBAR_ICON = "Statusbar.icon";
    
    /** The Constant STATUSBAR_ACTION. */
    public final static String STATUSBAR_ACTION = "Statusbar.action";
    
    /** The Constant READING_NOW_URL. */
    public static final String READING_NOW_URL = "content://com.ereader.android/last";
    
    /** The m_ first time. */
    protected boolean m_FirstTime = true;
    
    /** The m_ version. */
    protected String m_Version;
    
    /**
     * Show soft keyboard.
     */
    public final void showSoftKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getListView(),InputMethodManager.SHOW_FORCED);
    }
    
    /**
     * Hide soft keyboard.
     */
    public final void hideSoftKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getListView().getWindowToken(),0);
    }
    
    /**
     * Gets the wallpaper file.
     *
     * @return the wallpaper file
     */
    protected String getWallpaperFile() {
        return m_WallPaper;
    }
    
    /**
     * Gets the airplane mode.
     *
     * @return the airplane mode
     */
    protected boolean getAirplaneMode() {
        return m_AirplaneMode;
    }
    
    /**
     * Gets the screen saver delay.
     *
     * @return the screen saver delay
     */
    protected long getScreenSaverDelay() {
        return m_ScreenSaverDelay;
    }
    
    /** The LOGTAG. */
    protected static String LOGTAG = "nookActivity";
    
    /** The NAME. */
    protected static String NAME = "nookActivity";
    
    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState the saved instance state
     */
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
        
        updateTitle(NAME);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
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
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
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
        
        updateTitle(NAME + " " + m_Version);
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onUserInteraction()
     */
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (screenLock != null) {
            screenLock.acquire(m_ScreenSaverDelay);
        }
    }
    
    /**
     * Go home.
     */
    protected void goHome() {
        String action = "android.intent.action.MAIN";
        String category = "android.intent.category.HOME";
        Intent intent = new Intent();
        intent.setAction(action);
        intent.addCategory(category);
        startActivity(intent);
    }
    
    /**
     * Go back.
     */
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
    
    /**
     * Close alert.
     */
    public void closeAlert() {
        if (m_AlertDialog != null) {
            m_AlertDialog.dismiss();
        }
    }
    
    /**
     * Update reading now.
     *
     * @param intent the intent
     */
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
    
    /**
     * Display alert.
     *
     * @param title the title
     * @param msg the msg
     * @param type the type
     * @param listener the listener
     * @param drawable the drawable
     */
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
    
    /**
     * Update title.
     *
     * @param title the title
     */
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
    
    /**
     * Show page number.
     *
     * @param curpage the curpage
     * @param maxpage the maxpage
     */
    protected void showPageNumber(int curpage, int maxpage) {
        Intent msg = new Intent(UPDATE_STATUSBAR);
        msg.putExtra(STATUSBAR_ICON, 7);
        msg.putExtra(STATUSBAR_ACTION, 1);
        msg.putExtra("current", curpage);
        msg.putExtra("max", maxpage);
        sendBroadcast(msg);
    }
    
    /**
     * Read settings.
     */
    protected void readSettings() {
        String[] values = {
            "value"
        };
        String name = null;
        String[] fields = {
            "airplane_mode_on", "bnScreensaverDelay", "bnWallpaper"
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
    
    /**
     * Wait for network.
     *
     * @param lock the lock
     * @return true, if successful
     */
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
