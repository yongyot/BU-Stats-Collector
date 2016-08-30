package CoreStats;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import th.ac.bu.science.mit.allappstatscollector.Notify;
import th.ac.bu.science.mit.allappstatscollector.Settings;

/**
 * Created by Komal on 11/2/2015.
 */
public class NET {
    //up
    public String BG_UP_DATA;
    public String FG_UP_DATA;
    public String BG_UP_WiFi;
    public String FG_UP_WiFi;
    //down
    public String BG_DOWN_DATA;
    public String FG_DOWN_DATA;
    public String BG_DOWN_WiFi;
    public String FG_DOWN_WiFi;

    public boolean error=false;

    /**/
    public NET() {
        BG_UP_DATA="0";
        FG_UP_DATA="0";
        BG_UP_WiFi="0";
        FG_UP_WiFi="0";
        BG_DOWN_DATA="0";
        FG_DOWN_DATA="0";
        BG_DOWN_WiFi="0";
        FG_DOWN_WiFi="0";
    }


    public void CopyNet(NET SourceNet) {
        this.BG_UP_DATA = SourceNet.BG_UP_DATA;
        this.FG_UP_DATA = SourceNet.FG_UP_DATA;
        this.BG_UP_WiFi = SourceNet.BG_UP_WiFi;
        this.FG_UP_WiFi = SourceNet.FG_UP_WiFi;
        this.BG_DOWN_DATA = SourceNet.BG_DOWN_DATA;
        this.FG_DOWN_DATA = SourceNet.FG_DOWN_DATA;
        this.BG_DOWN_WiFi = SourceNet.BG_DOWN_WiFi;
        this.FG_DOWN_WiFi = SourceNet.FG_DOWN_WiFi;
        this.error=SourceNet.error;
    }

    public void setEmtpy() {
        BG_UP_DATA = "-";
        FG_UP_DATA = "-";
        BG_UP_WiFi = "-";
        FG_UP_WiFi = "-";
        BG_DOWN_DATA = "-";
        FG_DOWN_DATA = "-";
        BG_DOWN_WiFi = "-";
        FG_DOWN_WiFi = "-";
    }

    public void setNegative() {
        BG_UP_DATA = "-1";
        FG_UP_DATA = "-1";
        BG_UP_WiFi = "-1";
        FG_UP_WiFi = "-1";
        BG_DOWN_DATA = "-1";
        FG_DOWN_DATA = "-1";
        BG_DOWN_WiFi = "-1";
        FG_DOWN_WiFi = "-1";
    }


    public static String getMacAddress(Context context) {
        return getMacAddr(context);
    }

    private static String getMacAddr(Context context) {

        if(android.os.Build.VERSION.SDK_INT >=23) {

            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                            StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(Integer.toHexString(b & 0xFF) + ":");
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            } catch (Exception ex) {
                Log.d(Settings.TAG,"Can not retrieve mac address on Marshmallow");
                Notify.showNotification(context,"Error getting mac address.");
            }
            return null;
        } else {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getMacAddress();
        }
    }

    public static String getWifiInterfaceName() {
        try {
            return NetworkInterface.getByName("wlan0")!=null? "wlan0": NetworkInterface.getByName("eth0")!=null ?  "eth1":NetworkInterface.getByName("eth1")!=null?"eth1": null;
        } catch(Exception ex) {
            Log.d(Settings.TAG,"Can not get wifi network interface name.");
        }
        return null;
    }

    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }
}
