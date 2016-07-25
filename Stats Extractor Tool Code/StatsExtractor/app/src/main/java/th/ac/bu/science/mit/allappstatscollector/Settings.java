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
public class Settings
{
    public static final String SETTINGS_CONFIG = "bu_setting.config";
    static public int interval=1;
    //static public int maxInterval=300;
    //static public int minInterval=1;
    //static private String deviceID="";
    static public int NetInterval=5;
    //static private String outputFileName="";
    static private Context cont;
    public static String TAG="stats-results";
    public static boolean IS_WIFI_AVAILABLE;

    static public int network_type;
    //2 wifi, 1 mobile, 0 no network.

    public static String WIFI_INTERFACE=null;
    public static String MAC=null;
    //static String defaultDeviceID="0";
    //public static String LogFile="logs.txt";

 //  public final static int FIRST_TIME=0;

    //change these settings for interval and upload size.
   // static String defaultInterval="3";
    public static int UploadSize=2048; // 2 MB (Unit: bytes)

    public static String getHashFilePath()
    {
        return Environment.getExternalStorageDirectory()+"/BU-Stat-Collector/hashData";
    }


    public static void loadSettings(Context context)
    {
       cont=context;
        WIFI_INTERFACE=NET.getWifiInterfaceName();
        MAC=NET.getMacAddress(context);
        IS_WIFI_AVAILABLE=NET.isWifiAvailable(context);


     /*  String path = context.getFilesDir().getAbsolutePath() + "/" + SETTINGS_CONFIG;
       File file = new File(path);


        if(!(file.exists()))
        {
            ShowMessage.message("No settings were found. Creating default file.", context);
            writeToFile(context, true);
            Log.d("stats-results", "Default settings file is created.");

        }
        try
        {
            FileInputStream fileIn=context.openFileInput(SETTINGS_CONFIG);    //read default setting profile
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
            String s="";
            //interval=Integer.parseInt(br.readLine());
            //deviceID=br.readLine();
            br.close();
            Log.d("stats-results","Settings loaded: "+  "Interval: "+interval + " device Id"+deviceID);
        }
        catch (Exception e)
        {
            Log.d("stats-results", "Can't load setting files. Details: "+e.toString());
        }*/

    }

    private static void writeToFile(Context context,boolean isDefault)
    {
   /*     String keys="";
        if(isDefault)
            keys=defaultInterval+"\r\n"+defaultDeviceID;
        else
            keys=interval+"\r\n"+deviceID;

        FileOutputStream outputStream;

        try
        {
            outputStream = context.openFileOutput(SETTINGS_CONFIG, Context.MODE_PRIVATE);
            outputStream.write(keys.getBytes());
            outputStream.close();

            if(isDefault)
            Log.d("stats-results", "Default settings saved.");
            else
                Log.d("stats-results","Settings saved. (Interval: "+ interval +" and Device ID: "+deviceID+")");
        }
        catch (Exception  e)
        {
            Log.d("stats-results", "Error writing settings file. Details: " + e.toString());
        }*/
    }


    public static boolean isUsageAccessGranted(Context context)
    {
        boolean result=false;
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)     //on lollipop or above try to get top most
        {
            //noinspection ResourceType
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*60*60*50, time);
            if (appList != null && appList.size() > 0)
            {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);

                }
                if (mySortedMap != null && !mySortedMap.isEmpty())
                {
                    result=true;
                }
            }
        }
        else    //below 5.0 return true since no settings are rquired.
        {
            result=true;
        }
        return result;
    }

    public static void saveSettings(Context context)
    {
        writeToFile(context,false);
    }

   /* public static void setDeviceID(String newDeviceID)
    {
        deviceID=newDeviceID;
    }*/

  /*  public static String getDeviceID()
    {
        return deviceID;
    }*/

    public static int getInterval()
    {
        return interval;
    }

    public static void setInterval(int newInterval )
    {
        interval=newInterval;
    }

    public static String getOutputFileName(Context context)
    {
        return MAC.replace(":","-")+".stats";
    }
    public static String getMacAddress()
    {
        return MAC;
    }
}
