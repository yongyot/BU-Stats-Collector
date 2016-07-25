package th.ac.bu.science.mit.allappstatscollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Set;

import CoreStats.NET;

/**
 * Created by Komal on 3/16/2016.
 */
public class WifiReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
//        Settings.IS_WIFI_AVAILABLE= NET.isWifiAvailable(context);
 //       Log.d(Settings.TAG,"Wifi : "+Settings.IS_WIFI_AVAILABLE);
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if( wifi != null && wifi.isConnectedOrConnecting())
            Settings.network_type=2;
        else if(mobile != null && mobile.isConnectedOrConnecting())
            Settings.network_type=1;
        else
            Settings.network_type=0;

       // Log.d("network_type", Settings.network_type +"--------------------------------------------------------******");
    }
}


/*


public class NetworkChangeReceiver extends BroadcastReceiver
{
@Override
public void onReceive(Context context, Intent intent)
{

}
}

 */