package CoreStats;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

import th.ac.bu.science.mit.allappstatscollector.Settings;

/**
 * Created by Komal on 2/11/2016.
 */
public class StepDownExtraction {

    public static int memoryThreshold=3000;
    public static int count=0;
    public static int threshold=5;
                                                                                                                         ;
    public int cpuTotal=0;
    public int memoryTotal=0;


    public int totalNetwork=0;

   public static boolean isDeviceActive(Context context)   //returns true if screen is on.
    {
        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        boolean result = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && powerManager.isInteractive() || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH && powerManager.isScreenOn();
        return result;
    }

    public StepDownExtraction (List<Stats> stats)
    {
        int cpuUsage=0, memory=0,network=0;
        for(int i=0;i<stats.size();i++)
        {
            if(stats.get(i).PackageName.equalsIgnoreCase("th.ac.bu.science.mit.allappstatscollector"))
                continue;
            String tempCpu=stats.get(i).CPUStats.CPU;
            String tempVss=stats.get(i).CPUStats.VSS;
            String tempRss=stats.get(i).CPUStats.RSS;

            String BG_UP_DATA = stats.get(i).NetStats.BG_UP_DATA;
            String FG_UP_DATA = stats.get(i).NetStats.FG_UP_DATA;
            String BG_UP_WiFi = stats.get(i).NetStats.BG_UP_WiFi;
            String FG_UP_WiFi = stats.get(i).NetStats.FG_UP_WiFi;
            String BG_DOWN_DATA = stats.get(i).NetStats.BG_UP_DATA;
            String FG_DOWN_DATA = stats.get(i).NetStats.FG_DOWN_DATA;
            String BG_DOWN_WiFi = stats.get(i).NetStats.BG_DOWN_WiFi;
            String FG_DOWN_WiFi = stats.get(i).NetStats.FG_DOWN_WiFi;

            //Log.d(Settings.TAG, "BG DOWN " + BG_DOWN_DATA + " FG_UP_DATA : " + FG_UP_DATA+ " BG_UP_WiFi: " +BG_UP_WiFi +" FG_UP_WiFi: " + FG_UP_WiFi+  "BG_DOWN_DATA"+ BG_DOWN_DATA);

            if(!(BG_DOWN_DATA.equals("-1") || BG_DOWN_DATA.equals("-")))
            {

                  totalNetwork=  Integer.parseInt(BG_UP_DATA) + Integer.parseInt(FG_UP_DATA) + Integer.parseInt(BG_UP_WiFi) + Integer.parseInt(FG_UP_WiFi);
                  totalNetwork+=   Integer.parseInt(BG_DOWN_DATA)+ Integer.parseInt(FG_DOWN_DATA)+Integer.parseInt(BG_DOWN_WiFi)+Integer.parseInt(FG_DOWN_WiFi);
            }
            else
            {
                totalNetwork=0;
            }

            int cpu=Integer.parseInt(tempCpu.substring(0, tempCpu.lastIndexOf('%')));
            int vss=Integer.parseInt(tempVss.substring(0, tempVss.lastIndexOf('K')));
            int rss=Integer.parseInt(tempRss.substring(0, tempRss.lastIndexOf('K')));

            cpuUsage+=cpu;

            memory=vss+rss+memory;
        }
        cpuTotal= cpuUsage;
        Log.d(Settings.TAG,"CPU USage: "+cpuUsage);
        memoryTotal=memory;

    }
}
