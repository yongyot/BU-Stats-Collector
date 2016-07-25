package CoreStats;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Komal on 11/2/2015.
 */
public class Stats
{
    static public int STATE_BACKGROUND=0;
    static public int STATE_FOREGROUND=1;

    public String UID;
    public String PackageName;
    public boolean isInteracting;
    public CPU CPUStats;
    public NET NetStats;
    public int State;

    public Stats()
    {
        isInteracting=false;
        UID=new String();
        PackageName=new String();

        CPUStats = new CPU();
        NetStats = new NET();
    }

    public Stats(Stats obj)
    {

        CPUStats = new CPU();
        NetStats = new NET();

        this.isInteracting=obj.isInteracting;
        this.UID=obj.UID;
        this.PackageName=obj.PackageName;
        this.CPUStats.Status=obj.CPUStats.Status;
        this.CPUStats.Priority=obj.CPUStats.Priority;
        this.CPUStats.THR= obj.CPUStats.THR;
        this.CPUStats.RSS=obj.CPUStats.RSS;
        this.CPUStats.CPU=obj.CPUStats.CPU;
        this.CPUStats.success=obj.CPUStats.success;
        this.CPUStats.TopUID    =obj.CPUStats.TopUID;
        this.CPUStats.VSS=obj.CPUStats.VSS;

        this.NetStats.CopyNet(obj.NetStats);

    }


    public static List<Stats> copyList(List<Stats> appListSource)       //makes the copy of the stats list.
    {
        List<Stats> newAppList = new ArrayList<Stats>();

        for (Stats stat : appListSource) {
            newAppList.add(new Stats(stat));
        }
        return newAppList;
    }

    public boolean isMainProcess()
    {
        return !PackageName.contains(":");
    }

    public String getStringData()
    {
        Calendar calTime = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String date = sdf.format(calTime.getTime());

                                       //date
        String formatStr = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s";
        //String formatStr = "%-8s %-50s %-25s %-16s %-8s %-12s %-12s %-5s %-12s %-10s %-14s %-14s %-14s %-14s %-14s %-14s %-14s %-14s";
       return  String.format(formatStr,date,UID,PackageName,isMainProcess(),isInteracting,State,
                             CPUStats.CPU,CPUStats.VSS,CPUStats.RSS,CPUStats.THR,CPUStats.Priority,CPUStats.Status,
                             NetStats.BG_UP_DATA,NetStats.BG_DOWN_DATA,NetStats.FG_UP_DATA,NetStats.FG_DOWN_DATA,
                             NetStats.BG_UP_WiFi,NetStats.BG_DOWN_WiFi,NetStats.FG_UP_WiFi,NetStats.FG_DOWN_WiFi);

        //String title="LogTime\tisInteractive8\tCPU5%\tVSS12\tRSS12\tTHR5\tPriority4\tStatus4\t\tBG_UP_DATA\tBG_DOWN_DATA\tFG_UP_DATA\tFG_DOWN_DATA\tBG_UP_WiFi\tBG_DOWN_WiFi\tFG_UP_WiFi\tFG_DOWN_WiFi";
        /*String head=date+"\t"+this.UID+"\t"+this.PackageName+"\t"+ isInteracting+"\t";
        String cpu=this.CPUStats.CPU+"\t"+this.CPUStats.VSS+"\t"+this.CPUStats.RSS+"\t"+this.CPUStats.THR+"\t"+this.CPUStats.Priority+"\t"+this.CPUStats.Status+"\t\t";
        String net=this.NetStats.BG_UP_DATA+"\t"+this.NetStats.BG_DOWN_DATA+"\t"+this.NetStats.FG_UP_DATA+"\t"+this.NetStats.FG_DOWN_DATA+"\t"+this.NetStats.BG_UP_WiFi+"\t"+this.NetStats.BG_DOWN_WiFi+"\t"+this.NetStats.FG_UP_WiFi+"\t"+this.NetStats.FG_DOWN_WiFi;

        return head+cpu+net;*/
    }

    public static int getIndexByUID(List<Stats> appStats, String UID)
    {
        for(int i=0;i<appStats.size();i++)
        {
            if(appStats.get(i).UID.equalsIgnoreCase(UID))
                return i;
        }
        return -1;
    }

    public static List<Stats> NETDifference(List<Stats> oldList, List<Stats> newList)
    {
        List<Stats> diff=copyList(newList);

        for (int i=0;i<newList.size();i++)
        {
            if(!newList.get(i).isMainProcess())
                continue;

            int index=newList.get(i).getIndexByUID(oldList, newList.get(i).UID);    //returns the index of uid of old list.
            if(index<0)
            {
                //this app is not in old list. It means we just found it's data. So its net stats to 0.
                diff.get(i).NetStats=new NET();  //set it to empty object.
            }
            else {
                int oBG_UP_DATA, oBG_DOWN_DATA, oFG_UP_DATA, oFG_DOWN_DATA, oBG_UP_WiFi, oBG_DOWN_WiFi, oFG_UP_WiFi, oFG_DOWN_WiFi;
                int nBG_UP_DATA, nBG_DOWN_DATA, nFG_UP_DATA, nFG_DOWN_DATA, nBG_UP_WiFi, nBG_DOWN_WiFi, nFG_UP_WiFi, nFG_DOWN_WiFi;

                try {
                    oBG_UP_DATA = Integer.parseInt(oldList.get(index).NetStats.BG_UP_DATA);
                    oBG_DOWN_DATA = Integer.parseInt(oldList.get(index).NetStats.BG_DOWN_DATA);
                    oFG_UP_DATA = Integer.parseInt(oldList.get(index).NetStats.FG_UP_DATA);
                    oFG_DOWN_DATA = Integer.parseInt(oldList.get(index).NetStats.FG_DOWN_DATA);
                    oBG_UP_WiFi = Integer.parseInt(oldList.get(index).NetStats.BG_UP_WiFi);
                    oBG_DOWN_WiFi = Integer.parseInt(oldList.get(index).NetStats.BG_DOWN_WiFi);
                    oFG_UP_WiFi = Integer.parseInt(oldList.get(index).NetStats.FG_UP_WiFi);
                    oFG_DOWN_WiFi = Integer.parseInt(oldList.get(index).NetStats.FG_DOWN_WiFi);

                    nBG_UP_DATA = Integer.parseInt(newList.get(i).NetStats.BG_UP_DATA);
                    nBG_DOWN_DATA = Integer.parseInt(newList.get(i).NetStats.BG_DOWN_DATA);
                    nFG_UP_DATA = Integer.parseInt(newList.get(i).NetStats.FG_UP_DATA);
                    nFG_DOWN_DATA = Integer.parseInt(newList.get(i).NetStats.FG_DOWN_DATA);
                    nBG_UP_WiFi = Integer.parseInt(newList.get(i).NetStats.BG_UP_WiFi);
                    nBG_DOWN_WiFi = Integer.parseInt(newList.get(i).NetStats.BG_DOWN_WiFi);
                    nFG_UP_WiFi = Integer.parseInt(newList.get(i).NetStats.FG_UP_WiFi);
                    nFG_DOWN_WiFi = Integer.parseInt(newList.get(i).NetStats.FG_DOWN_WiFi);

                    diff.get(i).NetStats.BG_UP_DATA = nBG_UP_DATA - oBG_UP_DATA + "";
                    diff.get(i).NetStats.BG_DOWN_DATA = nBG_DOWN_DATA - oBG_DOWN_DATA + "";
                    diff.get(i).NetStats.FG_UP_DATA = nFG_UP_DATA - oFG_UP_DATA + "";
                    diff.get(i).NetStats.FG_DOWN_DATA = nFG_DOWN_DATA - oFG_DOWN_DATA + "";
                    diff.get(i).NetStats.BG_UP_WiFi = nBG_UP_WiFi - oBG_UP_WiFi + "";
                    diff.get(i).NetStats.BG_DOWN_WiFi = nBG_DOWN_WiFi - oBG_DOWN_WiFi + "";
                    diff.get(i).NetStats.FG_UP_WiFi = nFG_UP_WiFi - oFG_UP_WiFi + "";
                    diff.get(i).NetStats.FG_DOWN_WiFi = nFG_DOWN_WiFi - oFG_DOWN_WiFi + "";
                } catch (Exception e) {
                    Log.d("stats-results", "Data difference error." + e.toString());

                }
            }
        }

     /*   Log.d("stats-results","NEW "+newList.get(0).NetStats.getString());
        Log.d("stats-results","OLD "+oldList.get(0).NetStats.getString());
        Log.d("stats-results", "Diff " + diff.get(0).NetStats.getString());*/

        return diff;
    }

    public static List<Stats>  getStats(Context context)
    {
        List<Stats> appStats=CPUStatsExtractor.getRunningApps(context);      //this method generates list of running apps and also feeds the CPU stats for each app
        appStats=new NetStatsExtractor(context).collectNetStats(appStats);   //This method feeds the network info in the model

        return appStats;
    }

    public static List<Stats> getStatsWithoutNetowrk(Context context)
    {
        List<Stats> appStats=CPUStatsExtractor.getRunningApps(context);      //this method generates list of running apps and also feeds the CPU stats for each app

        appStats=new NetStatsExtractor(context).collectEmptyNetStats(appStats);   //This method
        return appStats;
    }
}
