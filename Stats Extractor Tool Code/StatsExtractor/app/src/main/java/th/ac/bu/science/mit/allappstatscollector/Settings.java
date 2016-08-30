package th.ac.bu.science.mit.allappstatscollector;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Environment;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import CoreStats.NET;

/**
 * Created by Komal on 11/2/2015.
 */
public class Settings {
    static public int interval = 1;
    static public int NetInterval = 5;
    public static String TAG="stats-results";
    public static boolean IS_WIFI_AVAILABLE;
    static public int network_type; //2 wifi, 1 mobile, 0 no network.
    public static String WIFI_INTERFACE=null;
    public static String MAC=null;
    public static int UploadSize = 2040; // 2 MB (Unit: bytes)

    public static String getHashFilePath(){
        return Environment.getExternalStorageDirectory()+"/BU-Stat-Collector/hashData";
    }

    public static void loadSettings(Context context) {
        WIFI_INTERFACE=NET.getWifiInterfaceName();
        MAC=NET.getMacAddress(context);
        IS_WIFI_AVAILABLE=NET.isWifiAvailable(context);
    }

    public static boolean isUsageAccessGranted(Context context) {
        boolean result = false;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){     //on lollipop or above try to get top most
            //noinspection ResourceType
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*60*60*50, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);

                }

                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    result = true;
                }
            }
        } else {    //below 5.0 return true since no settings are rquired.
            result = true;
        }
        return result;
    }

    public static int getInterval() {
        return interval;
    }

    public static String getOutputFileName(Context context) {
        return MAC.replace(":","-")+".stats";
    }
    public static String getMacAddress() {
        return MAC;
    }
}
