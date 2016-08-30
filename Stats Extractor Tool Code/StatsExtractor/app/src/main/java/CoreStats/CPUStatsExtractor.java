package CoreStats;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import th.ac.bu.science.mit.allappstatscollector.Notify;
import th.ac.bu.science.mit.allappstatscollector.Settings;

/**
 * Created by Komal on 11/2/2015.
 */

public class CPUStatsExtractor {
    static Context context;

    public static String  getTopCommandData(){                       //This method will read top command file and filter out all the applications from it and return to calling function as a string.

        StringBuffer topData = new StringBuffer();
        try {
            Process process = Runtime.getRuntime().exec("top -d 0 -n 1");
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            //String patternString = "u\\d_";                 //For example, u0_112 is a UID in top command. We are looking for u<a digit> and an underscore.
            //Pattern pattern = Pattern.compile(patternString);

            while ((line = in.readLine()) != null) {         //read a line from top command's virtual file

              //  Matcher matcher = pattern.matcher(line);
               // int count = 0;
                //while(matcher.find())
               // {
               //     count++;
               //     break;
               // }
               // if(count>0)                                //This line contains u<d>_ .it is most likely an application. Add it.
                topData.append(line+"\n");
            }
            in.close();
            //Log.d("debug", "TOP RESULT\n+" + topData.toString());

        } catch (Exception e) {
            Log.d(Settings.TAG, "Top command error. Top command couldn't execute successfuly. Details:\n"+e.toString());
            Notify.showNotification(context,"Error: Top command couldn't be executed successfully");
            e.printStackTrace();
        }
        //remove header of the data first.

        String data = topData.toString();
        int i = data.indexOf("Name");       //removes the header from top of the file.
        //Log.d("Top-commnad",data.toString());
        return data.substring(i+4);
    }

    //This method will return the list of running applications with CPU stats.
   public static  List<Stats> getRunningApps(Context _context) {
        List AppList =new ArrayList<>();
        context=_context;

        final int INDEX_OF_PR = 1;
        final int INDEX_OF_CPU = 2;
        final int INDEX_OF_Status = 3;
        final int INDEX_OF_THR = 4;
        final int INDEX_OF_VSS = 5;
        final int INDEX_OF_RSS = 6;
        final int INDEX_OF_PCY = 7;
        final int INDEX_OF_UID = 8;
        final int INDEX_OF_NAME = 9;
        final int TOP_LENGTH= 10;

        String stats = getTopCommandData();

       //("Top-Command",stats);
        String lines[] = stats.trim().split("\n");

        //int i=1;
        for (String line : lines) {
            String data[] = line.trim().split("\\s+");      //Split the row. Example: 14885  0   0% S    29 1064464K  61308K  bg u0_a69   com.google.android.gm
            if(data.length<TOP_LENGTH) {                  //root and system processes have 9 fields. But an app has 10.
                continue;
            }

            int uid = isSystemPackage(data[INDEX_OF_NAME]);
            if(uid > 0) {                                   //It's a user-installed app.

                Stats statsData=new Stats();

                statsData.PackageName=data[INDEX_OF_NAME];
                statsData.isInteracting=isInteractive(data[INDEX_OF_NAME]);
                statsData.UID=uid+"";
                statsData.CPUStats.CPU = data[INDEX_OF_CPU];
                statsData.CPUStats.VSS = data[INDEX_OF_VSS];
                statsData.CPUStats.RSS = data[INDEX_OF_RSS];
                //statsData.CPUStats.PCY=data[INDEX_OF_PCY];
                statsData.CPUStats.THR = data[INDEX_OF_THR];
                statsData.CPUStats.Priority = data[INDEX_OF_PR];
                statsData.CPUStats.TopUID = data[INDEX_OF_UID];
                statsData.CPUStats.Status = data[INDEX_OF_Status];

                if(data[INDEX_OF_PCY].equalsIgnoreCase("fg")){
                    statsData.State=Stats.STATE_FOREGROUND;
                } else if(data[INDEX_OF_PCY].equalsIgnoreCase("bg")) {
                    statsData.State=Stats.STATE_BACKGROUND;
                }

                AppList.add(statsData);
            }
        }

        Collections.sort(AppList, new PackageNameComparator());

        return AppList;
    }

    static class PackageNameComparator implements Comparator<Stats> {
        @Override
        public int compare(Stats s1,Stats s2) {
            return s1.PackageName.compareTo(s2.PackageName);
        }
    }

    private static int isSystemPackage(String packageName){      //if package is user-installed then returns uid of given pacakage otherwise returns a negative number.

        //in top command packages can also have multiple processes. Name of a process is name of package. But when having multiple processes, a tag is also included in package name.
        //for example: com.facebook.katana is a process. And it there can also another process called com.facebook.katana:Dash
        //When there are multiple processes, process name contains package and a tag in the following syntax. <packageName:tag>
        //In this application we are not going to combine stats usage for multiple processes.
        //We will store this information in a file. Since these processes share same UID our server will be able to distinguish the apps with multiple processes.

        if(packageName.contains(":")){   //multiple process. Remove the tag including colon.

            int pos = packageName.indexOf(':');
            packageName = packageName.substring(0, pos);
        }

        final PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo;
        try {
            pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            if(( pkgInfo.applicationInfo.flags &  ApplicationInfo.FLAG_SYSTEM) == 0){
                return pkgInfo.applicationInfo.uid;
            }
        } catch (PackageManager.NameNotFoundException ex) {
            ///Log.d("test-debug","Not a package: "+packageName);
        } catch (Exception e) {
            Log.d(Settings.TAG,"Unknown error occurred while checking package type in CPUStatsExtractor class.. Details: "+e.toString());
        }

        return -1;
    }

    public static boolean isInteractive(String packageName){     //returns whether the given package is top activity or not.

        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            //noinspection ResourceType
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*60*60*50, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                  //  Log.d("Apps", usageStats.getPackageName());
                } if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        if(currentApp.equals(packageName)){
            return true;
        }

        return false;
    }
}