package th.ac.bu.science.mit.allappstatscollector;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Komal on 11/2/2015.
 */
public class StatsFileManager {

    private Context context;

    public StatsFileManager(Context _context) {
        context = _context;
    }

    public void SaveStats(String data) {

        Log.d(Settings.TAG,"Data Saved..");
        writeToFile(data, true);
    }


    public void createNewFile() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss:SSS", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String createtime = sdf.format(cal.getTime());

        String metaInfo = "File Name: " + Settings.getOutputFileName(context) + "\r\n" +
                "Extraction Started: " + createtime + "\r\n";
        String formatStr = "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s";
        String title = String.format(formatStr, "LogTime", "UID", "PackageName", "isMainProcess", "isInteracting", "Status",
                "CPU%", "VSS", "RSS", "THREADS", "Priority", "Status",
                "BG_UP_DATA", "BG_DOWN_DATA", "FG_UP_DATA", "FG_DOWN_DATA",
                "BG_UP_WiFi", "BG_DOWN_WiFi", "FG_UP_WiFi", "FG_DOWN_WiFi");

        String line = "";
        for (int i = 0; i <= title.length() + 48; i++)
            line = line + "-";

        String head = metaInfo + line + "\r\n" + title + "\r\n" + line + "\r\n";
        writeToFile(head, false);
    }

    public static long getFileSize(Context context) {
        long size = 0;
        try {
            File rootPath = Environment.getExternalStorageDirectory();
            File statsDir;
            statsDir = new File(rootPath + "/BU-Stat-Collector/");

            if (!statsDir.exists())
                statsDir.mkdirs();

            File file = new File(statsDir, Settings.getOutputFileName(context));

            size = file.length() / 1024;

            return size;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    private synchronized void writeToFile(String data, boolean append) {

        File rootPath = Environment.getExternalStorageDirectory();
        File statsDir;
        statsDir = new File(rootPath + "/BU-Stat-Collector/");

        if (!statsDir.exists())
            statsDir.mkdirs();

        try {
            File file = new File(statsDir, Settings.getOutputFileName(context));
            FileOutputStream fos = new FileOutputStream(file, append);
            fos.write(data.getBytes());
            fos.close();
        } catch (Exception e) {

            Log.d(Settings.TAG, "Unable to write stats data in file. Method: SaveStats.\nData:" + data + "Details:\n" + e.toString());
        }
    }

    public static boolean compressFile(Context cont) {
        boolean result = false;
        byte[] buffer = new byte[1024];
        String outputFile = Settings.getOutputFileName(cont);
        try {

            File rootPath = Environment.getExternalStorageDirectory();
            File statsDir;
            statsDir = new File(rootPath + "/BU-Stat-Collector/");

            FileOutputStream fos = new FileOutputStream(statsDir + "/" + outputFile + ".zip");

            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(outputFile);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(statsDir + "/" + outputFile);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();
            zos.close();
            fos.close();

        } catch (IOException ex) {
            Log.d(Settings.TAG, "Error occurred while compressing file. Details: " + ex.toString());
            ex.printStackTrace();
            Notify.showNotification(cont, "Data compression error.");
        }
        return result;
    }
}