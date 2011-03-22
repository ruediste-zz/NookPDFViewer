/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.net;

/**
 * Class that answers queries about the state of network connectivity. It also
 * notifies applications when network connectivity changes. Get an instance of
 * this class by calling
 * {@link android.content.Context#getSystemService(String)
 * Context.getSystemService(Context.CONNECTIVITY_SERVICE)}.
 * <p>
 * The primary responsibilities of this class are to:
 * <ol>
 * <li>Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)</li>
 * <li>Send broadcast intents when network connectivity changes</li>
 * <li>Attempt to "fail over" to another network when connectivity to a network
 * is lost</li>
 * <li>Provide an API that allows applications to query the coarse-grained or
 * fine-grained state of the available networks</li>
 * </ol>
 */
public class ConnectivityManager {
    /**
     * A change in network connectivity has occurred. A connection has either
     * been established or lost. The NetworkInfo for the affected network is
     * sent as an extra; it should be consulted to see what kind of connectivity
     * event occurred.
     * <p/>
     * If this is a connection that was the result of failing over from a
     * disconnected network, then the FAILOVER_CONNECTION boolean extra is set
     * to true.
     * <p/>
     * For a loss of connectivity, if the connectivity manager is attempting to
     * connect (or has already connected) to another network, the NetworkInfo
     * for the new network is also passed as an extra. This lets any receivers
     * of the broadcast know that they should not necessarily tell the user that
     * no data traffic will be possible. Instead, the reciever should expect
     * another broadcast soon, indicating either that the failover attempt
     * succeeded (and so there is still overall data connectivity), or that the
     * failover attempt failed, meaning that all connectivity has been lost.
     * <p/>
     * For a disconnect event, the boolean extra EXTRA_NO_CONNECTIVITY is set to
     * {@code true} if there are no connected networks at all.
     */
    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    /**
     * The lookup key for a {@link NetworkInfo} object. Retrieve with
     * {@link android.content.Intent#getParcelableExtra(String)}.
     */
    public static final String EXTRA_NETWORK_INFO = "networkInfo";
    /**
     * The lookup key for a boolean that indicates whether a connect event is
     * for a network to which the connectivity manager was failing over
     * following a disconnect on another network. Retrieve it with
     * {@link android.content.Intent#getBooleanExtra(String,boolean)}.
     */
    public static final String EXTRA_IS_FAILOVER = "isFailover";
    /**
     * The lookup key for a {@link NetworkInfo} object. This is supplied when
     * there is another network that it may be possible to connect to. Retrieve
     * with {@link android.content.Intent#getParcelableExtra(String)}.
     */
    public static final String EXTRA_OTHER_NETWORK_INFO = "otherNetwork";
    /**
     * The lookup key for a boolean that indicates whether there is a complete
     * lack of connectivity, i.e., no network is available. Retrieve it with
     * {@link android.content.Intent#getBooleanExtra(String,boolean)}.
     */
    public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
    /**
     * The lookup key for a string that indicates why an attempt to connect to a
     * network failed. The string has no particular structure. It is intended to
     * be used in notifications presented to users. Retrieve it with
     * {@link android.content.Intent#getStringExtra(String)}.
     */
    public static final String EXTRA_REASON = "reason";
    /**
     * The lookup key for a string that provides optionally supplied extra
     * information about the network state. The information may be passed up
     * from the lower networking layers, and its meaning may be specific to a
     * particular network type. Retrieve it with
     * {@link android.content.Intent#getStringExtra(String)}.
     */
    public static final String EXTRA_EXTRA_INFO = "extraInfo";
    
    /**
     * Broadcast Action: The setting for background data usage has changed
     * values. Use {@link #getBackgroundDataSetting()} to get the current value.
     * <p>
     * If an application uses the network in the background, it should listen
     * for this broadcast and stop using the background data if the value is
     * false.
     */
    
    public static final String ACTION_BACKGROUND_DATA_SETTING_CHANGED =
        "android.net.conn.BACKGROUND_DATA_SETTING_CHANGED";
    
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_WIFI = 1;
    
    public static final int DEFAULT_NETWORK_PREFERENCE = TYPE_WIFI;
    
    // private IConnectivityManager mService;
    
    static public boolean isNetworkTypeValid(int networkType) {
        return networkType == TYPE_WIFI || networkType == TYPE_MOBILE;
    }
    
    public void setNetworkPreference(int preference) {
        // try {
        // mService.setNetworkPreference(preference);
        // } catch (RemoteException e) {
        // }
    }
    
    public int getNetworkPreference() {
        try {
            // return mService.getNetworkPreference();
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }
    
    public NetworkInfo getActiveNetworkInfo() {
        try {
            return null; // mService.getActiveNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }
    
    public NetworkInfo getNetworkInfo(int networkType) {
        try {
            return null; // .getNetworkInfo(networkType);
        } catch (Exception e) {
            return null;
        }
    }
    
    public NetworkInfo[] getAllNetworkInfo() {
        try {
            return null; // mService.getAllNetworkInfo();
        } catch (Exception e) {
            return null;
        }
    }
    
    /** {@hide} */
    public boolean setRadios(boolean turnOn) {
        try {
            return false; // mService.setRadios(turnOn);
        } catch (Exception e) {
            return false;
        }
    }
    
    /** {@hide} */
    public boolean setRadio(int networkType, boolean turnOn) {
        try {
            return false; // mService.setRadio(networkType, turnOn);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Tells the underlying networking system that the caller wants to begin
     * using the named feature. The interpretation of {@code feature} is
     * completely up to each networking implementation.
     * 
     * @param networkType
     *            specifies which network the request pertains to
     * @param feature
     *            the name of the feature to be used
     * @return an integer value representing the outcome of the request. The
     *         interpretation of this value is specific to each networking
     *         implementation+feature combination, except that the value {@code
     *         -1} always indicates failure.
     */
    public int startUsingNetworkFeature(int networkType, String feature) {
        try {
            return -1; // mService.startUsingNetworkFeature(networkType,
            // feature);
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Tells the underlying networking system that the caller is finished using
     * the named feature. The interpretation of {@code feature} is completely up
     * to each networking implementation.
     * 
     * @param networkType
     *            specifies which network the request pertains to
     * @param feature
     *            the name of the feature that is no longer needed
     * @return an integer value representing the outcome of the request. The
     *         interpretation of this value is specific to each networking
     *         implementation+feature combination, except that the value {@code
     *         -1} always indicates failure.
     */
    public int stopUsingNetworkFeature(int networkType, String feature) {
        try {
            return -1; // mService.stopUsingNetworkFeature(networkType,
            // feature);
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Ensure that a network route exists to deliver traffic to the specified
     * host via the specified network interface. An attempt to add a route that
     * already exists is ignored, but treated as successful.
     * 
     * @param networkType
     *            the type of the network over which traffic to the specified
     *            host is to be routed
     * @param hostAddress
     *            the IP address of the host to which the route is desired
     * @return {@code true} on success, {@code false} on failure
     */
    public boolean requestRouteToHost(int networkType, int hostAddress) {
        try {
            return false; // mService.requestRouteToHost(networkType,
            // hostAddress);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Returns the value of the setting for background data usage. If false,
     * applications should not use the network if the application is not in the
     * foreground. Developers should respect this setting, and check the value
     * of this before performing any background data operations.
     * <p>
     * All applications that have background services that use the network
     * should listen to {@link #ACTION_BACKGROUND_DATA_SETTING_CHANGED}.
     * 
     * @return Whether background data usage is allowed.
     */
    public boolean getBackgroundDataSetting() {
        try {
            return false; // mService.getBackgroundDataSetting();
        } catch (Exception e) {
            // Err on the side of safety
            return false;
        }
    }
    
    /**
     * Sets the value of the setting for background data usage.
     * 
     * @param allowBackgroundData
     *            Whether an application should use data while it is in the
     *            background.
     * 
     * @attr ref android.Manifest.permission#CHANGE_BACKGROUND_DATA_SETTING
     * @see #getBackgroundDataSetting()
     * @hide
     */
    public void setBackgroundDataSetting(boolean allowBackgroundData) {
        // try {
        // mService.setBackgroundDataSetting(allowBackgroundData);
        // } catch (RemoteException e) {
        // }
    }
    
    /**
     * Don't allow use of default constructor.
     */
    private ConnectivityManager() {
    }
    
    // /**
    // * {@hide}
    // */
    // public ConnectivityManager(IConnectivityManager service) {
    // if (service == null) {
    // throw new IllegalArgumentException(
    // "ConnectivityManager() cannot be constructed with null service");
    // }
    // mService = service;
    // }
    public class WakeLock {
        static final int RELEASE_WAKE_LOCK = 1;
        
        Runnable mReleaser = new Runnable() {
            public void run() {
                release();
            }
        };
        
        int mFlags;
        String mTag;
        int mCount = 0;
        boolean mRefCounted = true;
        boolean mHeld = false;
        
        WakeLock(int flags, String tag) {
            
        }
        
        /**
         * Sets whether this WakeLock is ref counted.
         * 
         * @param value
         *            true for ref counted, false for not ref counted.
         */
        public void setReferenceCounted(boolean value) {
            mRefCounted = value;
        }
        
        /**
         * Makes sure the device is on at the level you asked when you created
         * the wake lock.
         */
        public void acquire() {
            
        }
        
        /**
         * Makes sure the device is on at the level you asked when you created
         * the wake lock. The lock will be released after the given timeout.
         * 
         * @param timeout
         *            Release the lock after the give timeout in milliseconds.
         */
        public void acquire(long timeout) {
        }
        
        /**
         * Release your claim to the CPU or screen being on.
         * 
         * <p>
         * It may turn off shortly after you release it, or it may not if there
         * are other wake locks held.
         */
        public void release() {
            
        }
        
        public boolean isHeld() {
            return mHeld;
        }
        
        @Override
        public String toString() {
            return "";
        }
        
        @Override
        protected void finalize() throws Throwable {
            
        }
    }
    
    /**
     * Get a wake lock at the level of the flags parameter. Call
     * {@link WakeLock#acquire() acquire()} on the object to acquire the wake
     * lock, and {@link WakeLock#release release()} when you are done.
     * 
     * {@samplecode PowerManager pm = (PowerManager)mContext.getSystemService(
     * Context.POWER_SERVICE); PowerManager.WakeLock wl = pm.newWakeLock(
     * PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
     * wl.acquire(); // ... wl.release(); * }
     * 
     * @param flags
     *            Combination of flag values defining the requested behavior of
     *            the WakeLock.
     * @param tag
     *            Your class name (or other tag) for debugging purposes.
     * 
     * @see WakeLock#acquire()
     * @see WakeLock#release()
     */
    public WakeLock newWakeLock(int flags, String tag) {
        return new WakeLock(flags, tag);
    }
    
}
