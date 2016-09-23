package th.ac.bu.science.mit.allappstatscollector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.*;

import th.ac.bu.science.mit.allappstatscollector.Models.AppsInfo;

/**
 * Created by Komal on 1/13/2016.
 */
public class HashGen {

    public volatile static boolean isGenerating = false;

    private  void writeToFile(String data, boolean append) {

        data = Settings.getMacAddress() + "\n" + data;
        File statsDir;
        statsDir = new File(Settings.APPLICATION_PATH);

        if(!statsDir.exists()){
            statsDir.mkdirs();
        }

        try {
            File file = new File(Settings.getHashFilePath());
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.close();
        }  catch (Exception e) {
            Log.d(Settings.TAG, "Unable to write hash info in file. Details:\n" + e.toString());
        }
    }

    public void getAllAppInfo(Context context) {
        isGenerating = true;
        String data = "";

        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS",Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        new ArrayList<>();{
            final List<ApplicationInfo> packs = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo appInfo : packs) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AppsInfo app = getPackageInfo(appInfo.packageName, context);

                    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                    calendar.setTimeInMillis(app.lastUpdate);
                    String date=  formatter.format(calendar.getTime());
                    data = data + app.packageName + "*" + date + "*" + app.hash + "*"+ app.versionName + "*" + app.versionCode + "\n";
                }
            }
        }
        writeToFile(data,false);
        isGenerating = false;
       // Log.d("stats-results", data);
    }


    public AppsInfo getPackageInfo(String packageName,Context context) {
        AppsInfo appInfo = new AppsInfo();

        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            long millis = packageInfo.lastUpdateTime;
            appInfo.hash = getHash(packageInfo.applicationInfo.sourceDir);
            appInfo.lastUpdate = millis;
            appInfo.packageName = packageName;
            appInfo.versionName = packageInfo.versionName;
            appInfo.versionCode = packageInfo.versionCode + "";
            return appInfo;

        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error occurred in getPackageInfo method. Details: "+ ex.toString());
        }
        return null;
    }


    private String getHash(String file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] bytesBuffer = new byte[1024];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
                digest.update(bytesBuffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();

            return convertByteArrayToHexString(hashedBytes);
        } catch (Exception ex) {
            Log.d(Settings.TAG,"Error while generating hashcode. Details: "+ex.toString());
        }
        return null;
    }

    private  String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
