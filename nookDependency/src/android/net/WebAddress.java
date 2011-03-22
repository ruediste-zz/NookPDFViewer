package android.net;

//This is for Eclipse - not showing this class while trying to build browser code..
public class WebAddress {
    public String mPath = null;
    public String mScheme, mAuthInfo, mHost;
    public int mPort;
    
    public WebAddress(String url) {
        mPath = url;
    }
    
}
