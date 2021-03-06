package th.ac.bu.science.mit.allappstatscollector;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import CoreStats.NET;
import CoreStats.Stats;
import CoreStats.StepDownExtraction;

/**
 * Created by Komal on 2/4/2016.
 */
public class BackgroundIntentService extends Service {
    public static boolean is_Service_Running = false;
    public static volatile boolean stop_request;
    final Handler handler = new Handler();
    Runnable runnable;
    boolean isThreadRunning;
    final Context context = this;
    public static int counter = 0;
    Date currentDate;
    List<Stats> oldStats, newStats;
    PowerManager.WakeLock wakeLock;
    int ONGOING_NOTIFICATION_ID = 2147483;
    int internalCounter = 0;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;
    boolean isForeground=false;
    boolean forceStop=false;
    int networkErrorCount=0;
    boolean retry=false;


    /*public BackgroundIntentService()
    {
        super("Extractor Thread");
        context=this;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

    }*/
    /*Code for step down
    BroadcastReceiver mybroadcast = new BroadcastReceiver() {

        //When Event is published, onReceive method is called
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
           // Log.i("[BroadcastReceiver]", "MyReceiver");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //Log.i("[BroadcastReceiver]", "Screen ON");

                if(Settings.interval>=12)
                {
                    handler.removeCallbacks(runnable);
                  //  Settings.interval=Settings.minInterval;
                    runnable.run();
                }

                StepDownExtraction.count = 0;
                //Settings.interval = Settings.minInterval;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
               // Log.i("[BroadcastReceiver]", "Screen OFF");
            }
        }
    };
    Code for step down ends here*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();


        is_Service_Running = true;
        date = new Date();

        /*
        //code for step down
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        //code for step down
        */

        PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
        wakeLock.acquire();

        Settings.loadSettings(context);

        String path = Environment.getExternalStorageDirectory() + "/BU-Stat-Collector/" + Settings.getOutputFileName(context);
        File file = new File(path);
        stop_request = false;

        if (!file.exists()) {
            new StatsFileManager(context).createNewFile();
        }

    }


    private void updateNotification() {
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int icon;
        if(Build.VERSION.SDK_INT>21)
            icon=R.mipmap.ic_test;
        else
            icon=R.mipmap.ic_test103;

        mBuilder.setContentTitle("Stats Collector")
                .setContentText("Started: " + dateFormat.format(date))
                .setSubText("Session: " + counter)
                .setContentInfo("Interval: " + Settings.interval)
                .setSmallIcon(icon)
                .setColor(Color.parseColor("#78909C"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_LOW)

                .setContentIntent(resultPendingIntent);
        //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_test1));
        mNotifyManager.notify(ONGOING_NOTIFICATION_ID, mBuilder.build());
    }


    private void startAsForeground()
    {
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int icon;
        if(Build.VERSION.SDK_INT>21)
            icon=R.mipmap.ic_test;
        else
        icon=R.mipmap.ic_test103;


        mBuilder.setContentTitle("Stats Collector")
                .setContentText("Started: " + dateFormat.format(date))
                .setSubText("Session: " + counter)
                .setContentInfo("Interval: " + Settings.interval)
                .setSmallIcon(icon)
                .setColor(Color.parseColor("#78909C"))
                .setCategory(Notification.CATEGORY_SERVICE)
                .setPriority(Notification.PRIORITY_LOW)

                .setContentIntent(resultPendingIntent);
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_test1));

        startForeground(ONGOING_NOTIFICATION_ID, mBuilder.build());

        isForeground=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startAsForeground();
        networkErrorCount=0;
        retry=true;

        currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        internalCounter = 0;

        if (!isThreadRunning)       //thread is not running so start the thread
        {

            counter = 0;
            Log.d(Settings.TAG, "Stats extraction process started.");

            try {
                handler.postDelayed(runnable = new Runnable() {

                    @Override
                    public void run() {
                        List<Stats> diff=null;
                        try{
                            if(isForeground) {

                                if(android.os.Build.VERSION.SDK_INT <=19)   //below lollipop we need to remove the notification by stop foreground and restart the notification
                                {
                                    stopForeground(true);
                                    updateNotification();
                                }
                                else {
                                    stopForeground(false);
                                }
                                isForeground=false;
                            }
                        }
                        catch(Exception ex)
                        {
                            Log.d(Settings.TAG,"Error occurred while stop foreground");
                        }

                        if (compare(currentDate, new Date()) != 0) {        //this code generates the hash code every midnight.s
                            currentDate = new Date();
                            Log.d(Settings.TAG, "Generating hash code..Date changed");
                            HashGen hashGen = new HashGen();
                            hashGen.getAllAppInfo(context);//creates hashcode file for every app
                        }

                        //scheduler for uploading hashcode
                        File file = new File(Settings.getHashFilePath());

                        boolean isExist = file.exists();
                        //boolean isWifiAvailable = NET.isWifiAvailable(context);

                        //if hash file exist upload it to server..
                        if (isExist && HashFileUploader.isUploading == false && Settings.IS_WIFI_AVAILABLE && HashGen.isGenerating == false) {     //if there is hash file available and wifi is on upload the file and delete it.
                            HashFileUploader hfUploader = new HashFileUploader(context);
                            hfUploader.execute();   //this method uploads the hash file to server and delete the file from device.
                        }

                        isExist = file.exists();   //make sure file does not exist.

                        Log.d("bu-stats","Upload Size:"+Settings.UploadSize);
                        Log.d("bu-stats","isUploading:"+FileUploader.isUploading);
                        Log.d("bu-stats","IS_WIFI_AVAILABLE:"+Settings.IS_WIFI_AVAILABLE);
                        Log.d("bu-stats","isExist:"+isExist);
                        Log.d("bu-stats","IsGenerating Hash Code: "+HashGen.isGenerating);

                        //if hash file is uploaded and does not exist and wifi is available and hash generation process is not working and uploading of stats file is not working and current file size has surpass the threshold then upload it.
                        if (StatsFileManager.getFileSize(context) >= Settings.UploadSize && FileUploader.isUploading == false && Settings.IS_WIFI_AVAILABLE && isExist == false && HashGen.isGenerating == false) //if hash file exist, info is not uploaded yet. So wait for this file to upload before uploading stats.
                        {
                            Log.d("bu-stats","Uploading File.");
                            FileUploader fileUploader = new FileUploader(context);
                            fileUploader.execute();
                        }

                        //Log.d("test-test", "FileUploader.IsUploading = " + FileUploader.isUploading);
                        if (!FileUploader.isUploading)           //if file is not uploading. (file is not in use) then calculate the stats and store it in database.
                        {
                            String data = "";
                            //List<Stats> stepDownStats = null;
                            if (internalCounter == 0)  //get the stats for the first time.
                            {
                                oldStats = Stats.getStats(context);
                            }
                            else if (internalCounter > Settings.NetInterval)
                            {
                                newStats = Stats.getStats(context);
                                if(newStats==null || oldStats==null)
                                {
                                    //can't read network proc file.
                                    Log.e(Settings.TAG,"Unable to read network file. Stopping process");
                                }
                                diff = Stats.NETDifference(oldStats, newStats);


/*                                Log.d(Settings.TAG,"New Data..--------------------");
                                for(Stats st: newStats)
                                {
                                    Log.d(Settings.TAG, st.getStringData());
                                }*/

                                oldStats = newStats;
                                try {
                                    startAsForeground();
                                }
                                catch (Exception ex)
                                {
                                    Log.d(Settings.TAG,"Error occurred while starting foreground");
                                }

                                boolean error = false;

                                String networkType=getNetworkType();
                                for (Stats stats : diff) {
                                    if (stats.NetStats.error) {
                                        error = true;
                                    }

                                    data = data + stats.getStringData()+"|" + networkType +"\n";
                                }
                            //    Log.d(Settings.TAG,"data without difference...++++++++++++++++++++++++++++++++++++++++++++++");
                              //  Log.d(Settings.TAG,data);
                                if (error)
                                {
                                    networkErrorCount++;
                                    //retry for network file...but skip this malformed data...

                                    if(networkErrorCount>=3)
                                    {
                                        retry=false;
                                        forceStop = true;
                                        ShowMessage.message("Malformed data. Need to restart phone.", context);
                                        Log.d(Settings.TAG, "Malformed network file");
                                        Notify.showNotification(context, "Malformed data in system file. Please restart the device to clean the file.");
                                        isThreadRunning = false;
                                        sendMessage();
                                        stop_request = true;
                                        onDestroy();
                                        return;
                                    }
                                    else
                                        retry=true;
                                }
                                else
                                {
                                    networkErrorCount = 0;
                                }

                                internalCounter = 1;
                                //stepDownStats = newStats;
                                Log.d(Settings.TAG, "data with network...++++++++++++++++++++++++++++++++++++++");
                                Log.d(Settings.TAG, data);
                                if(retry)
                                new StatsFileManager(context).SaveStats(data);

                            } else {
                                List<Stats> statsWithoutNet = Stats.getStatsWithoutNetowrk(context);

                                String networkType= getNetworkType();
                                for (Stats stats : statsWithoutNet)
                                    data = data + stats.getStringData() +"|" + networkType + "\n";
                                //stepDownStats = statsWithoutNet;
                                //Log.d(Settings.TAG,"Data without netowrk-------------------------------------------");
                                //Log.d(Settings.TAG, data);
                                new StatsFileManager(context).SaveStats(data);
                            }
                            //Log.d("stats-result", "Internal counter: " + internalCounter);
                    internalCounter++;
/*


                           //Step-down for changing interval begins here;.,..


                            boolean isActive= StepDownExtraction.isDeviceActive(context);
                            boolean checkNetworkStepdown=false;
                            if(!isActive && stepDownStats!=null)
                            {
                                StepDownExtraction tempStepDown =new StepDownExtraction(stepDownStats);
                                if(tempStepDown.cpuTotal >0)
                                {
                                    Settings.interval=Settings.minInterval;
                                    StepDownExtraction.count=0;
                                    Log.d(Settings.TAG,"CPU: "+ tempStepDown.cpuTotal);
                                }
                                else
                                {
                                    StepDownExtraction.count++;
                                    checkNetworkStepdown=true;
                                }
                            }
                            Log.d(Settings.TAG,"Count: "+StepDownExtraction.count + "  \t Threshold: "+StepDownExtraction.threshold);


                            if (!isActive && diff != null && StepDownExtraction.count >= Settings.NetInterval)  //initialize if screen is off or not.)  //device sleeping.  Start step down approach
                            {
                                StepDownExtraction stepDownExtraction = new StepDownExtraction(diff);
                                int totalCpu = stepDownExtraction.cpuTotal;
                                int networkUsage=stepDownExtraction.totalNetwork;
                                Log.d(Settings.TAG,"Netowork : "+  networkUsage);
                                if (totalCpu <= 0 && networkUsage<=0)  //if CPU and memory is not in use then increase the interval;
                                {
                                    Log.d(Settings.TAG,"Going into step down mode");

                                    StepDownExtraction.count++;
                                    Log.d(Settings.TAG,"Count: "+StepDownExtraction.count);
                                    Log.d(Settings.TAG,"threshold: "+StepDownExtraction.threshold);
                                    if (true)  //cpu was 0 for n (threshold) times. increase the delay
                                    {
                                        Log.d(Settings.TAG,"Went to step down");

                                        if ((Settings.interval * 2) <= Settings.maxInterval)
                                            Settings.interval += Settings.interval;
                                        else
                                            Settings.interval = Settings.maxInterval; //set it to max
                                    }
                                }
                                else
                                {
                                    Log.d(Settings.TAG, "both are not zero reseting counter");
                                    Settings.interval = Settings.minInterval;
                                    StepDownExtraction.count = 0;                             //reset the counter

                                }
                                Log.d(Settings.TAG,"Interval: "+Settings.interval);
                                //Log.d("step-down", "Device sleeping. Interval: " + Settings.interval + " CPU: " + totalCpu);
                            }
                            else if(isActive)       //device is awake set default interval...
                            {
                                Log.d(Settings.TAG, "awake reseting counter");
                                StepDownExtraction.count = 0;                             //reset the counter
                                Settings.interval = Settings.minInterval;//set default interval
                            }
                            //step down ends here...
*/
                        }
                        counter++;
                        handler.postDelayed(this, Settings.getInterval() * 1000);
                    }
                }, Settings.getInterval() * 1000);

            } catch (Exception ex) {
                Log.d(Settings.TAG, "Error occured in stats collector thread. " + ex.toString());
                Notify.showNotification(context, "Error occurred while collecting data.");
            }

        }
        isThreadRunning = !isThreadRunning;

        if(stop_request)
            return START_NOT_STICKY;
        else
        return START_STICKY;
    }


    public String getNetworkType()
    {
        return Settings.network_type+"";
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try
        {
            Log.d(Settings.TAG, "Swipped out.");
            super.onTaskRemoved(rootIntent);
            if(wakeLock.isHeld())
                wakeLock.release();

            handler.removeCallbacks(runnable);
            stopForeground(true);
           /*code for step down*/ //unregisterReceiver(mybroadcast);
            is_Service_Running = false;

            if (!stop_request)
                sendBroadcast(new Intent("YouWillNeverKillMe"));

            stopSelf();
        }
        catch (Exception ex)
        {
            Log.d(Settings.TAG, "error " + ex.toString());
        }
    }

    private void sendMessage()
    {
        Intent intent=new Intent("updateUI");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try
        {

           /*code for stpe down*/ //unregisterReceiver(mybroadcast);
            if(wakeLock.isHeld())
                wakeLock.release();
            handler.removeCallbacks(runnable);
            is_Service_Running = false;
            stopForeground(true);
            if(forceStop) {
                Intent i = new Intent(this, BackgroundIntentService.class);
                stopService(i);
            }

            if (!stop_request)
                sendBroadcast(new Intent("YouWillNeverKillMe"));
        }
        catch (Exception ex) {
            Log.d(Settings.TAG,"error "+ex.toString());
        }

    }

    public int compare(Date d1, Date d2) {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(d1);

        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(d2);


        if (calendar1.YEAR != calendar2.YEAR)
            return calendar1.YEAR - calendar2.YEAR;

        if (calendar1.MONTH != calendar2.MONTH)
            return calendar1.MONTH - calendar2.MONTH;

        return calendar1.DATE - calendar2.DATE;

    }
}
