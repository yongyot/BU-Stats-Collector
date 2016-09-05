package CoreStats;


import android.content.Context;
import android.net.Network;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import th.ac.bu.science.mit.allappstatscollector.Notify;
import th.ac.bu.science.mit.allappstatscollector.Settings;


/**
 * Created by Komal on 11/4/2015.
 *
 * This class reads network stats from /proc/net/xt_qtaguid/stats file. It contains a table of tcp and udp net stats based on UID and
 * contains 20 columns.
 * * Description of some important colums is as follow:
 *
 * data[4] is UID of the package
 * data[5] is cnet_set which tells whether the data is background or foreground. if cnet_set is 1 it's foreground data and if it's 0 it is background data.
 * data[6] is total bytes received and
 * data[8] is total bytes sent
 *There are two records for each UID. first record is for background and second next in table is for foreground.
 *cnt_set=0, background and second cnt_set=1 which means foreground data.

 Sample data of the file:
 idx iface acct_tag_hex uid_tag_int cnt_set rx_bytes rx_packets tx_bytes tx_packets     rx_tcp_bytes rx_tcp_packets rx_udp_bytes rx_udp_packets rx_other_bytes   rx_other_packets tx_tcp_bytes tx_tcp_packets tx_udp_bytes tx_udp_packets tx_other_bytes tx_other_packets
 2 rmnet0 0x0 0 0 18393 326 8506 166 10889 267 7504 59 0 0 4180 101 3397 54 929 11
 3 rmnet0 0x0 0 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 4 rmnet0 0x0 1000 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 5 rmnet0 0x0 1000 1 7181 14 1834 19 7023 12 158 2 0 0 1616 16 218 3 0 0
 6 rmnet0 0x0 10001 0 5723 19 3162 26 5723 19 0 0 0 0 3162 26 0 0 0 0
 7 rmnet0 0x0 10001 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 8 rmnet0 0x0 10007 0 1895740 1570 44556 898 1895740 1570 0 0 0 0 44556 898 0 0 0 0
 9 rmnet0 0x0 10007 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0

 Ref:
 https://groups.google.com/forum/#!topic/android-platform/B8cGFQzcyO4
 http://comments.gmane.org/gmane.comp.handhelds.android.platform/26896
 http://stackoverflow.com/questions/12904809/tracking-an-applications-network-statistics-netstats-using-adb

 */

public class NetStatsExtractor {

    public String DATA_INTERFACE;
    Context context;

    public NetStatsExtractor(Context _context) {
        context = _context;
        DATA_INTERFACE = "rmnet0";
    }

    public List<Stats> collectNetStats(List<Stats> appsList) {
        //In this method we will go through each application we found in Top command and we will set the network data in appList.net for each application
        //Some applications can have multiple processes. So when there is a multiple process we will read the data for it just once.

        String unFilteredStats=readProcFile();

        if(unFilteredStats.trim().isEmpty()){
            return null;
        }

        for(int i=0;i<appsList.size();i++) {
            if(appsList.get(i).isMainProcess()) {
                NET netStats = getNetStats(appsList.get(i).UID, unFilteredStats);
                appsList.get(i).NetStats.CopyNet(netStats);
            } else {
                appsList.get(i).NetStats.setEmtpy();
            }
        }
        return appsList;
    }

    public List<Stats> collectEmptyNetStats(List<Stats> appsList) {
        //In this method we will go through each application we found in Top command and we will set the network data in appList.net for each application
        //Some applications can have multiple processes. So when there is a multiple process we will read the data for it just once.

        for(int i=0;i<appsList.size();i++) {
                appsList.get(i).NetStats.setNegative();
        }
        return appsList;
    }

    public NET getNetStats(String UID,String unFilteredStats) {
/*        final int CNET=4;
        final String DATA_BG="0";
        final String DATA_FG="1";*/
        final int TAG = 2;
        final int RX_BYTES = 5;
        final int TX_BYTES = 7;
        final int INTERFACE=1;
        final int APP_UID=3;

        boolean wifiFound=false;
        boolean dataFound=false;

        NET netStats=new NET();

        String lines[] = unFilteredStats.split("\n");

        try {
            for(int i=0;i<lines.length;i++) {

                String dataBG[] = lines[i].trim().split("\\s+");

                if (dataBG[APP_UID].equalsIgnoreCase(UID) && dataBG[TAG].equalsIgnoreCase("0x0")){  //this is the uid we are looking for. There will be two consecutive lines. First for background data and second for foreground data for same interface.

                    String dataFG[] = lines[i + 1].trim().split("\\s+");    // We already read the first now read second one.
                    i++;
                    if (false){//(dataBG[INTERFACE].equalsIgnoreCase(Settings.WIFI_INTERFACE)){    //wifi background data for up and down is here

                        //read for the wifi background and foreground here

                        netStats.BG_UP_WiFi = dataBG[TX_BYTES];
                        netStats.BG_DOWN_WiFi = dataBG[RX_BYTES];
                        netStats.FG_UP_WiFi = dataFG[TX_BYTES];
                        netStats.FG_DOWN_WiFi = dataFG[RX_BYTES];
                        wifiFound = true;
                    } else if (dataBG[INTERFACE].equalsIgnoreCase(DATA_INTERFACE)){    //read for mobile background and foreground here

                        netStats.BG_UP_DATA = dataBG[TX_BYTES];
                        netStats.BG_DOWN_DATA = dataBG[RX_BYTES];
                        netStats.FG_UP_DATA = dataFG[TX_BYTES];
                        netStats.FG_DOWN_DATA = dataFG[RX_BYTES];
                        dataFound = true;
                    }
                }
                if (wifiFound && dataFound) {      //both are done. break the loop.
                    break;
                }
            }
        }
        catch (Exception e) {
            netStats.error=true;
            Log.d(Settings.TAG, "Might be malformed proc file from net stats extractor. Details: " + e.toString());
        }

        return netStats;
    }

    private  String readProcFile() {
        String procFileName="/proc/net/xt_qtaguid/stats";
        StringBuffer fileData = new StringBuffer();
        BufferedReader bufferedReader=null;
        try {
            bufferedReader = new BufferedReader(new FileReader(procFileName));
            String line;
            line = bufferedReader.readLine();                   //ignore header
            while ((line = bufferedReader.readLine()) != null) {
                fileData.append(line + "\n");
            }

            bufferedReader.close();
        } catch (Exception e) {
            Log.d(Settings.TAG, "Error reading proc file in NetStatsCollector. Details: " + e.toString());
            Notify.showNotification(context, "Couldn't read network file");
            try {
                if(bufferedReader!=null)
                    bufferedReader.close();

            } catch (Exception ex) {
             Log.e(Settings.TAG,"Can not close buffer reader for proc file.");
            }
        }

        return fileData.toString();
    }
}
